package dev.coms4156.project.livesched;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator class for sorting Task by its priority.
 */
public class TaskComparator implements Comparator<Task>, Serializable {
  @Serial
  private static final long serialVersionUID = 1006L;

  @Override
  public int compare(Task x, Task y) {
    if (x.getPriority() < y.getPriority()) {
      return -1;
    }
    if (x.getPriority() > y.getPriority()) {
      return 1;
    }
    return 0;
  }
}