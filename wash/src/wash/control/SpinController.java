package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {
    // TODO: add attributes
    WashingIO io;

    private final static int OFF = 0;
    private final static int SLOW = 1;
    private final static int FAST = 2;

    int currentSetting;
    boolean spinDirectionToggle = false;

    ActorThread<WashingMessage> t;

    public SpinController(WashingIO io) {
        // TODO
        this.io = io;
    }

    @Override
    public void run() {
        try {

            // ... TODO ...

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                WashingMessage ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);;

                // if m is null, it means a minute passed and no message was received
                if (m != null) {

                    switch (m.getCommand()){
                        case WashingMessage.SPIN_SLOW:
                            currentSetting = SLOW;
                            t = m.getSender();
                            t.send(ack);
                            break;
                        case WashingMessage.SPIN_FAST:
                            currentSetting = FAST;
                            t = m.getSender();
                            t.send(ack);
                            break;
                        case WashingMessage.SPIN_OFF:
                            currentSetting = OFF;
                            io.setSpinMode(WashingIO.SPIN_IDLE);
                            t = m.getSender();
                            t.send(ack);
                            break;
                    }
                    System.out.println("got " + m);
                }
                switch (currentSetting) {
                    case SLOW:
                        if(spinDirectionToggle) {
                            io.setSpinMode(WashingIO.SPIN_LEFT);
                            spinDirectionToggle = false;
                        } else {
                            io.setSpinMode(WashingIO.SPIN_RIGHT);
                            spinDirectionToggle = true;
                        }
                        break;
                    case FAST:
                        io.setSpinMode(WashingIO.SPIN_FAST);
                        break;
                }
                // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
