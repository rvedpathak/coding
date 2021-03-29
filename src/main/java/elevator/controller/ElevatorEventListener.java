package elevator.controller;

import elevator.model.Elevator;

import java.io.Writer;

public interface ElevatorEventListener {
    public void handleEvent(int elevatorId, int time, Elevator.ElevatorDoorStatus status, int floor);
    public void setWriter(Writer writer);
}
