package elevator.utils;

import elevator.model.Elevator;

public interface ElevatorFactory {
    ElevatorFactory setCompany(String company);
    ElevatorFactory setFloors(int floors);
    ElevatorFactory setDefaultFloor(int floorId);
    ElevatorFactory setElevatorId(int id);
    Elevator build();
}
