/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.model.ApiError;
import com.mycompany.smartcampusapi.model.Room;
import com.mycompany.smartcampusapi.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author user
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Response getAllRooms() {
        return Response.ok(DataStore.getAllRooms()).build();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || isBlank(room.getId()) || isBlank(room.getName()) || room.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(
                            400,
                            "Bad Request",
                            "Room must include valid id, name, and capacity greater than 0.",
                            uriInfo.getPath()))
                    .build();
        }

        Room cleanRoom = new Room(
                room.getId().trim(),
                room.getName().trim(),
                room.getCapacity(),
                new ArrayList<String>());

        boolean created = DataStore.addRoom(cleanRoom);

        if (!created) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiError(
                            409,
                            "Conflict",
                            "A room with id '" + cleanRoom.getId() + "' already exists.",
                            uriInfo.getPath()))
                    .build();
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(cleanRoom.getId()).build();
        return Response.created(location).entity(cleanRoom).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        Room room = DataStore.getRoom(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(
                            404,
                            "Not Found",
                            "Room '" + roomId + "' was not found.",
                            uriInfo.getPath()))
                    .build();
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        boolean deleted = DataStore.deleteRoom(roomId);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(
                            404,
                            "Not Found",
                            "Room '" + roomId + "' was not found.",
                            uriInfo.getPath()))
                    .build();
        }

        return Response.ok(Collections.singletonMap("message",
                "Room '" + roomId + "' deleted successfully.")).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}