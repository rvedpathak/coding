package elevator.controller;

import elevator.utils.ElevatorDefaults;
import elevator.utils.ElevatorFinderStrategy;
import elevator.utils.ElevatorWorker;
import elevator.model.Direction;
import elevator.model.Elevator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElevatorControllerImpl implements ElevatorController{
    int noOfElevators;
    int noOfFloors;
    ExecutorService workerService = null;
    Map<Integer, Elevator> elevatorMap = null;
    Map<Integer, ElevatorWorker> workerMap = new HashMap<>();
    ElevatorFinderStrategy elevatorFinderStrategy;
    public ElevatorControllerImpl(int noOfElevators, int noOfFloors){
        this.noOfElevators = noOfElevators;
        this.noOfFloors = noOfFloors;
        workerService = Executors.newFixedThreadPool(noOfElevators);
        elevatorMap = new HashMap<>(noOfElevators, 1);
        workerMap = new HashMap<>(noOfElevators, 1);
        FileWriter fw =  null;
        try {
            fw = new FileWriter(ElevatorDefaults.DEFAULT_OUTPUT_FILE_NAME, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=1;i<=noOfElevators;i++){
            Elevator.ElevatorBuilder builder = new Elevator.ElevatorBuilder();
            Elevator elevator = builder.setCompany(ElevatorDefaults.DEFAULT_COMPANY_NAME).setFloors(noOfFloors)
                    .setElevatorId(i).build();
            elevator.setWriter(fw);
            elevatorMap.put(i,elevator);
        }
    }
    public void printStatus() {
        for(Elevator elevator : elevatorMap.values()){
            System.out.println(elevator.getElevatorStatus());
        }
    }

    public Map<Integer, Elevator> getElevatorMap() {
        return elevatorMap;
    }

    @Override
    public void setElevatorFinderStrategy(ElevatorFinderStrategy finderStrategy) {
        this.elevatorFinderStrategy = finderStrategy;
    }

    public Elevator getElevator(int elevatorId) {
        return elevatorMap.get(elevatorId);
    }

    public Elevator.ElevatorStatus getElevatorStatus(int elevatorId) {
        return elevatorMap.get(elevatorId).getElevatorStatus();
    }

    public void setTargetFloor(int elevatorId, int floor) {
        elevatorMap.get(elevatorId).setTargetFloor(floor);
    }

    @Override
    public synchronized void handleCall(int source, int target) {
        //System.out.println("Source :"+ source+", target :"+ target);
        Elevator elevator = call(source, target>source);
        System.out.println("Found Elevator :"+ elevator.getId());
        setTargetFloor(elevator.getId(), target);
    }

    public Elevator call(int floor, boolean isUp) {
        if((floor<0) || (floor > noOfFloors )) {
            throw new IllegalArgumentException("Invalid Source");
        }

        Elevator closestElevator = elevatorFinderStrategy.findNextElevator(floor, isUp);
        closestElevator.setInMove(true);
        if(closestElevator.getCurrentFloor()== floor) {
            closestElevator.openDoor(floor);
        }
        closestElevator.setTargetFloor(floor);
        return closestElevator;
    }

    private int calculateRoute(int afloor, int bfloor) {
        return Math.abs(afloor - bfloor);
    }

    private int calculateRoute(int xfloor, int xefloor, int tfloor) {
        return calculateRoute(xefloor, tfloor) + calculateRoute(xfloor, tfloor);
    }

    public void startAll() {
        for(Elevator elevator : elevatorMap.values()){
            ElevatorWorker worker = new ElevatorWorker(elevator);
            workerMap.put(elevator.getId(), worker);
            workerService.execute(worker);
        }
    }

    public void stopAll() {
        workerService.shutdownNow();
    }

    @Override
    public void start(int elevatorId) {
        if(elevatorMap.containsKey(elevatorId)){
            ElevatorWorker worker = new ElevatorWorker(elevatorMap.get(elevatorId));
            workerMap.put(elevatorId,worker);
            workerService.execute(worker);
        }
    }

    @Override
    public void stop(int elevatorId) {
        workerMap.get(elevatorId).setShutdown(true);
        workerMap.remove(elevatorId);
    }
}
