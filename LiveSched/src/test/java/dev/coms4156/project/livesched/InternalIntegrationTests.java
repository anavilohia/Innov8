package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

/**
 * Internal integration tests for service to ensure
 * that all the components within it interact correctly.
 */
@SpringBootTest
@ContextConfiguration
public class InternalIntegrationTests {

  /**
   * This test ensures that:
   * 1. The RouteController correctly interacts with LiveSchedApplication
   *    and MyFileDatabase to update schedules.
   * 2. The database reflects the updated schedule after the API call.
   * 3. The resources are correctly assigned to tasks in the schedule.
   */
  @Test
  void integrationTestUpdateSchedule() {
    // Setup test database and controller
    MyFileDatabase testDatabase = new MyFileDatabase(1,
        "tasks.dat", "resources.dat", "schedules.dat",
        "tasksObject", "resourcesObject", "schedulesObject");

    // Use overrideDatabase to set up the test database in the application
    LiveSchedApplication.overrideDatabase(testDatabase, "testClient");

    // Add ResourceTypes
    ResourceType resourceType1 = new ResourceType("Type1", 3, 40.71, -74.0);
    ResourceType resourceType2 = new ResourceType("Type2", 5, 40.71, -74.0);
    testDatabase.addResourceType(resourceType1);
    testDatabase.addResourceType(resourceType2);

    // Add a task that needs the above ResourceTypes
    Map<ResourceType, Integer> resources = new HashMap<>();
    resources.put(resourceType1, 1);
    resources.put(resourceType2, 2);
    Task task = new Task("1", "Test Task", resources, 1,
        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.71, -74.0);
    testDatabase.addTask(task);

    // Call the updateSchedule endpoint
    RouteController routeController = new RouteController();
    ResponseEntity<?> response = routeController.updateSchedule(10.0, "testClient");

    // Assert that the response contains the updated schedule
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should have status 200 OK");
    List<Map<String, Object>> schedule = (List<Map<String, Object>>) response.getBody();
    assertNotNull(schedule, "Schedule should not be null");
    assertFalse(schedule.isEmpty(), "Schedule should not be empty");

    // Assert that the database contains the updated schedule
    Schedule masterSchedule = testDatabase.getMasterSchedule();
    assertNotNull(masterSchedule, "Database's master schedule should not be null");
    assertEquals(1, masterSchedule.getTaskSchedule().size(),
        "Master schedule should contain 1 task");

    // Verify that the resources were correctly assigned in the schedule
    Map.Entry<Task, List<Resource>> scheduleEntry =
        masterSchedule.getTaskSchedule().entrySet().iterator().next();
    assertEquals(task.getTaskId(), scheduleEntry.getKey().getTaskId(), "Task ID should match");
    assertEquals(3, scheduleEntry.getValue().size(),
        "Total three resources should be assigned");

    LiveSchedApplication.restoreDatabase("testClient");
  }

}
