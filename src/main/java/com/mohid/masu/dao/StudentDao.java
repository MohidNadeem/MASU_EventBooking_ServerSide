package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class StudentDao {

    private final MongoCollection<Document> studentCollection;

    public StudentDao() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.studentCollection = database.getCollection("students");
    }

    // Function for: Searching a Student
    public Student findByUsername(String username) {
        Document doc = studentCollection.find(new Document("username", username)).first();

        if (doc == null) {
            return null;
        }

        Student student = new Student();
        student.setId(doc.getObjectId("_id").toHexString());
        student.setUsername(doc.getString("username"));
        student.setPassword(doc.getString("password"));
        student.setFullName(doc.getString("fullName"));
        student.setStatus(doc.getString("status"));

        return student;
    }

    // Function for: Creating a new Student
    public boolean createStudent(Student student) {
        Document existing = studentCollection.find(new Document("username", student.getUsername())).first();

        if (existing != null) {
            return false;
        }

        Document doc = new Document("username", student.getUsername())
                .append("password", student.getPassword())
                .append("fullName", student.getFullName())
                .append("status", student.getStatus());

        studentCollection.insertOne(doc);
        return true;
    }
    
    // Function for: Updating Student Status (Active / Alumni)
    public boolean updateStudentStatus(String studentId, String status) {
        Document query = new Document("_id", new org.bson.types.ObjectId(studentId));
        Document update = new Document("$set", new Document("status", status));

        return studentCollection.updateOne(query, update).getModifiedCount() > 0;
    }
}