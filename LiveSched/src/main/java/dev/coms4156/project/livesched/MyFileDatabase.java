package dev.coms4156.project.livesched;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a file-based database that stores and manages {@code Task} and
 * {@code ResourceType} objects. It provides functionality to serialize and deserialize data
 * from both local files and Google Cloud Storage (GCS), ensuring data persistence.
 */
public class MyFileDatabase {

  /**
   * Constructs a MyFileDatabase object and loads up the data structures with
   * the contents of the files or initializes them as empty based on the provided flag.
   *
   * @param flag     used to distinguish mode of database;
   *                 0 for loading from files, 1 for initializing an empty database
   * @param taskFilePath the path to the file containing serialized task entries
   * @param resourceTypeFilePath the path to the file containing serialized resourceTypes entries
   * @param scheduleFilePath the path to the file containing serialized schedule entries
   * @param taskObjectName the name of the task object in GCS
   * @param resourceTypeObjectName the name of the resource type object in GCS
   */
  public MyFileDatabase(int flag,
                        String taskFilePath,
                        String resourceTypeFilePath,
                        String scheduleFilePath,
                        String taskObjectName,
                        String resourceTypeObjectName,
                        String scheduleObjectName) {
    this.taskFilePath = taskFilePath;
    this.resourceTypeFilePath = resourceTypeFilePath;
    this.scheduleFilePath = scheduleFilePath;
    this.taskObjectName = taskObjectName;
    this.resourceTypeObjectName = resourceTypeObjectName;
    this.scheduleObjectName = scheduleObjectName;

    if (flag == 0) {
      this.allTasks = (List<Task>) deSerializeObjectFromFile(taskContentType);
      this.allResourceTypes =
          (List<ResourceType>) deSerializeObjectFromFile(resourceTypeContentType);
      this.masterSchedule = (Schedule) deSerializeObjectFromFile(scheduleContentType);
    } else {
      this.allTasks = new ArrayList<>();
      this.allResourceTypes = new ArrayList<>();
      this.masterSchedule = new Schedule();
    }
  }

  /**
   * Sets allTasks of the database.
   *
   * @param tasks the list of all tasks to be added to database
   */
  public void setAllTasks(List<Task> tasks) {
    this.allTasks = tasks == null ? new ArrayList<>() : tasks;
  }

  /**
   * Sets allResourceTypes of the database.
   *
   * @param resourceTypes the list of all resourceTypes to be added to database
   */
  public void setAllResourceTypes(List<ResourceType> resourceTypes) {
    this.allResourceTypes = resourceTypes == null ? new ArrayList<>() : resourceTypes;
  }

  /**
   * Sets masterSchedule of the database.
   *
   * @param masterSchedule the master schedule to be added to database
   */
  public void setMasterSchedule(Schedule masterSchedule) {
    this.masterSchedule = masterSchedule == null ? new Schedule() : masterSchedule;
  }

  /**
   * Deserializes the object from file. Throws exception if data in file is invalid.
   *
   * @param contentType the type of content to deserialize
   *
   * @return The deserialized objects from the file, or an empty list if an error occurs
   */
  public final Object deSerializeObjectFromFile(int contentType) {
    String filePath;
    String gcsObjectName;
    if (contentType == taskContentType) {
      filePath = taskFilePath;
      gcsObjectName = taskObjectName;
    } else if (contentType == resourceTypeContentType) {
      filePath = resourceTypeFilePath;
      gcsObjectName = resourceTypeObjectName;
    } else if (contentType == scheduleContentType) {
      filePath = scheduleFilePath;
      gcsObjectName = scheduleObjectName;
    } else {
      throw new IllegalArgumentException("Invalid content type in file.");
    }

    // Download from GCS if the useGCS flag is enabled
    if (LiveSchedApplication.useGCS) {
      try {
        downloadFileFromCloud(BUCKET_NAME, gcsObjectName, filePath);
      } catch (IOException e) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
      }
    } else {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info("GCS is disabled. Using local file: " + filePath);
      }
    }

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
      Object obj = in.readObject();

      // Return the appropriate type based on content type
      if (contentType == taskContentType && obj instanceof List<?> listObj) {
        return listObj; // Return List<Task>
      } else if (contentType == resourceTypeContentType && obj instanceof List<?> listObj) {
        return listObj; // Return List<ResourceType>
      } else if (contentType == scheduleContentType && obj instanceof Schedule schedule) {
        return schedule; // Return Schedule
      } else {
        throw new IllegalArgumentException(INVALID_OBJ_TYPE_ERROR);
      }
    } catch (IOException | ClassNotFoundException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
      return null;
    }
  }

  /**
   * Downloads a file from Google Cloud Storage to a local destination path.
   *
   * @param bucketName the name of the GCS bucket
   * @param objectName the name of the object in GCS
   * @param destinationPath the local path where the object will be downloaded
   * @throws IOException if the object cannot be found or downloaded
   */
  private void downloadFileFromCloud(String bucketName, String objectName, String destinationPath)
      throws IOException {
    Blob blob = storage.get(BlobId.of(bucketName, objectName));

    if (blob != null) {
      blob.downloadTo(Paths.get(destinationPath));
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info("Downloaded file from GCS: " + objectName + " to " + destinationPath);
      }
    } else {
      throw new FileNotFoundException("The requested object " + objectName
          + " was not found in bucket " + bucketName);
    }
  }

  /**
   * Saves the contents of the internal data structures to the file. Contents of the file are
   * overwritten with this operation.
   *
   * @param contentType the type of content to be saved to file
   */
  public void saveContentsToFile(int contentType) {
    String filePath;
    String gcsObjectName;
    if (contentType == taskContentType) {
      filePath = taskFilePath;
      gcsObjectName = taskObjectName;
    } else if (contentType == resourceTypeContentType) {
      filePath = resourceTypeFilePath;
      gcsObjectName = resourceTypeObjectName;
    } else if (contentType == scheduleContentType) {
      filePath = scheduleFilePath;
      gcsObjectName = scheduleObjectName;
    } else {
      throw new IllegalArgumentException("Invalid content type in file.");
    }

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      if (contentType == taskContentType) {
        out.writeObject(allTasks);
      } else if (contentType == resourceTypeContentType) {
        out.writeObject(allResourceTypes);
      } else if (contentType == scheduleContentType) {
        out.writeObject(masterSchedule);
      }

      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info("Object serialized successfully in local file: " + filePath);
      }

      // Upload to GCS only if GCS operations are enabled
      if (LiveSchedApplication.useGCS) {
        uploadFileToCloud(BUCKET_NAME, gcsObjectName, filePath);
      } else {
        if (LOGGER.isLoggable(Level.INFO)) {
          LOGGER.info("GCS is disabled. Uploading to GCS is skipped.");
        }
      }
    } catch (IOException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
  }

  /**
   * Uploads a local file to Google Cloud Storage.
   *
   * @param bucketName the name of the GCS bucket
   * @param objectName the name to save the object as in GCS
   * @param filePath   the path of the local file to upload
   * @throws IOException if the upload fails
   */
  private void uploadFileToCloud(String bucketName, String objectName, String filePath)
      throws IOException {
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    // Add a precondition to avoid race conditions
    Storage.BlobWriteOption precondition;
    if (storage.get(bucketName, objectName) == null) {
      precondition = Storage.BlobWriteOption.doesNotExist();
    } else {
      precondition = Storage.BlobWriteOption.generationMatch(
          storage.get(bucketName, objectName).getGeneration());
    }

    storage.createFrom(blobInfo, Paths.get(filePath), precondition);
    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.info("File " + filePath + " uploaded to GCS as " + objectName);
    }
  }

  /**
   * Gets all tasks from the database.
   *
   * @return a list containing all Task objects
   */
  public List<Task> getAllTasks() {
    return this.allTasks;
  }

  /**
   * Gets all ResourceTypes from the database.
   *
   * @return a list containing all ResourceType objects
   */
  public List<ResourceType> getAllResourceTypes() {
    return this.allResourceTypes;
  }

  /**
   * Gets master schedule from the database.
   *
   * @return a Schedule object containing all schedules
   */
  public Schedule getMasterSchedule() {
    return this.masterSchedule;
  }

  /**
   * Gets task by Id from the database.
   *
   * @return a Task object with specified taskId
   */
  public Task getTaskById(String taskId) {
    List<Task> tasks = this.allTasks;
    for (Task task : tasks) {
      if (task.getTaskId().equals(taskId)) {
        return task;
      }
    }
    return null;
  }

  /**
   * Adds a task to the database.
   *
   */
  public void addTask(Task task) {
    this.allTasks.add(task);
  }

  /**
   * Deletes a task to the database.
   *
   */
  public void deleteTask(Task task) {
    this.allTasks.remove(task);
  }

  /**
   * Adds a resource type to the database.
   *
   */
  public void addResourceType(ResourceType newResourceType) {
    for (ResourceType existingResource : this.allResourceTypes) {
      if (existingResource.equals(newResourceType)) {
        int newUnits = newResourceType.getTotalUnits();
        for (int resource = 0; resource < newUnits; resource++) {
          existingResource.addResource();
        }
        return;
      }
    }
    // If no match is found, add the new resource type
    this.allResourceTypes.add(newResourceType);
  }

  /**
   * Deletes a resource type to the database.
   *
   */
  public void deleteResourceType(ResourceType resourceType) {

    this.allResourceTypes.remove(resourceType);
  }

  /**
   * Gets task file path.
   */
  public String getTaskFilePath() {
    return taskFilePath;
  }

  public int getTaskContentType() {
    return taskContentType;
  }

  private final int taskContentType = 1;
  private final int resourceTypeContentType = 2;
  private final int scheduleContentType = 3;

  /**
   * Google Cloud Storage service instance used to interact with the GCS bucket.
   */
  private final Storage storage = StorageOptions.getDefaultInstance().getService();

  /**
   * The name of the Google Cloud Storage bucket where the serialized files are stored.
   */
  private static final String BUCKET_NAME = "innov8-livesched-bucket";

  /**
   * The path to the file containing the Task entries.
   */
  private final String taskFilePath;

  /**
   * The path to the file containing the ResourceType entries.
   */
  private final String resourceTypeFilePath;

  /**
   * The path to the file containing the ResourceType entries.
   */
  private final String scheduleFilePath;

  /**
   * The object name under which the Task data is stored in the GCS bucket.
   */
  private final String taskObjectName;

  /**
   * The object name under which the ResourceType data is stored in the GCS bucket.
   */
  private final String resourceTypeObjectName;

  /**
   * The object name under which the ResourceType data is stored in the GCS bucket.
   */
  private final String scheduleObjectName;

  /**
   * The list of tasks to be assigned.
   */
  private List<Task> allTasks;

  /**
   * The list of all resourceTypes available.
   */
  private List<ResourceType> allResourceTypes;

  /**
   * A master schedule containing all schedules.
   */
  private Schedule masterSchedule;

  /**
   * Logger to print information and exceptions.
   */
  private static final Logger LOGGER = Logger.getLogger(MyFileDatabase.class.getName());
  private static final String INVALID_OBJ_TYPE_ERROR = "Invalid object type in file.";
}
