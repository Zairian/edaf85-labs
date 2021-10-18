package lift;

public class LiftThread extends Thread{

    private LiftView view;
    private LiftMonitor monitor;

    public LiftThread(LiftView v, LiftMonitor m){
        view = v;
        monitor = m;
    }

    public void run(){
        while (true){
            int[] elevatorPositions = monitor.liftContinue();
            view.moveLift(elevatorPositions[0], elevatorPositions[1]);
            monitor.incrementFloor();
        }
    }
}
