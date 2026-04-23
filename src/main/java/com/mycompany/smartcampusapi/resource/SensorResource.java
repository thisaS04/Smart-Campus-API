package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.ApiError;
import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.store.DataStore;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        return Response.ok(DataStore.getSensorsByType(type)).build();
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null
                || isBlank(sensor.getId())
                || isBlank(sensor.getType())
                || isBlank(sensor.getStatus())
                || isBlank(sensor.getRoomId())) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(
                            400,
                            "Bad Request",
                            "Sensor must include valid id, type, status, and roomId.",
                            uriInfo.getPath()))
                    .build();
        }

        Sensor cleanSensor = new Sensor(
                sensor.getId().trim(),
                sensor.getType().trim(),
                sensor.getStatus().trim().toUpperCase(),
                sensor.getCurrentValue(),
                sensor.getRoomId().trim());

        boolean created = DataStore.addSensor(cleanSensor);

        if (!created) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiError(
                            409,
                            "Conflict",
                            "A sensor with id '" + cleanSensor.getId() + "' already exists.",
                            uriInfo.getPath()))
                    .build();
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(cleanSensor.getId()).build();
        return Response.created(location).entity(cleanSensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
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

        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}