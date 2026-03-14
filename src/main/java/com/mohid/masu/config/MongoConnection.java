package com.mohid.masu.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mohid.masu.util.PropertiesLoader;

// Created this class to get the Mongo DB Connection
// Will return the database client object 
public class MongoConnection {

    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() {

        if (mongoClient == null) {

            String uri = PropertiesLoader.getProperty("mongodb.uri");
            String databaseName = PropertiesLoader.getProperty("mongodb.database");

            mongoClient = MongoClients.create(uri);

            return mongoClient.getDatabase(databaseName);
        }

        String databaseName = PropertiesLoader.getProperty("mongodb.database");

        return mongoClient.getDatabase(databaseName);
    }
}