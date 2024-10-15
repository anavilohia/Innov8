package dev.coms4156.project.livesched;

import java.time.LocalDateTime;

/**
 * Represents a resource that can be assigned to a task.
 * This class stores the id, availability, and location of the resource.
 */
public class Resource {
  private final String resourceId;
  private LocalDateTime availableFrom;
  private double latitude;
  private double longitude;

  /**
   * Constructs a new Resource object with the given parameters.
   *
   * @param resourceId  the unique ID of the resource
   * @param latitude    the latitude of the resource's location
   * @param longitude   the longitude of the resource's location
   * @throws IllegalArgumentException if the {@code resourceId} is null or empty,
   *                                  or if the latitude or longitude is out of bounds
   */
  public Resource(String resourceId, double latitude, double longitude) {
    if (resourceId == null || resourceId.trim().isEmpty()) {
      throw new IllegalArgumentException("Resource ID cannot be null or empty.");
    }
    validateLatLong(latitude, longitude);

    this.resourceId = resourceId;
    this.availableFrom = LocalDateTime.now(); // Initially available now
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
    if (taskEndTime == null) {
      throw new IllegalArgumentException("Task end time cannot be null.");
    }
    if (taskEndTime.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Task end time cannot be in the past.");
    }
    if (taskEndTime.equals(LocalDateTime.now())) {
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
   * Updates the location of the resource.
   *
   * @param latitude  the new latitude of the resource's location
   * @param longitude the new longitude of the resource's location
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  public void updateLocation(double latitude, double longitude) {
    validateLatLong(latitude, longitude);
    this.latitude = latitude;
    this.longitude = longitude;
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

  public String getLocation() {
    return latitude + ", " + longitude;
  }

}
