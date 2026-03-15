package com.mohid.masu.resource;

import com.mohid.masu.dao.StudentDao;
import com.mohid.masu.dto.CreateStudentRequest;
import com.mohid.masu.dto.LoginRequest;
import com.mohid.masu.model.Student;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private final StudentDao studentDao = new StudentDao();

    @POST
    public Response createStudent(CreateStudentRequest request) {

        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setPassword(request.getPassword());
        student.setFullName(request.getFullName());
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

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

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

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok("{\"message\":\"Student API working\"}").build();
    }
}