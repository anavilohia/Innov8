package dev.coms4156.project.livesched;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a specific type or group of resources.
 * This class stores the resources within the resource type and the location of the resource type.
 */
public class ResourceType implements Serializable {
  @Serial
  private static final long serialVersionUID = 1002L;

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
   * Adds a new resource within this resource type.
   */
  public final void addResource() {
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

  public final int getTotalUnits() {
    return resources.size();
  }

  public Location getLocation() {
    return location;
  }

  public String getTypeName() {
    return typeName;
  }

  /**
   * Counts the number of available resources within this resource type at the specified time.
   *
   * @param time the time at which to check availability
   *
   * @return the number of available resources at the given time
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
   * Returns a string representation of the resource type, including its name,
   * resources and location.
   *
   * @return A string representing the task.
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(typeName).append(" ")
            .append("Location: ").append(location.getCoordinates()).append("\n")
            .append("Resources: \n");
    for (Map.Entry<String, Resource> entry : resources.entrySet()) {
      String key = entry.getKey();
      Resource value = entry.getValue();
      result.append(key).append(": \n")
              .append(value.toString()).append("\n");
    }
    return result.toString();
  }

  /**
   * Updates the location of the resource type.
   *
   * @param latitude  the new latitude of the resource type's location
   * @param longitude the new longitude of the resource type's location
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  public void updateLocation(double latitude, double longitude) {
    this.location = new Location(latitude, longitude);
  }
}
