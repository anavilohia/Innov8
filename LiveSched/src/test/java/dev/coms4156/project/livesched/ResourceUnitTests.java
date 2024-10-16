// package dev.coms4156.project.livesched;
//
// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// import java.time.LocalDateTime;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ContextConfiguration;
//
// /**
//  * Unit tests to be used for Resource class.
//  */
// @SpringBootTest
// @ContextConfiguration
// class ResourceUnitTests {
//
//   /**
//    * Set up to be run before all tests.
//    */
//   @BeforeEach
//   void setupResourceForTesting() {
//     testResource = new Resource(testResourceId, testLatitude, testLongitude);
//     testDateTime = LocalDateTime.now().plusHours(3);
//   }
//
//   /**
//    * Test for Resource class isAvailableAt method.
//    */
//   @Test
//   void isAvailableAtTest() {
//     assertThrows(IllegalArgumentException.class, () -> testResource.isAvailableAt(null),
//         "Time parameter cannot be set to null for isAvailableAt method");
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should be available at current time when newly created");
//     assertFalse(testResource.isAvailableAt(LocalDateTime.now().minusHours(1)),
//         "Resource should not be available at past time");
//
//     testResource.assignUntil(testDateTime);
//     assertFalse(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should not be available at current time after being assigned");
//     assertTrue(testResource.isAvailableAt(testDateTime.plusHours(1)),
//         "Resource should be available at future time after assigned task's end time");
//     assertTrue(testResource.isAvailableAt(testDateTime),
//         "Resource should be available at assigned task's end time");
//   }
//
//   /**
//    * Test for Resource class assign method.
//    */
//   @Test
//   void assignUntilTest() {
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should be available at current time when newly created");
//
//     testResource.assignUntil(testDateTime);
//     assertFalse(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should not be available at current time after assign");
//     assertEquals(testResource.getAvailableFrom(), testDateTime,
//         "Resource availableFrom time should be " + testDateTime.toString());
//
//     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//       testResource.assignUntil(null);
//     }, "Resource cannot be assigned until null");
//     assertEquals("Task end time cannot be null.", exception.getMessage(),
//         "Exception message should match for null task end time.");
//
//     exception = assertThrows(IllegalArgumentException.class, () -> {
//       testResource.assignUntil(LocalDateTime.now().minusHours(1));
//     }, "Resource cannot be assigned past time");
//     assertEquals("Task end time cannot be in the past.", exception.getMessage(),
//         "Exception message should match for past task end time.");
//   }
//
//   /**
//    * Test for Resource class release method.
//    */
//   @Test
//   void releaseTest() {
//     testResource.assignUntil(testDateTime);
//     assertFalse(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should not be available at current time after assign");
//
//     testResource.release();
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should be available at current time after release");
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now().plusHours(2)),
//         "Resource should be available at future time after release");
//   }
//
//   /**
//    * Test for Resource class updateLocation method.
//    */
//   @Test
//   void updateLocationTest() {
//     String expectedResult = testLatitude + ", " + testLongitude;
//     assertEquals(testResource.getLocation(), expectedResult,
//         "Resource location should be " + expectedResult + " before update");
//
//     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//       testResource.updateLocation(-91.0, testLongitude);
//     }, "Resource cannot be assigned invalid latitude");
//     assertEquals("Latitude must be between -90 and 90.", exception.getMessage(),
//         "Exception message should match for latitude out of bounds");
//
//     exception = assertThrows(IllegalArgumentException.class, () -> {
//       testResource.updateLocation(testLatitude, 300.2);
//     }, "Resource cannot be assigned invalid longitude");
//     assertEquals("Longitude must be between -180 and 180.", exception.getMessage(),
//         "Exception message should match for longitude out of bounds");
//
//
//     testResource.updateLocation(-90.0, 145.34);
//     assertEquals(testResource.getLocation(), "-90.0, 145.34",
//         "Resource location should be -90.0, 145.34 after update");
//   }
//
//   /**
//    * Test for Resource class getResourceId method.
//    */
//   @Test
//   void getResourceIdTest() {
//     assertEquals(testResource.getResourceId(), testResourceId,
//         "Resource ID should be " + testResourceId);
//     assertNotEquals(testResource.getResourceId(), testResourceId.toLowerCase(),
//         "Resource ID should not be " + testResourceId.toLowerCase());
//     assertNotEquals(testResource.getResourceId(), "",
//         "Resource ID should not be empty string");
//   }
//
//   /**
//    * Test for Resource class getAvailableFrom method.
//    */
//   @Test
//   void getAvailableFromTest() {
//     testResource.assignUntil(testDateTime);
//     assertEquals(testResource.getAvailableFrom(), testDateTime,
//         "Available from should be " + testDateTime.toString() + " after assign");
//   }
//
//   /**
//    * Test for Resource class setAvailableFrom method.
//    */
//   @Test
//   void setAvailableFromTest() {
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should be available at current time when newly created");
//
//     testResource.setAvailableFrom(testDateTime);
//     assertFalse(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should not be available at current time after setAvailableFrom to future");
//     assertEquals(testResource.getAvailableFrom(), testDateTime,
//         "Resource availableFrom time should be " + testDateTime.toString());
//
//     IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//       testResource.setAvailableFrom(null);
//     }, "Resource cannot be set available from null");
//     assertEquals("Time available from cannot be null.", exception.getMessage(),
//         "Exception message should match for null parameter");
//
//     LocalDateTime currentTime = LocalDateTime.now();
//     testResource.setAvailableFrom(currentTime.minusHours(1));
//     assertTrue(testResource.isAvailableAt(LocalDateTime.now()),
//         "Resource should be available at current time after setAvailableFrom to past");
//     assertEquals(testResource.getAvailableFrom(), currentTime.minusHours(1),
//         "Resource availableFrom time should be " + currentTime.minusHours(1).toString());
//   }
//
//   /**
//    * Test for Resource class getLocation method.
//    */
//   @Test
//   void getLocationTest() {
//     String expectedResult = testLatitude + ", " + testLongitude;
//     assertEquals(testResource.getLocation(), expectedResult,
//         "Resource location should be " + expectedResult);
//
//     expectedResult = testLongitude + ", " + testLatitude;
//     assertNotEquals(testResource.getLocation(), expectedResult,
//         "Resource location should not be " + expectedResult);
//
//     assertNotEquals(testResource.getLocation(), "",
//         "Resource location should not be empty string");
//   }
//
//   /**
//    * These instances are used for testing.
//    */
//   public static Resource testResource;
//   final String testResourceId = "Test Resource";
//   final double testLatitude = 80.0;
//   final double testLongitude = 55.65;
//   LocalDateTime testDateTime;
// }
//
