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
  private Map<String, Resource> resources; // Key = resourceId, Value = resource
  private Location location;

  /**
   * Constructs a new ResourceType object with the given parameters.
   *
   * @param typeName      the type of resource (e.g., "bed", "doctor")
   * @param totalUnits    the total number of units for this resource type
   * @param latitude    the latitude of the resource type's default location
   * @param longitude   the longitude of the resource type's default location
   * @throws IllegalArgumentException if {@code typeName} is null or empty,
   *                                  if {@code totalUnits} is negative,
   *                                  or if the latitude or longitude is out of bounds
   */
  public ResourceType(String typeName, int totalUnits, double latitude, double longitude) {
    if (typeName == null || typeName.trim().isEmpty()) {
      throw new IllegalArgumentException("Resource type name cannot be null or empty.");
    }
    if (totalUnits < 0) {
      throw new IllegalArgumentException("Number of total units cannot be negative.");
    }

    this.typeName = typeName;
    this.resources = new HashMap<>();
    this.location = new Location(latitude, longitude);

    // Create initial resources
    for (int resourceNumber = 1; resourceNumber <= totalUnits; resourceNumber++) {
      addResource();
    }
  }

  /**
   * Adds a new resource with the specified location.
   *
   * @throws IllegalArgumentException if {@code latitude} or {@code longitude} are out of bounds
   */
  public void addResource() {
    int resourceNumber = getTotalUnits() + 1;
    String resourceId = typeName + " " + resourceNumber;
    Resource newResource = new Resource(resourceId);
    resources.put(resourceId, newResource);
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
    return resources.size();
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

  /**
   * Updates the location of the resource.
   *
   * @param latitude  the new latitude of the resource's location
   * @param longitude the new longitude of the resource's location
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  public void updateLocation(double latitude, double longitude) {
    this.location = new Location(latitude, longitude);
  }
}
