package com.decker.sean.business;

import companydata.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.*;
import java.sql.Timestamp;
import java.text.*;
import java.io.*;
import javax.print.attribute.standard.Media;
import jakarta.json.*;
import java.text.SimpleDateFormat;

public class BusinessLayer {
    
    DataLayer dl = null;

    //
    // COMPANY FUNCTIONS
    //

    public String deleteCompany(String company) {

        int rowsDeleted;

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid company input.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            rowsDeleted = dl.deleteCompany(company);

            if (rowsDeleted == 0) {
                return ("{\n"
                    + "\"error\":\"No company was deleted.\"\n" 
                + "}");
            } else {
                return ("{\n"
                    + "\"success\":\"" + company + "\'s information deleted.\"\n" 
                + "}");
            }

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not delete the specified company. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends deleteCompany

    //
    // DEPARTMENT FUNCTIONS
    //
    
    public String getDepartment(String company, int dept_id) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            Department department = dl.getDepartment(company, dept_id);

            return("{\n"
                + "\"dept_id\":" + department.getId() + ",\n" 
                + "\"company\":\"" + department.getCompany() + "\",\n"
                + "\"dept_name\":\"" + department.getDeptName() + "\",\n"
                + "\"dept_no\":\"" + department.getDeptNo() + "\",\n"
                + "\"location\":\"" + department.getLocation() + "\"\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not retrieve the specified department. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getDepartment
    
    public String getDepartments(String company) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            List<Department> departments = dl.getAllDepartment(company);
            String returnString = "[\n";

            for (int i = 0; i < departments.size(); i++) {
 
                returnString = returnString + 
                "{\n"
                    + "\"dept_id\":" + departments.get(i).getId() + ",\n" 
                    + "\"company\":\"" + departments.get(i).getCompany() + "\",\n"
                    + "\"dept_name\":\"" + departments.get(i).getDeptName() + "\",\n"
                    + "\"dept_no\":\"" + departments.get(i).getDeptNo() + "\",\n"
                    + "\"location\":\"" + departments.get(i).getLocation() + "\"\n"
                + "}";
                
                // Adds comma to the end of object if it is not the last item in the list
                if (i < (departments.size() - 1)) {
                    returnString = returnString + ",";
                } // Ends if

            } // Ends for

            returnString = returnString + "\n]";
            return returnString;

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not retrieve the departments for the specified company. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getDepartments

    public String insertDepartment(String company, String dept_name, String dept_no, String location) {

        String unique_dept_no = verifyDepartmentNoUnique(company, dept_no);

        if (
            !validateString(company, 10) || !validateString(dept_name, 255) ||
            !validateString(dept_no, 20) || !validateString(location, 255)
        ) {
            return ("{\n"
                + "\"error\":\"Invalid input. Correct fields and try again.\"\n" 
            + "}");
        } else if (unique_dept_no.equals("400")) {
            return ("{\n"
                + "\"error\":\"Department number must be unique.\"\n" 
            + "}");
        } // Ends validation if

        try {

            dl = new DataLayer(company);
            Department department = new Department(company, dept_name, unique_dept_no, location);
            Department insertedDepartment = dl.insertDepartment(department);

            return("{\n"
                + "\"success\":{\n"
                    + "\"dept_id\":" + insertedDepartment.getId() + ",\n" 
                    + "\"company\":\"" + insertedDepartment.getCompany() + "\",\n"
                    + "\"dept_name\":\"" + insertedDepartment.getDeptName() + "\",\n"
                    + "\"dept_no\":\"" + insertedDepartment.getDeptNo() + "\",\n"
                    + "\"location\":\"" + insertedDepartment.getLocation() + "\"\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not add the specified department. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends insertDepartment
    
    public String updateDepartment(String inputJSON) {

        final JSONObject obj = new JSONObject(inputJSON);

        try {

            if (!validateString(obj.getString("company"), 10)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for company.\"\n" 
                + "}");
            }

            dl = new DataLayer(obj.getString("company"));

            Department department = dl.getDepartment(obj.getString("company"), obj.getInt("dept_id"));

            if ( department == null ) {
                return ("{\n"
                    + "\"error\":\"Specified department does not exist.\"\n" 
                + "}");
            }
            
            if ( obj.has("dept_name") ) {

                if (!validateString(obj.getString("dept_name"), 255)) { 
                    return ("{\n"
                        + "\"error\":\"Invalid input for department name.\"\n" 
                    + "}");
                }
                department.setDeptName(obj.getString("dept_name"));

            }
            
            if ( obj.has("dept_no") ) {

                String unique_dept_no = verifyDepartmentNoUnique(obj.getString("company"), obj.getString("dept_no"));

                if ( !validateString(unique_dept_no, 20) ) { 
                    return ("{\n"
                        + "\"error\":\"Invalid input for department number.\"\n" 
                    + "}");
                } else if (unique_dept_no.equals("400")) {
                    return ("{\n"
                        + "\"error\":\"Department number must be unique.\"\n" 
                    + "}");
                } // Ends if

                department.setDeptNo(unique_dept_no);

            }
            
            if ( obj.has("location") ) {

                if (!validateString(obj.getString("location"), 255)) { 
                    return ("{\n"
                        + "\"error\":\"Invalid input for location.\"\n" 
                    + "}"); 
                }
                department.setLocation(obj.getString("location"));

            } // Ends if else

            Department updatedDepartment = dl.updateDepartment(department);

            return("{\n"
                + "\"success\":{\n"
                    + "\"dept_id\":" + updatedDepartment.getId() + ",\n" 
                    + "\"company\":\"" + updatedDepartment.getCompany() + "\",\n"
                    + "\"dept_name\":\"" + updatedDepartment.getDeptName() + "\",\n"
                    + "\"dept_no\":\"" + updatedDepartment.getDeptNo() + "\",\n"
                    + "\"location\":\"" + updatedDepartment.getLocation() + "\",\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not update the specified department. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends updateDepartment
    
    public String deleteDepartment(String company, int dept_id) {

        int rowsDeleted;

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);

            if (!verifyEmployeeDepartmentExists(company, dept_id)) {
                return ("{\n"
                    + "\"error\":\"Department does not exist.\"\n" 
                + "}");
            }

            List<Employee> employees = dl.getAllEmployee(company);
            for (int i = 0; i < employees.size(); i++) {

                if (employees.get(i).getDeptId() == dept_id) {
                    deleteEmployee(company, employees.get(i).getId());
                }

            } // Ends for

            rowsDeleted = dl.deleteDepartment(company, dept_id);

            if (rowsDeleted == 0) {
                return ("{\n"
                    + "\"error\":\"No departments were deleted.\"\n" 
                + "}");
            } else {
                return ("{\n"
                    + "\"success\":\"Department" + dept_id + " from " + company + " deleted.\"\n" 
                + "}");
            }

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not delete the specified department. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends deleteDepartment

    //
    // EMPLOYEE FUNCTIONS
    //

    public String getEmployee(String company, int emp_id) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            Employee employee = dl.getEmployee(emp_id);

            return("{\n"
                + "\"emp_id\":" + employee.getId() + ",\n" 
                + "\"emp_name\":\"" + employee.getEmpName() + "\",\n"
                + "\"emp_no\":\"" + employee.getEmpNo() + "\",\n"
                + "\"hire_date\":\"" + employee.getHireDate() + "\",\n"
                + "\"job\":\"" + employee.getJob() + "\",\n"
                + "\"salary\":" + employee.getSalary() + ",\n"
                + "\"dept_id\":" + employee.getDeptId() + ",\n"
                + "\"mng_id\":" + employee.getMngId() + "\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                    + "\"error\":\"Unable to fetch the requested employee. Correct fields and try again.\"\n" 
                + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getEmployee

    public String getEmployees(String company) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            List<Employee> employees = dl.getAllEmployee(company);
            String returnString = "[\n";

            for (int i = 0; i < employees.size(); i++) {
 
                returnString = returnString + 
                "{\n"
                    + "\"emp_id\":" + employees.get(i).getId() + ",\n" 
                    + "\"emp_name\":\"" + employees.get(i).getEmpName() + "\",\n"
                    + "\"emp_no\":\"" + employees.get(i).getEmpNo() + "\",\n"
                    + "\"hire_date\":\"" + employees.get(i).getHireDate() + "\",\n"
                    + "\"job\":\"" + employees.get(i).getJob() + "\",\n"
                    + "\"salary\":" + employees.get(i).getSalary() + ",\n"
                    + "\"dept_id\":" + employees.get(i).getDeptId() + ",\n"
                    + "\"mng_id\":" + employees.get(i).getMngId() + "\n"
                + "}";
                
                // Adds comma to the end of object if it is not the last item in the list
                if (i < (employees.size() - 1)) {
                    returnString = returnString + ",";
                } // Ends if

            } // Ends for

            returnString = returnString + "\n]";
            return returnString;

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Unable to fetch requested list of employees. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getEmployees

    public String insertEmployee(String company, String emp_name, String emp_no, String hire_date, String job, Double salary, int dept_id, int mng_id) {

        String unique_emp_no = verifyEmployeeNoUnique(company, emp_no);

        if (
            !validateString(company, 10) || !validateString(emp_name, 50) ||
            !validateString(unique_emp_no, 20) || !validateString(job, 30)
        ) {
            return ("{\n"
                + "\"error\":\"Invalid input. Check fields and try again.\"\n" 
            + "}");
        } else if (!verifyEmployeeExists(company, mng_id)) {
            mng_id = 0;
        } else if (!verifyEmployeeDepartmentExists(company, dept_id)) {
            return ("{\n"
                + "\"error\":\"No department exists with the specified department ID. Correct this field and try again.\"\n" 
            + "}");
        } else if (unique_emp_no.equals("400")) {
            return ("{\n"
                + "\"error\":\"Employee number must be unique. Correct this field and try again.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);

            java.sql.Date formatted_hire_date = validateDate(hire_date);
            if (formatted_hire_date == null) { 
                return ("{\n"
                    + "\"error\":\"Invalid format for hire date.\"\n" 
                + "}"); 
            } else if (!verifyEmployeeHireDate(formatted_hire_date)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for hire date.\"\n" 
                + "}"); 
            }

            Employee employee = new Employee(emp_name, unique_emp_no, formatted_hire_date, job, salary, dept_id, mng_id);
            Employee insertedEmployee = dl.insertEmployee(employee);

            return("{\n"
                + "\"success\":{\n"
                    + "\"emp_id\":" + insertedEmployee.getId() + ",\n" 
                    + "\"emp_name\":\"" + insertedEmployee.getEmpName() + "\",\n"
                    + "\"emp_no\":\"" + insertedEmployee.getEmpNo() + "\",\n"
                    + "\"hire_date\":\"" + insertedEmployee.getHireDate() + "\",\n"
                    + "\"job\":\"" + insertedEmployee.getJob() + "\",\n"
                    + "\"salary\":" + insertedEmployee.getSalary() + ",\n"
                    + "\"dept_id\":" + insertedEmployee.getDeptId() + ",\n"
                    + "\"mng_id\":" + insertedEmployee.getMngId() + "\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not add the specified employee. Correct fields and try again.\"\n" 
            + "}"); 
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends insertEmployee

    public String updateEmployee(String inputJSON) {

        final JSONObject obj = new JSONObject(inputJSON);

        try {

            if (!validateString(obj.getString("company"), 10)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for company.\"\n" 
                + "}");
            }

            dl = new DataLayer(obj.getString("company"));

            Employee employee = dl.getEmployee(obj.getInt("emp_id"));

            if (employee == null) {
                return ("{\n"
                    + "\"error\":\"The specified employee does not exist\"\n" 
                + "}"); 
            }
            
            if ( obj.has("emp_name") ) {

                if (!validateString(obj.getString("emp_name"), 50)) {
                    return ("{\n"
                        + "\"error\":\"Invalid input for employee name.\"\n" 
                    + "}"); 
                }
                employee.setEmpName(obj.getString("emp_name"));

            }
            
            if ( obj.has("emp_no") ) {

                String unique_emp_no = verifyEmployeeNoUnique(obj.getString("company"), obj.getString("emp_no"));

                if (!validateString(unique_emp_no, 20)) { 
                    return ("{\n"
                        + "\"error\":\"Invalid input for employee number.\"\n" 
                    + "}"); 
                } else if (unique_emp_no.equals("400")) {
                    return ("{\n"
                        + "\"error\":\"Employee number must be unique.\"\n" 
                    + "}"); 
                }

                employee.setEmpNo(unique_emp_no);

            }
            
            if ( obj.has("hire_date") ) {

                java.sql.Date formatted_hire_date = validateDate(obj.getString("hire_date"));
                if (formatted_hire_date == null) {
                    return ("{\n"
                        + "\"error\":\"Invalid format for hire date.\"\n" 
                    + "}"); 
                } else if (!verifyEmployeeHireDate(formatted_hire_date)) {
                    return ("{\n"
                        + "\"error\":\"Invalid input for hire date.\"\n" 
                    + "}"); 
                }
                employee.setHireDate(formatted_hire_date);

            }
            
            if ( obj.has("job") ) {

                if (!validateString(obj.getString("job"), 30)) {
                    return ("{\n"
                        + "\"error\":\"Invalid input for employee job.\"\n" 
                    + "}"); 
                }
                employee.setJob(obj.getString("job"));

            }
            
            if ( obj.has("salary") ) {

                try {
                    employee.setSalary(obj.getDouble("salary"));
                } catch (Exception e) {
                    return ("{\n"
                            + "\"error\":\"Invalid input for employee salary.\"\n" 
                        + "}");  
                } // Ends try catch

            }
            
            if ( obj.has("dept_id") ) {

                try {

                    if (!verifyEmployeeDepartmentExists(obj.getString("company"), obj.getInt("dept_id"))) {
                        return ("{\n"
                            + "\"error\":\"Specified department does not exist.\"\n" 
                        + "}"); 
                    }
                    employee.setDeptId(obj.getInt("dept_id"));

                } catch (Exception e) {
                    return ("{\n"
                            + "\"error\":\"Invalid input for department ID.\"\n" 
                        + "}");  
                } // Ends try catch

            }
            
            if ( obj.has("mng_id") ) {

                try {

                    if (!verifyEmployeeExists(obj.getString("company"), obj.getInt("mng_id"))) {
                        employee.setMngId(0);
                    } else {
                        employee.setMngId(obj.getInt("mng_id"));
                    }

                } catch (Exception e) {
                    return ("{\n"
                            + "\"error\":\"Invalid input for manager ID.\"\n" 
                        + "}");  
                } // Ends try catch
            
            } // Ends if else

            Employee updatedEmployee = dl.updateEmployee(employee);

            return("{\n"
                + "\"success\":{\n"
                    + "\"emp_id\":" + updatedEmployee.getId() + ",\n" 
                    + "\"emp_name\":\"" + updatedEmployee.getEmpName() + "\",\n"
                    + "\"emp_no\":\"" + updatedEmployee.getEmpNo() + "\",\n"
                    + "\"hire_date\":\"" + updatedEmployee.getHireDate() + "\",\n"
                    + "\"job\":\"" + updatedEmployee.getJob() + "\",\n"
                    + "\"salary\":" + updatedEmployee.getSalary() + ",\n"
                    + "\"dept_id\":" + updatedEmployee.getDeptId() + ",\n"
                    + "\"mng_id\":" + updatedEmployee.getMngId() + "\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not update the specified employee. Correct fields and try again.\"\n" 
            + "}"); 
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends insertEmployee

    public String deleteEmployee(String company, int emp_id) {

        int rowsDeleted;

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);

            if (!verifyEmployeeExists(company, emp_id)) {
                return ("{\n"
                    + "\"error\":\"Employee does not exist.\"\n" 
                + "}");
            }

            List<Timecard> timecards = dl.getAllTimecard(emp_id);

            for (int i = 0; i < timecards.size(); i++) {
                dl.deleteTimecard(timecards.get(i).getId());
            } // Ends for

            List<Employee> employees = dl.getAllEmployee(company);

            for (int i = 0; i < employees.size(); i++) {

                if (employees.get(i).getMngId() == emp_id) {
                    employees.get(i).setMngId(0);
                    dl.updateEmployee(employees.get(i));
                }
                
            } // Ends for

            rowsDeleted = dl.deleteEmployee(emp_id);

            if (rowsDeleted == 0) {
                return ("{\n"
                    + "\"error\":\"No employees deleted.\"\n" 
                + "}");
            } else {

                return ("{\n"
                    + "\"success\":\"Employee " + emp_id + " deleted.\"\n" 
                + "}");
            }

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not delete specified employee. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends deleteEmployee

    //
    // TIMECARD FUNCTIONS
    // 

    public String getTimecard(String company, int timecard_id) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            Timecard timecard = dl.getTimecard(timecard_id);

            return("{\n"
                + "\"timecard\":{\n"
                    + "\"timecard_id\":" + timecard.getId() + ",\n" 
                    + "\"start_time\":\"" + timecard.getStartTime() + "\",\n"
                    + "\"end_time\":\"" + timecard.getEndTime() + "\",\n"
                    + "\"emp_id\":" + timecard.getEmpId() + ",\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not retrieve specified timecard. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getTimecard

    public String getTimecards(String company, int emp_id) {

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            List<Timecard> timecards = dl.getAllTimecard(emp_id);
            String returnString = "[\n";

            for (int i = 0; i < timecards.size(); i++) {
 
                returnString = returnString + 
                "{\n"
                    + "\"timecard_id\":" + timecards.get(i).getId() + ",\n" 
                    + "\"start_time\":\"" + timecards.get(i).getStartTime() + "\",\n"
                    + "\"end_time\":\"" + timecards.get(i).getEndTime() + "\",\n"
                    + "\"emp_id\":" + timecards.get(i).getEmpId() + ",\n"
                + "}";
                
                // Adds comma to the end of object if it is not the last item in the list
                if (i < (timecards.size() - 1)) {
                    returnString = returnString + ",";
                } // Ends if

            } // Ends for

            returnString = returnString + "\n]";
            return returnString;

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Could not retrieve list of timecards. Correct fields and try again\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends getTimecards

    public String insertTimecard(String company, int emp_id, String start_time, String end_time) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        } else if (!verifyEmployeeExists(company, emp_id)) {
            return ("{\n"
                + "\"error\":\"Specified employee does not exist.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);

            //Timestamp formatted_start_time = new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time).getTime() );
            //Timestamp formatted_end_time = new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time).getTime() );
            Timestamp formatted_start_time = validateTimestamp(start_time);
            if (formatted_start_time == null) { 
                return ("{\n"
                    + "\"error\":\"Invalid format for start time.\"\n" 
                + "}");
            } else if (!verifyTimecardStartTime(formatted_start_time)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for start time.\"\n" 
                + "}");
            } else if (!verifyTimecardNoStartTimeMatch(company, emp_id, formatted_start_time)) {
                return ("{\n"
                    + "\"error\":\"Timecard already exists on this day for specified employee.\"\n" 
                + "}");
            }

            Timestamp formatted_end_time = validateTimestamp(end_time);
            if (formatted_end_time == null) { 
                return ("{\n"
                    + "\"error\":\"Invalid format for end time.\"\n" 
                + "}");
            } else if (!verifyTimecardEndTime(formatted_end_time, formatted_start_time)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for end time.\"\n" 
                + "}");
            }

            Timecard timecard = new Timecard(formatted_start_time, formatted_end_time, emp_id);
            Timecard insertedTimecard = dl.insertTimecard(timecard);

            String string_start_time = df.format(insertedTimecard.getStartTime());
            String string_end_time = df.format(insertedTimecard.getEndTime());

            return("{\n"
                + "\"success\":{\n"
                    + "\"timecard_id\":" + insertedTimecard.getId() + ",\n" 
                    + "\"start_time\":\"" + string_start_time + "\",\n"
                    + "\"end_time\":\"" + string_end_time + "\",\n"
                    + "\"emp_id\":" + insertedTimecard.getEmpId() + ",\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Timecard could not be added. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends insertTimecard

    public String updateTimecard(String inputJSON) {

        final JSONObject obj = new JSONObject(inputJSON);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            if (!validateString(obj.getString("company"), 10)) {
                return ("{\n"
                    + "\"error\":\"Invalid input for company.\"\n" 
                + "}");
            }

            dl = new DataLayer(obj.getString("company"));

            Timecard timecard = dl.getTimecard(obj.getInt("timecard_id"));
            
            if (timecard == null) {
                return ("{\n"
                    + "\"error\":\"Timecard does not exist.\"\n" 
                + "}");
            }
            
            if ( obj.has("start_time") ) {

                Timestamp formatted_start_time = validateTimestamp(obj.getString("start_time"));

                if (formatted_start_time == null) { 
                    return ("{\n"
                        + "\"error\":\"Invalid format for start time.\"\n" 
                    + "}");
                } else if (!verifyTimecardStartTime(formatted_start_time)) {
                    return ("{\n"
                        + "\"error\":\"Invalid input for start time.\"\n" 
                    + "}");
                }

                timecard.setStartTime(formatted_start_time);

            }
            
            if ( obj.has("end_time") ) {

                Timestamp formatted_end_time = validateTimestamp(obj.getString("end_time"));

                if (formatted_end_time == null) { 
                    return ("{\n"
                        + "\"error\":\"Invalid format for end time.\"\n" 
                    + "}");
                } else if (!verifyTimecardEndTime(formatted_end_time, timecard.getStartTime())) {
                    return ("{\n"
                        + "\"error\":\"Invalid input for end time.\"\n" 
                    + "}");
                }

                timecard.setEndTime(formatted_end_time);

            }
            
            if ( obj.has("emp_id") ) {

                try {

                    if (!verifyEmployeeExists(obj.getString("company"), obj.getInt("emp_id"))) {
                        return ("{\n"
                            + "\"error\":\"No employee exists with the specified employee ID\"\n" 
                        + "}");
                    } else {
                        timecard.setEmpId(obj.getInt("emp_id"));
                    } // Ends emp_id if

                } catch (Exception e) {
                    return ("{\n"
                            + "\"error\":\"Invalid input for employee ID.\"\n" 
                        + "}");  
                } // Ends try catch

            } // Ends validation if

            // Checks for timecards on the same day
            if (!verifyTimecardNoStartTimeMatch( obj.getString("company"), timecard.getEmpId(), timecard.getStartTime() )) {
                return ("{\n"
                    + "\"error\":\"Timecard already exists on this day for specified employee.\"\n" 
                + "}");
            }

            Timecard updatedTimecard = dl.updateTimecard(timecard);

            String string_start_time = df.format(updatedTimecard.getStartTime());
            String string_end_time = df.format(updatedTimecard.getEndTime());

            return("{\n"
                + "\"success\":{\n"
                + "\"timecard_id\":" + updatedTimecard.getId() + ",\n" 
                + "\"start_time\":\"" + string_start_time + "\",\n"
                + "\"end_time\":\"" + string_end_time + "\",\n"
                + "\"emp_id\":" + updatedTimecard.getEmpId() + ",\n"
                + "}\n"
            + "}");

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Timecard could not be updated. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends updateTimecard

    public String deleteTimecard(String company, int timecard_id) {

        int rowsDeleted;

        if (!validateString(company, 10)) {
            return ("{\n"
                + "\"error\":\"Invalid input for company.\"\n" 
            + "}");
        }

        try {

            dl = new DataLayer(company);
            rowsDeleted = dl.deleteTimecard(timecard_id);

            if (rowsDeleted == 0) {
                return ("{\n"
                    + "\"error\":\"No timecards were deleted.\"\n" 
                + "}");
            } else {
                return ("{\n"
                    + "\"success\":\"Timecard " + timecard_id + " deleted.\"\n" 
                + "}");
            }

        } catch(Exception e) {
            return ("{\n"
                + "\"error\":\"Timecard could not be deleted. Correct fields and try again.\"\n" 
            + "}");
        } finally {
            dl.close();
        } // Ends try catch

    } // Ends deleteTimecard

    //
    // GENERAL VALIDATION FUNCTIONS
    //

    public Boolean validateString(String inputString, int characterLimit) {
        inputString = inputString.replaceAll("\\s", "");
        if ( inputString.isEmpty() || inputString.length() > characterLimit ) {
            return false;
        } 
        return true;
    } // Ends validateString

    // Validates and formats a string date
    public java.sql.Date validateDate(String inputDate) {

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = formatter.parse(inputDate);
            return new java.sql.Date(date.getTime());

        } catch (Exception e) {
            return null;
        }

    } // Ends validateDate

    // Validates and formats a string timestamp
    public Timestamp validateTimestamp(String inputTimestamp) {

        try {

            Timestamp formatted_timestamp = new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputTimestamp).getTime() );
            return formatted_timestamp;

        } catch (Exception e) {
            return null;
        }

    } // Ends validateTimestamp

    //
    // ADDITIONAL VALIDATION FUNCTIONS 
    //

    // Return error if department already exists, returns dept_no with company appended if department does not exist
    public String verifyDepartmentNoUnique(String company, String dept_no) {
        
        // If the dept_no does not already contain the company name, it gets appended
        if ( !dept_no.contains(company) ) {
            dept_no = dept_no + company;
        }

        try {

            dl = new DataLayer(company);
            Department department = dl.getDepartmentNo(company, dept_no);

            if (department != null) {
                return "400";    
            } else { 
                return dept_no;
            }

        } catch (Exception e) {
            return "400";
        }

    } // Ends verifyDepartmentNoUnique

    // Return false if passed in ID does not return a valid employee
    public Boolean verifyEmployeeExists(String company, int emp_id) {
        
        try {

            dl = new DataLayer(company);
            Employee employee = dl.getEmployee(emp_id);

            if (employee == null) {
                return false;    
            } else { 
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyEmployeeManagerExists

    // Return false if passed in ID does not return a valid department
    public Boolean verifyEmployeeDepartmentExists(String company, int dept_id) {
        
        try {

            dl = new DataLayer(company);
            Department department = dl.getDepartment(company, dept_id);

            if (department == null) {
                return false;    
            } else { 
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyEmployeeDepartmentExists

    // Return false if hire_date is after today, or if hire_date is not on a weekday
    public Boolean verifyEmployeeHireDate(java.sql.Date hire_date) {

        try {

            java.sql.Date today = new java.sql.Date( Calendar.getInstance().getTime().getTime() );

            if (hire_date.after(today)) {
                return false;
            }

            Calendar c = Calendar.getInstance();
            c.setTime(hire_date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek == 1 || dayOfWeek == 7) {
                return false; 
            }

            return true;

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyEmployeeHireDate

    // Return error if emp_no already exists, returns emp_no with company appended if employee does not exist
    public String verifyEmployeeNoUnique(String company, String emp_no) {
        
        // If the emp_no does not already contain the company name, it gets appended
        if ( !emp_no.contains(company) ) {
            emp_no = company + "-" + emp_no;
        }

        try {

            dl = new DataLayer(company);
            List<Employee> employees = dl.getAllEmployee(company);
            String emp_no_from_list;

            // Loops through all employees, compare emp_no passed in to emp_no of employees in list, return error if any match
            for (int i = 0; i < employees.size(); i++) {
 
                emp_no_from_list = employees.get(i).getEmpNo();

                if (emp_no.equals(emp_no_from_list)) {
                    return "400";
                }

            } // End for

            return emp_no;

        } catch (Exception e) {
            return "400";
        }

    } // Ends verifyEmployeeNoUnique

    // Return false if start_time is not on a week day, is outside of time range, or is before today but not on a Monday
    public Boolean verifyTimecardStartTime(Timestamp start_time) {

        try {

            long now = System.currentTimeMillis();
            Timestamp today = new Timestamp(now);
            Calendar today_c = Calendar.getInstance();
            today_c.setTime(today);
            int todayDayOfYear = today_c.get(Calendar.DAY_OF_YEAR);
            int todayWeekOfYear = today_c.get(Calendar.WEEK_OF_YEAR);

            Calendar start_time_c = Calendar.getInstance();
            start_time_c.setTime(start_time);
            int dayOfWeek = start_time_c.get(Calendar.DAY_OF_WEEK);
            int hourOfDay = start_time_c.get(Calendar.HOUR_OF_DAY);
            int dayOfYear = start_time_c.get(Calendar.DAY_OF_YEAR);
            int weekOfYear = start_time_c.get(Calendar.WEEK_OF_YEAR);

            // Check hour, if it's outside range, yell at them, then check day, if it's a weekend, yell at them
            if (hourOfDay <= 8 || hourOfDay >= 18) {
                return false;
            } else if (dayOfWeek == 1 || dayOfWeek == 7) {
                return false; 
            }

            // Compare to day, if it's not the same, check if it's the same week, if not, yell at them
            if (dayOfYear != todayDayOfYear) {
                return (weekOfYear == todayWeekOfYear);
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyTimecardStartTime

    // Return false if a timecard already exists on the same day
    public Boolean verifyTimecardNoStartTimeMatch(String company, int emp_id, Timestamp start_time) {

        try {

            dl = new DataLayer(company);
            List<Timecard> timecards = dl.getAllTimecard(emp_id);

            Calendar start_time_c = Calendar.getInstance();
            start_time_c.setTime(start_time);
            int dayOfYear = start_time_c.get(Calendar.DAY_OF_YEAR);
            int year = start_time_c.get(Calendar.YEAR);

            // Vars for checking timecard list
            Calendar timecard_c = Calendar.getInstance();
            int timecardDayOfYear;
            int timecardYear;

            // Loops through all timecards, compares start_time passed in to start_time of timecards in list, returns false if any match
            for (int i = 0; i < timecards.size(); i++) {
 
                timecard_c.setTime(timecards.get(i).getStartTime());
                timecardDayOfYear = timecard_c.get(Calendar.DAY_OF_YEAR);
                timecardYear = timecard_c.get(Calendar.YEAR);

                if (dayOfYear == timecardDayOfYear && year == timecardYear) {
                    return false;
                }

            } // End for

            return true;

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyTimecardNoStartTimeMatch

    // Return false if end_time is outside time range, if end_time is not on the same day as start_time, or if end_date is not an hour after start_date
    public Boolean verifyTimecardEndTime(Timestamp end_time, Timestamp start_time) {

        try {

            Calendar start_time_c = Calendar.getInstance();
            start_time_c.setTime(start_time);
            int startDayOfYear = start_time_c.get(Calendar.DAY_OF_YEAR);

            Calendar end_time_c = Calendar.getInstance();
            end_time_c.setTime(end_time);
            int endHourOfDay = end_time_c.get(Calendar.HOUR_OF_DAY);
            int endDayOfYear = end_time_c.get(Calendar.DAY_OF_YEAR);

            // Check hour, if it's outside range, yell at them, then compare day of year to that of start_time, yell at them if different
            if (endHourOfDay <= 8 || endHourOfDay >= 18 || endDayOfYear != startDayOfYear) {
                return false;
            }

            // Add an hour to start_time and compare to end_time
            start_time.setTime(start_time.getTime() + 3600000);
            return end_time.after(start_time);

        } catch (Exception e) {
            return false;
        }

    } // Ends verifyTimecardEndTime

} // Ends BusinessLayer class