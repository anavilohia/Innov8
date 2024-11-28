package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MyFileDatabaseUnitTests {

  private MyFileDatabase database;
  private static final String TASK_FILE = "tasks.dat";
  private static final String RESOURCE_FILE = "resources.dat";
  private static final String SCHEDULE_FILE = "schedules.dat";

  @TempDir
  File tempDir;

  @BeforeEach
  void setUp() {
    String taskPath = new File(tempDir, TASK_FILE).getAbsolutePath();
    String resourcePath = new File(tempDir, RESOURCE_FILE).getAbsolutePath();
    String schedulePath = new File(tempDir, SCHEDULE_FILE).getAbsolutePath();
    database = new MyFileDatabase(1,
            taskPath, resourcePath, schedulePath,
            taskPath, resourcePath, schedulePath);
  }

  @Test
  void testConstructor() {
    assertNotNull(database);
    assertTrue(database.getAllTasks().isEmpty());
    assertTrue(database.getAllResourceTypes().isEmpty());
    assertTrue(database.getMasterSchedule().getTaskSchedule().isEmpty());
  }

  @Test
  void testSetAndGetAllTasks() {
    List<Task> tasks = new ArrayList<>();
    tasks.add(createDummyTask());
    database.setAllTasks(tasks);
    assertEquals(1, database.getAllTasks().size());

    // Adding edge case of empty task list
    database.setAllTasks(new ArrayList<>());
    assertTrue(database.getAllTasks().isEmpty(), "Tasks should be empty after setting an empty list.");
  }

  @Test
  void testSetAndGetAllResourceTypes() {
    List<ResourceType> resources = new ArrayList<>();
    resources.add(createDummyResourceType());
    database.setAllResourceTypes(resources);
    assertEquals(1, database.getAllResourceTypes().size());

    // Adding edge case of empty resource list
    database.setAllResourceTypes(new ArrayList<>());
    assertTrue(database.getAllResourceTypes().isEmpty(), "Resource types should be empty after setting an empty list.");
  }

  @Test
  void testSetAndGetMasterSchedule() {
    Schedule schedule = new Schedule();
    database.setMasterSchedule(schedule);
    assertNotNull(database.getMasterSchedule());
    assertTrue(database.getMasterSchedule().getTaskSchedule().isEmpty());

    // Test setting a null schedule
    database.setMasterSchedule(null);
    assertNotNull(database.getMasterSchedule(), "Master schedule should not be null even if set to null.");
    assertTrue(database.getMasterSchedule().getTaskSchedule().isEmpty());
  }

  @Test
  void testInvalidContentType() {
    assertThrows(IllegalArgumentException.class, () -> database.saveContentsToFile(4));
    assertThrows(IllegalArgumentException.class, () -> database.deSerializeObjectFromFile(4));
  }

  @Test
  void testNullInput() {
    // Test null input for setter methods
    database.setAllTasks(null);
    assertTrue(database.getAllTasks().isEmpty());

    database.setAllResourceTypes(null);
    assertTrue(database.getAllResourceTypes().isEmpty());

    database.setMasterSchedule(null);
    assertNotNull(database.getMasterSchedule());
    assertTrue(database.getMasterSchedule().getTaskSchedule().isEmpty());
  }

  @Test
  void testDeSerializeObjectFromFile() throws IOException, ClassNotFoundException {
    List<Task> tasks = new ArrayList<>();
    tasks.add(createDummyTask());

    try (ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(database.getTaskFilePath()))) {
      out.writeObject(tasks);
    }

    Object deserializedTasks = database.deSerializeObjectFromFile(database.getTaskContentType());

    assertNotNull(deserializedTasks);
    assertTrue(deserializedTasks instanceof List<?>);
    List<?> deserializedList = (List<?>) deserializedTasks;
    assertEquals(1, deserializedList.size());
    assertTrue(deserializedList.get(0) instanceof Task);
    assertEquals("DummyTask", ((Task) deserializedList.get(0)).getTaskName());
  }

  @Test
  void testInvalidDeserialization() throws IOException {
    try (ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(database.getTaskFilePath()))) {
      out.writeObject("Invalid Data");
    }

    assertThrows(IllegalArgumentException.class,
            () -> database.deSerializeObjectFromFile(database.getTaskContentType()));
  }

  @Test
  void testDeserializationWithNullFile() {
    Object result = database.deSerializeObjectFromFile(database.getTaskContentType());
    assertNull(result);
  }

  @Test
  void testGetTaskById() {
    Task dummyTask1 = createDummyTask("Task1", "First Task", 1);
    Task dummyTask2 = createDummyTask("Task2", "Second Task", 2);
    List<Task> tasks = new ArrayList<>();
    tasks.add(dummyTask1);
    tasks.add(dummyTask2);

    database.setAllTasks(tasks);

    Task resultTask = database.getTaskById("Task1");
    assertNotNull(resultTask);
    assertEquals("First Task", resultTask.getTaskName());

    Task missingTask = database.getTaskById("NonExistentTask");
    assertNull(missingTask);
  }

  @Test
  void testGetTaskByIdEmptyList() {
    database.setAllTasks(new ArrayList<>());
    Task result = database.getTaskById("Task1");
    assertNull(result);
  }

  @Test
  void testGetTaskFilePath() {
    String expectedFilePath = new File(tempDir, TASK_FILE).getAbsolutePath();
    String actualFilePath = database.getTaskFilePath();
    assertEquals(expectedFilePath, actualFilePath);
  }

  @Test
  void testGetTaskContentType() {
    int expectedContentType = 1;
    int actualContentType = database.getTaskContentType();
    assertEquals(expectedContentType, actualContentType);
  }

  @Test
  void testAddTask() {
    Task task = createDummyTask();
    database.addTask(task);
    assertTrue(database.getAllTasks().contains(task));
  }

  @Test
  void testDeleteTask() {
    Task task = createDummyTask();
    database.addTask(task);
    database.deleteTask(task);
    assertFalse(database.getAllTasks().contains(task));
  }

  @Test
  void testAddResourceType() {
    ResourceType resourceType = createDummyResourceType();
    database.addResourceType(resourceType);
    assertTrue(database.getAllResourceTypes().contains(resourceType));
  }

  @Test
  void testDeleteResourceType() {
    ResourceType resourceType = createDummyResourceType();
    database.addResourceType(resourceType);
    database.deleteResourceType(resourceType);
    assertFalse(database.getAllResourceTypes().contains(resourceType));
  }

  @Test
  void testDeleteTaskNotPresent() {
    Task task = createDummyTask();
    database.deleteTask(task);
    assertFalse(database.getAllTasks().contains(task));
  }

  @Test
  void testDeleteResourceTypeNotPresent() {
    ResourceType resourceType = createDummyResourceType();
    database.deleteResourceType(resourceType);
    assertFalse(database.getAllResourceTypes().contains(resourceType));
  }

  @Test
  void testAddNullTask() {
    assertDoesNotThrow(() -> database.addTask(null));
  }

  @Test
  void testAddNullResourceType() {
    assertDoesNotThrow(() -> database.addResourceType(null));
  }

  private Task createDummyTask() {
    return createDummyTask("DummyTask", "DummyTask", 1);
  }

  private Task createDummyTask(String id, String name, int priority) {
    Map<ResourceType, Integer> resources = new HashMap<>();
    resources.put(createDummyResourceType(), 1);
    return new Task(id, name, resources, priority,
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 0, 0);
  }

  private ResourceType createDummyResourceType() {
    return new ResourceType("DummyResource", 5, 0, 0);
  }
}
