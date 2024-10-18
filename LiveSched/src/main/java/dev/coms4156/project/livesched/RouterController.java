package dev.coms4156.project.livesched;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        res.append(task.toString());
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
  public ResponseEntity<?> retrieveTasks(@RequestParam(value = "taskId") String taskId) {
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
        res.append(resourceType.toString());
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

  /**
   * Returns the details of resource types a task needs.
   *
   * @param taskId A {@code String} representing the task of the resources the user wishes
   *                 to retrieve.
   *
   * @return A {@code ResponseEntity} object containing either the details of the ResourceTypes and
   *         an HTTP 200 response or, an appropriate message indicating the proper response.
   */
  @GetMapping(value = "/retrieveResourcesFromTask", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> retrieveResourcesFromTask(@RequestParam(value = "resourceId") String taskId) {
    try {
      boolean doesTaskExist = retrieveTask(taskId).getStatusCode() == HttpStatus.OK;
      if (doesTaskExist) {
        List<ResourceType> resourceTypeList;
        Task task = LiveSchedApplication.myFileDatabase.getTaskById(taskId);
        Map<ResourceType, Integer> resourcesNeeded = task.getResources();
        resourceTypeList = LiveSchedApplication.myFileDatabase.getAllResourceTypes();
        String res = "";
        for (ResourceType resourceType : resourceTypeList) {
          if (resourcesNeeded.containsKey(resourceType)) {
            res.append(resourceType.toString());
          }
        }
        if (res.isEmpty()) {
          return new ResponseEntity<>("ResourceType Not Found", HttpStatus.NOT_FOUND);
        } else {
          return return new ResponseEntity<>(res, HttpStatus.OK);
        }
      }
      return new ResponseEntity<>("Task Not Found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return handleException(e);
    }
  }

}