/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.mapper;
import com.mycompany.smartcampusapi.exception.RoomNotEmptyException;
import com.mycompany.smartcampusapi.model.ApiError;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


/**
 *
 * @author user
 */

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ApiError error = new ApiError(
                409,
                "Conflict",
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown");

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
    
}
