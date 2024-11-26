package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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

  private RouteController routeController;
  private TestMyFileDatabase testDatabase;
  final String testClientId = "defaultClientId";

  /**
   * Test implementation of MyFileDatabase for unit testing purposes.
   */
  private class TestMyFileDatabase extends MyFileDatabase {
    private List<Task> testTasks = new ArrayList<>();
    private List<ResourceType> testResourceTypes = new ArrayList<>();

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
  }

  /**
   * Sets up the test environment before each test.
   */
  @BeforeEach
  void setUp() {
    routeController = new RouteController();
    testDatabase = new TestMyFileDatabase();
    Map<String, MyFileDatabase> testClientDatabases = new HashMap<>();
    testClientDatabases.put(testClientId, testDatabase);
    LiveSchedApplication.clientDatabases = testClientDatabases;

    testDatabase.clearAll();

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
}