package com.mohid.masu.resource;

import com.mohid.masu.dao.AdminDao;
import com.mohid.masu.dto.LoginRequest;
import com.mohid.masu.dto.UpdatePasswordRequest;
import com.mohid.masu.model.Admin;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

        return Response.ok("{\"message\":\"Admin login successful\",\"id\":\"" 
               + admin.getId() + "\",\"username\":\"" 
               + admin.getUsername() + "\",\"fullName\":\"" 
               + admin.getFullName() + "\"}").build();
    }
    
    // PUT API to update admin pass
    @PUT
    @Path("/{id}/password")
    public Response updateAdminPassword(@PathParam("id") String id, UpdatePasswordRequest request) {

        if (request == null
                || request.getOldPassword() == null || request.getOldPassword().isBlank()
                || request.getNewPassword() == null || request.getNewPassword().isBlank()
                || request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Old password, new password and confirm password are required\"}")
                    .build();
        }

        Admin admin = adminDao.findById(id);

        if (admin == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Admin not found\"}")
                    .build();
        }

        if (!admin.getPassword().equals(request.getOldPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Old password is incorrect\"}")
                    .build();
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"New password and confirm password do not match\"}")
                    .build();
        }

        boolean updated = adminDao.updatePassword(id, request.getNewPassword());

        if (!updated) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Failed to update password\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Admin password updated successfully\"}").build();
    }
}