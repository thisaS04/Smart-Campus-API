/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.mapper;

import com.mycompany.smartcampusapi.model.ApiError;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
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
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "unknown";

        if (exception instanceof WebApplicationException) {
            WebApplicationException webException = (WebApplicationException) exception;
            int status = webException.getResponse().getStatus();
            String reason = webException.getResponse().getStatusInfo().getReasonPhrase();

            ApiError error = new ApiError(
                    status,
                    reason,
                    webException.getMessage() != null ? webException.getMessage() : reason,
                    path);

            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        LOGGER.log(Level.SEVERE, "Unhandled server error", exception);

        ApiError error = new ApiError(
                500,
                "Internal Server Error",
                "An unexpected server error occurred.",
                path);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
    
}
