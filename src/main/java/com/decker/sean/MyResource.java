package com.decker.sean;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.decker.sean.business.*;
import java.io.*;
import javax.print.attribute.standard.Media;

@Path("CompanyServices")
public class MyResource {

    @Context
    UriInfo uriInfo;

    BusinessLayer bl = new BusinessLayer();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    // Path: /company | Verb: DELETE | Produces: JSON
    @Path("company")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompany(
        @QueryParam("company") String company
    ) {

        String response = bl.deleteCompany(company);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends DELETE/company

    // Path: /department | Verb: GET | Produces: JSON
    @Path("department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartment(
        @QueryParam("company") String company,
        @QueryParam("dept_id") int dept_id
    ) {

        String response = bl.getDepartment(company, dept_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/department

    // Path: /departments | Verb: GET | Produces: JSON
    @Path("departments")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartments(
        @QueryParam("company") String company
    ) {

        String response = bl.getDepartments(company);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/departments

    // Path: /department | Verb: POST | Produces: JSON
    @Path("department")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertDepartment(
        @FormParam("company") String company,
        @FormParam("dept_name") String dept_name,
        @FormParam("dept_no") String dept_no,
        @FormParam("location") String location
    ) {

        String response = bl.insertDepartment(company, dept_name, dept_no, location);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
            //return Response.status(Response.Status.CREATED).ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends POST/department     

    // Path: /department | Verb: PUT | Produces: JSON --NOT DONE
    @Path("department")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDepartment(
        String inputJSON
    ) {

        String response = bl.updateDepartment(inputJSON);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends PUT/department 

    // Path: /department | Verb: DELETE | Produces: JSON
    @Path("department")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDepartment(
        @QueryParam("company") String company,
        @QueryParam("dept_id") int dept_id
    ) {

        String response = bl.deleteDepartment(company, dept_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends DELETE/department

    // Path: /employee | Verb: GET | Produces: JSON
    @Path("employee")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployee(
        @QueryParam("company") String company,
        @QueryParam("emp_id") int emp_id
    ) {

        String response = bl.getEmployee(company, emp_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/employee

    // Path: /employees | Verb: GET | Produces: JSON
    @Path("employees")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployees(
        @QueryParam("company") String company
    ) {

        String response = bl.getEmployees(company);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/employees

    // Path: /employee | Verb: POST | Produces: JSON
    @Path("employee")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(
        @FormParam("company") String company,
        @FormParam("emp_name") String emp_name,
        @FormParam("emp_no") String emp_no,
        @FormParam("hire_date") String hire_date,
        @FormParam("job") String job,
        @FormParam("salary") Double salary,
        @FormParam("dept_id") int dept_id,
        @FormParam("mng_id") int mng_id
    ) {

        String response = bl.insertEmployee(company, emp_name, emp_no, hire_date, job, salary, dept_id, mng_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends POST/employee   

    // Path: /employee | Verb: PUT | Produces: JSON
    @Path("employee")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(
        String inputJSON
    ) {

        String response = bl.updateEmployee(inputJSON);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends PUT/employee

    // Path: /employee | Verb: DELETE | Produces: JSON
    @Path("employee")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(
        @QueryParam("company") String company,
        @QueryParam("emp_id") int emp_id
    ) {

        String response = bl.deleteEmployee(company, emp_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends DELETE/employee

    // Path: /timecard | Verb: GET | Produces: JSON
    @Path("timecard")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecard(
        @QueryParam("company") String company,
        @QueryParam("timecard_id") int timecard_id
    ) {

        String response = bl.getTimecard(company, timecard_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/timecard

    // Path: /timecards | Verb: GET | Produces: JSON
    @Path("timecards")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecards(
        @QueryParam("company") String company,
        @QueryParam("emp_id") int emp_id
    ) {

        String response = bl.getTimecards(company, emp_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends GET/timecards

    // Path: /timecard | Verb: POST | Produces: JSON
    @Path("timecard")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTimecard(
        @FormParam("company") String company,
        @FormParam("emp_id") int emp_id,
        @FormParam("start_time") String start_time,
        @FormParam("end_time") String end_time
    ) {

        String response = bl.insertTimecard(company, emp_id, start_time, end_time);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends POST/timecard   

    // Path: /timecard | Verb: PUT | Produces: JSON
    @Path("timecard")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTimecard(
        String inputJSON
    ) {

        String response = bl.updateTimecard(inputJSON);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends PUT/timecard

    // Path: /timecard | Verb: DELETE | Produces: JSON
    @Path("timecard")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTimecard(
        @QueryParam("company") String company,
        @QueryParam("timecard_id") int timecard_id
    ) {

        String response = bl.deleteTimecard(company, timecard_id);

        if (!response.contains("{\n\"error\":")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        } // Ends if else

    } // Ends DELETE/timecard

} // Ends MyResource class