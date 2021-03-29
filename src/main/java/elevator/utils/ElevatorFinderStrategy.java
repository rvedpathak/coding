package elevator.utils;

import elevator.model.Elevator;

public interface ElevatorFinderStrategy {
    Elevator findNextElevator(int floor, boolean isUp);
}
