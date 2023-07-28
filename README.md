
# Poker application

## Initial setup
- This repository is supposed to be forked for your training so that you can share code changes among your training team
- The code requires at least Java 8 and Maven 3 to compile and package
- If you use Eclipse as an IDE, import the project as "Existing Maven project"
- The project is a self-contained Spring Boot project
- You can start the application either:
	- using `mvn spring-boot:run`, from the command line or within your IDE
	- if you are using [Spring Tool Suite](https://spring.io/tools): using "Run As" -> "Spring Boot App" or from the "Boot Dashboard" view
- The application will be available at http://localhost:8080
- If you would like to use Gradle instead of Maven:
  - Use `gradle init` to generate Gradle artifacts from the Maven pom.xml
  - Add `mavenCentral()` to the `repositories`
  - [Add the Spring Boot plugin for Gradle](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/), otherwise the `bootRun` Gradle task will not work
