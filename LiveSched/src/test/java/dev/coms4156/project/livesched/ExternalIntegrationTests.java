package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

/**
 * External integration tests for service to validate that
 * saving to and loading from external dependencies like
 * files or Google Cloud Storage work as expected.
 */
@SpringBootTest
@ContextConfiguration
public class ExternalIntegrationTests {

  String tempTaskFile;
  String tempResourceFile;
  String tempScheduleFile;
  String taskObjectName;
  String resourceObjectName;
  String scheduleObjectName;

  @BeforeEach
  void setup() throws IOException {
    tempTaskFile = "/tmp/testClient_tasks.txt";
    tempResourceFile = "/tmp/testClient_resourceTypes.txt";
    tempScheduleFile = "/tmp/testClient_schedules.txt";

    taskObjectName = "gcs_testClient_tasks.txt";
    resourceObjectName = "gcs_testClient_resourceTypes.txt";
    scheduleObjectName = "gcs_testClient_schedules.txt";

    // Set up Google Cloud credentials
    String keyPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    if (keyPath == "$HOME/gcp-key.json") {
      keyPath = System.getProperty("user.home") + "/gcp-key.json";
    }
    if (keyPath == null || keyPath.isEmpty()) {
      keyPath = "innov8-livesched-503ee847946e.json";
    }
    File keyFile = new File(keyPath);
    if (!keyFile.exists()) {
      throw new FileNotFoundException("Google Cloud credentials file not found at: " + keyPath);
    }
    System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", keyPath);
    System.out.println("Using Google Cloud credentials from: " + keyPath);
  }

  @AfterEach
  void cleanup() {
    // Delete files in tmp directory
    if (tempTaskFile != null) {
      File taskFile = new File(tempTaskFile);
      if (taskFile.exists()) {
        taskFile.delete();
      }
    }
    if (tempResourceFile != null) {
      File resourceFile = new File(tempResourceFile);
      if (resourceFile.exists()) {
        resourceFile.delete();
      }
    }
    if (tempScheduleFile != null) {
      File scheduleFile = new File(tempScheduleFile);
      if (scheduleFile.exists()) {
        scheduleFile.delete();
      }
    }
  }

  /**
   * Tests file-based database functionality, including saving,
   * clearing, and reloading data from local files.
   *
   * @throws IOException if an error occurs during file operations
   */
  @Test
  void externalIntegrationTestFileDatabase() throws IOException {
    // Initialize the database with temporary file paths
    MyFileDatabase testDatabase = new MyFileDatabase(1,
        tempTaskFile, tempResourceFile, tempScheduleFile,
        taskObjectName, resourceObjectName, scheduleObjectName);

    LiveSchedApplication.clientDatabases.put("testClient", testDatabase);

    RouteController routeController = new RouteController();

    // Add a task
    String startTime = LocalDateTime.now().plusHours(1)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String endTime = LocalDateTime.now().plusHours(2)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    ResponseEntity<?> taskResponse = routeController.addTask(
        "Test Task", 1, startTime, endTime, 40.71, -74.0, "testClient");
    assertEquals(HttpStatus.OK, taskResponse.getStatusCode(),
        "Task addition should return 200 OK");

    // Add a resource type
    ResponseEntity<?> resourceResponse = routeController.addResourceType(
        "Type1", 3, 40.71, -74.0, "testClient");
    assertEquals(HttpStatus.OK, resourceResponse.getStatusCode(),
        "Resource addition should return 200 OK");

    // Modify required resources for task
    ResponseEntity<?> modifyResourceResponse = routeController.modifyResourceType(
        "1", "Type1", 1, "testClient");
    assertEquals(HttpStatus.OK, modifyResourceResponse.getStatusCode(),
        "Resource modification for task should return 200 OK");

    // Update schedule
    ResponseEntity<?> scheduleResponse = routeController.updateSchedule(10, "testClient");
    assertEquals(HttpStatus.OK, scheduleResponse.getStatusCode(),
        "Update schedule should return 200 OK");

    // Save contents to files
    testDatabase.saveContentsToFile(1);
    testDatabase.saveContentsToFile(2);
    testDatabase.saveContentsToFile(3);

    // Clear existing data
    testDatabase.setAllTasks(new ArrayList<>());
    testDatabase.setAllResourceTypes(new ArrayList<>());
    testDatabase.setMasterSchedule(new Schedule());

    // Reload the data from the file system
    testDatabase = new MyFileDatabase(0,
        tempTaskFile, tempResourceFile, tempScheduleFile,
        taskObjectName, resourceObjectName, scheduleObjectName);

    // Verify reloaded data
    assertEquals(1, testDatabase.getAllTasks().size(),
        "Task list should contain 1 task after reload");
    assertEquals(1, testDatabase.getAllResourceTypes().size(),
        "Resource type list should contain 1 type after reload");

    Schedule reloadedSchedule = testDatabase.getMasterSchedule();
    assertNotNull(reloadedSchedule, "Master schedule should not be null after reload");
    assertFalse(reloadedSchedule.getTaskSchedule().isEmpty(),
        "Master schedule should not be empty after reload");
    assertEquals(1, reloadedSchedule.getTaskSchedule().size(),
        "Master schedule should contain 1 task after reload");

    LiveSchedApplication.clientDatabases.remove("testClient");
  }

  /**
   * Tests Google Cloud Storage-based database functionality, including saving,
   * uploading, and reloading data from GCS.
   *
   * @throws IOException if an error occurs during file or GCS operations
   */
  @Test
  void externalIntegrationTestCloudDatabase() throws IOException {
    // Initialize the database with temporary file paths
    MyFileDatabase testDatabase = new MyFileDatabase(1,
        tempTaskFile, tempResourceFile, tempScheduleFile,
        taskObjectName, resourceObjectName, scheduleObjectName);

    LiveSchedApplication.useGCS = true;
    LiveSchedApplication.clientDatabases.put("testClient", testDatabase);

    RouteController routeController = new RouteController();

    // Add a task
    String startTime = LocalDateTime.now().plusHours(1)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String endTime = LocalDateTime.now().plusHours(2)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    ResponseEntity<?> taskResponse = routeController.addTask(
        "Test Task", 1, startTime, endTime, 40.71, -74.0, "testClient");
    assertEquals(HttpStatus.OK, taskResponse.getStatusCode(),
        "Task addition should return 200 OK");

    // Add a resource type
    ResponseEntity<?> resourceResponse = routeController.addResourceType(
        "Type1", 3, 40.71, -74.0, "testClient");
    assertEquals(HttpStatus.OK, resourceResponse.getStatusCode(),
        "Resource addition should return 200 OK");

    // Modify required resources for task
    ResponseEntity<?> modifyResourceResponse = routeController.modifyResourceType(
        "1", "Type1", 1, "testClient");
    assertEquals(HttpStatus.OK, modifyResourceResponse.getStatusCode(),
        "Resource modification for task should return 200 OK");

    // Update schedule
    ResponseEntity<?> scheduleResponse = routeController.updateSchedule(10, "testClient");
    assertEquals(HttpStatus.OK, scheduleResponse.getStatusCode(),
        "Update schedule should return 200 OK");

    // Save contents to files (Involves uploading files to GCS)
    testDatabase.saveContentsToFile(1);
    testDatabase.saveContentsToFile(2);
    testDatabase.saveContentsToFile(3);

    // Reload client databases from GCS
    LiveSchedApplication application = new LiveSchedApplication();
    application.reloadClientDatabasesCloud();

    // Validate the reloaded data
    MyFileDatabase reloadedDatabase = LiveSchedApplication.clientDatabases.get("testClient");
    assertNotNull(reloadedDatabase, "Reloaded database should not be null");
    assertEquals(1, reloadedDatabase.getAllTasks().size(),
        "Reloaded task list should contain 1 task");
    assertEquals(1, reloadedDatabase.getAllResourceTypes().size(),
        "Reloaded resource type list should contain 1 type");

    Schedule reloadedSchedule = reloadedDatabase.getMasterSchedule();
    assertNotNull(reloadedSchedule, "Reloaded master schedule should not be null");
    assertFalse(reloadedSchedule.getTaskSchedule().isEmpty(),
        "Reloaded master schedule should not be empty");
    assertEquals(1, reloadedSchedule.getTaskSchedule().size(),
        "Reloaded master schedule should contain 1 task");

    // Cleanup GCS files
    deleteFromCloud("innov8-livesched-bucket", taskObjectName);
    deleteFromCloud("innov8-livesched-bucket", resourceObjectName);
    deleteFromCloud("innov8-livesched-bucket", scheduleObjectName);

    // Remove test database from client databases
    LiveSchedApplication.clientDatabases.remove("testClient");

    // Reset GCS usage flag
    LiveSchedApplication.useGCS = false;
  }

  /**
   * Deletes a file from Google Cloud Storage.
   *
   * @param bucketName the name of the GCS bucket
   * @param objectName the object name in the GCS bucket
   */
  private void deleteFromCloud(String bucketName, String objectName) {
    Storage storage = StorageOptions.getDefaultInstance().getService();
    storage.delete(BlobId.of(bucketName, objectName));
  }

}