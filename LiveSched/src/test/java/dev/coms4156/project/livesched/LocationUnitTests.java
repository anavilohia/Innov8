package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the Location class.
 */
@SpringBootTest
@ContextConfiguration
public class LocationUnitTests {

  double validLatitude = 40.842770;
  double validLongitude = -73.942863;

  /**
   * Test for the constructor in Location class with valid latitude and longitude.
   */
  @Test
  void constructorValidTest() {
    assertDoesNotThrow(() -> new Location(validLatitude, validLongitude),
            "Location constructor should not throw an exception with valid parameters.");

    Location location = new Location(validLatitude, validLongitude);
    assertEquals(validLatitude + ", " + validLongitude, location.getCoordinates(),
            "Location should be 40.842770, -73.942863.");
  }

  /**
   * Test for the constructor in Location class with invalid latitude.
   */
  @Test
  void constructorInvalidLatitudeTest() {
    assertThrows(IllegalArgumentException.class, () -> new Location(-90.5, validLongitude),
            "Latitude less than -90 should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(90.3, validLongitude),
            "Latitude greater than 90 should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(Double.NaN, validLongitude),
            "Latitude NaN should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(Double.POSITIVE_INFINITY, validLongitude),
            "Latitude positive infinity should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(Double.NEGATIVE_INFINITY, validLongitude),
            "Latitude negative infinity should throw an exception.");
  }

  /**
   * Test for the constructor in Location class with invalid longitude.
   */
  @Test
  void constructorInvalidLongitudeTest() {
    assertThrows(IllegalArgumentException.class, () -> new Location(validLatitude, -180.9),
            "Longitude less than -180 should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(validLatitude, 180.1),
            "Longitude greater than 180 should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(validLatitude, Double.NaN),
            "Longitude NaN should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(validLatitude, Double.POSITIVE_INFINITY),
            "Longitude positive infinity should throw an exception.");

    assertThrows(IllegalArgumentException.class, () -> new Location(validLatitude, Double.NEGATIVE_INFINITY),
            "Longitude negative infinity should throw an exception.");
  }

  /**
   * Test for getCoordinates method in Location class.
   */
  @Test
  void getCoordinatesTest() {
    Location location = new Location(34.05, -118.25);
    assertEquals("34.05, -118.25", location.getCoordinates(),
            "Location should be 34.05, -118.25.");
  }

  @Test
  void testGetDistanceSameLocation() {
    Location loc1 = new Location(40.7128, -74.0060);
    double distance = loc1.getDistance(loc1);
    assertEquals(0.0, distance, 0.01, "Distance between the same location should be 0.");
  }

  @Test
  void testGetDistanceDifferentLocations() {
    Location loc1 = new Location(40.7128, -74.0060);
    Location loc2 = new Location(34.0522, -118.2437);

    double distance = loc1.getDistance(loc2);
    assertEquals(3935.7462546097213, distance, 1.0,
            "Distance between New York and Los Angeles should be approximately 3940 km.");
  }

  @Test
  void testGetLatitude() {
    Location location = new Location(40.7128, -74.0060);
    assertEquals(40.7128, location.getLatitude(), 0.0001,
            "getLatitude should return the latitude value set during initialization.");
  }

  @Test
  void testGetLongitude() {
    Location location = new Location(40.7128, -74.0060);
    assertEquals(-74.0060, location.getLongitude(), 0.0001,
            "getLongitude should return the longitude value set during initialization.");
  }
}