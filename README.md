# COMS 4156: Innov8 Team Project

This is the GitHub repository for the service portion of the team project for group 'Innov8' associated with COMS 4156 W Advanced Software Engineering at Columbia University.

## Team name

Innov8

## Team members

Anavi Lohia (al3750) \
Jane Lim (jl6094) \
Jonghyun Lee (jl6509) \
Jungyun Kim (jk4661) \
Nathan Philip Zepeda (npz2000)

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

---------------------------------------------

## Building and Running a Local Instance

To set up and run the repository after installation, you can use the following commands inside LiveSched folder:
<br/>
<br/>

### Switch to code directory from root
```
cd LiveSched
```

### Setup
```
mvn spring-boot:run -Dspring-boot.run.arguments="setup"
```

### Run
```
mvn spring-boot:run
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

---------------------------------------------

## Running a Cloud based Instance

This service is currently available as a google cloud based instance that can be accessed using the following url:

[ADD URL]

A successful connection should lead you to a homepage that displays the following message:

[ADD IMAGE OF HOMEPAGE]

Additional data requests and tests can be made using a service like [Postman](https://www.postman.com/) using the following format:

[ADD URL]/endpoint?arg=value&ar=value

---------------------------------------------

## Endpoints

This section describes the endpoints that the service provides, as well as their inputs and outputs. 

#### GET /index
* Expected Input Parameters: N/A
* Expected Output: Welcome message (String)
* Redirects to the homepage.
* Upon Success:
  * HTTP 200 Status Code with a welcome message in the response body.

#### GET /retrieveTasks
* Expected Input Parameters: N/A
* Expected Output: A String containing the details of all tasks (ResponseEntity\<String\>)
* Returns the details of all tasks in the database.
* Upon Success:
  * HTTP 200 Status Code with a list of tasks in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Tasks Not Found" if there are no tasks.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.
 
#### GET /retrieveTask
* Expected Input Parameters: taskId (String)
* Expected Output: A String containing the details of the specified task (ResponseEntity\<String\>)
* Returns the details of a specified task in the database.
* Upon Success:
  * HTTP 200 Status Code with the task's details in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the specified task does not exist.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

#### GET /retrieveResourceTypes
* Expected Input Parameters: N/A
* Expected Output: A String containing the details of all resourcetypes (ResponseEntity\<String\>)
* Returns the details of all resource types in the database.
* Upon Success:
  * HTTP 200 Status Code with the list of resource types in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "ResourceTypes Not Found" if there are no resource types.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.
 
#### GET /retrieveResourcesFromTask
* Expected Input Parameters: taskId (String)
* Expected Output: A String containing the details of the resourceTypes needed for the specified task. (ResponseEntity\<String\>)
* Returns the details of resource types a task needs.
* Upon Success:
  * HTTP 200 Status Code with the resource types in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the task does not exist.
  * HTTP 404 Status Code with "ResourceType Not Found" if no resource types are found for the task.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

#### PATCH /addTask
* Expected Input Parameters: priority (int), startTime (String), endTime (String), latitude (double), longitude (double)
* Expected Output: A String confirming the task was added successfully. (ResponseEntity\<String\>)
* Attempts to add a task to the database.
* Upon Success:
  * HTTP 200 Status Code with "Attribute was updated successfully." in the response body.
* Upon Failure:
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.
 
#### PATCH /addResourceType
* Expected Input Parameters: typeName (String), totalUnits (int), latitude (double), longitude (double)
* Expected Output: A String confirming the resource type was added successfully. (ResponseEntity\<String\>)
* Attempts to add a resource type to the database.
* Upon Success:
  * HTTP 200 Status Code with "Attribute was updated successfully." in the response body.
* Upon Failure:
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.
 
#### PATCH /modifyResourceType
* Expected Input Parameters: typeName (String), totalUnits (int), latitude (double), longitude (double)
* Expected Output: A String confirming the resource type for the task was modified successfully. (ResponseEntity\<String\>)
* Attempts to modify resource type for a specified task to the database.
* Upon Success:
  * HTTP 200 Status Code with "Attribute was updated successfully." in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Task Not Found" if the task does not exist.
  * HTTP 404 Status Code with "ResourceType Not Found" if the resource type does not exist.
  * HTTP 500 Status Code with "An Error has occurred" if an unexpected error occurs.

---------------------------------------------

## Tools used

The following tools were used in the development and modification of this repository:

* Maven Package Manager
* GitHub Actions CI
  * Enabled via the "Actions" tab on GitHub, it runs automatically for every pull request and commit to 'main' branch
  * It runs a Maven build to make sure the code builds on branch 'main'
* GitHub Branch Protection Rules
  * Requires at least one review approval for every pull request into 'main' branch
  * Requires a successful build
* Checkstyle
  * Checks that the code follows style guidelines, generating warnings or errors as needed
  * Currently, this needs to be manually run using the code specified in above sections, it is not a part of the CI pipeline
  * It can also be run using the "Checkstyle-IDEA" plugin for IntelliJ
  * Most recent checkstyle results \
    <img width="650" alt="Screenshot 2024-10-18 at 10 49 14 PM" src="https://github.com/user-attachments/assets/d78ed7ab-477d-47e3-97a9-e8142f7c9d59">

* PMD
  * Performs static analysis of the Java code, generating errors and warnings as needed
  * Currently, this needs to be manually run using the code specified in above sections, it is not a part of the CI pipeline
  * The current code includes the following rulesets as specified in pom.xml:
    ```
    <ruleset>/category/java/errorprone.xml</ruleset>
    <ruleset>/rulesets/java/maven-pmd-plugin-default.xml</ruleset>
    ```
  * Most recent PMD results \
    <img width="1214" alt="Screenshot 2024-10-18 at 10 51 10 PM" src="https://github.com/user-attachments/assets/f4a8f933-4dbe-4584-a206-82ad23c49e00">
* JUnit
  * JUnit tests get run automatically as part of the CI pipeline
  * They can also be manually run using the code specified in above sections
* JaCoCo
  * JaCoCo generates code test coverage reports such as branch analysis
  * Currently, this needs to be manually run using the code specified in above sections, it is not a part of the CI pipeline
  * Most recent jacoco report with 74% overall branch coverage\
    <img width="1465" alt="Screenshot 2024-10-18 at 10 57 11 PM" src="https://github.com/user-attachments/assets/2d89ce68-00ba-4eec-8ca2-a9a467059aea">

* Postman
  * Used for testing that the API and its endpoints work as intended
  * API Endpoint Test Results
   * `/index`
     <img width="855" alt="Screenshot 2024-10-18 at 9 26 47 PM" src="https://github.com/user-attachments/assets/2fdb2da3-b6bc-431f-8989-6f02e25dfaf8">  
   * `/retrieveTasks`
     <img width="1055" alt="Screenshot 2024-10-18 at 9 27 33 PM" src="https://github.com/user-attachments/assets/cf4f1965-fbdd-4d38-a47f-b4751a0c0de5">
   * `/retrieveTask?taskId=ER-1`
     <img width="1043" alt="Screenshot 2024-10-18 at 9 29 22 PM" src="https://github.com/user-attachments/assets/2b23b9f3-4ee9-44d7-a4b8-bcb66c326cb5">
   * `/retrieveResourceTypes`
     <img width="1044" alt="Screenshot 2024-10-18 at 9 29 46 PM" src="https://github.com/user-attachments/assets/c644667c-77cf-4853-ad7b-7134ad91f370">
   * `/retrieveResourcesFromTask?taskld=ER-1`
     <img width="1043" alt="Screenshot 2024-10-18 at 9 32 47 PM" src="https://github.com/user-attachments/assets/a9aa1730-6158-4ed0-be9a-02621e0a50a5">
   * `/addTask?priority=1&startTime=2024-10-19 09:14&endTime=2024-10-19 10:14&latitude=20&longitude=-10`
     <img width="1042" alt="Screenshot 2024-10-18 at 9 40 56 PM" src="https://github.com/user-attachments/assets/9bdd4472-ab8f-4fb3-936d-3099ad1e255b">
     <img width="1064" alt="Screenshot 2024-10-18 at 10 18 22 PM" src="https://github.com/user-attachments/assets/c8f08166-3bd9-4378-9774-4ac37fb5103c">
   * `/addResourceType?typeName=Table&totalUnits=10&latitude=30&longitude=-40`
     <img width="1052" alt="Screenshot 2024-10-18 at 9 42 18 PM" src="https://github.com/user-attachments/assets/c2924406-a2e9-4e06-be73-613bb0eed61f">
     <img width="1039" alt="Screenshot 2024-10-18 at 10 19 33 PM" src="https://github.com/user-attachments/assets/9c6d4a6f-1c5b-4ccb-968f-368eb18b6044">
   * `/modifyResourceType?taskId=ER-1&typeName=Doctor&quantity=1`
     <img width="1051" alt="Screenshot 2024-10-18 at 10 27 42 PM" src="https://github.com/user-attachments/assets/6a4fa3ee-d6dc-4581-871d-2994c550b650">
     <img width="1052" alt="Screenshot 2024-10-18 at 10 29 13 PM" src="https://github.com/user-attachments/assets/2514d7e1-4ad7-4bd6-9f49-d14a4da0125c">


     
---------------------------------------------

citations.txt is located at root level of this repository, it specifies urls for all resources used as reference in the development of this repository
