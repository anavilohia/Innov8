package dev.coms4156.project.livesched;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a resource that can be assigned to a task.
 * This class stores the id and availability of the resource.
 */
public class Resource implements Serializable {
  @Serial
  private static final long serialVersionUID = 1003L;

  private final String resourceId;
  private LocalDateTime availableFrom;

  /**
   * Constructs a new Resource object with the given parameters.
   *
   * @param resourceId  the unique ID of the resource
   * @throws IllegalArgumentException if the {@code resourceId} is null or empty,
   *                                  or if the latitude or longitude is out of bounds
   */
  public Resource(String resourceId) {
    if (resourceId == null || resourceId.trim().isEmpty()) {
      throw new IllegalArgumentException("Resource ID cannot be null or empty.");
    }
    this.resourceId = resourceId;
    this.availableFrom = LocalDateTime.now(); // Initially available now
  }

  /**
   * Checks if the resource is available at the given time.
   *
   * @param time  the time to check availability for
   *
   * @return true if the resource is available, false otherwise
   * @throws IllegalArgumentException if the {@code time} parameter is null
   */
  public boolean isAvailableAt(LocalDateTime time) {
    if (time == null) {
      throw new IllegalArgumentException("Time to check availability for cannot be null.");
    }
    return time.isEqual(availableFrom) || time.isAfter(availableFrom);
  }

  /**
   * Assigns the resource to a task until the specified end time.
   *
   * @param taskEndTime the time when the task ends
   * @throws IllegalArgumentException if {@code taskEndTime} is null, in the past, or exactly now
   */
  public void assignUntil(LocalDateTime taskEndTime) {
    // Capture current time and truncate to minutes for comparison
    final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    if (taskEndTime == null) {
      throw new IllegalArgumentException("Task end time cannot be null.");
    }

    // Truncate taskEndTime to minutes as well, for the same precision during comparison
    LocalDateTime truncatedTaskEndTime = taskEndTime.truncatedTo(ChronoUnit.MINUTES);

    if (truncatedTaskEndTime.isBefore(now)) {
      throw new IllegalArgumentException("Task end time cannot be in the past.");
    }
    if (truncatedTaskEndTime.equals(now)) {
      throw new IllegalArgumentException("Task end time cannot be exactly now.");
    }
    setAvailableFrom(taskEndTime);
  }

  /**
   * Releases the resource, making it available immediately.
   */
  public void release() {
    setAvailableFrom(LocalDateTime.now());
  }

  /**
   * Gets the ID of the Resource.
   *
   * @return A {@code String} of resourceId
   */
  public String getResourceId() {
    return resourceId;
  }

  public LocalDateTime getAvailableFrom() {
    return availableFrom;
  }

  /**
   * Sets the time from which this resource will be available.
   *
   * @param availableFrom the time from which this resource will be available
   *
   * @throws IllegalArgumentException if the {@code availableFrom} parameter is null
   */
  public void setAvailableFrom(LocalDateTime availableFrom) {
    if (availableFrom == null) {
      throw new IllegalArgumentException("Time available from cannot be null.");
    }
    this.availableFrom = availableFrom;
  }

  /**
   * Returns a string representation of the resource,
   * including its id and start time.
   *
   * @return A string representing the task.
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Resource ID: ").append(resourceId).append("; ")
            .append("Available From: ").append(availableFrom.toString()).append("\n");
    return result.toString();
  }

}
