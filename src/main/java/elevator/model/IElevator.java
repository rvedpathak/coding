package elevator.model;

public interface IElevator {
    Elevator.ElevatorStatus getElevatorStatus();
    void reset();
    public void setTargetFloor(int floorId);
    public boolean move();
    public boolean isMoving();
}
