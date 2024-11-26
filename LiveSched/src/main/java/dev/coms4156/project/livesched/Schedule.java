package dev.coms4156.project.livesched;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates scheduling given a list of tasks.
 * This class creates a schedule that pairs tasks with resourceTypes and
 * updates their values accordingly
 */
public class Schedule implements Serializable {
  @Serial
  private static final long serialVersionUID = 1005L;

  private Map<Task, List<Resource>> taskSchedule;

  /**
   * Constructs a new Schedule object.
   */
  public Schedule() {
    taskSchedule = new LinkedHashMap<>();
  }

  /**
   * Updates the schedule by assigning available resources to tasks based
   * on their requirements and start times.
   *
   * @param tasks    The list of tasks to schedule.
   * @param maxDistance The maximum distance between tasks and resources.
   * @return a {@code Map<Task, List<Resource>>} where each schedulable task is mapped to
   *        the list of resources assigned to it, or an empty map if no tasks could be scheduled.
   * @throws IllegalArgumentException if tasks is null or maxDistance is negative
   */
  public Map<Task, List<Resource>> updateSchedule(List<Task> tasks, double maxDistance) {
    if (tasks == null) {
      throw new IllegalArgumentException("Tasks list cannot be null.");
    }
    if (maxDistance < 0) {
      throw new IllegalArgumentException("Maximum distance cannot be negative.");
    }

    // Sort tasks by priority
    tasks.sort(new TaskComparator());

    for (Task task : tasks) {
      // Skip tasks that are already scheduled
      if (taskSchedule.containsKey(task)) {
        continue;
      }

      // Skip tasks with no resources required
      if (task.getResources() == null || task.getResources().isEmpty()) {
        continue;
      }

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
   * Completes a task by removing it from the taskSchedule.
   *
   * @throws IllegalArgumentException if task is null
   */
  public void unscheduleTask(Task task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null.");
    }
    if (taskSchedule.containsKey(task)) {
      List<Resource> assignedResources = taskSchedule.get(task);
      for (Resource resource : assignedResources) {
        resource.release();
      }
      taskSchedule.remove(task);
    }
  }

  public Map<Task, List<Resource>> getTaskSchedule() {
    return taskSchedule;
  }
}