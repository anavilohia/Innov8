package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  }

  /**
   * Test for the constructor in Location class with boundary values of latitude and longitude.
   */
  @Test
  void constructorBoundaryTest() {
    assertDoesNotThrow(() -> new Location(-90.0, validLongitude),
        "Location constructor should not throw an exception for latitude at lower boundary.");

    assertDoesNotThrow(() -> new Location(90.0, validLongitude),
        "Location constructor should not throw an exception for latitude at upper boundary.");

    assertDoesNotThrow(() -> new Location(validLatitude, -180),
        "Location constructor should not throw an exception for longitude at lower boundary.");

    assertDoesNotThrow(() -> new Location(validLatitude, 180),
        "Location constructor should not throw an exception for longitude at upper boundary.");
  }

  /**
   * Test for getCoordinates method in Location class.
   */
  @Test
  void getCoordinatesTest() {
    Location location = new Location(34.05, -118.25); // Different, valid location
    assertEquals("34.05, -118.25", location.getCoordinates(),
        "Location should be 34.05, -118.25.");
  }
}
