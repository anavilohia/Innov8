package dev.coms4156.project.livesched;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a specific type or group of resources.
 * This class stores the total number of units and the resources within the resource type.
 */
public class ResourceType {
  private String typeName;
  private int totalUnits;
  private Map<String, Resource> resources; // Key = resourceId, Value = resource
  private int resourceCounter; // Counter to assign next unique resourceId

  /**
   * Constructs a new ResourceType object with the given parameters.
   *
   * @param typeName      the type of resource (e.g., "bed", "doctor")
   * @param totalUnits    the total number of units for this resource type
   * @param defaultLat    the latitude of the resources' default location
   * @param defaultLong   the longitude of the resources' default location
   * @throws IllegalArgumentException if {@code typeName} is null or empty,
   *                                  if {@code totalUnits} is negative,
   *                                  or if the latitude or longitude is out of bounds
   */
  public ResourceType(String typeName, int totalUnits, double defaultLat, double defaultLong) {
    if (typeName == null || typeName.trim().isEmpty()) {
      throw new IllegalArgumentException("Resource type name cannot be null or empty.");
    }
    if (totalUnits < 0) {
      throw new IllegalArgumentException("Number of total units cannot be negative.");
    }
    validateLatLong(defaultLat, defaultLong);

    this.typeName = typeName;
    this.totalUnits = totalUnits;
    this.resources = new HashMap<>();

    // Create initial resources
    for (int i = 1; i <= totalUnits; i++) {
      String resourceId = typeName + " " + i;
      resources.put(resourceId, new Resource(resourceId, defaultLat, defaultLong));
    }

    this.resourceCounter = totalUnits;
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
   * Adds a new resource with the specified location.
   *
   * @param latitude    the latitude of the new resource's location
   * @param longitude   the longitude of the new resource's location
   * @throws IllegalArgumentException if {@code latitude} or {@code longitude} are out of bounds
   */
  public void addResource(double latitude, double longitude) {
    validateLatLong(latitude, longitude);

    String resourceId = typeName + " " + (++resourceCounter);
    Resource newResource = new Resource(resourceId, latitude, longitude);
    resources.put(resourceId, newResource);
    totalUnits++;
  }

  /**
   * Finds an available resource at the specified start time.
   *
   * @param startTime the time at which the resource should be available
   *
   * @return an available resource, or null if no resources are available
   * @throws IllegalArgumentException if {@code startTime} is null
   */
  public Resource findAvailableResource(LocalDateTime startTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null.");
    }

    for (Resource resource : resources.values()) {
      if (resource.isAvailableAt(startTime)) {
        return resource;
      }
    }
    return null;
  }

  public int getTotalUnits() {
    return totalUnits;
  }

  /**
   * Counts the number of available units at the specified time.
   *
   * @param time the time at which to check availability
   *
   * @return the number of available units at the given time
   * @throws IllegalArgumentException if the time is null
   */
  public int countAvailableUnits(LocalDateTime time) {
    if (time == null) {
      throw new IllegalArgumentException("Time cannot be null.");
    }

    int count = 0;
    for (Resource resource : resources.values()) {
      if (resource.isAvailableAt(time)) {
        count++;
      }
    }
    return count;
  }

}
