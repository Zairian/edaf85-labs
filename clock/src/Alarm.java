import clock.io.ClockOutput;

public class Alarm extends Thread {

    ClockData data;
    ClockOutput output;

    public Alarm(ClockData d, ClockOutput o){
        data = d;
        output = o;
    }

    public void run(){

    }
}
