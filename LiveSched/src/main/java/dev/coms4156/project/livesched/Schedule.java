package dev.coms4156.project.livesched;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Coordinates scheduling given a list of tasks.
 * This class creates a schedule that pairs tasks with resourceTypes and
 * updates their values accordingly
 */
public class Schedule {
  private final String scheduleId;
  private Queue<Task> tasks;
  private double maxDistance;
  private Map<Task, List<Resource>> taskSchedule = new LinkedHashMap<>();

  /**
   * Constructs a new Schedule object with the given parameters.
   *
   * @param scheduleId        the unique ID of the schedule
   * @param tasks             list of all tasks
   * @param maxDistance       maximum distance from task to resource
   * @throws IllegalArgumentException if scheduleId is null or empty, resourceTypes is null,
   *                                  or maxDistance is negative
   */
  public Schedule(String scheduleId, List<Task> tasks, double maxDistance) {
    if (scheduleId == null || scheduleId.trim().isEmpty()) {
      throw new IllegalArgumentException("Schedule ID cannot be null or empty.");
    }
    if (tasks == null) {
      throw new IllegalArgumentException("Tasks list cannot be null.");
    }
    if (maxDistance < 0) {
      throw new IllegalArgumentException("Maximum distance cannot be negative.");
    }
    this.scheduleId = scheduleId;
    this.maxDistance = maxDistance;

    Comparator<Task> comparator = new TaskComparator();
    this.tasks = new PriorityQueue<Task>(comparator);  // queue of tasks in order of priority
    this.tasks.addAll(tasks);
  }

  /**
   * Creates a schedule by assigning available resources to tasks based
   * on their requirements and start times.
   *
   * @return a {@code Map<Task, List<Resource>>} where each schedulable task is mapped to
   *     the list of resources assigned to it,
   *     or an empty map if no tasks could be scheduled.
   *
   */
  public Map<Task, List<Resource>> createSchedule() {
    for (Task task : tasks) {
      // Store assigned resources for this task
      List<Resource> assignedResources = new ArrayList<>();
      // Track whether we can schedule this task
      boolean canSchedule = true;

      // Iterate over the required resource types for the task
      for (Map.Entry<ResourceType, Integer> entry : task.getResources().entrySet()) {
        ResourceType resourceType = entry.getKey();
        int requiredUnits = entry.getValue();

        if (requiredUnits > resourceType.countAvailableUnits(task.getStartTime())) {
          canSchedule = false;
          break;
        }

        List<Resource> availableResources = new ArrayList<>();

        for (int i = 0; i < requiredUnits; i++) {
          Resource resource = resourceType.findAvailableResource(task.getStartTime());
          if (resource != null
              && resourceType.getLocation().getDistance(task.getLocation()) <= maxDistance) {
            availableResources.add(resource);
            resource.assignUntil(task.getEndTime());
          } else {
            canSchedule = false;
            break;
          }
        }
        if (!canSchedule) {
          for (Resource resource : availableResources) {
            resource.release();  // Free up the resources since task cannot be scheduled right now
          }
          break;
        }
        assignedResources.addAll(availableResources);
      }
      // all resourceTypes available in required quantities
      if (canSchedule) {
        taskSchedule.put(task, assignedResources);
      }
    }
    return taskSchedule;
  }

  /**
   * Completes a task by removing it from allTasks and taskSchedule.
   */
  public void completeTask(Task task) {
    if (!tasks.contains(task)) {
      throw new IllegalArgumentException("Task not found in the tasks list.");
    }
    if (taskSchedule.containsKey(task)) {
      List<Resource> assignedResources = taskSchedule.get(task);
      for (Resource resource : assignedResources) {
        resource.release();
      }
      taskSchedule.remove(task);
    }
    tasks.remove(task);
  }

  /**
   * Receives task from network by adding given task to a priority queue.
   *
   * @param task the task to add to the queue
   * @throws IllegalArgumentException if task is null
   */
  public void receiveTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null.");
    }
    tasks.add(task);
  }

  public String getScheduleId() {
    return scheduleId;
  }
}