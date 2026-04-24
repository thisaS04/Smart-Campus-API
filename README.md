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

### 1. Get API discovery information
'''bash 
curl -X GET http://localhost:9095/api/v1/
### 2. Get all rooms
'''bash
curl -X GET http://localhost:9095/api/v1/rooms
### 3. Create a new room
'''bash
curl -X POST http://localhost:9095/api/v1/rooms \
-H "Content-Type: application/json" \
-d "{\"id\":\"ENG-101\",\"name\":\"Engineering Lab\",\"capacity\":50}"
### 4. Get one room by ID
'''bash
curl -X GET http://localhost:9095/api/v1/rooms/ENG-101
### 5. Get all sensors
'''bash
curl -X GET http://localhost:9095/api/v1/sensors
### 6. Create a new sensor linked to a valid room
'''bash
curl -X POST http://localhost:9095/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"OCC-002\",\"type\":\"Occupancy\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"ENG-101\"}"
### 7. Filter sensors by type
'''bash
curl -X GET "http://localhost:9095/api/v1/sensors?type=CO2"
### 8. Add a new reading to a sensor
'''bash
curl -X POST http://localhost:9095/api/v1/sensors/OCC-002/readings \
-H "Content-Type: application/json" \
-d "{\"value\":17}"
### 9. Get reading history for a sensor
'''bash
curl -X GET http://localhost:9095/api/v1/sensors/OCC-002/readings
### 10. Trigger 409 Conflict by deleting a room that still has sensors
'''bash
curl -X DELETE http://localhost:9095/api/v1/rooms/LIB-301
### 11. Trigger 422 Unprocessable Entity by linking a sensor to a non-existent room
'''bash
curl -X POST http://localhost:9095/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.0,\"roomId\":\"ROOM-DOES-NOT-EXIST\"}"
### 12. Trigger 403 Forbidden by posting a reading to a maintenance sensor
'''bash
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

## Report

## Part 1: Service Architecture & Setup
**Question 01:** 
Resource classes developed using the JAX-RS framework are normally single-use per request. What this implies is that an application normally creates a new instance of a resource class when a new request comes in. This is an excellent idea since this way, there is less possibility that data from one request may unintentionally be passed over to another one by accident.

Nevertheless, this fact alone will not guarantee the overall thread safety of the application. As far as this particular application goes, the crucial data resides not in the resource class instances but in some other shared structures. There are a number of collections of HashMap and ArrayList classes in the datastore. These collections contain information about rooms, sensors, and sensor readings.

In light of what was said above, several users may simultaneously read and write to the mentioned data collections. Consequently, in order to avoid race conditions and other undesirable effects, it becomes necessary to use synchronization.




**Question 02:**
First of all, hypermedia can be viewed as the hallmark of advanced RESTful implementations since it helps in making an API self-explanatory. The developer who works on the client-side application does not have to depend only on an API specification provided as an external document. Instead, the API can provide information about related resources and actions that can be done on these resources.

For instance, the discovery endpoint might return important metadata, such as the API version and its major resources, i.e., rooms and sensors. With this approach, the client can see how to move around the system based on the responses received.

Secondly, the advantage of hypermedia is that it helps in improving the flexibility of APIs, and in maintaining their functionality. In comparison with documentation, it is static and may become outdated if the structure of the API has been changed. On the contrary, hypermedia returns dynamic content that depends on the current API structure.



## Part 2: Room Management
**Question 3:**
If one chooses to return only room IDs in the response body, the payload will be relatively light. The client gets fewer bytes to process and save memory, which could be useful if the client application does not require much information from this endpoint and only needs IDs for further processing or linking with other endpoints.

The downside to only returning room IDs is that the client application might have to initiate an additional request for any further data on each of these rooms, as there will be no way to get the name, capacity, or even sensor ID from the current API call.

On the other hand, if the response contains a list of room objects, it would contain all necessary and relevant data at once. Such a response will be larger in size compared to room IDs, but will also provide more useful data for the client application.



**Question 4:** 

The DELETE operation implemented here is idempotent in its effect on the final state of the server in question. Idempotency here is the characteristic of making sure that repeated requests for deletion would have no additional impact other than the single time it is made.

If a certain room was successfully deleted by the server, then there would be nothing more to remove after that. A subsequent DELETE request would simply be met by a different HTTP response, possibly not found error messages or responses like that. This way, no additional impact would occur aside from what happened on the very first delete request.

It is thus that despite the difference in the HTTP response on the second request compared to the first one, the server state stays the same as far as the DELETE operation goes.


## Part 3: Sensor Operations & Linking
**Question 5:** 
The @Consumes annotation is used to define what kind of media types can be consumed by the resource method. In this case, it is specified that the method consumes JSON messages; hence, JAX-RS attempts to find an appropriate message body reader for the received data and convert it to java objects.
Therefore, if the client passes parameters using any type that doesn't correspond to that specified in the @Consumes annotation, JAX-RS may fail to locate the corresponding message body reader. As a consequence, the body of the request cannot be interpreted as per the method's requirements.
Under the assumption that JAX-RS is used properly, the most common behavior in such situations is to return an HTTP 415 error. Such an error code is valid in this situation since we cannot properly consume the received message's body.



**Question 6:**
Usage of query parameters like /sensors?type=CO2 is a better practice when filtering, as in this case, we are still working with the same resource, although filtered in some way. Thus, here the basic resource is the same collection of sensors, but the type is an attribute which defines the filtering of data related to certain conditions.

Query parameters have their own advantage over other approaches as they make it possible to include additional criteria of filtering such as status, roomId, or even date-based criteria without changing the URI path structure. Moreover, query parameters represent a common practice in REST, as they are used to implement searches over the resources of a collection.

As was mentioned before, it is better to use paths to represent resources rather than filters. The main problem here is that the filter criterion is an optional part of a request, and we should try to make the URI design as simple and flexible as possible.



## Part 4: Deep Nesting with Sub – Resources
**Question 7:**
In this regard, the Sub-Resource Locator pattern enhances the API development process since it enables one to extract nested functions into their corresponding classes rather than placing all these functions in one resource class. In the implementation provided above, SensorResource performs operations that correspond to sensors, whereas SensorReadingResource performs operations that correspond to readings performed on a particular sensor.

Thus, the implementation enhances the clarity of the design since the responsibility for performing certain operations in an application will not be concentrated on a single class; in other words, one would have two classes performing certain tasks within a particular application.

The approach used enhances the ability of developers to make updates and changes to an API, thereby ensuring greater flexibility in terms of scalability.
Question 8:
Here, the historical data of each sensor is collected in the form of their readings. The sub-resource that provides sensor readings enables the client to fetch the complete reading history of a particular sensor, as well as add new readings whenever new readings are received from the sensors.

As soon as the reading is added to the sensor history list, a corresponding update to the currentValue property of the parent sensor object must take place. This step is crucial as, at any point in time, the current state of a sensor must contain its most recently added reading.

Thus, this operation ensures consistency. Otherwise, the reading history resource might show a certain reading while the main sensor resource will display an out-of-date value for the sensor's current value.




## Part 5: Advanced Error Handling, Exception Mapping & Logging
**Question 9:**
In this case, an HTTP status code of 422 Unprocessable Entity would be more appropriate since there is no problem with the request's syntax. The JSON request body is valid, the server parses it successfully, and the requested endpoint path is valid.

The problem with the request is that a value provided in the request is semantically incorrect. In other words, although the client sends a perfectly good request for creating a sensor, the value of the roomId provided in the request's body is not valid since such a room does not exist.

Typically, a 404 status code would be returned when the requested URI itself does not exist. In our case, however, the URI exists, but something else is wrong, namely, some information included in the request is not correct. That is why HTTP status code 422 is the best fit.

**Question 10:** 
The reason why exposing raw Java stack traces could be harmful in terms of cybersecurity is that it gives the hacker insight into the internal workings of the software. Information like the class names, the package name, method name, behavior of the framework, the logic flow, and even file paths are revealed.

This can be very beneficial for the hacker in their attempt to break into the application. By having all this knowledge about the structure of the application and where the exception has happened, the hacker is able to get a much better idea of what to focus on in order to exploit some security issues.

A secure API will not expose such internal data to a client, but rather generate user-friendly errors and log all the information into the logs.




**Question 11:** 
JAX-RS filters are preferred for logging since logging itself is a cross-cutting concern. It affects many different endpoints in an entire API and not only one particular method. Therefore, having manually placed logging instructions in every method leads to code duplication and makes it hard to manage and maintain.

With filters, the issue above gets solved through centralization. One request filter is used to log each incoming HTTP method and URI, while a response filter logs outgoing status codes. Thus, every endpoint in the API gets logged using the same pattern, without adding duplicate code to all resource methods.

Additionally, logging through filters makes resource methods simpler and more legible since the responsibility for logging is separated from the responsibility to process the request or return a response to it.

