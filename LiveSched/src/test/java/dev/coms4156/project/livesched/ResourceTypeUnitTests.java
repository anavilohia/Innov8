package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
class ResourceTypeUnitTests {

  @BeforeEach
  void setupResourceTypeForTesting() {
    testResourceType = new ResourceType(testTypeName, testTotalUnits, testLatitude, testLongitude);
    testStartTime = LocalDateTime.now();
  }

  @Test
  void addResourceTest() {
    testResourceType.addResource();
    assertEquals(testResourceType.getTotalUnits(), testTotalUnits + 1, "Total number of resources should be 1 after adding one resource");

    testResourceType.addResource();
    testResourceType.addResource();
    assertEquals(testResourceType.getTotalUnits(), testTotalUnits + 3, "Total number of resources should be 3 after adding three resources");

    // Test adding after multiple additions
    testResourceType.addResource();
    assertEquals(testResourceType.getTotalUnits(), testTotalUnits + 4, "Total number of resources should be 4 after adding one more resource");
  }

  @Test
  void findAvailableResourceTest() {
    mockResource1 = mock(Resource.class);
    mockResource2 = mock(Resource.class);

    try {
      Field resourcesField = ResourceType.class.getDeclaredField("resources");
      resourcesField.setAccessible(true);
      Map<String, Resource> resources = (Map<String, Resource>) resourcesField.get(testResourceType);
      resources.put("Hospital 1", mockResource1);
      resources.put("Hospital 2", mockResource2);
    } catch (Exception e) {
      fail("Reflection failed to access or modify the 'resources' field: " + e.getMessage());
    }

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(true);
    Resource availableResource = testResourceType.findAvailableResource(testStartTime);
    assertEquals(mockResource2, availableResource, "The available resource should be 'Hospital 2'");

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(false);
    availableResource = testResourceType.findAvailableResource(testStartTime);
    assertNull(availableResource, "No resources should be available at the given time");

    // Edge case with no resources at all
    testResourceType = new ResourceType("Empty Hospital", 0, 0.0, 0.0);
    availableResource = testResourceType.findAvailableResource(testStartTime);
    assertNull(availableResource, "No resources should be available when none are added");

    // Test for null start time
    Exception exception = assertThrows(IllegalArgumentException.class, () -> testResourceType.findAvailableResource(null));
    assertEquals("Start time cannot be null.", exception.getMessage(), "The exception message should indicate that start time cannot be null.");
  }

  @Test
  void countAvailableUnitsTest() {
    mockResource1 = mock(Resource.class);
    mockResource2 = mock(Resource.class);
    mockResource3 = mock(Resource.class);

    try {
      Field resourcesField = ResourceType.class.getDeclaredField("resources");
      resourcesField.setAccessible(true);
      Map<String, Resource> resources = (Map<String, Resource>) resourcesField.get(testResourceType);
      resources.put("Hospital 1", mockResource1);
      resources.put("Hospital 2", mockResource2);
      resources.put("Hospital 3", mockResource3);
    } catch (Exception e) {
      fail("Reflection failed to access or modify the 'resources' field: " + e.getMessage());
    }

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(false);
    when(mockResource3.isAvailableAt(testStartTime)).thenReturn(false);
    int availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(0, availableResourceCount, "There should be no available resources");

    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(true);
    availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(1, availableResourceCount, "One resource should be available");

    when(mockResource2.isAvailableAt(testStartTime)).thenReturn(true);
    when(mockResource3.isAvailableAt(testStartTime)).thenReturn(true);
    availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(3, availableResourceCount, "Three resources should be available");

    // Edge case with only 1 resource available
    when(mockResource1.isAvailableAt(testStartTime)).thenReturn(false);
    availableResourceCount = testResourceType.countAvailableUnits(testStartTime);
    assertEquals(2, availableResourceCount, "Two resources should be available after one is unavailable");
  }

  @Test
  void updateLocationTest() {
    assertEquals(testResourceType.getLocation().getCoordinates(), "-90.0, 55.65", "Initial coordinates should be -90.0, 55.65");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      testResourceType.updateLocation(-91.0, testLongitude);
    });
    assertEquals("Latitude must be between -90 and 90.", exception.getMessage());

    exception = assertThrows(IllegalArgumentException.class, () -> {
      testResourceType.updateLocation(testLatitude, 300.2);
    });
    assertEquals("Longitude must be between -180 and 180.", exception.getMessage());

    testResourceType.updateLocation(90.0, -180.0);
    assertEquals(testResourceType.getLocation().getCoordinates(), "90.0, -180.0", "Coordinates should be updated correctly to the boundary values");

    testResourceType.updateLocation(0.0, 0.0);
    assertEquals(testResourceType.getLocation().getCoordinates(), "0.0, 0.0", "Coordinates should be updated correctly to (0.0, 0.0)");
  }

  @Test
  void toStringTest() {
    String expectedResult = testTypeName;
    assertEquals(testResourceType.toString(), testTypeName, "String representation of test resource should be " + expectedResult);
  }

  // Instances for testing
  public static ResourceType testResourceType;
  private Resource mockResource1;
  private Resource mockResource2;
  private Resource mockResource3;
  final String testTypeName = "Hospital";
  final int testTotalUnits = 0;
  final double testLatitude = -90.0;
  final double testLongitude = 55.65;
  LocalDateTime testStartTime;
}
