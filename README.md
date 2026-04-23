# Smart Campus Sensor & Room Management API

## Module Information
- **Module:** 5COSC022W – Client-Server Architectures
- **Coursework Title:** Smart Campus Sensor & Room Management API
- **Student:** Dinithi Thisarani Senevirathna
- **Student ID:** w2153628/20240583
- **Repository:** https://github.com/thisaS04/Smart-Campus-API.git
---

## Overview

This project is a RESTful API developed for the Client-Server Architectures coursework using Java, Maven, JAX-RS (Jersey), and an embedded Grizzly HTTP Server.

The API simulates a Smart Campus environment where:
- rooms can be created, viewed, and deleted,
- sensors can be registered and linked to rooms,
- sensors can be filtered by type,
- each sensor keeps a history of readings,
- readings update the parent sensor’s current value,
- custom exception handling is used for common error scenarios,
- request and response logging is implemented using JAX-RS filters.

This project uses **in-memory data structures only** such as `HashMap`, `ArrayList`, and `Map<String, List<SensorReading>>`. No external database is used.

---

## API Design Summary

The API is designed around resource-based REST principles.

Main resources:
- **Discovery resource** → root API information and links
- **Room resource** → room collection and room-by-id access
- **Sensor resource** → sensor collection, sensor-by-id access, and filtering
- **Sensor reading sub-resource** → nested reading history under a sensor

The design uses:
- `GET` for retrieval
- `POST` for creation
- `DELETE` for removal
- `@QueryParam` for filtering
- a **sub-resource locator** for `sensors/{sensorId}/readings`
- **exception mappers** for controlled error responses
- **filters** for centralized logging

---

## Technology Stack

- Java 8
- Maven
- JAX-RS
- Jersey
- Grizzly HTTP Server
- Apache NetBeans
- Postman (for testing)



## Project Structure
src/main/java/com/mycompany/smartcampusapi
├── JakartaRestConfiguration.java
├── Main.java
├── model
│   ├── Room.java
│   ├── Sensor.java
│   ├── SensorReading.java
│   └── ApiError.java
├── store
│   └── DataStore.java
├── resource
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   └── SensorUnavailableException.java
├── mapper
│   ├── RoomNotEmptyExceptionMapper.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── SensorUnavailableExceptionMapper.java
│   └── GlobalExceptionMapper.java
└── filter
    └── LoggingFilter.java

## In-Memory Data Design

The system stores runtime data in memory using Java collections.

Main collections:

* Map<String, Room> for rooms
* Map<String, Sensor> for sensors
* Map<String, List<SensorReading>> for reading history

This means:
- data is available while the server is running,
- if the server restarts, runtime-created data is reset,
- some sample seed data is included for testing.

Example seeded data:

* room: LIB-301
* room: LAB-201
* sensor: TEMP-001
* sensor: CO2-001

## How to Build the Project
Using Apache NetBeans

1. Open the SmartCampusAPI project in Apache NetBeans.
2. Let Maven finish loading dependencies.
3. Right-click the project.
4. Click Clean and Build.
   
Using Maven in Terminal
Open terminal in the project folder and run:
- mvn clean install
  
## How to Run the Server

Using Apache NetBeans
1. Open the project in NetBeans.
2. Make sure the run configuration uses: com.mycompany.smartcampusapi.Main
3. Right-click the project.
4. Click Run.
   
Using Maven in Terminal
mvn exec:java -Dexec.mainClass=com.mycompany.smartcampusapi.Main

## Base URL
When the server starts successfully, the local base URL is:
http://localhost:9095/api/v1/

## Main Endpoints

Discovery
- GET /api/v1/
  
Rooms
- GET /api/v1/rooms
- POST /api/v1/rooms
- GET /api/v1/rooms/{roomId}
- DELETE /api/v1/rooms/{roomId}
  
Sensors
- GET /api/v1/sensors
- GET /api/v1/sensors?type=CO2
- POST /api/v1/sensors
- GET /api/v1/sensors/{sensorId}
  
Sensor Reading History
- GET /api/v1/sensors/{sensorId}/readings
- POST /api/v1/sensors/{sensorId}/readings

## Sample curl Commands

1. Get API discovery information
curl -X GET http://localhost:9095/api/v1/
2. Get all rooms
curl -X GET http://localhost:9095/api/v1/rooms
3. Create a new room
curl -X POST http://localhost:9095/api/v1/rooms \
-H "Content-Type: application/json" \
-d "{\"id\":\"ENG-101\",\"name\":\"Engineering Lab\",\"capacity\":50}"
4. Get one room by ID
curl -X GET http://localhost:9095/api/v1/rooms/ENG-101
5. Get all sensors
curl -X GET http://localhost:9095/api/v1/sensors
6. Create a new sensor linked to a valid room
curl -X POST http://localhost:9095/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"OCC-002\",\"type\":\"Occupancy\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"ENG-101\"}"
7. Filter sensors by type
curl -X GET "http://localhost:9095/api/v1/sensors?type=CO2"
8. Add a new reading to a sensor
curl -X POST http://localhost:9095/api/v1/sensors/OCC-002/readings \
-H "Content-Type: application/json" \
-d "{\"value\":17}"
9. Get reading history for a sensor
curl -X GET http://localhost:9095/api/v1/sensors/OCC-002/readings
10. Trigger 409 Conflict by deleting a room that still has sensors
curl -X DELETE http://localhost:9095/api/v1/rooms/LIB-301
11. Trigger 422 Unprocessable Entity by linking a sensor to a non-existent room
curl -X POST http://localhost:9095/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.0,\"roomId\":\"ROOM-DOES-NOT-EXIST\"}"
12. Trigger 403 Forbidden by posting a reading to a maintenance sensor
curl -X POST http://localhost:9095/api/v1/sensors/CO2-001/readings \
-H "Content-Type: application/json" \
-d "{\"value\":460}"

## Example JSON Requests
Create Room
{
  "id": "ENG-101",
  "name": "Engineering Lab",
  "capacity": 50
}
Create Sensor
{
  "id": "OCC-002",
  "type": "Occupancy",
  "status": "ACTIVE",
  "currentValue": 0,
  "roomId": "ENG-101"
}
Create Reading
{
  "value": 17
}

## Error Handling Summary

This API uses custom exception mappers so that it does not return raw Java stack traces to clients.

Implemented cases:

- 409 Conflict → deleting a room that still has assigned sensors
- 422 Unprocessable Entity → creating a sensor with a room ID that does not exist
- 403 Forbidden → posting a reading to a sensor in MAINTENANCE
- 500 Internal Server Error → catch-all global safety net for unexpected runtime errors

All error responses return a structured JSON body using the ApiError model.

## Logging
A custom JAX-RS filter logs:

the HTTP method and URI for every incoming request,
the final HTTP status code for every outgoing response.

This improves API observability and keeps logging logic centralized instead of duplicating logger statements in every resource method.

## Known Limitations
The project uses in-memory data only, so data is reset when the server restarts.
The API is designed for coursework demonstration and local testing.
Authentication and persistent database storage are intentionally not included because they are outside the coursework requirements.

## Conclusion
This project demonstrates the design and implementation of a RESTful Smart Campus API using JAX-RS. It includes resource-based routing, nested sub-resources, query-based filtering, in-memory data storage, structured exception mapping, and centralized logging. The final result is a clean and testable API that satisfies the coursework requirements while following core REST principles.
