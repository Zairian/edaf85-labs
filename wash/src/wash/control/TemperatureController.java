package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    int currentSetting;
    private final static int IDLE = 0;
    private final static int SET = 1;

    double wantedTempLevel = 0;

    boolean ackHasBeenSent = false;

    WashingIO io;

    ActorThread<WashingMessage> t;

    public TemperatureController(WashingIO io) {
        // TODO
        this.io = io;
    }

    @Override
    public void run() {
        // TODO
        try {
            while (true){
                WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);


                WashingMessage ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);;

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    switch (m.getCommand()){
                        case WashingMessage.TEMP_SET:
                            currentSetting = SET;
                            wantedTempLevel = m.getValue();
                            t = m.getSender();
                            ackHasBeenSent = false;
                            break;
                        case WashingMessage.TEMP_IDLE:
                            currentSetting = IDLE;
                            t = m.getSender();
                            break;
                    }
                    System.out.println("got " + m);
                }

                switch (currentSetting) {
                    case SET:
                        if(io.getTemperature() < (wantedTempLevel - 2)) {
                            io.heat(true);
                        } else if(io.getTemperature() >= (wantedTempLevel - 2) && io.getTemperature() < (wantedTempLevel - 2) + 0.21) {
                            io.heat(true);
                        } else if(io.getTemperature() >= (wantedTempLevel - 0.678)){
                            if(!ackHasBeenSent) {
                                t.send(ack);
                                ackHasBeenSent = true;
                            }
                            io.heat(false);
                        }
                        break;
                    case IDLE:
                        io.heat(false);
                        ackHasBeenSent = false;
                        break;
                }
            }

        } catch (InterruptedException unexpected) {
            throw new Error(unexpected);
        }
    }
}
