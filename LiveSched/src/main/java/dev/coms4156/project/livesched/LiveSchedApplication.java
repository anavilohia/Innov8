package dev.coms4156.project.livesched;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class contains all the startup logic for the application.
 */
@SpringBootApplication
public class LiveSchedApplication implements CommandLineRunner {

  /**
   * The main launcher for the services all it does
   * is make a call to the overridden run method.
   *
   * @param args A {@code String[]} of any potential
   *             runtime arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(LiveSchedApplication.class, args);
  }

  /**
   * This contains all the setup logic, it will mainly be focused
   * on loading up and creating an instance of the database based
   * off a saved file or will create a fresh database if the file
   * is not present.
   *
   * @param args A {@code String[]} of any potential runtime args
   */
  @Override
  public void run(String[] args) {
    boolean isSetupMode = false;

    for (String arg : args) {
      switch (arg) {
        case "setup":
          isSetupMode = true;
          break;
        case "--useGCS":
          useGCS = true;
          System.out.println("GCS operations enabled.");
          break;
        default:
          System.out.println("Unknown argument: " + arg);
      }
    }

    if (isSetupMode) {
      myFileDatabase = new MyFileDatabase(1, TASK_FILE_PATH, RESOURCE_TYPE_FILE_PATH,
          TASK_OBJECT_NAME, RESOURCE_TYPE_OBJECT_NAME);
      setupDataFile();
      System.out.println("System setup completed.");
      return;
    }
    myFileDatabase = new MyFileDatabase(0, TASK_FILE_PATH, RESOURCE_TYPE_FILE_PATH,
        TASK_OBJECT_NAME, RESOURCE_TYPE_OBJECT_NAME);
    System.out.println("System start up.");
  }

  /**
   * Overrides the database reference, used when testing.
   *
   * @param testData A {@code MyFileDatabase} object referencing test data.
   */
  public static void overrideDatabase(MyFileDatabase testData) {
    myFileDatabase = testData;
    saveData = false;
  }

  /**
   * Populates the database with some example resources and tasks.
   */
  public void setupDataFile() {
    ResourceType bed = new ResourceType("Bed", 20, 40.84, -73.94);
    ResourceType nurse = new ResourceType("Nurse", 15, 40.84, -73.94);
    ResourceType doctor = new ResourceType("Doctor", 10, 40.84, -73.94);
    ResourceType ambulance = new ResourceType("Ambulance", 5, 40.84, -73.94);

    List<ResourceType> allResourceTypes = new ArrayList<>();
    allResourceTypes.add(bed);
    allResourceTypes.add(nurse);
    allResourceTypes.add(doctor);
    allResourceTypes.add(ambulance);

    Map<ResourceType, Integer> emergencyResources = new HashMap<>();
    emergencyResources.put(bed, 2); // for two people at the same time
    emergencyResources.put(doctor, 2);
    emergencyResources.put(nurse, 2);

    Task emergency = new Task(
        "1", emergencyResources, 1,
        LocalDateTime.now(), LocalDateTime.now().plusHours(3),
        40.81, -73.96);

    List<Task> allTasks = new ArrayList<>();
    allTasks.add(emergency);

    Map<ResourceType, Integer> checkupResources = new HashMap<>();
    checkupResources.put(nurse, 1);
    checkupResources.put(doctor, 1);

    Task checkup = new Task(
        "2", checkupResources, 3,
        LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusMinutes(30),
        40.81, -73.96);

    allTasks.add(checkup);

    Map<ResourceType, Integer> transportResources = new HashMap<>();
    transportResources.put(ambulance, 1);
    transportResources.put(nurse, 1);

    Task patientTransport = new Task(
        "3", transportResources, 2,
        LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(45),
        40.83, -73.91);

    allTasks.add(patientTransport);

    myFileDatabase.setAllResourceTypes(allResourceTypes);
    myFileDatabase.setAllTasks(allTasks);
  }

  /**
   * This contains all the overheading teardown logic, it will
   * mainly be focused on saving all the created user data to a
   * file, so it will be ready for the next setup.
   */
  @PreDestroy
  public void onTermination() {
    System.out.println("Termination");
    if (saveData) {
      myFileDatabase.saveContentsToFile(1); // Save tasks
      myFileDatabase.saveContentsToFile(2); // Save resourceTypes
    }
  }

  public static MyFileDatabase myFileDatabase;
  public static boolean useGCS = false; // Default is local mode (Not use Google Cloud Storage)

  private static final String TASK_FILE_PATH = "/tmp/tasks.txt";
  private static final String RESOURCE_TYPE_FILE_PATH = "/tmp/resourceTypes.txt";
  private static final String TASK_OBJECT_NAME =  "gcs_tasks.txt";
  private static final String RESOURCE_TYPE_OBJECT_NAME = "gcs_resourceTypes.txt";
  private static final String APP_ENGINE_ENV = "standard"; // Constant for environment check
  private static boolean saveData = true;

  // Detect App Engine environment and enable GCS if running in App Engine
  static {
    String env = System.getenv("GAE_ENV");
    if (APP_ENGINE_ENV.equals(env)) {
      useGCS = true;
      System.out.println("Running in App Engine: GCS enabled.");
    }
  }
}