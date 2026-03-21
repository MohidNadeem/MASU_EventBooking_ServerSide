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
import com.mohid.masu.dto.EventFullDetailsResponse;
import com.mohid.masu.dto.UpdateEventRequest;
import com.mohid.masu.dto.UpdateEventStatusRequest;
import com.mohid.masu.service.ExternalApiService;
import java.util.List;
import java.time.LocalDate;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
    private final ExternalApiService externalApiService = new ExternalApiService();


    @POST
    public Response createEvent(CreateEventRequest request) {

        if (request == null
                || request.getPublisherId() == null
                || request.getTitle() == null
                || request.getType() == null
                || request.getDate() == null
                || request.getStartTime() == null
                || request.getEndTime() == null
                || request.getVenueName() == null
                || request.getLocation() == null
                || request.getCountry() == null
                || request.getPostalCode() == null
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
        event.setVenueName(request.getVenueName());
        event.setLocation(request.getLocation());
        event.setCountry(request.getCountry());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setPostalCode(request.getPostalCode());
        event.setDescription(request.getDescription());
        event.setGender(eventGender);
        event.setCurrency(request.getCurrency());
        event.setDuration(request.getDuration());
        event.setCost(request.getCost());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setAlumniReservedSlots(request.getAlumniReservedSlots());
        event.setStatus("ACTIVE");

        eventDao.createEvent(event);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Event created successfully\"}")
                .build();
    }
    
    private void attachRemainingCounts(Event event) {
        if (event == null) {
            return;
        }

        long totalBookings = bookingDao.countBookingsForEvent(event.getId());
        long alumniBookings = bookingDao.countAlumniBookingsForEvent(event.getId());

        int remainingSeats = event.getMaxParticipants() - (int) totalBookings;
        int remainingAlumniSlots = event.getAlumniReservedSlots() - (int) alumniBookings;

        event.setRemainingSeats(Math.max(remainingSeats, 0));
        event.setRemainingAlumniSlots(Math.max(remainingAlumniSlots, 0));
    }

    private void attachRemainingCounts(List<Event> events) {
        for (Event event : events) {
            attachRemainingCounts(event);
        }
    }

    @GET
    public Response getAllEvents() {
        eventDao.updatePassedEvents();
        List<Event> events = eventDao.getAllEvents();
        attachRemainingCounts(events);
        return Response.ok(events).build();
    }

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") String id) {
        eventDao.updatePassedEvents();
        Event event = eventDao.getEventById(id);

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        attachRemainingCounts(event);
        return Response.ok(event).build();
    }

    @GET
    @Path("/search")
    public Response searchEvents(@QueryParam("type") String type,
                                 @QueryParam("date") String date,
                                 @QueryParam("gender") String gender,
                                 @QueryParam("costType") String costType,
                                 @QueryParam("keyword") String keyword) {
        eventDao.updatePassedEvents();
        List<Event> events = eventDao.searchEvents(type, date, gender, costType, keyword);
        attachRemainingCounts(events);
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
        
        if ("CANCELLED".equalsIgnoreCase(event.getStatus())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"This event has been cancelled\"}")
                    .build();
        }

        if ("PASSED".equalsIgnoreCase(event.getStatus())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"This event has already passed\"}")
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
    
    // API Endpoint to get full details for an event
    @GET
    @Path("/{id}/full-details")
    public Response getFullDetails(@PathParam("id") String eventId) {
        try {
            Event event = eventDao.getEventById(eventId);

            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Event not found\"}")
                        .build();
            }

            attachRemainingCounts(event);

            double averageRating = ratingDao.getAverageRatingForEvent(eventId);

            String weather = externalApiService.getWeatherByCoordinates(
                    event.getLatitude(),
                    event.getLongitude(),
                    event.getVenueName() + ", " + event.getLocation()
            );

            String nearbyLocations = externalApiService.getNearbyLocationsForEvent(
                    event.getPostalCode(),
                    event.getCountry()
            );

            EventFullDetailsResponse response = new EventFullDetailsResponse();
            response.setEvent(event);
            response.setAverageRating(averageRating);
            response.setWeather(weather);
            response.setNearbyLocations(nearbyLocations);

            return Response.ok(response).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to fetch full event details\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateEvent(@PathParam("id") String eventId, UpdateEventRequest request) {

        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Request body is required\"}")
                    .build();
        }

        Event existingEvent = eventDao.getEventById(eventId);

        if (existingEvent == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        existingEvent.setDate(request.getDate());
        existingEvent.setStartTime(request.getStartTime());
        existingEvent.setEndTime(request.getEndTime());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setDuration(request.getDuration());
        existingEvent.setCost(request.getCost());
        existingEvent.setMaxParticipants(request.getMaxParticipants());
        existingEvent.setAlumniReservedSlots(request.getAlumniReservedSlots());

        boolean updated = eventDao.updateEvent(eventId, existingEvent);

        if (!updated) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to update event\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Event updated successfully\"}").build();
    }
    
    @PUT
    @Path("/{id}/status")
    public Response updateEventStatus(@PathParam("id") String eventId, UpdateEventStatusRequest request) {

        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Status is required\"}")
                    .build();
        }

        String status = request.getStatus().toUpperCase();

        if (!status.equals("ACTIVE") && !status.equals("CANCELLED") && !status.equals("PASSED")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Status must be ACTIVE, CANCELLED or PASSED\"}")
                    .build();
        }

        Event event = eventDao.getEventById(eventId);

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Event not found\"}")
                    .build();
        }

        boolean updated = eventDao.updateEventStatus(eventId, status);

        if (!updated) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to update event status\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Event status updated successfully\"}").build();
    }
    
    @DELETE
    @Path("/{id}/unbook")
    public Response unbookEvent(@PathParam("id") String eventId, BookingRequest request) {

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

        Booking booking = bookingDao.getBookingByEventAndStudent(eventId, request.getStudentId());
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No booking found for this student and event\"}")
                    .build();
        }

        if ("PASSED".equalsIgnoreCase(event.getStatus())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Cannot unbook a passed event\"}")
                    .build();
        }

        boolean deleted = bookingDao.deleteBookingById(booking.getId());

        if (!deleted) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to unbook event\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Event unbooked successfully\"}").build();
    }

}