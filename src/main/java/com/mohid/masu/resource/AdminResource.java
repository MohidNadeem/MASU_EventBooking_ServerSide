package com.mohid.masu.resource;

import com.mohid.masu.dao.AdminDao;
import com.mohid.masu.dto.LoginRequest;
import com.mohid.masu.model.Admin;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)  // Using Jersey
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private final AdminDao adminDao = new AdminDao();

    // I prefer using POST for the URLs to send the JSON object to functions
    // Will be testing using PostMan
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {  // Jersey converts incoming JSON body to object LoginRequest

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Username and password are required\"}")
                    .build();
        }

        Admin admin = adminDao.findByUsername(request.getUsername());

        if (admin == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .build();
        }

        if (!admin.getPassword().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Admin login successful\",\"username\":\"" 
               + admin.getUsername() + "\",\"fullName\":\"" + admin.getFullName() + "\"}").build(); }
}