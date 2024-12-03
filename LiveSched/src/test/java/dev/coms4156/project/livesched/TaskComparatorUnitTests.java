package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests to be used for TaskComparator class.
 */
@SpringBootTest
@ContextConfiguration
class TaskComparatorUnitTests {

  private LocalDateTime startTime = LocalDateTime.now();
  private Task testTask1 = new Task("1", "testTask1", new HashMap<>(), 3,
          startTime, startTime.plusHours(1), 40.81, -73.96);
  private Task testTask2 = new Task("2", "testTask2", new HashMap<>(), 3,
          startTime, startTime.plusHours(2), 40.81, -73.96);
  private Task testTask3 = new Task("3", "testTask3", new HashMap<>(), 1,
          startTime, startTime.plusHours(2), 40.81, -73.96);
  private TaskComparator testTaskComparator = new TaskComparator();

  /**
   * Test for compare method in TaskComparator class.
   */
  @Test
  void compareTest() {
    int actualResult = testTaskComparator.compare(testTask1, testTask2);
    int expectedResult = 0;
    assertEquals(expectedResult, actualResult, "Result should be 0");

    actualResult = testTaskComparator.compare(testTask1, testTask3);
    expectedResult = 1;
    assertEquals(expectedResult, actualResult, "Result should be 1");

    actualResult = testTaskComparator.compare(testTask3, testTask1);
    expectedResult = -1;
    assertEquals(expectedResult, actualResult, "Result should be -1");
  }
  
  @Test
  void compareInvalidTasksTest() {
    assertThrows(IllegalArgumentException.class,
            () -> testTaskComparator.compare(null, testTask2),
            "Tasks should be non-null.");
    assertThrows(IllegalArgumentException.class,
            () -> testTaskComparator.compare(testTask1, null),
            "Tasks should be non-null.");
    assertThrows(IllegalArgumentException.class,
            () -> testTaskComparator.compare(null, null),
            "Tasks should be non-null.");
  }
}