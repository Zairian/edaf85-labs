package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WashingProgram1 extends ActorThread<WashingMessage>{

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin)
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }

    @Override
    public void run() {
        try {
            // Lock the hatch
            io.lock(true);

            // Fill barrel with 10L of water
            // Expect an acknowledgment in response when filled.
            water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
            WashingMessage ack0 = receive();
            System.out.println("washing program 1 got " + ack0);

            // Stop water tap
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

            // Start waterheating
            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
            WashingMessage ack1 = receive();
            System.out.println("washing program 1 got " + ack1);

            // Instruct SpinController to rotate barrel slowly, back and forth
            // Expect an acknowledgment in response.
            System.out.println("setting SPIN_SLOW...");
            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            WashingMessage ack2 = receive();
            System.out.println("washing program 1 got " + ack2);

            // Spin for 30 simulated minutes (one minute == 60000 milliseconds)
            Thread.sleep(30 * 60000 / Settings.SPEEDUP);

            // Turn off heating
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));

            // Drain water from barrel
            // Expect an acknowledgment in response when water has been drained.
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            WashingMessage ack3 = receive();
            System.out.println("washing program 1 got " + ack3);

            // Close drain
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

            for(int i = 0; i<5; i++){
                // Fill barrel with 10L of water
                // Expect an acknowledgment in response when filled.
                water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
                WashingMessage ack4 = receive();
                System.out.println("washing program 1 got " + ack4);

                // Stop water tap
                water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

                // Spin for five simulated minutes (one minute == 60000 milliseconds)
                Thread.sleep(2 * 60000 / Settings.SPEEDUP);

                // Drain water from barrel
                // Expect an acknowledgment in response when water has been drained.
                water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
                WashingMessage ack5 = receive();
                System.out.println("washing program 1 got " + ack5);

                // Close drain
                water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            }

            // Drain water from barrel
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));

            // Start centrifuge
            // Expect an acknowledgment in response when centrifuge has begun
            spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
            WashingMessage ack6 = receive();
            System.out.println("washing program 1 got " + ack6);

            // Spin for five simulated minutes (one minute == 60000 milliseconds)
            Thread.sleep(5 * 60000 / Settings.SPEEDUP);

            // Wait for water to drain
            WashingMessage ack7 = receive();
            System.out.println("washing program 1 got " + ack7);

            // Close drain
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

            // Instruct SpinController to stop spin barrel spin.
            // Expect an acknowledgment in response.
            System.out.println("setting SPIN_OFF...");
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            WashingMessage ack8 = receive();
            System.out.println("washing program 1 got " + ack8);

            // Now that the barrel has stopped and the water has been drained, it is safe to open the hatch.
            io.lock(false);

            System.out.println("Washing program 1 FINISHED");

        } catch (InterruptedException e) {

            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
