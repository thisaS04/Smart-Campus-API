/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.mapper;
import com.mycompany.smartcampusapi.exception.SensorUnavailableException;
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
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ApiError error = new ApiError(
                403,
                "Forbidden",
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown");

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    } 
    
    
}
