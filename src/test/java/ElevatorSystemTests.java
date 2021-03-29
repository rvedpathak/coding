import elevator.controller.ElevatorController;
import elevator.controller.ElevatorControllerImpl;
import elevator.model.Direction;
import elevator.model.Elevator;
import elevator.utils.ElevatorDefaults;
import elevator.utils.ElevatorFinderStrategy;
import elevator.utils.NearestElevatorFinderStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
public class ElevatorSystemTests {
    @Test
    public void testElevatorControllerSetup(){
        System.out.println("testElevatorControllerSetup");
        ElevatorController elevatorController = new ElevatorControllerImpl(2, 10);
        Map<Integer, Elevator> elevatorMap = elevatorController.getElevatorMap();
        Assert.assertNotNull("Elevator Controller Map is null", elevatorMap);
        Assert.assertTrue(elevatorMap.containsKey(1));
        Assert.assertTrue(elevatorMap.containsKey(2));
    }
    @Test
    public void testInitialElevatorStatus(){
        System.out.println("testInitialElevatorStatus");
        ElevatorController elevatorController = new ElevatorControllerImpl(2, 10);
        Elevator elevator = elevatorController.getElevator(1);
        Assert.assertNotNull(elevator);
        Elevator.ElevatorStatus status = elevator.getElevatorStatus();
        Assert.assertEquals(status.isInMove(), false);
        Assert.assertEquals(status.getDirection(), Direction.NONE);
        Assert.assertEquals(status.getCurrentFloor(), ElevatorDefaults.DEFAULT_FLOOR);
        Assert.assertEquals(status.getElevatorId(), 1);
    }
    @Test
    public void testElevatorFinderStrategy(){
        System.out.println("testElevatorFinderStrategy");
        ElevatorController controller = new ElevatorControllerImpl(2, 10);
        ElevatorFinderStrategy finderStrategy = new NearestElevatorFinderStrategy(controller.getElevatorMap(), 10);
        controller.setElevatorFinderStrategy(finderStrategy);
        Elevator elevator = controller.call(0,true);
        controller.setTargetFloor(elevator.getId(),10);
        Elevator elevator2 = finderStrategy.findNextElevator(5, true);
        Assert.assertEquals(elevator, elevator2);
    }
}
