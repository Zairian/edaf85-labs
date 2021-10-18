package lift;

import java.util.Arrays;

public class LiftMonitor {

    private int floor; // the floor the lift is currently on
    private boolean moving; // true if the lift is moving, false if standing still with doors open
    private int direction; // +1 if lift is going up, -1 if going down
    private int[] waitEntry; // number of passengers waiting to enter the lift at the various floors
    private int[] waitExit; // number of passengers (in lift) waiting to leave at the various floors
    private int load; // number of passengers currently in the lift
    private LiftView view; // view object passed from main
    private boolean doorState; // State (open/closed) of door on current floor
    private int passengersEntering; // Counter of number of passengers currently entering the elevator
    private int passengersExiting; // Counter of number of passengers currently exiting the elevator

    public LiftMonitor(LiftView v){
        floor = 0;
        moving = true;
        direction = 1;
        waitEntry = new int[7];
        waitExit = new int[7];
        load = 0;
        view = v;
        passengersEntering = 0;
        passengersExiting = 0;
    }

    // Increases the number of waiting passengers on given floor
    public synchronized void increaseWaitEntry(int passengerFloor) {
        waitEntry[passengerFloor]++;
        notifyAll();
    }

    // Handles passengers waiting to enter arriving lift
    public synchronized void enterLift(int passengerFloor, int passengerDestination) {
        while (floor != passengerFloor || load == 4 || moving || passengersEntering == 4) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new Error("Monitor.enterLift interrupted " + e);
            }
        }
        waitEntry[floor]--;
        waitExit[passengerDestination]++;
        load++;
        passengersEntering++;
        notifyAll();
    }

    // Called after passengers has completed entry animation
    public synchronized void enterCompleted() {
        passengersEntering--;
        notifyAll();
    }

    // Handles passengers waiting to exit lift
    public synchronized void exitLift(int passengerDestination){
        while (floor != passengerDestination){
            try {
                wait();
            }catch (InterruptedException e){
                throw new Error("Monitor.exitLift interrupted " + e);
            }
        }
        waitExit[passengerDestination]--;
        load--;
        passengersExiting++;
        notifyAll();
    }

    // Called after passengers has completed exit animation
    public synchronized void exitCompleted() {
        passengersExiting--;
        notifyAll();
    }

    // Handles the conditions when the elevator is to wait for passengers entering/exiting the lift and waiting when there are no passengers waiting on any floor.
    public synchronized int[] liftContinue() {

        while (Arrays.stream(waitEntry).sum() == 0 && Arrays.stream(waitExit).sum() == 0){
            try {
                wait();
            }catch (InterruptedException e){
                throw new Error("LiftContinue no passengers interrupted " + e);
            }
        }

        //Stops the elevator to allow passengers to enter or exit
        while (waitEntry[floor] > 0 && load != 4 || waitExit[floor] > 0 || passengersEntering > 0 || passengersExiting > 0) {
            if(!doorState){
                view.openDoors(floor);
                doorState = true;
            }
            moving = false;
            notifyAll();
            try {
                wait();
            } catch (InterruptedException e) {
                throw new Error("Monitor.liftContinue interrupted " + e);
            }
        }

        if(!moving) view.closeDoors();
        if(doorState) doorState = false;
        moving = true;

        calculateDirection();

        //Sends the elevators current position and next position to the LiftThread
        int[] movingPositions = new int[2];
        movingPositions[0] = floor;
        movingPositions[1] = floor + direction;
        return movingPositions;
    }

    //Moves the elevator in the given direction
    public synchronized void incrementFloor(){
        floor = floor + direction;
    }

    // Handles calculation of elevator direction
    // Changes the direction of the elevator if there are no passengers that wants to enter or exit on floors either above or below the elevator
    private void calculateDirection() {
        if(Arrays.stream(waitEntry, floor, 7).sum() == 0 && Arrays.stream(waitExit, floor, 7).sum() == 0 && floor != 0){
            direction = -1;
        } else if(Arrays.stream(waitEntry, 0, floor+1).sum() == 0 && Arrays.stream(waitExit, 0, floor+1).sum() == 0 && floor != 6) {
            direction = 1;
        } else if(floor == 6){
            direction = -1;
        } else if(floor == 0){
            direction = 1;
        }
    }
}
