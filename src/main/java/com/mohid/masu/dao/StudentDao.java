package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;

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

        return mapDocumentToStudent(doc);
    }

    // Function for: Search Student by Collection Data Id
    public Student findById(String id) {
        Document doc = studentCollection.find(new Document("_id", new ObjectId(id))).first();

        if (doc == null) {
            return null;
        }

        return mapDocumentToStudent(doc);
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
                .append("gender", student.getGender())
                .append("status", student.getStatus())
                .append("passwordUpdatedByStudent", false);

        studentCollection.insertOne(doc);
        return true;
    }

    // Function for: Updating Student Status (Active / Alumni)
    public boolean updateStudentStatus(String studentId, String status) {
        Document query = new Document("_id", new ObjectId(studentId));
        Document update = new Document("$set", new Document("status", status));

        return studentCollection.updateOne(query, update).getModifiedCount() > 0;
    }
    
    // Function for: Updating Student Pass
    public boolean updatePassword(String studentId, String newPassword) {
        Document query = new Document("_id", new ObjectId(studentId));

        Document updateFields = new Document()
                .append("password", newPassword)
                .append("passwordUpdatedByStudent", true);

        Document update = new Document("$set", updateFields);

        return studentCollection.updateOne(query, update).getModifiedCount() > 0;
    }
    
    // Function for: Get all Students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        MongoCursor<Document> cursor = studentCollection.find().iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            students.add(mapDocumentToStudent(doc));
        }

        return students;
    }
    
    // Function for: Mapping Doc Collection to Student Object
    private Student mapDocumentToStudent(Document doc) {
        Student student = new Student();
        student.setId(doc.getObjectId("_id").toHexString());
        student.setUsername(doc.getString("username"));
        student.setPassword(doc.getString("password"));
        student.setFullName(doc.getString("fullName"));
        student.setGender(doc.getString("gender"));
        student.setStatus(doc.getString("status"));
        student.setPasswordUpdatedByStudent(doc.getBoolean("passwordUpdatedByStudent", false));
        return student;
    }
}