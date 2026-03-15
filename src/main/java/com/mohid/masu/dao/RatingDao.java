package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Rating;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class RatingDao {

    private final MongoCollection<Document> ratingCollection;

    public RatingDao() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.ratingCollection = database.getCollection("ratings");
    }

    public boolean hasStudentRated(String eventId, String studentId) {
        Document doc = ratingCollection.find(
                new Document("eventId", eventId).append("studentId", studentId)
        ).first();

        return doc != null;
    }

    public void createRating(Rating rating) {
        Document doc = new Document("eventId", rating.getEventId())
                .append("studentId", rating.getStudentId())
                .append("stars", rating.getStars());

        ratingCollection.insertOne(doc);
    }

    public List<Rating> getRatingsByEventId(String eventId) {
        List<Rating> ratings = new ArrayList<>();
        MongoCursor<Document> cursor = ratingCollection.find(new Document("eventId", eventId)).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();

            Rating rating = new Rating();
            rating.setId(doc.getObjectId("_id").toHexString());
            rating.setEventId(doc.getString("eventId"));
            rating.setStudentId(doc.getString("studentId"));
            rating.setStars(doc.getInteger("stars"));

            ratings.add(rating);
        }

        return ratings;
    }

    public double getAverageRatingForEvent(String eventId) {
        List<Rating> ratings = getRatingsByEventId(eventId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getStars();
        }

        return (double) sum / ratings.size();
    }
}