package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    // TODO: add attributes

    int currentSetting;
    private final static int IDLE = 0;
    private final static int DRAIN = 1;
    private final static int FILL = 2;

    double wantedWaterLevel = 0;

    WashingIO io;
    ActorThread<WashingMessage> t;

    boolean ackHasBeenSent = false;

    public WaterController(WashingIO io) {
        // TODO
        this.io = io;
    }

    @Override
    public void run() {
        // TODO
        try {
            while (true){
                WashingMessage m = receiveWithTimeout(6000 / Settings.SPEEDUP);


                WashingMessage ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);;

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    switch (m.getCommand()){
                        case WashingMessage.WATER_FILL:
                            wantedWaterLevel = m.getValue();
                            currentSetting = FILL;
                            t = m.getSender();
                            break;
                        case WashingMessage.WATER_DRAIN:
                            currentSetting = DRAIN;
                            t = m.getSender();
                            break;
                        case WashingMessage.WATER_IDLE:
                            currentSetting = IDLE;
                            t = m.getSender();
                            break;
                    }
                    System.out.println("got " + m);
                }

                switch (currentSetting) {
                    case FILL:
                        if(io.getWaterLevel() < 10){
                            io.fill(true);
                        } else {
                            t.send(ack);
                        }
                        break;
                    case DRAIN:
                        io.drain(true);
                        if(io.getWaterLevel() == 0){
                            if(!ackHasBeenSent) {
                                t.send(ack);
                                ackHasBeenSent = true;
                            }
                        }
                        break;
                    case IDLE:
                        io.fill(false);
                        io.drain(false);
                        ackHasBeenSent = false;
                        break;
                }
            }

        } catch (InterruptedException unexpected) {
            throw new Error(unexpected);
        }
    }
}
