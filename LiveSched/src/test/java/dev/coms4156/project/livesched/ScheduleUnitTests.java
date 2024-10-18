package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

  private Schedule testSchedule;
  private String scheduleId = "TestSchedule";
  private List<ResourceType> resourceTypes;
  private double maxDistance = 10.0;
  private Task testTask;
  private ResourceType testResourceType;

  /**
   * Set up to be run before all tests.
   */
  @BeforeEach
  void setupScheduleForTesting() {
    resourceTypes = new ArrayList<>();
    testResourceType = new ResourceType("Hospital", 5, 40.7128, -74.0060);
    resourceTypes.add(testResourceType);
    
    testSchedule = new Schedule(scheduleId, resourceTypes, maxDistance);
    
    Map<ResourceType, Integer> resourceList = new HashMap<>();
    resourceList.put(testResourceType, 1);
    LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    LocalDateTime endTime = startTime.plusHours(2);
    testTask = new Task("TestTask", resourceList, 3, startTime, endTime, 40.7128, -74.0060);
  }

  /**
   * Test for the constructor in Schedule class.
   */
  @Test
  void constructorTest() {
    assertDoesNotThrow(() -> new Schedule(scheduleId, resourceTypes, maxDistance),
        "Schedule constructor should not throw an exception with valid parameters.");

    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> new Schedule(null, resourceTypes, maxDistance),
        "Schedule constructor should throw an exception if scheduleId is null.");
    assertEquals("Schedule ID cannot be null or empty.", exception.getMessage());

    exception = assertThrows(IllegalArgumentException.class, 
        () -> new Schedule("  ", resourceTypes, maxDistance),
        "Schedule constructor should throw an exception if scheduleId is empty.");
    assertEquals("Schedule ID cannot be null or empty.", exception.getMessage());

    exception = assertThrows(IllegalArgumentException.class, 
        () -> new Schedule(scheduleId, null, maxDistance),
        "Schedule constructor should throw an exception if resourceTypes is null.");
    assertEquals("Resource types list cannot be null.", exception.getMessage());

    exception = assertThrows(IllegalArgumentException.class, 
        () -> new Schedule(scheduleId, resourceTypes, -1.0),
        "Schedule constructor should throw an exception if maxDistance is negative.");
    assertEquals("Maximum distance cannot be negative.", exception.getMessage());
  }

  /**
   * Test for matchTaskWithResource method in Schedule class.
   */
  @Test
  void matchTaskWithResourceTest() {
    ResourceType result = testSchedule.matchTaskWithResource(testTask);
    assertEquals(testResourceType, result, "Should return the matching resource type.");

    // Test with a task that requires more resources than available
    Map<ResourceType, Integer> resourceList = new HashMap<>();
    resourceList.put(testResourceType, 10);
    Task largeTask = new Task("LargeTask", resourceList, 3, 
        LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), 40.7128, -74.0060);
    result = testSchedule.matchTaskWithResource(largeTask);
    assertNull(result, "Should return null when no matching resource type is found.");

    // Test with a task that is too far away
    Task farTask = new Task("FarTask", resourceList, 3, 
        LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), 51.5074, -0.1278);
    result = testSchedule.matchTaskWithResource(farTask);
    assertNull(result, "Should return null when task is too far from resource.");

    // Test with null task
    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> testSchedule.matchTaskWithResource(null),
        "Should throw IllegalArgumentException when task is null.");
    assertEquals("Task cannot be null.", exception.getMessage());
  }

  /**
   * Test for receiveTask method in Schedule class.
   */
  @Test
  void receiveTaskTest() {
    assertDoesNotThrow(() -> testSchedule.receiveTask(testTask),
        "receiveTask should not throw an exception with a valid task.");

    // Test with null task
    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> testSchedule.receiveTask(null),
        "Should throw IllegalArgumentException when task is null.");
    assertEquals("Task cannot be null.", exception.getMessage());
  }

  /**
   * Test for processTask method in Schedule class.
   */
  @Test
  void processTaskTest() {
    testSchedule.receiveTask(testTask);

    Map<Task, ResourceType> result = testSchedule.processTask();
    
    assertEquals(1, result.size(), "Should return one task-resource pair.");
    assertTrue(result.containsKey(testTask), "The processed task should be in the result.");
    assertEquals(testResourceType, result.get(testTask), "The resource type should match.");
  }

  /**
   * Test for assignResourceToTask method in Schedule class.
   */
  @Test
  void assignResourceToTaskTest() {
    assertDoesNotThrow(() -> testSchedule.assignResourceToTask(testResourceType, testTask),
        "assignResourceToTask should not throw an exception with valid inputs.");

    // Test with null resourceType
    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> testSchedule.assignResourceToTask(null, testTask),
        "Should throw IllegalArgumentException when resourceType is null.");
    assertEquals("ResourceType cannot be null.", exception.getMessage());

    // Test with null task
    exception = assertThrows(IllegalArgumentException.class, 
        () -> testSchedule.assignResourceToTask(testResourceType, null),
        "Should throw IllegalArgumentException when task is null.");
    assertEquals("Task cannot be null.", exception.getMessage());
  }

  /**
   * Test for getScheduleId method in Schedule class.
   */
  @Test
  void getScheduleIdTest() {
    assertEquals(scheduleId, testSchedule.getScheduleId(), 
        "getScheduleId should return the correct schedule ID.");
  }
}