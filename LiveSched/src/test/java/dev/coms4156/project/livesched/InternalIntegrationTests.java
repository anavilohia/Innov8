package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InternalIntegrationTests {

  private RouteController routeController;

  @BeforeEach
  void setUp() {
    routeController = new RouteController();
  }

  /**
   * This test ensures that a task can be added and retrieved successfully.
   */
  @Test
  @Order(1)
  void integrationTestAddAndRetrieveTask() {
    // Add tasks for TestClient1 and TestClient2
    String startTime = LocalDateTime.now().plusHours(2)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String endTime = LocalDateTime.now().plusHours(4)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    routeController.addTask("Task1", 1, startTime, endTime, 40.71, -74.0, "TestClient1");
    routeController.addTask("Task2", 3, startTime, endTime, 20.71, -80.3, "TestClient2");

    // Retrieve tasks for TestClient1
    ResponseEntity<?> response = routeController.retrieveTasks("TestClient1");
    List<Task> responseBody = (List<Task>) response.getBody();

    // Assert that response body size is accurate to clientId
    assertEquals(1, responseBody.size(), "TestClient1 database should contain 1 task");

    // Assert that details of the retrieved Task are accurate
    Task task1 = responseBody.get(0);
    assertEquals("Task1", task1.getTaskName(), "Task name should match");
    assertTrue(task1.getResources().isEmpty(), "Task resources should be empty");
  }

  /**
   * This test ensures that resource types can be added and retrieved successfully.
   */
  @Test
  @Order(2)
  void integrationTestAddAndRetrieveResourceTypes() {
    // Add resource types
    routeController.addResourceType("ResourceType1", 5, 40.71, -74.0, "TestClient1");
    routeController.addResourceType("ResourceType2", 10, 43.65, -84.0, "TestClient1");

    // Retrieve resource types using the RouteController
    ResponseEntity<?> response = routeController.retrieveResourceTypes("TestClient1");
    List<ResourceType> responseBody = (List<ResourceType>) response.getBody();

    // Assert that the response contains the added resource types
    assertNotNull(responseBody, "Response body should not be null");
    assertEquals(2, responseBody.size(), "There should be 2 resource types");

    // Assert the details of the retrieved resourceType are accurate
    ResourceType resourceType2 = responseBody.get(1);
    assertEquals("ResourceType2", resourceType2.getTypeName(),
        "Second resource type name should match");
    assertEquals(10, resourceType2.getTotalUnits(),
        "Second resource type units should match");
  }

  /**
   * This test ensures that resource types for a specific task can be modified successfully.
   */
  @Test
  @Order(3)
  void integrationTestModifyResourceType() {
    // Find taskId
    ResponseEntity<?> response = routeController.retrieveTasks("TestClient1");
    List<Task> responseBody = (List<Task>) response.getBody();
    String task1Id = responseBody.get(0).getTaskId();

    // Modify the resource type for Task1
    routeController.modifyResourceType(task1Id, "ResourceType1", 3, "TestClient1");

    // Retrieve modified task
    response = routeController.retrieveTask(task1Id, "TestClient1");
    Task modifiedTask = (Task) response.getBody();

    // Assert that the resourceType was modified correctly for task
    Map<ResourceType, Integer> resources = modifiedTask.getResources();
    assertEquals(1, resources.size(), "Task should have exactly 1 resource type assigned");
    ResourceType assignedResource = resources.keySet().iterator().next();
    assertEquals("ResourceType1", assignedResource.getTypeName(),
        "Resource type name should match");
    assertEquals(3, resources.get(assignedResource), "Resource quantity should be updated to 3");
  }

  /**
   * This test ensures that:
   * 1. The RouteController correctly interacts with LiveSchedApplication
   *    and MyFileDatabase to update schedules.
   * 2. The database reflects the updated schedule after the API call.
   * 3. The resources are correctly assigned to tasks in the schedule.
   */
  @Test
  @Order(4)
  void integrationTestUpdateandRetrieveSchedule() {
    // Call the updateSchedule endpoint
    ResponseEntity<?> response = routeController.updateSchedule(10.0, "TestClient1");

    // Assert that the response contains the updated schedule
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should have status 200 OK");
    List<Map<String, Object>> schedule = (List<Map<String, Object>>) response.getBody();
    assertNotNull(schedule, "Schedule should not be null");
    assertFalse(schedule.isEmpty(), "Schedule should not be empty");

    // Assert that the database contains the updated schedule
    MyFileDatabase testDatabase = LiveSchedApplication.getClientFileDatabase("TestClient1");
    Schedule masterSchedule = testDatabase.getMasterSchedule();
    assertNotNull(masterSchedule, "Database's master schedule should not be null");
    assertEquals(1, masterSchedule.getTaskSchedule().size(),
        "Master schedule should contain 1 task");

    // Retrieve the updated schedule again using retrieveSchedule
    response = routeController.retrieveSchedule("TestClient1");
    schedule = (List<Map<String, Object>>) response.getBody();

    // Assert that the schedule contains Task1 with the correct assigned resourceType
    Map<String, Object> scheduleEntry = schedule.get(0);

    Task scheduledTask = (Task) scheduleEntry.get("task");
    assertEquals("Task1", scheduledTask.getTaskName(), "Task name should match");

    List<Resource> scheduledResources = (List<Resource>) scheduleEntry.get("assignedResources");
    Resource resource1 = scheduledResources.get(0);
    assertEquals(3, scheduledResources.size(), "Task should have 3 assigned resources");
    assertTrue(resource1.getResourceId().contains("ResourceType1"),
        "First resource should be ResourceType1");

    LiveSchedApplication.restoreDatabase("TestClient1");
  }
}