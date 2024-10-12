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
   */
  public ResourceType(String typeName, int totalUnits, double defaultLat, double defaultLong) {
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
   * Adds a new resource with the specified location.
   *
   * @param latitude    the latitude of the new resource's location
   * @param longitude   the longitude of the new resource's location
   */
  public void addResource(double latitude, double longitude) {
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
   */
  public Resource findAvailableResource(LocalDateTime startTime) {
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
   * Counts the number of currently available units.
   *
   * @return the number of available units
   */
  public int getAvailableUnits() {
    int count = 0;
    for (Resource resource : resources.values()) {
      if (resource.isAvailableAt(LocalDateTime.now())) {
        count++;
      }
    }
    return count;
  }

}
