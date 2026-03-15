package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Admin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class AdminDao {

    private final MongoCollection<Document> adminCollection;

    public AdminDao() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.adminCollection = database.getCollection("admins");
    }

    public Admin findByUsername(String username) {
        Document doc = adminCollection.find(new Document("username", username)).first();

        if (doc == null) {
            return null;
        }
        
        // Creating Admin object to get admin data from db collection
        Admin admin = new Admin();
        admin.setId(doc.getObjectId("_id").toHexString());
        admin.setUsername(doc.getString("username"));
        admin.setPassword(doc.getString("password"));
        admin.setFullName(doc.getString("fullName"));

        return admin;
    }
}