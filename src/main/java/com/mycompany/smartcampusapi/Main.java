/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author user
 */
public class Main {
     public static final String BASE_URI = "http://localhost:9095/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.mycompany.smartcampusapi");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();
        System.out.println("Smart Campus API started.");
        System.out.println("Base URL: " + BASE_URI);
        System.out.println("Press ENTER to stop the server...");
        System.in.read();
        server.shutdownNow();
    }
    
    
}
