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
   * @param taskObjectName the name of the task object in GCS
   * @param resourceTypeObjectName the name of the resource type object in GCS
   */
  public MyFileDatabase(int flag, String taskFilePath, String resourceTypeFilePath,
                        String taskObjectName, String resourceTypeObjectName) {
    this.taskFilePath = taskFilePath;
    this.resourceTypeFilePath = resourceTypeFilePath;
    this.taskObjectName = taskObjectName;
    this.resourceTypeObjectName = resourceTypeObjectName;

    if (flag == 0) {
      this.allTasks = deSerializeObjectFromFile(taskContentType);
      this.allResourceTypes = deSerializeObjectFromFile(resourceTypeContentType);
    } else {
      this.allTasks = new ArrayList<>();
      this.allResourceTypes = new ArrayList<>();
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
   * Deserializes the object from file. Throws exception if data in file is invalid.
   *
   * @param contentType the type of content to deserialize
   *
   * @return A list of deserialized objects from the file, or an empty list if an error occurs
   */
  public final <T> List<T> deSerializeObjectFromFile(int contentType) {
    String filePath;
    String gcsObjectName;
    if (contentType == taskContentType) {
      filePath = taskFilePath;
      gcsObjectName = taskObjectName;
    } else if (contentType == resourceTypeContentType) {
      filePath = resourceTypeFilePath;
      gcsObjectName = resourceTypeObjectName;
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
        return new ArrayList<>();
      }
    } else {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info("GCS is disabled. Using local file: " + filePath);
      }
    }

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
      Object obj = in.readObject();
      if (obj instanceof List<?> listObj) {

        List<T> finalList = new ArrayList<>();

        for (Object value : listObj) {
          if (contentType == taskContentType && !(value instanceof Task)) {
            throw new IllegalArgumentException("Invalid object type in file.");
          } else if (contentType == resourceTypeContentType && !(value instanceof ResourceType)) {
            throw new IllegalArgumentException("Invalid object type in file.");
          }
          finalList.add((T) value);
        }
        return finalList;
      } else {
        throw new IllegalArgumentException("Invalid object type in file.");
      }
    } catch (IOException | ClassNotFoundException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
      return new ArrayList<>();
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
    } else {
      throw new IllegalArgumentException("Invalid content type in file.");
    }

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      if (contentType == taskContentType) {
        out.writeObject(allTasks);
      } else if (contentType == resourceTypeContentType) {
        out.writeObject(allResourceTypes);
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
   * Adds a resource type to the database.
   *
   */
  public void addResourceType(ResourceType resourceType) {

    this.allResourceTypes.add(resourceType);
  }

  /**
   * Returns a string representation of the database.
   *
   * @return a string representation of the database
   */
  @Override
  public String toString() {
    // to be added
    return "";
  }


  private final int taskContentType = 1;
  private final int resourceTypeContentType = 2;

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
   * The object name under which the Task data is stored in the GCS bucket.
   */
  private final String taskObjectName;

  /**
   * The object name under which the ResourceType data is stored in the GCS bucket.
   */
  private final String resourceTypeObjectName;

  /**
   * The list of tasks to be assigned.
   */
  private List<Task> allTasks;

  /**
   * The list of all resourceTypes available.
   */
  private List<ResourceType> allResourceTypes;

  /**
   * Logger to print information and exceptions.
   */
  private static final Logger LOGGER = Logger.getLogger(MyFileDatabase.class.getName());
}
