# Task 7: Spring Boot Implementation

This is the server implementation for the Meeting Scheduler API designed in Task 6.

## Project Files

The Files i modified:

* **Configuration**: `pom.xml`
    * Added dependencies for Swagger UI and the OpenAPI Generator plugin.
* **Controller**: `src/main/java/com/example/scheduler/controller/MeetingsController.java`
    * Handles the logic for creating meetings, publishing, and voting.
* **Tests**: `src/test/java/com/example/scheduler/MeetingSchedulerApplicationTests.java`
    * Integration test that runs the full Create -> Publish -> Verify workflow.

## How to Run

1.  Open the project in Eclipse as a Maven project.
2.  Run `MeetingSchedulerApplication.java` as a Spring Boot App.
3.  The server starts on `localhost:8080`.

## How to Test

**Manual Testing**
I added the Swagger UI dependency so you can test endpoints in the browser:
http://localhost:8080/swagger-ui/index.html

**Automated Tests**
Run the JUnit test in `MeetingSchedulerApplicationTests.java` to verify the API workflow automatically.