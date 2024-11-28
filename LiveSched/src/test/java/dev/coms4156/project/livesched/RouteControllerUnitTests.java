package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the RouteController class.
 */
@SpringBootTest
@ContextConfiguration
public class RouteControllerUnitTests {

  private static final String TASK_NOT_FOUND = "Task Not Found";
  private RouteController routeController;
  private TestMyFileDatabase testDatabase;
  final String testClientId = "defaultClientId";

  /**
   * Test implementation of MyFileDatabase for unit testing purposes.
   */
  private class TestMyFileDatabase extends MyFileDatabase {
    private List<Task> testTasks = new ArrayList<>();
    private List<ResourceType> testResourceTypes = new ArrayList<>();
    private Schedule masterSchedule;

    public TestMyFileDatabase() {
      super(1, "testTaskPath", "testResourcePath", "testSchedulePath",
          "testTaskObject", "testResourceObject", "testScheduleObject");
    }

    @Override
    public List<Task> getAllTasks() {
      return testTasks;
    }

    @Override
    public List<ResourceType> getAllResourceTypes() {
      return testResourceTypes;
    }

    @Override
    public Schedule getMasterSchedule() {
      return masterSchedule;
    }

    @Override
    public Task getTaskById(String taskId) {
      return testTasks.stream()
          .filter(t -> t.getTaskId().equals(taskId))
          .findFirst()
          .orElse(null);
    }

    @Override
    public void addTask(Task task) {
      testTasks.add(task);
    }

    @Override
    public void addResourceType(ResourceType resourceType) {
      testResourceTypes.add(resourceType);
    }

    /**
     * Clears all test data.
     */
    public void clearAll() {
      testTasks.clear();
      testResourceTypes.clear();
      masterSchedule = null;
    }

    /**
     * Adds a task for testing purposes.
     *
     * @param task The task to add
     */
    public void addTestTask(Task task) {
      testTasks.add(task);
    }

    /**
     * Adds a resource type for testing purposes.
     *
     * @param resourceType The resource type to add
     */
    public void addTestResourceType(ResourceType resourceType) {
      testResourceTypes.add(resourceType);
    }

    /**
     * Sets masterSchedule of the database.
     *
     * @param masterSchedule the master schedule to be added to database
     */
    public void setMasterSchedule(Schedule masterSchedule) {
      this.masterSchedule = masterSchedule == null ? new Schedule() : masterSchedule;
    }
  }

  /**
   * Sets up the test environment before each test.
   */
  @BeforeEach
  void setUpForAll() {
    routeController = new RouteController();
    testDatabase = new TestMyFileDatabase();
    Map<String, MyFileDatabase> testClientDatabases = new HashMap<>();
    testClientDatabases.put(testClientId, testDatabase);
    LiveSchedApplication.clientDatabases = testClientDatabases;
  }

  @Nested
  class ValidTestCases {

    @BeforeEach
    void setUp() {
      ResourceType resourceType1 = new ResourceType("Type1", 5, 40.7128, -74.0060);
      ResourceType resourceType2 = new ResourceType("Type2", 10, 40.7128, -74.0060);
      testDatabase.addTestResourceType(resourceType1);
      testDatabase.addTestResourceType(resourceType2);

      Map<ResourceType, Integer> resources = new HashMap<>();
      resources.put(resourceType1, 2);
      resources.put(resourceType2, 3);

      Task task1 = new Task("1", "task1", resources, 1,
              LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.7128, -74.0060);
      Task task2 = new Task("2", "task2", new HashMap<>(), 2,
              LocalDateTime.now(), LocalDateTime.now().plusHours(2), 40.7128, -74.0060);
      testDatabase.addTestTask(task1);
      testDatabase.addTestTask(task2);

      testDatabase.setMasterSchedule(null);
      testDatabase.getMasterSchedule().updateSchedule(testDatabase.getAllTasks(), 100);
    }

    @AfterEach
    void tearDown() {
      testDatabase.clearAll(); // Clear the state after each test to avoid cross-test contamination.
    }

    /**
     * Tests the index method of RouteController.
     */
    @Test
    void indexTest() {
      String result = routeController.index();
      assertTrue(result.contains("Welcome"), "Index should return a welcome message");
    }

    /**
     * Tests the retrieveTasks method of RouteController.
     */
    @Test
    void retrieveTasksTest() {
      ResponseEntity<?> response = routeController.retrieveTasks(testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      List<Task> responseBody = (List<Task>) response.getBody();
      assertEquals(2, responseBody.size(), "Response should contain 2 tasks");
      assertEquals("1", responseBody.get(0).getTaskId(), "First task ID should be '1'");
      assertEquals("2", responseBody.get(1).getTaskId(), "Second task ID should be '2'");
    }

    /**
     * Tests the retrieveTask method of RouteController.
     */
    @Test
    void retrieveTaskTest() {
      ResponseEntity<?> response = routeController.retrieveTask("1", testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      Task responseBody = (Task) response.getBody();
      assertEquals("1", responseBody.getTaskId(), "Task ID should match '1'");
    }

    /**
     * Tests the retrieveResourceTypes method of RouteController.
     */
    @Test
    void retrieveResourceTypesTest() {
      ResponseEntity<?> response = routeController.retrieveResourceTypes(testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      List<ResourceType> responseBody = (List<ResourceType>) response.getBody();
      assertEquals(2, responseBody.size(), "Response should contain 2 resource types");
      assertEquals("Type1", responseBody.get(0).getTypeName(),
              "First resource type should be 'Type1'");
      assertEquals("Type2", responseBody.get(1).getTypeName(),
              "Second resource type should be 'Type2'");
    }

    /**
     * Tests the retrieveSchedule method of RouteController.
     */
    @Test
    void retrieveScheduleTest() {
      ResponseEntity<?> response = routeController.retrieveSchedule(testClientId);

      List<Map<String, Object>> scheduleList = new ArrayList<>();
      testDatabase.getMasterSchedule().getTaskSchedule().forEach((task, resources) -> {
        Map<String, Object> scheduleEntry = new HashMap<>();
        scheduleEntry.put("task", task);
        scheduleEntry.put("assignedResources", resources);
        scheduleList.add(scheduleEntry);
      });

      assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK (200)");

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> responseBody = (List<Map<String, Object>>) response.getBody();
      assertEquals(scheduleList.size(), responseBody.size(), "Schedule should contain 2 tasks");

      int index = 0;
      for (Map<String, Object> scheduleEntry : responseBody) {
        String actualTask = scheduleEntry.get("task").toString();
        int actualResourceSize = ((List<?>) scheduleEntry.get("assignedResources")).size();

        String expectedTask = scheduleList.get(index).get("task").toString();
        int expectedResourceSize = ((List<?>) scheduleList.get(index)
                .get("assignedResources")).size();

        assertEquals(expectedTask, actualTask, "Task IDs should match at index " + index);
        assertEquals(expectedResourceSize, actualResourceSize,
                "Resource size should match at index " + index);
        index++;
      }
    }

    /**
     * Tests the updateSchedule method of RouteController.
     */
    @Test
    void updateScheduleTest() {
      ResponseEntity<?> response = routeController.updateSchedule(80, testClientId);

      List<Map<String, Object>> scheduleList = new ArrayList<>();
      testDatabase.getMasterSchedule().getTaskSchedule().forEach((task, resources) -> {
        Map<String, Object> scheduleEntry = new HashMap<>();
        scheduleEntry.put("task", task);
        scheduleEntry.put("assignedResources", resources);
        scheduleList.add(scheduleEntry);
      });

      assertEquals(HttpStatus.OK, response.getStatusCode(),
              "Response status should be OK (200)");

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> responseBody = (List<Map<String, Object>>) response.getBody();
      assertEquals(scheduleList.size(), responseBody.size(),
              "Schedule should contain the same number of tasks");

      int index = 0;
      for (Map<String, Object> scheduleEntry : responseBody) {
        @SuppressWarnings("unchecked")
        Map<String, Object> actualTask = (Map<String, Object>) scheduleEntry.get("task");
        String actualTaskId = (String) actualTask.get("taskId");
        int actualResourceSize = ((List<?>) scheduleEntry.get("assignedResources")).size();

        Task expectedTask = (Task) scheduleList.get(index).get("task");
        String expectedTaskId = expectedTask.getTaskId();
        int expectedResourceSize = ((List<?>) scheduleList.get(index)
                .get("assignedResources")).size();

        assertEquals(expectedTaskId, actualTaskId, "Task IDs should match at index " + index);
        assertEquals(expectedResourceSize, actualResourceSize,
                "Resource size should match at index " + index);
        index++;
      }
    }

    /**
     * Tests the unscheduleTask method of RouteController.
     */
    @Test
    void unscheduleTaskTest() {
      ResponseEntity<?> response = routeController.unscheduleTask("1", testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK (200)");
      assertEquals("Task unscheduled succesfully", response.getBody(),
              "Message should say Task unscheduled succesfully");
    }

    /**
     * Tests the deleteTask method of RouteController.
     */
    @Test
    void deleteTaskTest() {
      int initialSize = testDatabase.getAllTasks().size();
      ResponseEntity<?> response = routeController.deleteTask("1", testClientId);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("1 successfully deleted", response.getBody(),
              "Deleted task ID should match '1'");
      // assertEquals(initialSize - 1, testDatabase.getAllTasks().size());
    }

    /**
     * Tests the addTask method of RouteController.
     */
    @Test
    void addTaskTest() {
      String startTime = LocalDateTime.now().plusHours(1)
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      String endTime = LocalDateTime.now().plusHours(2)
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      int priority = 3;
      double latitude = 40.7128;
      double longitude = -74.0060;

      int initialSize = testDatabase.getAllTasks().size();

      ResponseEntity<?> response = routeController.addTask("TestTask", priority, startTime,
              endTime, latitude, longitude, testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      Task responseBody = (Task) response.getBody();
      assertEquals("3", responseBody.getTaskId(), "New task ID should match '3'");
      assertEquals(initialSize + 1, testDatabase.getAllTasks().size());
    }

    /**
     * Tests the addResourceType method of RouteController.
     */
    @Test
    void addResourceTypeTest() {
      String typeName = "NewType";
      int totalUnits = 5;
      double latitude = 40.7128;
      double longitude = -74.0060;

      int initialSize = testDatabase.getAllResourceTypes().size();

      ResponseEntity<?> response = routeController.addResourceType(typeName, totalUnits,
              latitude, longitude, testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Attribute was updated successfully.", response.getBody());
      assertEquals(initialSize + 1, testDatabase.getAllResourceTypes().size());
    }

    /**
     * Tests the modifyResourceType method of RouteController.
     */
    @Test
    void modifyResourceTypeTest() {
      String taskId = "1";
      String typeName = "Type1";
      int quantity = 3;

      ResponseEntity<?> response = routeController.modifyResourceType(taskId,
              typeName, quantity, testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Attribute was updated successfully.", response.getBody());

      Task updatedTask = testDatabase.getTaskById("1");
      ResourceType resourceType = testDatabase.getAllResourceTypes().stream()
              .filter(rt -> rt.getTypeName().equals(typeName))
              .findFirst()
              .orElse(null);
      assertNotNull(resourceType, "Resource type should exist");
      assertEquals(quantity, updatedTask.getResources().get(resourceType));
    }

    /**
     * Tests the deleteResourceType method of RouteController.
     */
    @Test
    void deleteResourceTypeTest() {
      String typeName = "NewType";
      int totalUnits = 5;
      double latitude = 40.7128;
      double longitude = -74.0060;
      routeController.addResourceType(typeName, totalUnits, latitude, longitude, testClientId);

      int initialSize = testDatabase.getAllResourceTypes().size();
      ResponseEntity<?> response = routeController.deleteResourceType(typeName, testClientId);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(typeName + " successfully deleted", response.getBody());
      // assertEquals(initialSize - 1, testDatabase.getAllResourceTypes().size());
    }
  }

  @Nested
  class TasksNotFoundError {

    @BeforeEach
    void setUp() {
      ResourceType resourceType1 = new ResourceType("Type1", 5, 40.7128, -74.0060);
      ResourceType resourceType2 = new ResourceType("Type2", 10, 40.7128, -74.0060);
      testDatabase.addTestResourceType(resourceType1);
      testDatabase.addTestResourceType(resourceType2);

      testDatabase.setMasterSchedule(null);
      testDatabase.getMasterSchedule().updateSchedule(testDatabase.getAllTasks(), 100);
    }

    @AfterEach
    void tearDown() {
      testDatabase.clearAll(); // Clear the state after each test to avoid cross-test contamination.
    }

    /**
     * Tests the retrieveTasks method of RouteController.
     */
    @Test
    void retrieveTasksTest() {
      ResponseEntity<?> response = routeController.retrieveTasks(testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("Tasks Not Found", response.getBody(),
              "Error message should be Tasks Not Found");
    }

    /**
     * Tests the retrieveTask method of RouteController.
     */
    @Test
    void retrieveTaskTest() {
      ResponseEntity<?> response = routeController.retrieveTask("1", testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals(TASK_NOT_FOUND, response.getBody(), "Error message should be " + TASK_NOT_FOUND);
    }

    /**
     * Tests the updateSchedule method of RouteController.
     */
    @Test
    void updateScheduleTest() {
      ResponseEntity<?> response = routeController.updateSchedule(80, testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("Tasks Not Found", response.getBody(),
              "Error message should be Tasks Not Found");
    }

    /**
     * Tests the unscheduleTask method of RouteController.
     */
    @Test
    void unscheduleTaskTest() {
      ResponseEntity<?> response = routeController.unscheduleTask("1", testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals(TASK_NOT_FOUND, response.getBody(), "Error message should be Task Not Found");
    }

    /**
     * Tests the deleteTask method of RouteController.
     */
    @Test
    void deleteTaskTest() {
      int initialSize = testDatabase.getAllTasks().size();
      ResponseEntity<?> response = routeController.deleteTask("3", testClientId);
      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals(TASK_NOT_FOUND, response.getBody(), "Error message should be Task Not Found");
    }

  }

  @Nested
  class ResourceTypesNotFoundError {

    @BeforeEach
    void setUp() {
      ResourceType resourceType1 = new ResourceType("Type1", 5, 40.7128, -74.0060);
      ResourceType resourceType2 = new ResourceType("Type2", 10, 40.7128, -74.0060);

      Map<ResourceType, Integer> resources = new HashMap<>();
      resources.put(resourceType1, 2);
      resources.put(resourceType2, 3);

      Task task1 = new Task("1", "task1", resources, 1,
              LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.7128, -74.0060);
      Task task2 = new Task("2", "task2", new HashMap<>(), 2,
              LocalDateTime.now(), LocalDateTime.now().plusHours(2), 40.7128, -74.0060);
      testDatabase.addTestTask(task1);
      testDatabase.addTestTask(task2);
    }

    @AfterEach
    void tearDown() {
      testDatabase.clearAll(); // Clear the state after each test to avoid cross-test contamination.
    }

    /**
     * Tests the retrieveResourceTypes method of RouteController.
     */
    @Test
    void retrieveTasksTest() {
      ResponseEntity<?> response = routeController.retrieveResourceTypes(testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("ResourceTypes Not Found", response.getBody(),
              "Error message should be ResourceTypes Not Found");
    }

    /**
     * Tests the modifyResourceType method of RouteController.
     */
    @Test
    void modifyResourceTypeTest() {
      String taskId = "1";
      String typeName = "Type1";
      int quantity = 3;

      ResponseEntity<?> response = routeController.modifyResourceType(taskId,
              typeName, quantity, testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("ResourceType Not Found", response.getBody(),
              "Error message should be ResourceTypes Not Found");
    }

    /**
     * Tests the deleteResourceType method of RouteController.
     */
    @Test
    void deleteResourceTypeTest() {
      String typeName = "Type1";

      ResponseEntity<?> response = routeController.deleteResourceType(typeName, testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("ResourceType Not Found", response.getBody(),
              "Error message should be ResourceTypes Not Found");
    }
  }

  @Nested
  class SchedulesNotFoundError {

    @BeforeEach
    void setUp() {
      ResourceType resourceType1 = new ResourceType("Type1", 5, 40.7128, -74.0060);
      ResourceType resourceType2 = new ResourceType("Type2", 10, 40.7128, -74.0060);
      testDatabase.addTestResourceType(resourceType1);
      testDatabase.addTestResourceType(resourceType2);

      Map<ResourceType, Integer> resources = new HashMap<>();
      resources.put(resourceType1, 2);
      resources.put(resourceType2, 3);

      Task task1 = new Task("1", "task1", resources, 1,
              LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.7128, -74.0060);
      Task task2 = new Task("2", "task2", new HashMap<>(), 2,
              LocalDateTime.now(), LocalDateTime.now().plusHours(2), 40.7128, -74.0060);
      testDatabase.addTestTask(task1);
      testDatabase.addTestTask(task2);
    }

    @AfterEach
    void tearDown() {
      testDatabase.clearAll(); // Clear the state after each test to avoid cross-test contamination.
    }

    /**
     * Tests the retrieveSchedule method of RouteController.
     */
    @Test
    void retrieveScheduleTest() {
      ResponseEntity<?> response = routeController.retrieveSchedule(testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("Schedules Not Found", response.getBody(),
              "Error message should be Schedules Not Found");
    }

    /**
     * Tests the updateSchedule method of RouteController.
     */
    @Test
    void updateScheduleTest() {
      ResponseEntity<?> response = routeController.updateSchedule(80, testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("Master Schedule Not Found", response.getBody(),
              "Error message should be Master Schedule Not Found");
    }

    /**
     * Tests the unscheduleTask method of RouteController.
     */
    @Test
    void unscheduleTaskTest() {
      ResponseEntity<?> response = routeController.unscheduleTask("1", testClientId);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertEquals("Master Schedule Not Found", response.getBody(),
              "Error message should be Master Schedule Not Found");
    }
  }

  @Nested
  class BadRequestError {

    @BeforeEach
    void setUp() {
      ResourceType resourceType1 = new ResourceType("Type1", 5, 40.7128, -74.0060);
      ResourceType resourceType2 = new ResourceType("Type2", 10, 40.7128, -74.0060);
      testDatabase.addTestResourceType(resourceType1);
      testDatabase.addTestResourceType(resourceType2);

      Map<ResourceType, Integer> resources = new HashMap<>();
      resources.put(resourceType1, 2);
      resources.put(resourceType2, 3);

      Task task1 = new Task("1", "task1", resources, 1,
              LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.7128, -74.0060);
      Task task2 = new Task("2", "task2", new HashMap<>(), 2,
              LocalDateTime.now(), LocalDateTime.now().plusHours(2), 40.7128, -74.0060);
      testDatabase.addTestTask(task1);
      testDatabase.addTestTask(task2);

      testDatabase.setMasterSchedule(null);
    }

    @AfterEach
    void tearDown() {
      testDatabase.clearAll(); // Clear the state after each test to avoid cross-test contamination.
    }

    /**
     * Tests the unscheduleTask method of RouteController.
     */
    @Test
    void unscheduleTaskTest() {
      ResponseEntity<?> response = routeController.unscheduleTask("1", testClientId);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Task Not Scheduled Yet", response.getBody(),
              "Error message should be Task Not Scheduled Yet");
    }

    /**
     * Tests the deleteResourceType method of RouteController.
     */
    @Test
    void deleteResourceTypeTest() {
      String typeName = "Type1";

      ResponseEntity<?> response = routeController.deleteResourceType(typeName, testClientId);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Cannot delete a resourceType currently in use", response.getBody());
    }
  }
}