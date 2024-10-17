package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests to be used for ResourceType class.
 */
@SpringBootTest
@ContextConfiguration
class ResourceTypeUnitTests {

  /**
   * Set up to be run before all tests.
   */
  @BeforeEach
  void setupResourceTypeForTesting() {

    testResourceType = new ResourceType(testTypeName, testTotalUnits, testLatitude, testLongitude);
    testStartTime = LocalDateTime.now();
  }

  /**
   * Test for ResourceType class addResource method.
   */
  @Test
  void addResourceTest() {
    assertEquals(testResourceType.getTotalUnits(), testTotalUnits,
        "Total number of resources should be " + testTotalUnits + " before add");

    testResourceType.addResource();
    int expectedNewTotal = testTotalUnits + 1;
    assertEquals(testResourceType.getTotalUnits(), expectedNewTotal,
        "Total number of resources should be " + expectedNewTotal + " after adding one resource");

    testResourceType.addResource();
    testResourceType.addResource();
    testResourceType.addResource();
    expectedNewTotal = expectedNewTotal + 3;
    assertEquals(testResourceType.getTotalUnits(), expectedNewTotal,
        "Total number of resources should be "
            + expectedNewTotal
            + " after adding three resources");
  }

  /**
   * Test for ResourceType class findAvailableResource method.
   */
  @Test
  void findAvailableResourceTest() {

    // Create mock resources
    mockResource1 = mock(Resource.class);
    mockResource2 = mock(Resource.class);

    try {
      // Inject mock resources into the private 'resources' field
      Field resourcesField = ResourceType.class.getDeclaredField("resources");
      resourcesField.setAccessible(true); // Make the private field accessible
      Map<String, Resource> resources =
          (Map<String, Resource>) resourcesField.get(testResourceType);
      resources.put("Hospital 1", mockResource1);
      resources.put("Hospital 2", mockResource2);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
      fail("Reflection failed to access or modify the 'resources' field: " + e.getMessage());
    }

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(true);
    Resource availableResource = testResourceType.findAvailableResource(testStartTime);
    assertEquals(mockResource2, availableResource,
        "The available resource should be 'Hospital 2'");

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(false);
    availableResource = testResourceType.findAvailableResource(testStartTime);
    assertNull(availableResource,
        "No resources should be available at the given time");

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        testResourceType.findAvailableResource(null));
    assertEquals("Start time cannot be null.", exception.getMessage(),
        "The exception message should indicate that start time cannot be null.");
  }

  /**
   * Test for ResourceType class countAvailableUnits method.
   */
  @Test
  void countAvailableUnitsTest() {

    // Create mock resources
    mockResource1 = mock(Resource.class);
    mockResource2 = mock(Resource.class);
    mockResource3 = mock(Resource.class);

    try {
      // Inject mock resources into the private 'resources' field
      Field resourcesField = ResourceType.class.getDeclaredField("resources");
      resourcesField.setAccessible(true); // Make the private field accessible
      Map<String, Resource> resources =
          (Map<String, Resource>) resourcesField.get(testResourceType);
      resources.put("Hospital 1", mockResource1);
      resources.put("Hospital 2", mockResource2);
      resources.put("Hospital 3", mockResource2);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
      fail("Reflection failed to access or modify the 'resources' field: " + e.getMessage());
    }

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(false);
    int availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(0, availableResourceCount,
        "The available resource should be " + 0);

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(true);
    availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(1, availableResourceCount,
        "The available resource should be " + 1);

    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(true);
    when(mockResource3.isAvailableAt(testStartTime)).thenReturn(true);
    availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(3, availableResourceCount,
        "The available resource should be " + 3);
  }

  /**
   * Test for ResourceType class getTotalUnits method.
   */
  @Test
  void getTotalUnitsTest() {
    assertEquals(testResourceType.getTotalUnits(), testTotalUnits,
        "Total number of resources should be " + testTotalUnits + " before add");

    testResourceType.addResource();
    testResourceType.addResource();
    testResourceType.addResource();
    int expectedNewTotal = testTotalUnits + 3;
    assertEquals(testResourceType.getTotalUnits(), expectedNewTotal,
        "Total number of resources should be "
            + expectedNewTotal
            + " after adding three resources");
  }

  /**
   * Test for ResourceType class getLocation method.
   */
  @Test
  void getLocationTest() {
    String expectedResult = testLatitude + ", " + testLongitude;
    assertEquals(testResourceType.getLocation(), expectedResult,
        "Resource location should be " + expectedResult);

    expectedResult = testLongitude + ", " + testLatitude;
    assertNotEquals(testResourceType.getLocation(), expectedResult,
        "Resource location should not be " + expectedResult);

    assertNotEquals(testResourceType.getLocation(), "",
        "Resource location should not be empty string");
  }

  /**
   * Test for ResourceType class updateLocation method.
   */
  @Test
  void updateLocationTest() {
    String expectedResult = testLatitude + ", " + testLongitude;
    assertEquals(testResourceType.getLocation(), expectedResult,
        "Resource location should be " + expectedResult + " before update");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      testResourceType.updateLocation(-91.0, testLongitude);
    }, "Resource cannot be assigned invalid latitude");
    assertEquals("Latitude must be between -90 and 90.", exception.getMessage(),
        "Exception message should match for latitude out of bounds");

    exception = assertThrows(IllegalArgumentException.class, () -> {
      testResourceType.updateLocation(testLatitude, 300.2);
    }, "Resource cannot be assigned invalid longitude");
    assertEquals("Longitude must be between -180 and 180.", exception.getMessage(),
        "Exception message should match for longitude out of bounds");

    testResourceType.updateLocation(-90.0, 145.34);
    assertEquals(testResourceType.getLocation(), "-90.0, 145.34",
        "Resource location should be -90.0, 145.34 after update");
  }




  /**
   * These instances are used for testing.
   */
  public static ResourceType testResourceType;
  private Resource mockResource1;
  private Resource mockResource2;
  private Resource mockResource3;
  final String testTypeName = "Hospital";
  final int testTotalUnits = 0;
  final double testLatitude = 80.0;
  final double testLongitude = 55.65;
  LocalDateTime testStartTime;
}

