package com.mohid.masu.resource;

import com.mohid.masu.dao.EventDao;
import com.mohid.masu.dao.StudentDao;
import com.mohid.masu.dto.CreateEventRequest;
import com.mohid.masu.model.Event;
import com.mohid.masu.model.Student;

import java.util.List;
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
}