# COMS 4156: Innov8 Team Project - Service

This is the GitHub repository for the **service portion** of the team project for group 'Innov8' associated with COMS 4156 W Advanced Software Engineering at Columbia University.

## Viewing the Client App Repository

Please use the following link to view the repository relevant to the client app: https://github.com/jonghyun-joann-lee/Innov8-Client

## Team name

Innov8

## Team members

Anavi Lohia (al3750) \
Jane Lim (jl6094) \
Jonghyun Lee (jl6509) \
Jungyun Kim (jk4661)

Team member conrtibutions can be found on the JIRA project at this link:
[Innov8 JIRA - List of tasks](https://innov8-columbia.atlassian.net/jira/software/projects/SCRUM/list?atlOrigin=eyJpIjoiZDMwOWM1ZTQyMGQxNGRhNjg1MzUxM2Y0Mzc5YmNlZDEiLCJwIjoiaiJ9)

## Project

LiveSched:

A versatile scheduling and resource management service tailored to handle the complex needs of diverse industries, including but not limited to healthcare, manufacturing, and logistics. By dynamically adjusting schedules based on real-time data, specifications, and prioritization rules, LiveSched optimizes operations, maximizes resource utilization, and enhances client service delivery.

## Installation

This ReadMe has been tested using a Mac, please make changes as needed for Windows.

To build and use this service you must install the following:

1. [Maven 3.9.5](https://maven.apache.org/docs/3.9.5/release-notes.html)
2. [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
3. [IntelliJ IDE](https://www.jetbrains.com/idea/download/?section=mac) (Recommended IDE, optionally you may use any other)
4. [Clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) this repository using git clone or IntelliJ

---

## Building and Running a Local Instance

To set up and run the repository after installation, you can use the following commands inside LiveSched folder.
By default, the service operates in local mode, saving and loading files from local storage.
If you would like to use Google Cloud Storage (GCS), you can enable GCS operations by passing the `--useGCS` flag as noted below.
But please note that if you plan to run the service with GCS enabled, you need to install and set up [Google Cloud CLI](https://cloud.google.com/sdk/docs/install-sdk).

### Switch to code directory from root

```
cd LiveSched
```

### Setup

If you want to optionally set up an example database for the first time or want to reset it, run the following command:

```
mvn spring-boot:run -Dspring-boot.run.arguments="setup"
```

This will set up an example database for client ID: demoClientId.

If you want to enable GCS during setup:

```
mvn spring-boot:run -Dspring-boot.run.arguments="setup --useGCS"
```

### Run

To run the service normally (in local mode by default):

```
mvn spring-boot:run
```

If you want to enable GCS operations:

```
mvn spring-boot:run -Dspring-boot.run.arguments="--useGCS"
```

### Build and test

```
mvn -B clean install --file pom.xml
```

### Test

All unit tests are located under the directory src/test

They can be run individually in IntelliJ using right click on the test files or to run the whole test suite use the following command:

```
mvn clean test
```

### Check style issues

```
mvn checkstyle:check
mvn checkstyle:checkstyle
```

### Generate code coverage result

The results can be found under target/site/jacoco directory

```
mvn jacoco:report
```

### Run and generate static bug finder results

```
mvn pmd:check
```

---

## Running a Cloud based Instance

This service is currently available as a google cloud based instance that can be accessed using the following url:

https://innov8-livesched.ue.r.appspot.com

A successful connection should lead you to a homepage that displays the following message:

<img width="991" alt="Screenshot 2024-10-18 at 11 47 37 PM" src="https://github.com/user-attachments/assets/595f76e7-22b3-47d3-99fd-951f13822dd8">

Additional data requests and tests can be made using a service like [Postman](https://www.postman.com/) using the following format:

https://innov8-livesched.ue.r.appspot.com/endpoint?arg=value&arg=value

Note:
For the cloud based instance, GCS operations are enabled by default, meaning the service will load and save files to Google Cloud Storage without any additional configuration.

---

## Endpoints

This section describes the endpoints that the service provides, as well as their inputs and outputs.

### GET /index

* Expected Input Parameters: N/A
* Expected Output: Welcome message (String)
* Description: Redirects to the homepage.
* Upon Success: HTTP 200 Status Code with a welcome message in the response body.

### GET /retrieveTasks

* Expected Input Parameters: clientId (String)
* Expected Output: A JSON array containing the details of all tasks
* Description: Returns the details of all tasks in the database for the specified client.
* Upon Success: HTTP 200 Status Code with a list of tasks in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Tasks Not Found" if there are no tasks.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### GET /retrieveTask

* Expected Input Parameters:
  * taskId (String)
  * clientId (String)
* Expected Output: A JSON object containing the details of the specified task
* Description: Returns the details of a specified task in the database.
* Upon Success: HTTP 200 Status Code with the task's details in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the specified task does not exist.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### GET /retrieveResourceTypes

* Expected Input Parameters: clientId (String)
* Expected Output: A JSON array containing the details of all resource types
* Description: Returns the details of all resource types in the database.
* Upon Success: HTTP 200 Status Code with the list of resource types in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "ResourceTypes Not Found" if there are no resource types.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### GET /retrieveSchedule

* Expected Input Parameters: clientId (String)
* Expected Output: A JSON array containing the details of the master schedule
* Description: Returns the details of the master schedule in the database.
* Upon Success: HTTP 200 Status Code with the schedule details in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Schedules Not Found" if there are no schedules.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### PATCH /addTask

* Expected Input Parameters:
  * taskName (String)
  * priority (int)
  * startTime (String, format: "yyyy-MM-dd HH:mm")
  * endTime (String, format: "yyyy-MM-dd HH:mm")
  * latitude (double)
  * longitude (double)
  * clientId (String)
* Expected Output: A JSON object containing the details of the newly added task
* Description: Adds a new task to the database.
* Upon Success: HTTP 200 Status Code with the task's details in the response body.
* Upon Failure: HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### DELETE /deleteTask

* Expected Input Parameters:
  * taskId (String)
  * clientId (String)
* Expected Output: A success message string
* Description: Deletes a task from the database.
* Upon Success: HTTP 200 Status Code with "{taskId} successfully deleted" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the task doesn't exist.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### PATCH /addResourceType

* Expected Input Parameters:
  * typeName (String)
  * totalUnits (int)
  * latitude (double)
  * longitude (double)
  * clientId (String)
* Expected Output: A success message string
* Description: Adds a new resource type to the database.
* Upon Success: HTTP 200 Status Code with "Attribute was updated successfully" in the response body.
* Upon Failure: HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### PATCH /modifyResourceType

* Expected Input Parameters:
  * taskId (String)
  * typeName (String)
  * quantity (int)
  * clientId (String)
* Expected Output: A success message string
* Description: Modifies resource type for a specified task.
* Upon Success: HTTP 200 Status Code with "Attribute was updated successfully" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the task doesn't exist.
  * HTTP 404 Status Code with "ResourceType Not Found" if the resource type doesn't exist.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### DELETE /deleteResourceType

* Expected Input Parameters:
  * typeName (String)
  * clientId (String)
* Expected Output: A success message string
* Description: Deletes a resource type from the database.
* Upon Success: HTTP 200 Status Code with "{typeName} successfully deleted" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "ResourceType Not Found" if the resource type doesn't exist.
  * HTTP 400 Status Code with "Cannot delete a resourceType currently in use" if the resource type is being used by tasks.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### PATCH /updateSchedule

* Expected Input Parameters:
  * maxDistance (double)
  * clientId (String)
* Expected Output: A JSON array containing the updated schedule details
* Description: Updates and returns the schedule for current tasks and resources.
* Upon Success: HTTP 200 Status Code with the updated schedule in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Tasks Not Found" if there are no tasks.
  * HTTP 404 Status Code with "Master Schedule Not Found" if there is no master schedule.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

### PATCH /unscheduleTask

* Expected Input Parameters:
  * taskId (String)
  * clientId (String)
* Expected Output: A success message string
* Description: Removes a task from the master schedule.
* Upon Success: HTTP 200 Status Code with "Task unscheduled successfully" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the task doesn't exist.
  * HTTP 404 Status Code with "Master Schedule Not Found" if there is no master schedule.
  * HTTP 400 Status Code with "Task Not Scheduled Yet" if the task isn't in the schedule.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

## Tools used

The following tools were used in the development and modification of this repository:

* Maven Package Manager
* GitHub Actions CI

  * Enabled via the "Actions" tab on GitHub, it runs automatically for every pull request and commit to 'main' branch
  * It runs a Maven build to make sure the code builds on branch 'main'
  * Link to CI reports: https://github.com/anavilohia/Innov8/actions/workflows/maven.yml
* GitHub Branch Protection Rules

  * Requires at least one review approval for every pull request into 'main' branch
  * Requires a successful build
* Checkstyle

  * Checks that the code follows style guidelines, generating warnings or errors as needed
  * This runs as part of the CI pipeline; the most recent report can be found by clicking the above CI reports link and clicking the most recent workflow run > Artifacts > ci-reports (this is also included in the reports folder in the root of the repo)
  * It can also be run using the "Checkstyle-IDEA" plugin for IntelliJ
  * Most recent checkstyle results (Dec 4, 2024)
    <img width="647" alt="image" src="https://github.com/user-attachments/assets/68215630-c372-432f-966f-b8bb20b9e6f3">
* PMD

  * Performs static analysis of the Java code, generating errors and warnings as needed
  * This runs as part of the CI pipeline; the most recent report can be found by clicking the above CI reports link and clicking the most recent workflow run > Artifacts > ci-reports (this is also included in the reports folder in the root of the repo)
  * The current code includes the following rulesets as specified in pom.xml:
    ```
    <ruleset>/category/java/errorprone.xml</ruleset>
    <ruleset>/rulesets/java/maven-pmd-plugin-default.xml</ruleset>
    ```
  * Most recent PMD results (Dec 4, 2024)
    <img width="1197" alt="image" src="https://github.com/user-attachments/assets/c5d08673-21dc-49a3-89a6-967719590056">
* JUnit

  * JUnit tests get run automatically as part of the CI pipeline
  * They can also be manually run using the code specified in the above sections
* JaCoCo

  * JaCoCo generates code test coverage reports such as branch analysis
  * This runs as part of the CI pipeline; the most recent report can be found by clicking the above CI reports link and clicking the most recent workflow run > Artifacts > ci-reports (this is also included in the reports folder in the root of the repo)
  * Most recent jacoco report with 86% overall branch coverage
    <img width="1457" alt="image" src="https://github.com/user-attachments/assets/12c55acf-e826-4fc2-8310-42d123b7825d">
* Postman
  
  * Used for testing that the API and its endpoints work as intended
  * Ran the application locally in setup mode (please refer to the above build and run guide) to generate example data for testing purposes
  * API Endpoint Test Results for demoClientId
    * `/index`
      <img width="1008" alt="image" src="https://github.com/user-attachments/assets/3e7b8a00-9fe4-4e55-800d-6b7ab86e009e">
    * `/retrieveTasks?clientId=demoClientId`
      <img width="1010" alt="image" src="https://github.com/user-attachments/assets/bd7d730e-b406-425c-a9b4-6f3665578a77">
    * `/retrieveTask?taskId=1&clientId=demoClientId`
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/e65d324b-284b-44f3-b561-cce08db8c5de">
    * `/addTask?taskName=checkup&priority=4&startTime=2024-12-17 11:30&endTime=2024-12-17 12:00&latitude=40.83&longitude=-73.91&clientId=demoClientId`
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/76719d56-4b0a-4184-90f4-dcdbc31bdbd2">
      <img width="1007" alt="image" src="https://github.com/user-attachments/assets/23384dc9-929c-470f-98ff-78e2cc7931db">
    * `/modifyResourceType?taskId=4&typeName=Nurse&quantity=1&clientId=demoClientId`
      <img width="1007" alt="image" src="https://github.com/user-attachments/assets/03d7fd56-ed0b-4db2-9843-2683ca1ea5cb">
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/ef79cc12-ab82-43c3-be71-8799048b3e3b">
    * `/deleteTask?taskId=4&clientId=demoClientId`
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/e178c21e-81f8-44d1-b869-6ab5365112a0">
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/baad90ca-523b-4110-b0f3-a57415931718">
    * `/retrieveResourceTypes?clientId=demoClientId`
      <img width="999" alt="image" src="https://github.com/user-attachments/assets/4074b6f7-051e-4a55-af69-38de1e1b2310">
    * `/addResourceType?typeName=SurgeryRoom&totalUnits=10&latitude=40.84&longitude=-73.94&clientId=demoClientId`
      <img width="1004" alt="image" src="https://github.com/user-attachments/assets/c35f0129-4368-4205-b6e3-5860f4fcff02">
      <img width="1006" alt="image" src="https://github.com/user-attachments/assets/6e4ebc0e-3a32-4909-8c45-a54b06a3318a">
    * `/deleteResourceType?typeName=SurgeryRoom&clientId=demoClientId`
      <img width="1007" alt="image" src="https://github.com/user-attachments/assets/347765df-ec6d-40ea-84b8-2e5a49b86b7d">
      <img width="1004" alt="image" src="https://github.com/user-attachments/assets/1ba27ba6-592f-42dd-86cc-e3e7318478a2">
    * `/retrieveSchedule?clientId=demoClientId`
      <img width="1008" alt="image" src="https://github.com/user-attachments/assets/7468aab0-b879-41ef-88df-f9ff2281a5fb">
    * `/updateSchedule?maxDistance=10&clientId=demoClientId`
      <img width="997" alt="image" src="https://github.com/user-attachments/assets/eeb16bd1-1118-4832-b043-1297f4a77f03">
      <img width="1003" alt="image" src="https://github.com/user-attachments/assets/012e4d77-48cf-4476-b024-6af5bae90983">
    * `/unscheduleTask?taskId=1&clientId=demoClientId`
      <img width="1008" alt="image" src="https://github.com/user-attachments/assets/d6d55857-e953-4b52-801c-6c970e8de405">
      <img width="1003" alt="image" src="https://github.com/user-attachments/assets/6b5d95a9-d5d5-42fa-9e17-34446b73926b">
  * Note that the database for a different clientId is unaffected by the above API calls. Below are a few results of calling the APIs for another client.
    * `/retrieveTasks?clientId=otherClientId`
    * The database for this clientId is empty so you get the Tasks Not Found
      <img width="1003" alt="image" src="https://github.com/user-attachments/assets/c95e640b-1041-44fc-b611-5aa8bef41f8d">
    * `/retrieveResourceTypes?clientId=otherClientId`
    * The database for this clientId is empty so you get the ResourceTypes Not Found
      <img width="1003" alt="image" src="https://github.com/user-attachments/assets/69e6d72c-f8ce-4269-a270-492a146b8acb">
---------------------------------------------

`citations.txt` is located at root level of this repository, it specifies urls for all resources used as reference in the development of this repository
