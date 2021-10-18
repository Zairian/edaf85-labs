import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockData data = new ClockData();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        TimeIncrement incrementer = new TimeIncrement(data, out);

        incrementer.start();



        while (true) {
            in.getSemaphore().acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();
            switch (choice) {
                case 1:
                    data.setTime(h, m, s);
                    break;
                case 2:
                    data.setAlarm(h, m, s);
                    break;
                case 3:
                    data.toggleAlarmState();
                    out.setAlarmIndicator(data.getAlarmState());
                    break;
            }
            data.setAlarmCounter(0);
        }
    }
}
