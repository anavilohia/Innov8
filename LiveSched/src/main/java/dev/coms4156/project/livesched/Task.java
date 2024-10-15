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
   * @throws IllegalArgumentException if the {@code resourceId} is null or empty,
   *                                  or if the latitude or longitude is out of bounds
   */
  public Task(String taskId, Map<ResourceType, Integer> resourceList, int priority, 
        LocalDateTime startTime, LocalDateTime endTime, double latitude, double longitude) {
    if (taskId == null || taskId.trim().isEmpty()) {
      throw new IllegalArgumentException("Task ID cannot be null or empty.");
    }
    validateLatLong(latitude, longitude);
    this.taskId = taskId;
    this.resourceList = resourceList;
    this.priority = priority;
    this.startTime = startTime;
    this.endTime = endTime;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Validates that the given latitude and longitude are within their valid ranges.
   *
   * @param latitude  the latitude
   * @param longitude the longitude
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  private void validateLatLong(double latitude, double longitude) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90.");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180.");
    }
  }

  /**
   * Updates the start time of the task.
   *
   * @param startTime the time that task starts
   * @throws IllegalArgumentException if {@code startTime} is null
   */
  public void updateStartTime(LocalDateTime startTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null.");
    }
    this.startTime = startTime;
  }

  /**
   * Updates the end time of the task.
   *
   * @param endTime the time that task ends
   * @throws IllegalArgumentException if {@code taskEndTime} is null, in the past, or exactly now
   */
  public void updateEndTime(LocalDateTime endTime) {
    if (endTime == null) {
      throw new IllegalArgumentException("End time cannot be null.");
    }
    if (endTime.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("End time cannot be in the past.");
    }
    if (endTime.equals(LocalDateTime.now())) {
      throw new IllegalArgumentException("End time cannot be exactly now.");
    }
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
   * Updates the location of the task.
   *
   * @param latitude  the new latitude of the task's location
   * @param longitude the new longitude of the task's location
   * @throws IllegalArgumentException if {@code latitude} or {@code longitude} are out of bounds
   */
  public void updateLocation(double latitude, double longitude) {
    validateLatLong(latitude, longitude);
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
