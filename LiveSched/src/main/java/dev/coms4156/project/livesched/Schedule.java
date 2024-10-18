package dev.coms4156.project.livesched;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Coordinates scheduling given a Task and a List of ResourceTypes.
 * This class stores the available ResourceTypes and a Task
 * and modifies their values upon scheduling
 */
public class Schedule {
  private final String scheduleId;
  private Task task;
  private List<ResourceType> resourceTypes;
  private List<ResourceType> candidates;

  /**
   * Constructs a new Task object with the given parameters.
   *
   * @param scheduleId        the unique ID of the schedule
   * @param task              task to provide resource to
   * @param resourceTypes     list of available resource types
   * @param radius            maxinmum distance from task to resource
   */
  public Schedule(String scheduleId, Task task, List<ResourceType> resourceTypes, double radius) {
    this.scheduleId = scheduleId;
    this.task = task;
    this.resourceTypes = resourceTypes;
    this.candidates = findAvailableResources(radius);
  }

  /**
   * Given a distance range from the location of this Task,
   * returns a list of all available ResourceTypes nearby, if exists.
   *
   * @param radius  maximum distance from current location
   * @return list of all available ResourceTypes nearby; otherwise, empty list.
   */
  public List<ResourceType> findAvailableResources(double radius) {
    List<ResourceType> result = new ArrayList<>();
    Map<ResourceType, Integer> resourceNeeded = this.task.getResources();
    Location taskLoc = this.task.getLocation();
    // for each resource type
    // check whether it is within the provided distance and is available
    // if so, add to result
    // otherwise, disregard
    for (ResourceType resourceType : resourceTypes) {
      Location resourceLoc = resourceType.getLocation();
      double distance = taskLoc.getDistance(resourceLoc);
      // this means resourceType is what we're looking for
      if (distance <= radius && resourceNeeded.containsKey(resourceType)) {
        // this means the resourceType has the resource task is looking for at startTime
        // and there are enough resources
        if (resourceType.findAvailableResource(task.getStartTime()) != null
                && resourceNeeded.get(resourceType) <= resourceType.getTotalUnits()) {
          result.add(resourceType);
        }
      }
    }
    return result;
  }

  /**
   * Assigns the resource to a task until the specified end time.
   *
   * @param resourceType the resource type to assign a task to
   */
  public void assignTaskToResource(ResourceType resourceType) {
    resourceType.findAvailableResource(task.getStartTime()).assignUntil(task.getEndTime());
    task.getResources().remove(resourceType);
  }
}