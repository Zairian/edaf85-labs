package lift;

import java.util.concurrent.ThreadLocalRandom;

public class PassengerThread extends Thread{

    private LiftView view;
    private LiftMonitor monitor;

    public PassengerThread(LiftView v, LiftMonitor m) {
        view = v;
        monitor = m;
    }

    public void run(){
        while(true){
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 90000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Passenger pass = view.createPassenger();
            pass.begin();
            monitor.increaseWaitEntry(pass.getStartFloor());
            monitor.enterLift(pass.getStartFloor(), pass.getDestinationFloor());
            pass.enterLift();
            monitor.enterCompleted();
            monitor.exitLift(pass.getDestinationFloor());
            pass.exitLift();
            monitor.exitCompleted();
            pass.end();
        }
    }
}
