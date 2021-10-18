import clock.io.ClockInput;
import clock.io.ClockOutput;

import java.util.Objects;
import java.util.concurrent.Semaphore;

public class TimeIncrement extends Thread {

    ClockData data;
    ClockOutput output;

    public TimeIncrement(ClockData d, ClockOutput o){
        data = d;
        output = o;
    }

    @Override
    public void run() {
        long t;
        while (true) {

            t = System.currentTimeMillis();

            try {
                secondIncrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                alarmCheck();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                secondSync(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void secondSync(long t) throws InterruptedException {
        long diff;
        t += 1000;
        diff = t - System.currentTimeMillis();
        if (diff > 0) Thread.sleep(diff);
    }

    private void secondIncrement() throws InterruptedException {
        int[] time;
        time = data.incrementSecond();
        output.displayTime(time[0], time[1], time[2]);
    }

    private void alarmCheck() throws InterruptedException {
        if(Objects.equals(data.getAlarm(), data.getTime()) && data.getAlarmState()){
            output.alarm();
            data.setAlarmCounter(20);
        }else if(data.getAlarmCounter() > 0){
            output.alarm();
            data.setAlarmCounter(data.getAlarmCounter()-1);
        }
    }
}