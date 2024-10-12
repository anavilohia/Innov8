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
   */
  public Resource(String resourceId, double latitude, double longitude) {
    this.resourceId = resourceId;
    this.availableFrom = LocalDateTime.now(); // Initially available now
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Checks if the resource is available at the given time.
   *
   * @param time  the time to check availability for
   *
   * @return true if the resource is available, false otherwise
   */
  public boolean isAvailableAt(LocalDateTime time) {
    return time.isAfter(availableFrom);
  }

  /**
   * Assigns the resource to a task until the specified end time.
   *
   * @param taskEndTime the time when the task ends
   */
  public void assign(LocalDateTime taskEndTime) {
    this.availableFrom = taskEndTime;
  }

  /**
   * Releases the resource, making it available immediately.
   */
  public void release() {
    this.availableFrom = LocalDateTime.now();
  }

  /**
   * Updates the location of the resource.
   *
   * @param latitude  the new latitude of the resource's location
   * @param longitude the new longitude of the resource's location
   */
  public void updateLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getResourceId() {
    return resourceId;
  }

  public LocalDateTime getAvailableFrom() {
    return availableFrom;
  }

  public void setAvailableFrom(LocalDateTime availableFrom) {
    this.availableFrom = availableFrom;
  }

  public String getLocation() {
    return latitude + ", " + longitude;
  }

}
