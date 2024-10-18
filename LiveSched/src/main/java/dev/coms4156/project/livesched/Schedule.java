package dev.coms4156.project.livesched;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Coordinates scheduling given a Task and a List of ResourceTypes.
 * This class stores the available ResourceTypes and a Task
 * and modifies their values upon scheduling
 */
public class Schedule {
  private final String scheduleId;
  private PriorityQueue<Task> tasks;
  private List<ResourceType> resourceTypes;
  private List<ResourceType> candidates;
  private double maxDistance;

  /**
   * Constructs a new Schedule object with the given parameters.
   *
   * @param scheduleId        the unique ID of the schedule
   * @param resourceTypes     list of all resource types
   * @param maxDistance       maxinmum distance from task to resource
   */
  public Schedule(String scheduleId, List<ResourceType> resourceTypes, double maxDistance) {
    this.scheduleId = scheduleId;
    this.resourceTypes = resourceTypes;
    this.maxDistance = maxDistance;

    Comparator<Task> comparator = new TaskComparator();
    this.tasks = new PriorityQueue<Task>(comparator);  // queue of tasks in order of priority
  }

  /**
   * Given a maximum distance range from the location of a Task,
   * returns a list of all available ResourceTypes nearby, if exists.
   *
   * @param task         high priority task that needs to be matched with a resource type
   * @return first ResourceType matched; otherwise, null.
   */

  public ResourceType matchTaskWithResource(Task task) {
    Map<ResourceType, Integer> resourceNeeded = task.getResources();
    Location taskLoc = task.getLocation();
    for (ResourceType resourceType : resourceTypes) {
      Location resourceLoc = resourceType.getLocation();
      double distance = taskLoc.getDistance(resourceLoc);
      if (distance <= maxDistance && resourceNeeded.containsKey(resourceType)) {
        if (resourceType.findAvailableResource(task.getStartTime()) != null
                && resourceNeeded.get(resourceType) <= resourceType.getTotalUnits()) {
          return resourceType;
        }
      }
    }
    return null;
  }

  //  public List<ResourceType> matchTaskWithResource(Task task) {
  //    List<ResourceType> result = new ArrayList<>();
  //    Map<ResourceType, Integer> resourceNeeded = task.getResources();
  //    Location taskLoc = task.getLocation();
  //    for (ResourceType resourceType : resourceTypes) {
  //      Location resourceLoc = resourceType.getLocation();
  //      double distance = taskLoc.getDistance(resourceLoc);
  //      if (distance <= maxDistance && resourceNeeded.containsKey(resourceType)) {
  //        if (resourceType.findAvailableResource(task.getStartTime()) != null
  //                && resourceNeeded.get(resourceType) <= resourceType.getTotalUnits()) {
  //          result.add(resourceType);
  //        }
  //      }
  //    }
  //    return result;
  //  }

  /**
   * Receives task from network by adding given task to a priority queue.
   *
   * @param task the task to add to the queue
   */
  public void receiveTask(Task task) {
    tasks.add(task);
  }

  /**
   * Processes task by removing task from the queue.
   *
   * @return matched Task, ResourceType pair
   */
  public Map<Task, ResourceType> processTask() {
    Task task = tasks.remove();
    ResourceType resourceType = matchTaskWithResource(task);
    assignResourceToTask(resourceType, task);
    Map<Task, ResourceType> pair = new HashMap<>();
    pair.put(task, resourceType);
    return pair;
  }

  /**
   * Assigns the resource to a task until the specified end time.
   *
   * @param resourceType the resource type to assign a task to
   * @param task         the task to be matched with resource
   */
  public void assignResourceToTask(ResourceType resourceType, Task task) {
    resourceType.findAvailableResource(task.getStartTime()).assignUntil(task.getEndTime());
    task.getResources().remove(resourceType);
  }
}