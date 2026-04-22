/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.ApiError;
import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.model.SensorReading;
import com.mycompany.smartcampusapi.store.DataStore;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author user
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings(@Context UriInfo uriInfo) {
        Sensor sensor = DataStore.getSensor(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(
                            404,
                            "Not Found",
                            "Sensor '" + sensorId + "' was not found.",
                            uriInfo.getPath()))
                    .build();
        }

        return Response.ok(DataStore.getReadings(sensorId)).build();
    }

    @POST
    public Response createReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = DataStore.getSensor(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(
                            404,
                            "Not Found",
                            "Sensor '" + sensorId + "' was not found.",
                            uriInfo.getPath()))
                    .build();
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(
                            400,
                            "Bad Request",
                            "Request body must contain a reading.",
                            uriInfo.getPath()))
                    .build();
        }

        SensorReading readingToSave = new SensorReading(
                reading.getId(),
                reading.getTimestamp(),
                reading.getValue());

        SensorReading saved = DataStore.addReading(sensorId, readingToSave);

        URI location = uriInfo.getAbsolutePathBuilder().path(saved.getId()).build();
        return Response.created(location).entity(saved).build();
    }

}
