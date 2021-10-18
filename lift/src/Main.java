import lift.LiftMonitor;
import lift.LiftThread;
import lift.LiftView;
import lift.PassengerThread;

public class Main {

    public static void main(String[] args) {
        LiftView view = new LiftView();
        LiftMonitor monitor = new LiftMonitor(view);

        PassengerThread[] passengers = new PassengerThread[20];

        LiftThread lift = new LiftThread(view, monitor);

        for (PassengerThread p: passengers) {
            p = new PassengerThread(view, monitor);
            p.start();
        }

        lift.start();
    }
}
