package elevator.model;

import elevator.utils.ElevatorDefaults;
import elevator.controller.ElevatorEventListener;
import elevator.controller.ElevatorEventListenerImpl;
import elevator.utils.ElevatorFactory;
import elevator.exceptions.InvalidInputException;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

public class Elevator implements IElevator {
    private Direction direction = Direction.NONE;
    private String company;
    private int floors;
    private int id;

    Writer writer;
    public void setInMove(boolean inMove) {
        isInMove = inMove;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    private int currentFloor;

    public Direction getDirection() {
        return direction;
    }

    public String getCompany() {
        return company;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public boolean isInMove() {
        return isInMove;
    }

    private BitSet targetFloors;
    boolean isInMove;
    private int max;
    private int min;
    private int upCalls;
    private int downCalls;
    int time;
    private ElevatorEventListener eventListener;

    private Elevator(String company, int floors, int defaultFloor, int id) {
        this.company = company;
        this.floors = floors;
        this.id = id;
        currentFloor = defaultFloor;
        targetFloors = new BitSet(floors);
        min = 0;
        max = floors;
        eventListener = new ElevatorEventListenerImpl();
    }

    public void setWriter(Writer writer){
        this.writer = writer;
        eventListener.setWriter(writer);
    }
    public synchronized ElevatorStatus getElevatorStatus() {
        return new ElevatorStatus(this);
    }

    public enum ElevatorDoorStatus {
        OPEN,
        CLOSE
    }

    public void reset() {
        targetFloors.clear();
        isInMove = false;
        direction = Direction.NONE;
    }

    public synchronized void setTargetFloor(int floorId) {
        System.out.println("Lift :"+ id +", Target :"+ floorId);
        isInMove = true;
        if (floorId < 0 || floorId > floors) {
            throw new InvalidInputException("Invalid target floor :" + floorId);
        }
        if (currentFloor == floorId || targetFloors.get(floorId)) {
            return;
        }
        if (floorId > currentFloor) {
            upCalls++;
            max = (floorId > max) ? (floorId) : (max);
        } else {
            downCalls++;
            min = (floorId < min) ? (floorId) : (min);
        }
        if (direction == Direction.NONE)
            direction = (floorId > currentFloor) ? (Direction.UP) : (Direction.DOWN);
        targetFloors.set(floorId);
    }

    public int getTargetFloor() {
        if (direction == Direction.UP) return max;
        if (direction == Direction.DOWN) return min;
        return -1;
    }

    public synchronized void openDoor(int floor) {
        try {
            writer.write("Current Status :"+ getElevatorStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
        time += ElevatorDefaults.DEFAULT_OPEN_TIME;
        eventListener.handleEvent(id, time, ElevatorDoorStatus.OPEN, currentFloor);
        //System.out.println("Closing Door");
        time += ElevatorDefaults.DEFAULT_CLOSE_TIME;
        eventListener.handleEvent(id, time, ElevatorDoorStatus.CLOSE, currentFloor);
    }

    public synchronized boolean move() {
        //System.out.println("Move called for Lift :"+ id);
        if (!isInMove) {
            return false;
        }
        if (direction == Direction.UP) { // Move UP
            if (targetFloors.get(++currentFloor)) {
                time += ElevatorDefaults.DEFAULT_CLOSE_TIME;
                eventListener.handleEvent(id, time, ElevatorDoorStatus.CLOSE, currentFloor);
                openDoor(currentFloor);
                targetFloors.clear(currentFloor);
                if (--upCalls == 0) {
                    direction = (downCalls == 0) ? (Direction.NONE) : (Direction.DOWN);
                    max = floors;
                    // Direction is changed
                    System.out.println("LIFT " + id + ": " + time + ElevatorDefaults.DEFAULT_TIME_UNIT);
                    if(direction== Direction.NONE) {
                        time = 0;
                        isInMove = false;
                    }
                }
            } else {
                time+= ElevatorDefaults.DEFAULT_STEP_TIME;
                eventListener.handleEvent(id, time, ElevatorDoorStatus.CLOSE, currentFloor);
            }
        } else if (direction == Direction.DOWN) { //Move Down
            if (targetFloors.get(--currentFloor)) {
                time += ElevatorDefaults.DEFAULT_CLOSE_TIME;
                eventListener.handleEvent(id, time, ElevatorDoorStatus.CLOSE, currentFloor);
                openDoor(currentFloor);
                targetFloors.clear(currentFloor);
                if (--downCalls == 0) {
                    direction = (upCalls == 0) ? (Direction.NONE) : (Direction.UP);
                    min = ElevatorDefaults.DEFAULT_MIN_FLOOR;
                    // Direction is changed
                    System.out.println("LIFT " + id + ": " + time + ElevatorDefaults.DEFAULT_TIME_UNIT);
                    if(direction== Direction.NONE) {
                        time = 0;
                        isInMove = false;
                    }
                }
            } else {
                time+= ElevatorDefaults.DEFAULT_STEP_TIME;
                eventListener.handleEvent(id, time, ElevatorDoorStatus.CLOSE, currentFloor);
            }
        }
        return true;
    }

    public boolean isMoving() {
        return isInMove;
    }

    public class ElevatorStatus {
        boolean isInMove;
        private int currentFloor;
        private Direction direction;
        private int elevatorId;

        public ElevatorStatus(Elevator elevator) {
            this.isInMove = elevator.isInMove;
            this.direction = elevator.direction;
            this.currentFloor = elevator.currentFloor;
            this.elevatorId = elevator.id;
        }

        public boolean isInMove() {
            return isInMove;
        }

        public int getCurrentFloor() {
            return currentFloor;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getElevatorId() {
            return elevatorId;
        }

        @Override
        public String toString() {
            return "ElevatorStatus{" +
                    "isInMove=" + isInMove +
                    ", currentFloor=" + currentFloor +
                    ", direction=" + direction +
                    ", elevatorId=" + elevatorId +
                    '}';
        }
    }

    public static class ElevatorBuilder implements ElevatorFactory {
        private String company;
        private int floors = ElevatorDefaults.MAX_FLOORS;
        private int floorId = ElevatorDefaults.DEFAULT_FLOOR;
        private int id = -1;

        public static ElevatorFactory newBuilder() {
            return new ElevatorBuilder();
        }

        public ElevatorFactory setCompany(String company) {
            this.company = company;
            return this;
        }

        public ElevatorFactory setFloors(int floors) {
            this.floors = floors;
            return this;
        }

        public ElevatorFactory setDefaultFloor(int floorId) {
            this.floorId = floorId;
            return this;
        }

        public ElevatorFactory setElevatorId(int id) {
            this.id = id;
            return this;
        }

        public Elevator build() {
            if (id == -1) {
                throw new InvalidInputException("ID is missing");
            }
            return new Elevator(company, floors, floorId, id);
        }
    }

}
