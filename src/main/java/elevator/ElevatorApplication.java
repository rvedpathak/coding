package elevator;

import elevator.controller.ElevatorController;
import elevator.controller.ElevatorControllerImpl;
import elevator.utils.ElevatorFinderStrategy;
import elevator.utils.NearestElevatorFinderStrategy;

import java.util.Scanner;

public class ElevatorApplication {
    public static void main(String[] args) {
        // Main thread will take input, worker threads will run Lift
        System.out.println("Lift Management System");
        Scanner sc = new Scanner(System.in);
        System.out.println("No of lifts :");
        int noOfLifts = sc.nextInt();
        System.out.println("No of Floors :");
        int noOfFloors = sc.nextInt();
        ElevatorController elevatorController = new ElevatorControllerImpl(noOfLifts, noOfFloors);
        ElevatorFinderStrategy finderStrategy = new NearestElevatorFinderStrategy(elevatorController.getElevatorMap(), noOfFloors);
        elevatorController.setElevatorFinderStrategy(finderStrategy);
        elevatorController.startAll();
        while (true) {
            System.out.println("1. Call Lift\n 2. Show Status \n 3. Exit");
            int option = sc.nextInt();
            sc.nextLine();
            switch (option) {
                case 1:
                    //sc.next();
                    System.out.println("Enter Source Target Floors (e.g. \"0 7\")");
                    String str = sc.nextLine();
                    System.out.println("str :"+ str);
                    String[] floors = str.trim().split(" ");
                    if (floors.length != 2) {
                        System.err.println("Invalid number of input :" + str);
                    } else {
                        try {
                            int source = Integer.valueOf(floors[0]);
                            int target = Integer.valueOf(floors[1]);
                            elevatorController.handleCall(source, target);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Invalid input :" + str);
                        }
                    }
                    break;
                case 2:
                    elevatorController.printStatus();
                    break;
                case 3:
                    elevatorController.stopAll();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Option :"+ option);
            }
        }
    }
}
