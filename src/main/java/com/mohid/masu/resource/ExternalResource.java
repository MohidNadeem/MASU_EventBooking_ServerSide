package com.mohid.masu.resource;

import com.mohid.masu.dao.EventDao;
import com.mohid.masu.model.Event;
import com.mohid.masu.service.ExternalApiService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/external")
@Produces(MediaType.APPLICATION_JSON)
public class ExternalResource {

    private final EventDao eventDao = new EventDao();
    private final ExternalApiService externalApiService = new ExternalApiService();

    // GET API Endpoint to get Location Information (from GeoNames)
    @GET
    @Path("/events/{id}/location-info")
    public Response getLocationInfo(@PathParam("id") String eventId) {
        try {
            Event event = eventDao.getEventById(eventId);

            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Event not found\"}")
                        .build();
            }

            String locationInfo = externalApiService.getLocationInfo(event.getLocation());
            return Response.ok(locationInfo).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to fetch location info\"}")
                    .build();
        }
    }
    
    // GET API Endpoint for Nearby Locations (GeoNames)
    @GET
    @Path("/events/{id}/nearby-locations")
    public Response getNearbyLocationsForEvent(@PathParam("id") String eventId) {
        try {

            Event event = eventDao.getEventById(eventId);

            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Event not found\"}")
                        .build();
            }

            String result = externalApiService.getNearbyLocationsForEvent(
                    event.getPostalCode(),
                    event.getCountry()
            );

            return Response.ok(result).build();

        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to fetch nearby locations\"}")
                    .build();
        }
    }
    
    // GET API Endpoint to get Current Weather Information (from OpenWeather)
    @GET
    @Path("/events/{id}/weather")
    public Response getWeather(@PathParam("id") String eventId) {
        try {
            Event event = eventDao.getEventById(eventId);

            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Event not found\"}")
                        .build();
            }

            String weatherInfo = externalApiService.getWeatherByCoordinates(
                    event.getLatitude(),
                    event.getLongitude(),
                    event.getVenueName() + ", " + event.getLocation()
            );

            return Response.ok(weatherInfo).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to fetch weather info\"}")
                    .build();
        }
    }
    
    // GET API Endpoint to get Location Suggestions
    @GET
    @Path("/location-search")
    public Response searchLocationSuggestions(@QueryParam("q") String query) {
        try {
            if (query == null || query.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\":\"Query is required\"}")
                        .build();
            }

            String result = externalApiService.searchLocationSuggestions(query);
            return Response.ok(result).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to fetch location suggestions\"}")
                    .build();
        }
    }

    // GET API Endpoint to get static map preview image
    @GET
    @Path("/static-map")
    public Response getStaticMap(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
        try {
            String imageUrl = externalApiService.getStaticMapPreview(lat, lng);
            return Response.ok("{\"imageUrl\":\"" + imageUrl + "\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to generate static map preview\"}")
                    .build();
        }
    }

}