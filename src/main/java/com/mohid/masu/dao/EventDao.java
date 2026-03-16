package com.mohid.masu.dao;

import com.mohid.masu.config.MongoConnection;
import com.mohid.masu.model.Event;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

public class EventDao {

    private final MongoCollection<Document> eventCollection;

    public EventDao() {
        MongoDatabase database = MongoConnection.getDatabase();
        this.eventCollection = database.getCollection("events");
    }

    // Function for: Creating an Event
    public void createEvent(Event event) {
        Document doc = new Document("publisherId", event.getPublisherId())
                .append("title", event.getTitle())
                .append("type", event.getType())
                .append("date", event.getDate())
                .append("startTime", event.getStartTime())
                .append("endTime", event.getEndTime())
                .append("venueName", event.getVenueName())
                .append("location", event.getLocation())
                .append("postalCode", event.getPostalCode())
                .append("country", event.getCountry())
                .append("latitude", event.getLatitude())
                .append("longitude", event.getLongitude())
                .append("description", event.getDescription())
                .append("gender", event.getGender())
                .append("currency", event.getCurrency())
                .append("duration", event.getDuration())
                .append("cost", event.getCost())
                .append("maxParticipants", event.getMaxParticipants())
                .append("alumniReservedSlots", event.getAlumniReservedSlots());

        eventCollection.insertOne(doc);
    }

    // Function for: View all Events
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        MongoCursor<Document> cursor = eventCollection.find().iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            events.add(mapDocumentToEvent(doc));
        }

        return events;
    }

    // Function for: Get an Event
    public Event getEventById(String eventId) {
        Document doc = eventCollection.find(new Document("_id", new ObjectId(eventId))).first();

        if (doc == null) {
            return null;
        }

        return mapDocumentToEvent(doc);
    }

    // Function for: Search an Event (Either by Type, Date or Location of an Event)
    public List<Event> searchEvents(String type, String date, String location, String gender) {
        Document query = new Document();

        if (type != null && !type.isBlank()) {
            query.append("type", type);
        }
        if (date != null && !date.isBlank()) {
            query.append("date", date);
        }
        if (location != null && !location.isBlank()) {
            query.append("location", location);
        }
        if (gender != null && !gender.isBlank()) {
            query.append("gender", gender);
        }

        List<Event> events = new ArrayList<>();
        MongoCursor<Document> cursor = eventCollection.find(query).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            events.add(mapDocumentToEvent(doc));
        }

        return events;
    }

    // Function for: Mapping Function (Collection Doc -> Event Object)
    private Event mapDocumentToEvent(Document doc) {
        Event event = new Event();
        event.setId(doc.getObjectId("_id").toHexString());
        event.setPublisherId(doc.getString("publisherId"));
        event.setTitle(doc.getString("title"));
        event.setType(doc.getString("type"));
        event.setDate(doc.getString("date"));
        event.setStartTime(doc.getString("startTime"));
        event.setEndTime(doc.getString("endTime"));
        event.setVenueName(doc.getString("venueName"));
        event.setLocation(doc.getString("location"));
        event.setPostalCode(doc.getString("postalCode"));
        event.setCountry(doc.getString("country"));

        Object latObj = doc.get("latitude");
        if (latObj instanceof Number) {
            event.setLatitude(((Number) latObj).doubleValue());
        }

        Object lngObj = doc.get("longitude");
        if (lngObj instanceof Number) {
            event.setLongitude(((Number) lngObj).doubleValue());
        }

        event.setDescription(doc.getString("description"));
        event.setGender(doc.getString("gender"));
        event.setCurrency(doc.getString("currency"));
        event.setDuration(doc.getString("duration"));

        Object costObj = doc.get("cost");
        if (costObj instanceof Number) {
            event.setCost(((Number) costObj).doubleValue());
        }

        event.setMaxParticipants(doc.getInteger("maxParticipants", 0));
        event.setAlumniReservedSlots(doc.getInteger("alumniReservedSlots", 0));

        return event;
    }
}