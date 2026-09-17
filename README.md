# YPPPP - Yohoho Puzzle Pirate Pillage Program

Assists while running a pillage in Puzzle Pirates.

## Download

Grab the latest build from the [Releases page](https://github.com/roelandvanbatenburg/YPPPP/releases/latest) — no need to build from source. Download `YPPPP-<version>.jar` and run it with:

```bash
java -jar YPPPP-<version>.jar
```

Requires Java 8 or higher.

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

## Releasing

Releases are built and published automatically by GitHub Actions:

1. Bump the `<version>` in `pom.xml` if needed.
2. Commit the change and push to `main`.
3. Tag the commit with a `v`-prefixed version and push the tag:

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

4. The CI pipeline runs the tests, builds the jar with `mvn clean package`, and publishes a GitHub Release for that tag with `YPPPP-<version>.jar` attached.

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