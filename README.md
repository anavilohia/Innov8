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

[ADD ENDPOINT DETAILS]

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
    [ADD SCREENSHOT]
* PMD
  * Performs static analysis of the Java code, generating errors and warnings as needed
  * Currently, this needs to be manually run using the code specified in above sections, it is not a part of the CI pipeline
  * The current code includes the following rulesets as specified in pom.xml:
    ```
    <ruleset>/category/java/errorprone.xml</ruleset>
    <ruleset>/rulesets/java/maven-pmd-plugin-default.xml</ruleset>
    ```
  * Most recent PMD results \
    [ADD SCREENSHOT]
* JUnit
  * JUnit tests get run automatically as part of the CI pipeline
  * They can also be manually run using the code specified in above sections
* JaCoCo
  * JaCoCo generates code test coverage reports such as branch analysis
  * Currently, this needs to be manually run using the code specified in above sections, it is not a part of the CI pipeline
  * Most recent jacoco report \
    [ADD SCREENSHOT]
* Postman
  * Used for testing that the API and its endpoints work as intended

---------------------------------------------

citations.txt is located at root level of this repository, it specifies urls for all resources used as reference in the development of this repository
