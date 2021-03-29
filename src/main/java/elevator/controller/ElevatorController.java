package elevator.controller;

import elevator.model.Elevator;
import elevator.utils.ElevatorFinderStrategy;

import java.util.Map;

public interface ElevatorController {
    public void printStatus();
    public Elevator getElevator(int elevatorId);
    public Elevator.ElevatorStatus getElevatorStatus(int elevatorId);
    public void setTargetFloor(int elevatorId, int floor);
    public void handleCall(int source, int target);
    public Elevator call(int floor , boolean direction);
    public void startAll();
    public void stopAll();
    public void start(int elevatorId);
    public void stop(int elevatorId);
    public Map<Integer, Elevator> getElevatorMap();
    public void setElevatorFinderStrategy(ElevatorFinderStrategy finderStrategy);
}
