package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  }

  @Test
  void testSetAndGetAllResourceTypes() {
    List<ResourceType> resources = new ArrayList<>();
    resources.add(createDummyResourceType());
    database.setAllResourceTypes(resources);
    assertEquals(1, database.getAllResourceTypes().size());
  }

  @Test
  void testSetAndGetMasterSchedule() {
    List<Task> tasks = new ArrayList<>();
    tasks.add(createDummyTask());
    Schedule schedule = new Schedule();
    database.setMasterSchedule(schedule);
    assertNotNull(database.getMasterSchedule());
  }

  @Test
  void testInvalidContentType() {
    assertThrows(IllegalArgumentException.class, () -> database.saveContentsToFile(4));
    assertThrows(IllegalArgumentException.class, () -> database.deSerializeObjectFromFile(4));
  }

  @Test
  void testNullInput() {
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
    Task dummyTask1 = new Task(
            "Task1", "First Task", new HashMap<>(), 1,
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 40.81, -73.96);
    Task dummyTask2 = new Task(
            "Task2", "Second Task", new HashMap<>(), 2,
            LocalDateTime.now(), LocalDateTime.now().plusHours(2), 40.81, -73.96);
    List<Task> tasks = new ArrayList<>();
    tasks.add(dummyTask1);
    tasks.add(dummyTask2);

    database.setAllTasks(tasks);

    Task resultTask = database.getTaskById("Task1");

    assertNotNull(resultTask, "Task with ID 'Task1' should not be null.");
    assertEquals("First Task", resultTask.getTaskName(), "Task name should match.");
    assertEquals(1, resultTask.getPriority(), "Task priority should match.");

    Task missingTask = database.getTaskById("NonExistentTask");
    assertNull(missingTask, "Task with a non-existent ID should return null.");
  }

  @Test
  void testGetTaskByIdEmptyList() {
    database.setAllTasks(new ArrayList<>());
    Task result = database.getTaskById("Task1");
    assertNull(result, "getTaskById should return null when the task list is empty.");
  }

  @Test
  void testGetTaskFilePath() {
    String expectedFilePath = new File(tempDir, TASK_FILE).getAbsolutePath();
    String actualFilePath = database.getTaskFilePath();
    assertEquals(expectedFilePath, actualFilePath,
            "The task file path should match the expected value.");
  }

  @Test
  void testGetTaskContentType() {
    int expectedContentType = 1;
    int actualContentType = database.getTaskContentType();
    assertEquals(expectedContentType, actualContentType,
            "The task content type should match the expected value.");
  }

  @Test
  void testAddTask() {
    Task task = createDummyTask();
    database.addTask(task);
    assertTrue(database.getAllTasks().contains(task),
            "The task should be added to the task list.");
  }

  @Test
  void testDeleteTask() {
    Task task = createDummyTask();
    database.addTask(task);
    database.deleteTask(task);
    assertFalse(database.getAllTasks().contains(task),
            "The task should be removed from the task list.");
  }

  @Test
  void testAddResourceType() {
    ResourceType resourceType = createDummyResourceType();
    database.addResourceType(resourceType);
    assertTrue(database.getAllResourceTypes().contains(resourceType),
            "The resource type should be added to the resource types list.");
  }

  @Test
  void testDeleteResourceType() {
    ResourceType resourceType = createDummyResourceType();
    database.addResourceType(resourceType);
    database.deleteResourceType(resourceType);
    assertFalse(database.getAllResourceTypes().contains(resourceType),
            "The resource type should be removed from the resource types list.");
  }

  @Test
  void testDeleteTaskNotPresent() {
    Task task = createDummyTask();
    database.deleteTask(task);
    assertFalse(database.getAllTasks().contains(task),
            "Deleting a task that isn't in the list should not cause any errors.");
  }

  @Test
  void testDeleteResourceTypeNotPresent() {
    ResourceType resourceType = createDummyResourceType();
    database.deleteResourceType(resourceType);
    assertFalse(database.getAllResourceTypes().contains(resourceType),
            "Deleting a resource type that isn't in the list should not cause any errors.");
  }

  @Test
  void testAddNullTask() {
    assertDoesNotThrow(() -> database.addTask(null), "Adding a null task should not throw an exception.");
  }

  @Test
  void testAddNullResourceType() {
    assertDoesNotThrow(() -> database.addResourceType(null),
            "Adding a null resource type should not throw an exception.");
  }

  private Task createDummyTask() {
    Map<ResourceType, Integer> resources = new HashMap<>();
    resources.put(createDummyResourceType(), 1);
    return new Task(
        "DummyTask", "DummyTask", resources, 1,
        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 0, 0);
  }

  private ResourceType createDummyResourceType() {
    return new ResourceType("DummyResource", 5, 0, 0);
  }

}