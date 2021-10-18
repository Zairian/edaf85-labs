package wash.control;
import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);
        
        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        ActorThread currentProgram = null;

        while (true) {
            int n = io.awaitButton();
            switch(n){
                case 0:
                    assert currentProgram != null;
                    currentProgram.interrupt();
                    break;
                case 1:
                    currentProgram = new WashingProgram1(io, temp, water, spin);
                    currentProgram.start();
                    break;
                case 2:
                    currentProgram = new WashingProgram2(io, temp, water, spin);
                    currentProgram.start();
                    break;
                case 3:
                    currentProgram = new WashingProgram3(io, temp, water, spin);
                    currentProgram.start();
                    break;
            }
            System.out.println("user selected program " + n);

            // TODO:
            // if the user presses buttons 1-3, start a washing program
            // if the user presses button 0, and a program has been started, stop it
        }
    }
};
