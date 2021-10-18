import java.time.LocalTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClockData {
    private LocalTime time, alarm;
    private boolean alarmState;
    private int alarmCounter;
    private final Lock mutex;

    public ClockData() {
        time = LocalTime.of(0,0,0);
        alarm = LocalTime.of(0, 0, 0);
        alarmCounter = 0;
        mutex = new ReentrantLock();
    }

    public String getTime() {
        String t;
        mutex.lock();
        t = time.toString();
        mutex.unlock();
        return t;
    }

    public void setTime(int h, int m, int s) {
        mutex.lock();
        time = LocalTime.of(h, m, s);
        mutex.unlock();
    }

    public String getAlarm() {
        String tempAlarm;
        mutex.lock();
        tempAlarm = alarm.toString();
        mutex.unlock();
        return tempAlarm;
    }

    public void setAlarm(int h, int m, int s) {
        mutex.lock();
        alarm = LocalTime.of(h, m, s);
        mutex.unlock();
    }

    public void toggleAlarmState() {
        mutex.lock();
        alarmState = !alarmState;
        mutex.unlock();
    }

    public boolean getAlarmState() {
        boolean state;
        mutex.lock();
        state = alarmState;
        mutex.unlock();
        return state;
    }

    public int[] incrementSecond() {
        int[] hms = new int[3];
        mutex.lock();
        time = time.plusSeconds(1);
        hms[0] = time.getHour();
        hms[1] = time.getMinute();
        hms[2] = time.getSecond();
        mutex.unlock();
        return hms;
    }

    public void setAlarmCounter(int counter) {
        mutex.lock();
        alarmCounter = counter;
        mutex.unlock();
    }

    public int getAlarmCounter() {
        int counter;
        mutex.lock();
        counter = alarmCounter;
        mutex.unlock();
        return counter;
    }
}
