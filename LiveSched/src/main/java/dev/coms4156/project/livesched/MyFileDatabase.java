package dev.coms4156.project.livesched;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a file-based database containing Task and ResourceType objects.
 */
public class MyFileDatabase {

  /**
   * Constructs a MyFileDatabase object and loads up the data structure with
   * the contents of the file.
   *
   * @param flag     used to distinguish mode of database
   * @param taskFilePath the path to the file containing the entries of the database
   * @param resourceTypeFilePath the path to the file containing the entries of the database
   */
  public MyFileDatabase(int flag, String taskFilePath, String resourceTypeFilePath) {
    this.taskFilePath = taskFilePath;
    this.resourceTypeFilePath = resourceTypeFilePath;

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
   * Deserializes the object from the file and returns the department mapping.
   * Throws exception if data in file is invalid
   *
   * @return the deserialized department mapping
   */
  public final <T> List<T> deSerializeObjectFromFile(int contentType) {
    String filePath;
    if (contentType == taskContentType) {
      filePath = taskFilePath;
    } else if (contentType == resourceTypeContentType) {
      filePath = resourceTypeFilePath;
    } else {
      throw new IllegalArgumentException("Invalid content type in file.");
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
   * Saves the contents of the internal data structures to the file. Contents of the file are
   * overwritten with this operation.
   *
   * @param contentType the type of content to be saved to file
   */
  public void saveContentsToFile(int contentType) {
    String filePath;
    if (contentType == taskContentType) {
      filePath = taskFilePath;
    } else if (contentType == resourceTypeContentType) {
      filePath = resourceTypeFilePath;
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
        LOGGER.info("Object serialized successfully.");
      }
    } catch (IOException e) {
      if (LOGGER.isLoggable(Level.SEVERE)) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
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
   * The path to the file containing the Task entries.
   */
  private final String taskFilePath;

  /**
   * The path to the file containing the ResourceType entries.
   */
  private final String resourceTypeFilePath;

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
