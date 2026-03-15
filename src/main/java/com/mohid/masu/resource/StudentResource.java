package com.mohid.masu.resource;

import com.mohid.masu.dao.StudentDao;
import com.mohid.masu.dto.CreateStudentRequest;
import com.mohid.masu.dto.LoginRequest;
import com.mohid.masu.dto.UpdateStudentStatusRequest;
import com.mohid.masu.model.Student;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private final StudentDao studentDao = new StudentDao();

    // POST API to create a new student (Used by Admin role)
    @POST
    public Response createStudent(CreateStudentRequest request) {

        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null
                || request.getFullName() == null
                || request.getGender() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"All fields are required\"}")
                    .build();
        }

        String gender = request.getGender().toUpperCase();

        if (!gender.equals("BOY") && !gender.equals("GIRL")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Gender must be BOY or GIRL\"}")
                    .build();
        }

        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setPassword(request.getPassword());
        student.setFullName(request.getFullName());
        student.setGender(gender);
        student.setStatus("ACTIVE");

        boolean created = studentDao.createStudent(student);

        if (!created) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Username already exists\"}")
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Student created successfully\"}")
                .build();
    }

    // POST API for Student Login
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Username and password are required\"}")
                    .build();
        }

        Student student = studentDao.findByUsername(request.getUsername());

        if (student == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .build();
        }

        if (!student.getPassword().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .build();
        }

        return Response.ok(student).build();
    }

    // PUT API to update Student Status (Active / Alumni)
    @PUT
    @Path("/{id}/status")
    public Response updateStudentStatus(@PathParam("id") String id, UpdateStudentStatusRequest request) {

        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Status is required\"}")
                    .build();
        }

        String status = request.getStatus().toUpperCase();

        if (!status.equals("ACTIVE") && !status.equals("ALUMNI")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Status must be ACTIVE or ALUMNI\"}")
                    .build();
        }

        boolean updated = studentDao.updateStudentStatus(id, status);

        if (!updated) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Student not found or status unchanged\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Student status updated successfully\"}").build();
    }

    // Just a Testing API
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok("{\"message\":\"Student API working\"}").build();
    }
}