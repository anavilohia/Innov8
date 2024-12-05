package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

  @Test
  void unscheduleTaskWithNullTaskThrowsException() {
    Schedule schedule = new Schedule();

    assertThrows(IllegalArgumentException.class,
            () -> schedule.unscheduleTask(null),
            "unscheduleTask should throw IllegalArgumentException when task is null.");
  }

  @Test
  void unscheduleTaskDoesNothingIfTaskNotScheduled() {
    Schedule schedule = new Schedule();

    // Task is not in the schedule
    schedule.unscheduleTask(mockTask1);

    // Verify no interactions with any resource since the task was not scheduled
    verifyNoInteractions(mockResource1, mockResource2);
    assertTrue(schedule.getTaskSchedule().isEmpty(),
            "unscheduleTask should not modify schedule if task is not present.");
  }

  @Test
  void unscheduleTaskReleasesResourcesAndRemovesTask() {
    Schedule schedule = new Schedule();

    // Simulate the task being scheduled with assigned resources
    List<Resource> assignedResources = List.of(mockResource1, mockResource2);
    schedule.getTaskSchedule().put(mockTask1, assignedResources);

    schedule.unscheduleTask(mockTask1);

    // Verify resources were released
    verify(mockResource1, times(1)).release();
    verify(mockResource2, times(1)).release();

    // Verify the task was removed from the schedule
    assertFalse(schedule.getTaskSchedule().containsKey(mockTask1),
            "unscheduleTask should remove the task from the schedule.");
  }

  @Test
  void updateScheduleNullTaskListThrowsException() {
    Schedule schedule = new Schedule();
    assertThrows(IllegalArgumentException.class,
            () -> schedule.updateSchedule(null, maxDistance),
            "updateSchedule should throw IllegalArgumentException for null task list.");
  }

  @Test
  void updateScheduleNegativeMaxDistanceThrowsException() {
    Schedule schedule = new Schedule();
    assertThrows(IllegalArgumentException.class,
            () -> schedule.updateSchedule(mockTasks, -10.0),
            "updateSchedule should throw IllegalArgumentException for negative maxDistance.");
  }

  @Test
  void updateScheduleEmptyTaskListReturnsEmptySchedule() {
    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule =
        schedule.updateSchedule(new ArrayList<>(), maxDistance);
    assertTrue(taskSchedule.isEmpty(),
        "updateSchedule should return an empty map for an empty task list.");
  }

  @Test
  void updateScheduleTaskWithNoResourcesIsSkipped() {
    when(mockTask1.getResources()).thenReturn(null);

    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule = schedule.updateSchedule(mockTasks, maxDistance);

    assertFalse(taskSchedule.containsKey(mockTask1),
            "updateSchedule should skip tasks with no required resources.");
  }

  @Test
  void updateScheduleTaskWithUnsatisfiableRequirementsIsSkipped() {
    when(mockTask1.getResources()).thenReturn(Map.of(mockResourceType, 2));
    when(mockResourceType.countAvailableUnits(any())).thenReturn(1); // Not enough units available
    when(mockResourceType.getLocation()).thenReturn(resourceLocation);

    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule = schedule.updateSchedule(mockTasks, maxDistance);

    assertFalse(taskSchedule.containsKey(mockTask1),
            "updateSchedule should skip tasks that can't be scheduled.");
  }

  @Test
  void updateScheduleSchedulesValidTasks() {
    when(mockTask1.getResources()).thenReturn(Map.of(mockResourceType, 1));
    when(mockTask1.getLocation()).thenReturn(taskLocation);
    when(mockResourceType.getLocation()).thenReturn(resourceLocation);
    when(mockResourceType.countAvailableUnits(any())).thenReturn(1);
    when(mockResourceType.findAvailableResource(any())).thenReturn(mockResource1);

    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule = schedule.updateSchedule(mockTasks, maxDistance);

    assertTrue(taskSchedule.containsKey(mockTask1),
            "updateSchedule should schedule valid tasks.");
    assertEquals(1, taskSchedule.get(mockTask1).size(),
            "Scheduled task should have 1 assigned resource.");
    verify(mockResource1, times(1)).assignUntil(any());
  }

  @Test
  void updateScheduleReleasesResourcesOnFailure() {
    when(mockTask1.getResources()).thenReturn(Map.of(mockResourceType, 2));
    when(mockTask1.getLocation()).thenReturn(taskLocation);
    when(mockResourceType.getLocation()).thenReturn(resourceLocation);
    when(mockResourceType.countAvailableUnits(any())).thenReturn(2);
    when(mockResourceType.findAvailableResource(any()))
            .thenReturn(mockResource1)
            .thenReturn(null); // Second resource can't be assigned

    Schedule schedule = new Schedule();
    Map<Task, List<Resource>> taskSchedule = schedule.updateSchedule(mockTasks, maxDistance);

    assertFalse(taskSchedule.containsKey(mockTask1),
            "updateSchedule should skip tasks that fail to schedule.");
    verify(mockResource1, times(1)).release(); // Resource should be released
  }
}