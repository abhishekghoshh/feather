# Crud Application to test the feather annotation

> This application is using maven only to build the project, so you if you need to start a fresh maven project use the static files from this repo


**Create Maven wrapper**  
This command creates the Maven wrapper scripts.
```sh
mvn wrapper:wrapper
```

**Clean and package the project, skipping tests**  
This command cleans the project and packages the code, skipping the test phase.
```sh
mvn clean package -DskipTests
```

**Run the application**
This command runs the application using the Maven wrapper.
```sh
java -jar target/crud-0.0.1.jar
```


## Some useful resources

- [REST tutorial introduction](https://armeria.dev/tutorials/rest/blog)
  - [rest-api-annotated-service](https://github.com/line/armeria-examples/tree/main/tutorials/rest-api-annotated-service)
- [A Guide to JUnit 5](https://www.baeldung.com/junit-5)
- [Fundamentals of Logging in Java Applications](https://medium.com/@alxkm/fundamentals-of-logging-in-java-applications-16f94afb8f7c)
- [Neo4j orm tutorial](https://neo4j.com/docs/ogm-manual/current/tutorial/)
- [A Guide to Neo4J with Java](https://www.baeldung.com/java-neo4j)
- [Implementing Health Checks and Auto-Restarts for FastAPI Applications using Docker and Docker-Compose](https://medium.com/@ntjegadeesh/implementing-health-checks-and-auto-restarts-for-fastapi-applications-using-docker-and-4245aab27ece)