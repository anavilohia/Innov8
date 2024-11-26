package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the Schedule class.
 */
@SpringBootTest
@ContextConfiguration
public class ScheduleUnitTests {

  private List<Task> mockTasks;
  private ResourceType mockResourceType;
  private Task mockTask1;
  private Task mockTask2;
  private Resource mockResource1;
  private Resource mockResource2;
  private Location taskLocation;
  private Location resourceLocation;

  private final double maxDistance = 100.0;
  // private final double longitude = -73.96;

  /**
   * Set up to be run before all tests.
   */
  @BeforeEach
  void setupScheduleForTesting() {
    mockTask1 = mock(Task.class);
    mockTask2 = mock(Task.class);
    mockTasks = new ArrayList<Task>();
    mockTasks.add(mockTask1);
    mockTasks.add(mockTask2);

    mockResource1 = mock(Resource.class);
    mockResource2 = mock(Resource.class);
    mockResourceType = mock(ResourceType.class);

    taskLocation = mock(Location.class);
    resourceLocation = mock(Location.class);
    when(taskLocation.getDistance(any())).thenReturn(50.0);
    when(resourceLocation.getDistance(any())).thenReturn(50.0);
  }

  /**
   * Test for the constructor in Schedule class.
   */
  @Test
  void constructorTest() {
    assertDoesNotThrow(() -> new Schedule(),
        "Schedule constructor should not throw an exception.");
  }

  /**
   * Test for createSchedule method in Schedule class.
   */
  @Test
  void createScheduleTest() {
    when(mockTask1.getResources()).thenReturn(Map.of(mockResourceType, 1));
    when(mockTask1.getLocation()).thenReturn(taskLocation);
    when(mockResourceType.getLocation()).thenReturn(resourceLocation);
    when(mockResourceType.countAvailableUnits(any())).thenReturn(1);
    when(mockResourceType.findAvailableResource(any())).thenReturn(mockResource1);

    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule = schedule.updateSchedule(mockTasks, maxDistance);
    assertTrue(taskSchedule.containsKey(mockTask1),
        "Created schedule should contain mockTask1");
    assertEquals(1, taskSchedule.get(mockTask1).size(),
        "Created schedule should contain 1 resource for mockTask1");
  }

  /**
   * Test for unscheduleTask method in Schedule class.
   */
  @Test
  void unscheduleTaskTest() {
    when(mockTask1.getResources()).thenReturn(Map.of(mockResourceType, 1));
    when(mockTask1.getLocation()).thenReturn(taskLocation);
    when(mockResourceType.getLocation()).thenReturn(resourceLocation);
    when(mockResourceType.countAvailableUnits(any())).thenReturn(1);
    when(mockResourceType.findAvailableResource(any())).thenReturn(mockResource1);

    Schedule scheduleForInvalidTask = new Schedule();
    assertThrows(IllegalArgumentException.class,
        () -> scheduleForInvalidTask.unscheduleTask(null),
        "unscheduleTask method should throw exception when unscheduling null task");

    Schedule scheduleForValidTask = new Schedule();
    scheduleForValidTask.updateSchedule(mockTasks, maxDistance);
    assertDoesNotThrow(() -> scheduleForValidTask.unscheduleTask(mockTask1),
        "unscheduleTask method should not throw exception when unscheduling valid task");
    verify(mockResource1, times(1)).release();
  }

}