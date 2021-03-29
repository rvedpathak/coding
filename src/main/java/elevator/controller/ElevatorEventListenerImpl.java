package elevator.controller;

import elevator.model.Elevator;

import java.io.IOException;
import java.io.Writer;

public class ElevatorEventListenerImpl implements ElevatorEventListener {
    private Writer fileWriter;

    public void setWriter(Writer fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void handleEvent(int elevatorId, int time, Elevator.ElevatorDoorStatus status, int floor) {
        StringBuffer sb = new StringBuffer();
        sb.append("Time :" + time + "\n");
        sb.append("LIFT :" + elevatorId + "-->" + floor + "(" + status + ")\n");
        //System.out.println(sb.toString());
        try {
            fileWriter.write(sb.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
