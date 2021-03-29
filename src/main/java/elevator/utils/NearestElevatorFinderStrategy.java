package elevator.utils;

import elevator.model.Direction;
import elevator.model.Elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearestElevatorFinderStrategy implements ElevatorFinderStrategy{
    Map<Integer, Elevator> elevatorMap;
    int noOfFloors;
    public NearestElevatorFinderStrategy(Map<Integer, Elevator> elevatorMap, int noOfFloors){
        this.elevatorMap = elevatorMap;
        this.noOfFloors = noOfFloors;
    }

    @Override
    public Elevator findNextElevator(int floor, boolean isUp) {
        List<Integer> elevators = new ArrayList<>(elevatorMap.keySet());

        Direction userDirection = (isUp) ? (Direction.UP):(Direction.DOWN);
        int minDistance = noOfFloors;
        Elevator closestElevator = null;
        int d;
        for (int elevatorId : elevators ) {
            Elevator elevator = elevatorMap.get(elevatorId);
            if(	(elevator.isInMove() == false) ||
                    ((userDirection == Direction.UP) && (elevator.getDirection() == Direction.UP) && (floor >= elevator.getCurrentFloor())) ||
                    ((userDirection == Direction.DOWN) && (elevator.getDirection() == Direction.DOWN) && (floor <= elevator.getCurrentFloor())) )
                d =	calculateRoute(floor,elevator.getCurrentFloor());
            else
                d = calculateRoute(floor, elevator.getCurrentFloor(), elevator.getTargetFloor());

            if(d<minDistance) {
                minDistance = d;
                closestElevator = elevator;
            }
        }
        return closestElevator;
    }
    private int calculateRoute(int afloor, int bfloor) {
        return Math.abs(afloor - bfloor);
    }

    private int calculateRoute(int xfloor, int xefloor, int tfloor) {
        return calculateRoute(xefloor, tfloor) + calculateRoute(xfloor, tfloor);
    }
}
