# YPPPP - Yohoho Puzzle Pirate Pillage Program

Assists while running a pillage in Puzzle Pirates.

## Prerequisites

- Java 8 or higher
- Maven 3.x

## Building the Project

To compile the project:

```bash
mvn clean compile
```

To build a distributable JAR file:

```bash
mvn clean package
```

This creates an executable JAR file at `target/YPPPP-1.0.0.jar` that includes all dependencies.

## Running the Application

To run the application during development:

```bash
mvn exec:java
```

Alternatively, you can build and run in one command:

```bash
mvn clean compile exec:java
```

To run the distributable JAR:

```bash
java -jar target/YPPPP-1.0.0.jar
```

## Project Structure

- `src/` - Source code directory
- `pom.xml` - Maven configuration file
- `preferences.xml` - Application preferences
- `ships.xml` - Ship configuration data
- `icons/` - Application icons
- `release/` - Release files

## Notes

- The application uses standard Java XML parsing (DocumentBuilder)
- Compatible with modern Java versions (fixed deprecated DOMParser usage)
- Uses Maven for dependency management and build automation