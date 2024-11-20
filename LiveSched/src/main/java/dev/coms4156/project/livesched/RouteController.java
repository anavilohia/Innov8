package dev.coms4156.project.livesched;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class contains all the API routes for the system.
 */
@RestController
public class RouteController {

  /**
   * Redirects to the homepage.
   *
   * @return A String containing the name of the html file to be loaded.
   */
  @GetMapping({"/", "/index", "/home"})
  public String index() {
    return "Welcome, in order to make an API call direct your browser or Postman to an endpoint "
            + "\n\n This can be done using the following format: \n\n http:127.0.0"
            + ".1:8080/endpoint?arg=value";
  }

  /**
   * Returns the details of all tasks in the database.
   *
   * @return A {@code ResponseEntity} object containing either the details of the Tasks and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @GetMapping(value = "/retrieveTasks", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> retrieveTasks() {
    try {
      List<Task> taskList;
      taskList = LiveSchedApplication.myFileDatabase.getAllTasks();
      String res = "";
      for (Task task : taskList) {
        res = res + task.toString();
      }
      if (res.isEmpty()) {
        return new ResponseEntity<>("Tasks Not Found", HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(res, HttpStatus.OK);
      }

    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Returns the details of a specified task in the database.
   *
   * @param taskId     A {@code String} representing the task the user wishes
   *                   to retrieve.
   *
   * @return A {@code ResponseEntity} object containing either the details of the Task and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @GetMapping(value = "/retrieveTask", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> retrieveTask(@RequestParam(value = "taskId") String taskId) {
    try {
      Task task;
      task = LiveSchedApplication.myFileDatabase.getTaskById(taskId);
      if (task == null) {
        return new ResponseEntity<>("Task Not Found", HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(task.toString(), HttpStatus.OK);
      }

    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Returns the details of all resource types in the database.
   *
   * @return A {@code ResponseEntity} object containing either the details of the ResourceTypes and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @GetMapping(value = "/retrieveResourceTypes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> retrieveResourceTypes() {
    try {
      List<ResourceType> resourceTypeList;
      resourceTypeList = LiveSchedApplication.myFileDatabase.getAllResourceTypes();
      String res = "";
      for (ResourceType resourceType : resourceTypeList) {
        res = res + resourceType.toString();
      }
      if (res.isEmpty()) {
        return new ResponseEntity<>("ResourceTypes Not Found", HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(res, HttpStatus.OK);
      }

    } catch (Exception e) {
      return handleException(e);
    }
  }

  // to be re-add in Iteration 2 after testing

  // /**
  //  * Returns the details of resource types a task needs.
  //  *
  //  * @param taskId A {@code String} representing the task of the resources the user wishes
  //  *                 to retrieve.
  //  *
  //  * @return A {@code ResponseEntity} object containing
  //  either the details of the ResourceTypes and
  //  *         an HTTP 200 response or, an appropriate message indicating the proper response.
  //  */
  // @GetMapping(value = "/retrieveResourcesFromTask", produces = MediaType.APPLICATION_JSON_VALUE)
  // public ResponseEntity<?> retrieveResourcesFromTask(
  //         @RequestParam(value = "taskId") String taskId) {
  //   try {
  //     boolean doesTaskExist = retrieveTask(taskId).getStatusCode() == HttpStatus.OK;
  //     if (doesTaskExist) {
  //       List<ResourceType> resourceTypeList;
  //       Task task = LiveSchedApplication.myFileDatabase.getTaskById(taskId);
  //       Map<ResourceType, Integer> resourcesNeeded = task.getResources();
  //       resourceTypeList = LiveSchedApplication.myFileDatabase.getAllResourceTypes();
  //       String res = "";
  //       for (ResourceType resourceType : resourceTypeList) {
  //         if (resourcesNeeded.containsKey(resourceType)) {
  //           res = res + resourceType.toString();
  //         }
  //       }
  //       if (res.isEmpty()) {
  //         return new ResponseEntity<>("ResourceType Not Found", HttpStatus.NOT_FOUND);
  //       } else {
  //         return new ResponseEntity<>(res, HttpStatus.OK);
  //       }
  //     }
  //     return new ResponseEntity<>("Task Not Found", HttpStatus.NOT_FOUND);
  //   } catch (Exception e) {
  //     return handleException(e);
  //   }
  // }

  /**
   * Attempts to add a task to the database.
   *
   * @param priority       A {@code int} representing the priority of the new task.
   * @param startTime      A {@code String} representing the start time of the new task.
   * @param endTime        A {@code String} representing the end time of the new task.
   * @param latitude       A {@code double} representing the latitude of the new task.
   * @param longitude      A {@code double} representing the longitude of the new task.
   *
   * @return               A {@code ResponseEntity} object containing an HTTP 200
   *                       response with an appropriate message or the proper status
   *                       code in tune with what has happened.
   */
  @PatchMapping(value = "/addTask", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addTask(@RequestParam(value = "priority") int priority,
                                          @RequestParam(value = "startTime") String startTime,
                                          @RequestParam(value = "endTime") String endTime,
                                          @RequestParam(value = "latitude") double latitude,
                                          @RequestParam(value = "longitude") double longitude) {
    try {
      String taskId = String.valueOf(LiveSchedApplication.myFileDatabase.getAllTasks().size() + 1);
      Map<ResourceType, Integer> resourceTypeList = new HashMap<>();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      LocalDateTime startTimeFormatted = LocalDateTime.parse(startTime, formatter);
      LocalDateTime endTimeFormatted = LocalDateTime.parse(endTime, formatter);
      Task newTask = new Task(taskId, resourceTypeList, priority,
              startTimeFormatted, endTimeFormatted, latitude, longitude);
      LiveSchedApplication.myFileDatabase.addTask(newTask);
      return new ResponseEntity<>("Attribute was updated successfully.", HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Attempts to delete a task from the database.
   *
   * @param taskId           A {@code String} representing the taskId of task to be deleted.
   *
   * @return               A {@code ResponseEntity} object containing an HTTP 200
   *                       response with an appropriate message or the proper status
   *                       code in tune with what has happened.
   */
  @DeleteMapping(value = "/deleteTask", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteTask(@RequestParam(value = "taskId") String taskId) {
    try {
      Task task;
      task = LiveSchedApplication.myFileDatabase.getTaskById(taskId);
      if (task == null) {
        return new ResponseEntity<>("Task Not Found", HttpStatus.NOT_FOUND);
      } else {
        LiveSchedApplication.myFileDatabase.deleteTask(task);
        return new ResponseEntity<>(taskId + " successfully deleted", HttpStatus.OK);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Attempts to add a resource type to the database.
   *
   * @param typeName       A {@code String} representing the name of the new resource type.
   * @param totalUnits      A {@code int} representing the number of units of its resources.
   * @param latitude        A {@code double} representing the latitude of the new resource type.
   * @param longitude      A {@code double} representing the longitude of the new resource type.
   *
   * @return               A {@code ResponseEntity} object containing an HTTP 200
   *                       response with an appropriate message or the proper status
   *                       code in tune with what has happened.
   */
  @PatchMapping(value = "/addResourceType", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addResourceType(@RequestParam(value = "typeName") String typeName,
                                          @RequestParam(value = "totalUnits") int totalUnits,
                                          @RequestParam(value = "latitude") double latitude,
                                          @RequestParam(value = "longitude") double longitude) {
    try {
      ResourceType newResourceType = new ResourceType(typeName, totalUnits, latitude, longitude);
      LiveSchedApplication.myFileDatabase.addResourceType(newResourceType);
      return new ResponseEntity<>("Attribute was updated successfully.", HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Attempts to modify resource type for a specified task to the database.
   *
   * @param taskId          A {@code String} representing the task
   *                        the client wants to modify the resource type for.
   * @param typeName        A {@code String} representing the resource type to modify.
   * @param quantity        A {@code int} representing quantity of resource types to set.
   *
   * @return               A {@code ResponseEntity} object containing an HTTP 200
   *                       response with an appropriate message or the proper status
   *                       code in tune with what has happened.
   */
  @PatchMapping(value = "/modifyResourceType", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> modifyResourceType(@RequestParam(value = "taskId") String taskId,
                                           @RequestParam(value = "typeName") String typeName,
                                           @RequestParam(value = "quantity") int quantity) {
    try {
      boolean doesTaskExist = retrieveTask(taskId).getStatusCode() == HttpStatus.OK;
      if (doesTaskExist) {
        List<ResourceType> resourceTypeList;
        resourceTypeList = LiveSchedApplication.myFileDatabase.getAllResourceTypes();
        Task task = LiveSchedApplication.myFileDatabase.getTaskById(taskId);
        for (ResourceType resourceType : resourceTypeList) {
          if (resourceType.getTypeName().equals(typeName)) {
            task.updateResource(resourceType, quantity);
            return new ResponseEntity<>("Attribute was updated successfully.", HttpStatus.OK);
          }
        }
        return new ResponseEntity<>("ResourceType Not Found", HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>("Task Not Found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Attempts to delete a resourceType from the database.
   *
   * @param typeName        A {@code String} representing the resource type to delete.
   *
   * @return               A {@code ResponseEntity} object containing an HTTP 200
   *                       response with an appropriate message or the proper status
   *                       code in tune with what has happened.
   */
  @DeleteMapping(value = "/deleteResourceType", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteResourceType(@RequestParam(value = "typeName") String typeName) {
    try {
      List<Task> tasks = LiveSchedApplication.myFileDatabase.getAllTasks();
      List<ResourceType> resourceTypeList =
          LiveSchedApplication.myFileDatabase.getAllResourceTypes();
      for (ResourceType resourceType : resourceTypeList) {
        if (resourceType.getTypeName().equals(typeName)) {
          for (Task task : tasks) {
            if (task.getResources().containsKey(resourceType)) {
              return new ResponseEntity<>("Cannot delete a resourceType currently in use",
                  HttpStatus.BAD_REQUEST);
            }
          }
          LiveSchedApplication.myFileDatabase.deleteResourceType(resourceType);
          return new ResponseEntity<>(typeName + " successfully deleted", HttpStatus.OK);
        }
      }
      return new ResponseEntity<>("ResourceType Not Found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  private ResponseEntity<?> handleException(Exception e) {
    System.out.println(e.toString());
    return new ResponseEntity<>("An Error has occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
