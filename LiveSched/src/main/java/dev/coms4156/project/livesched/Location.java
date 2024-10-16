package dev.coms4156.project.livesched;

/**
 * Represents a location using geographic coordinate system.
 * This class stores the latitude and longitude values of a location on a map.
 */
public class Location {
  private double latitude;
  private double longitude;

  /**
   * Constructs a new ResourceType object with the given parameters.
   *
   * @param latitude    the latitude
   * @param longitude   the longitude
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  public Location(double latitude, double longitude) {
    validateLatLong(latitude, longitude);
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Validates that the given latitude and longitude are within their valid ranges.
   *
   * @param latitude  the latitude
   * @param longitude the longitude
   * @throws IllegalArgumentException if the latitude or longitude is out of bounds
   */
  private void validateLatLong(double latitude, double longitude) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90.");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180.");
    }
  }

  public String getCoordinates() {
    return latitude + ", " + longitude;
  }

}