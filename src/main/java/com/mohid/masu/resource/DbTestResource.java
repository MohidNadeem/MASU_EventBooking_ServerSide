package com.mohid.masu.resource;

import com.mohid.masu.config.MongoConnection;
import com.mongodb.client.MongoDatabase;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/db-test")
public class DbTestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String testDatabaseConnection() {
        try {
            MongoDatabase database = MongoConnection.getDatabase();
            return "Connected to MongoDB database: " + database.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "MongoDB connection failed: " + e.getMessage();
        }
    }
}