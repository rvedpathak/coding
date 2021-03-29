package elevator.utils;

import elevator.model.Elevator;
import elevator.utils.ElevatorDefaults;

public class ElevatorWorker implements Runnable {
    private boolean shutdown;
    private Elevator elevator;

    public ElevatorWorker(Elevator elevator) {
        this.elevator = elevator;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public void run() {
        System.out.println("Running LIFT :" + elevator.getId());
        while (!shutdown) {
            if (elevator.move()) {
                try {
                    Thread.sleep(ElevatorDefaults.DEFAULT_STEP_TIME * 1000);
                } catch (InterruptedException ie) {
                    System.out.println("Elevator Worker Interrupted");
                }
            }
        }
        System.out.println("Shutting Down");
        return;
    }
}
