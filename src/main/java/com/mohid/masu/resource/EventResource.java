package com.mohid.masu.resource;

import com.mohid.masu.dao.EventDao;
import com.mohid.masu.dao.StudentDao;
import com.mohid.masu.dto.CreateEventRequest;
import com.mohid.masu.model.Event;
import com.mohid.masu.model.Student;
import com.mohid.masu.dao.BookingDao;
import com.mohid.masu.dto.BookingRequest;
import com.mohid.masu.model.Booking;
import com.mohid.masu.dao.RatingDao;
import com.mohid.masu.dto.RatingRequest;
import com.mohid.masu.model.Rating;
import java.util.List;
import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventDao eventDao = new EventDao();
    private final StudentDao studentDao = new StudentDao();
    private final BookingDao bookingDao = new BookingDao();
    private final RatingDao ratingDao = new RatingDao();

    @POST
    public Response createEvent(CreateEventRequest request) {

        if (request == null
                || request.getPublisherId() == null
                || request.getTitle() == null
                || request.getType() == null
                || request.getDate() == null
                || request.getStartTime() == null
                || request.getEndTime() == null
                || request.getLocation() == null
                || request.getDescription() == null
                || request.getGender() == null
                || request.getCurrency() == null
                || request.getDuration() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Missing required fields\"}")
                    .build();
        }
        
        // Event Gender Check
        String eventGender = request.getGender().toUpperCase();

        if (!eventGender.equals("BOYS") && !eventGender.equals("GIRLS") && !eventGender.equals("BOTH")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Gender must be BOYS, GIRLS or BOTH\"}")
                    .build();
        }

        Student publisher = studentDao.findById(request.getPublisherId());

        if (publisher == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Publisher student not found\"}")
                    .build();
        }

        if ("ALUMNI".equalsIgnoreCase(publisher.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Alumni cannot create events\"}")
                    .build();
        }

        Event event = new Event();
        event.setPublisherId(request.getPublisherId());
        event.setTitle(request.getTitle());
        event.setType(request.getType());
        event.setDate(request.getDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setGender(eventGender);
        event.setCurrency(request.getCurrency());
        event.setDuration(request.getDuration());
        event.setCost(request.getCost());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setAlumniReservedSlots(request.getAlumniReservedSlots());

        eventDao.createEvent(event);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Event created successfully\"}")
                .build();
    }

    @GET
    public Response getAllEvents() {
        List<Event> events = eventDao.getAllEvents();
        return Response.ok(events).build();
    }

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") String id) {
        Event event = eventDao.getEventById(id);

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        return Response.ok(event).build();
    }

    @GET
    @Path("/search")
    public Response searchEvents(@QueryParam("type") String type,
                                 @QueryParam("date") String date,
                                 @QueryParam("location") String location,
                                 @QueryParam("gender") String gender) {
        List<Event> events = eventDao.searchEvents(type, date, location, gender);
        return Response.ok(events).build();
    }
    
    // POST API Call for booking an event
    @POST
    @Path("/{id}/book")
    public Response bookEvent(@PathParam("id") String eventId, BookingRequest request) {

        if (request == null || request.getStudentId() == null || request.getStudentId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Student ID is required\"}")
                    .build();
        }

        Event event = eventDao.getEventById(eventId);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        Student student = studentDao.findById(request.getStudentId());
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Student not found\"}")
                    .build();
        }

        if (bookingDao.hasStudentBooked(eventId, request.getStudentId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Student has already booked this event\"}")
                    .build();
        }

        String eventGender = event.getGender();
        String studentGender = student.getGender();

        if ("BOYS".equalsIgnoreCase(eventGender) && !"BOY".equalsIgnoreCase(studentGender)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Only boy students can register for this event\"}")
                    .build();
        }

        if ("GIRLS".equalsIgnoreCase(eventGender) && !"GIRL".equalsIgnoreCase(studentGender)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Only girl students can register for this event\"}")
                    .build();
        }

        long totalBookings = bookingDao.countBookingsForEvent(eventId);
        if (totalBookings >= event.getMaxParticipants()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Event is full\"}")
                    .build();
        }

        String bookingType = "STUDENT";

        if ("ALUMNI".equalsIgnoreCase(student.getStatus())) {
            long alumniBookings = bookingDao.countAlumniBookingsForEvent(eventId);

            if (alumniBookings >= event.getAlumniReservedSlots()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\":\"No alumni slots available for this event\"}")
                        .build();
            }

            bookingType = "ALUMNI";
        }

        Booking booking = new Booking();
        booking.setEventId(eventId);
        booking.setStudentId(request.getStudentId());
        booking.setBookingType(bookingType);
        booking.setBookingDate(LocalDate.now().toString());

        bookingDao.createBooking(booking);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Event booked successfully\"}")
                .build();
    }
    
    // POST API to mark the ratings of an eventy given by a student
    @POST
    @Path("/{id}/rate")
    public Response rateEvent(@PathParam("id") String eventId, RatingRequest request) {

        if (request == null || request.getStudentId() == null || request.getStudentId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Student ID is required\"}")
                    .build();
        }

        if (request.getStars() < 1 || request.getStars() > 5) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Rating must be between 1 and 5\"}")
                    .build();
        }

        Event event = eventDao.getEventById(eventId);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        Student student = studentDao.findById(request.getStudentId());
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Student not found\"}")
                    .build();
        }

        // Adding a Requirement Check / Constraint: Only students who booked the event can rate it
        if (!bookingDao.hasStudentBooked(eventId, request.getStudentId())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\":\"Only students who booked the event can rate it\"}")
                    .build();
        }

        if (ratingDao.hasStudentRated(eventId, request.getStudentId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Student has already rated this event\"}")
                    .build();
        }

        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setStudentId(request.getStudentId());
        rating.setStars(request.getStars());

        ratingDao.createRating(rating);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Rating submitted successfully\"}")
                .build();
    }
    
    @GET
    @Path("/{id}/ratings")
    public Response getEventRatings(@PathParam("id") String eventId) {

        Event event = eventDao.getEventById(eventId);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        List<Rating> ratings = ratingDao.getRatingsByEventId(eventId);
        return Response.ok(ratings).build();
    }
    
    @GET
    @Path("/{id}/ratings/average")
    public Response getAverageRating(@PathParam("id") String eventId) {

        Event event = eventDao.getEventById(eventId);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        double average = ratingDao.getAverageRatingForEvent(eventId);

        String responseJson = "{\"eventId\":\"" + eventId + "\",\"averageRating\":" + average + "}";
        return Response.ok(responseJson).build();
    }
}