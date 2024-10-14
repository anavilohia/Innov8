package dev.coms4156.project.livesched;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a task that has to be done. 
 * This class stores the id, resources needed, and the location of the task.
 */
public class Task {
  private final String taskId;
  private Map<ResourceType, Integer> resourceList; // Key = ResoureceType, Value = Units needed
  private int priority; // value between 1 and 5
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private double latitude;
  private double longitude;

  /**
   * Constructs a new Task object with the given parameters.
   *
   * @param taskId        the unique ID of the resource
   * @param resourceList  the map of resources and their quantity needed
   * @param priority      the priority of the task
   * @param startTime     the time that task starts
   * @param endTime       the time that task ends
   * @param latitude      the latitude of the task's location
   * @param longitude     the longitude of the task's location
   */
  public Task(String taskId, Map<ResourceType, Integer> resourceList, int priority, 
        LocalDateTime startTime, LocalDateTime endTime, double latitude, double longitude) {
    this.taskId = taskId;
    this.resourceList = resourceList;
    this.priority = priority;
    this.startTime = startTime;
    this.endTime = endTime;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Updates the start time of the task.
   *
   * @param startTime the time that task starts
   */
  public void updateStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * Updates the end time of the task.
   *
   * @param endTime the time that task ends
   */
  public void updateEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * Updates the quantity of or add a resource needed for the task.
   *
   * @param resourceType  the type of resource needed
   * @param quantity      the quantity of the resource needed
   */
  public void updateResource(ResourceType resourceType, int quantity) {
    if (resourceList.containsKey(resourceType)) {
      resourceList.replace(resourceType, quantity);
    } else {
      resourceList.put(resourceType, quantity);
    }
  }

  /**
   * Checks if all resources needed for the task is available.
   *
   * @return true if all resources are available, false otherwise
   */
  public boolean checkResourceAvailability() {
    for (ResourceType resource : resourceList.keySet()) {
      if (resource.getAvailableUnits() < resourceList.get(resource)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Updates the location of the task.
   *
   * @param latitude  the new latitude of the task's location
   * @param longitude the new longitude of the task's location
   */
  public void updateLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Map<ResourceType, Integer> getResources() {
    return resourceList;
  }

  public String getTaskId() {
    return taskId;
  }

  public String getLocation() {
    return latitude + ", " + longitude;
  }

  public int getPriority() {
    return priority;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

}
