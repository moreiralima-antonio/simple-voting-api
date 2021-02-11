# Simple Voting REST API

This project's goal is to provide a simple API to manage voting sessions and compute the results for each proposal.

## Application

The core application features are:

- Java 11 and Spring Boot 2.x application built with Maven 3.6.x
- REST API documented with Open API 3.0 (resources/api.yml)
- Data persistence with Spring Data MongoDB (MongoDB as an external service)
- Logging with SLF4J (Log4j2)
- Testing with Postman (postman/SimpleVotingAPI.postman_collection.json)

Current version is v0.2.0 and the API is exposed on: http://localhost:8080/api/v0.2.0.

## Features

Main API features are:

- Proposal creation
- Session creation based on a previously created proposal
- Session expiration control (timeout defaults to 60 seconds)
- Vote registration in an opened session (yes or no as options)
- Voting results generation taking into account duplicate votes

## Building

```bash
mvn clean package
```

## Running

```bash
mvn spring-boot:run
```

If you don´t have a MongoDB server in your computer you can use Docker as follows:

```bash
docker-compose up -d
```

## Testing

### Postman

Open Postman GUI and import **SimpleVotingAPI.postman_collection.json** collection into your workspace.

### Command line

```bash
curl -X POST "http://localhost:8080/api/v0.2.0/proposals" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"description\":\"Example\"}"
```
