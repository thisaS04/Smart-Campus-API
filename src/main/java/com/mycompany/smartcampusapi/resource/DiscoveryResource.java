/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resource;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**

/**
 *
 * @author user
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("admin", "w2153628@westminster.ac.uk");

        Map<String, String> resources = new LinkedHashMap<String, String>();
        resources.put("rooms", "http://localhost:9095/api/v1/rooms");
        resources.put("sensors", "http://localhost:9095/api/v1/sensors");

        response.put("resources", resources);

        return Response.ok(response).build();
    }
}