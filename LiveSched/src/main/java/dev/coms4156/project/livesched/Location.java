package dev.coms4156.project.livesched;

/**
 * Represents a location using a geographic coordinate system.
 * This class stores the latitude and longitude values of a location on a map.
 */
public class Location {
  private double latitude;
  private double longitude;

  /**
   * Constructs a new Location object with the given parameters.
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

  /**
   * Calculates a distance between this location and given location,
   * using Haversine method.
   *
   * @param location  the location
   * @return distance between two locations in kilometers (km)
   */
  public double getDistance(Location location) {
    final int radius = 6371;  // Radius of Earth in km
    double lat1 = Math.toRadians(this.latitude);
    double lat2 = Math.toRadians(location.getLatitude());
    double lng1 = Math.toRadians(this.longitude);
    double lng2 = Math.toRadians(location.getLongitude());
    return Math.acos(Math.sin(lat1) * Math.sin(lat2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng1 - lng2)) * radius;
  }

  public String getCoordinates() {
    return latitude + ", " + longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

}
