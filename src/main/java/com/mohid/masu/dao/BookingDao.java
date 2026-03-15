package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Booking;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class BookingDao {

    private final MongoCollection<Document> bookingCollection;

    public BookingDao() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.bookingCollection = database.getCollection("bookings");
    }

    // to validate if student has already booked
    public boolean hasStudentBooked(String eventId, String studentId) {
        Document doc = bookingCollection.find(
                new Document("eventId", eventId).append("studentId", studentId)
        ).first();

        return doc != null;
    }

    public long countBookingsForEvent(String eventId) {
        return bookingCollection.countDocuments(new Document("eventId", eventId));
    }

    public long countAlumniBookingsForEvent(String eventId) {
        return bookingCollection.countDocuments(
                new Document("eventId", eventId).append("bookingType", "ALUMNI")
        );
    }

    public void createBooking(Booking booking) {
        Document doc = new Document("eventId", booking.getEventId())
                .append("studentId", booking.getStudentId())
                .append("bookingType", booking.getBookingType())
                .append("bookingDate", booking.getBookingDate());

        bookingCollection.insertOne(doc);
    }

    // To fetch bookings that were booked by a particular student
    public List<Booking> getBookingsByStudentId(String studentId) {
        List<Booking> bookings = new ArrayList<>();
        // Using Cursor to iterate through the DB collection
        MongoCursor<Document> cursor = bookingCollection.find(new Document("studentId", studentId)).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();

            Booking booking = new Booking();
            booking.setId(doc.getObjectId("_id").toHexString());
            booking.setEventId(doc.getString("eventId"));
            booking.setStudentId(doc.getString("studentId"));
            booking.setBookingType(doc.getString("bookingType"));
            booking.setBookingDate(doc.getString("bookingDate"));

            bookings.add(booking);
        }

        return bookings;
    }
}