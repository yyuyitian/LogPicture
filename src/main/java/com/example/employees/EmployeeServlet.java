/* Copyright � 2015 Oracle and/or its affiliates. All rights reserved. */
package com.example.employees;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "EmployeeServlet",
        urlPatterns = {"/employee"}
)
public class EmployeeServlet extends HttpServlet {

    EmployeeService employeeService = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("searchAction");
        if (action!=null){
            switch (action) {           
            case "searchById":
                searchEmployeeById(req, resp);
                break;           
            case "searchByName":
                searchEmployeeByName(req, resp);
                break;
            }
        }else{
            List<Employee> result = employeeService.getAllEmployees();
            forwardListEmployees(req, resp, result);
        }
    }

    private void searchEmployeeById(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long idEmployee = Integer.valueOf(req.getParameter("idEmployee"));
        Employee employee = null;
        try {
            employee = employeeService.getEmployee(idEmployee);
        } catch (Exception ex) {
            Logger.getLogger(EmployeeServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        req.setAttribute("employee", employee);
        req.setAttribute("action", "edit");
        String nextJSP = "/jsp/new-employee.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req, resp);
    }
    
    private void searchEmployeeByName(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String firstName = String.valueOf(req.getParameter("employeeName"));
        System.out.println(firstName);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("coachingclass");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("instructor",firstName));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = null;
            searchResponse = Main.client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getTotalHits().value > 0) {
                SearchHit[] searchHit = searchResponse.getHits().getHits();
                List<Course> courseList =new ArrayList<>();
                for (SearchHit hit : searchHit) {
                    String jsonStr = hit.getSourceAsString();
                    System.out.println(jsonStr);
                    Course course = jsonToPojo(jsonStr, Course.class);
                    courseList.add(course);
                }
                forwardListCourses(req, resp, courseList);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void forwardListCourses( HttpServletRequest req, HttpServletResponse resp, List courseList)
            throws ServletException, IOException {
            String nextJSP = "/jsp/list-employees.jsp";
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
            req.setAttribute("employeeList", courseList);
            dispatcher.forward(req, resp);
    }

    private void forwardListEmployees(HttpServletRequest req, HttpServletResponse resp, List employeeList)
            throws ServletException, IOException {
        String nextJSP = "/jsp/list-employees.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        req.setAttribute("employeeList", employeeList);
        dispatcher.forward(req, resp);
    }   
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "add":
                addEmployeeAction(req, resp);
                break;
            case "edit":
                editEmployeeAction(req, resp);
                break;            
            case "remove":
                removeEmployeeByName(req, resp);
                break;            
        }

    }

    private void addEmployeeAction(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String lastName = req.getParameter("lastName");
        String birthday = req.getParameter("birthDate");
        String role = req.getParameter("role");
        String department = req.getParameter("department");
        String email = req.getParameter("email");
        Employee employee = new Employee(name, lastName, birthday, role, department, email);
        long idEmployee = employeeService.addEmployee(employee);
        List<Employee> employeeList = employeeService.getAllEmployees();
        req.setAttribute("idEmployee", idEmployee);
        String message = "The new employee has been successfully created.";
        req.setAttribute("message", message);
        forwardListEmployees(req, resp, employeeList);
    }

    private void editEmployeeAction(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String lastName = req.getParameter("lastName");
        String birthday = req.getParameter("birthDate");
        String role = req.getParameter("role");
        String department = req.getParameter("department");
        String email = req.getParameter("email");
        long idEmployee = Integer.valueOf(req.getParameter("idEmployee"));
        Employee employee = new Employee(name, lastName, birthday, role, department, email, idEmployee);
        employee.setId(idEmployee);
        boolean success = employeeService.updateEmployee(employee);
        String message = null;
        if (success) {
            message = "The employee has been successfully updated.";
        }
        List<Employee> employeeList = employeeService.getAllEmployees();
        req.setAttribute("idEmployee", idEmployee);
        req.setAttribute("message", message);
        forwardListEmployees(req, resp, employeeList);
    }  

    private void removeEmployeeByName(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long idEmployee = Integer.valueOf(req.getParameter("idEmployee"));
        boolean confirm = employeeService.deleteEmployee(idEmployee);
        if (confirm){
            String message = "The employee has been successfully removed.";
            req.setAttribute("message", message);
        }
        List<Employee> employeeList = employeeService.getAllEmployees();
        forwardListEmployees(req, resp, employeeList);
    }

    /**
       * 将json结果集转化为对象
       *
       * @param jsonData json数据
       * @param beanType 对象中的object类型
       * @return
       */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = Main.MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
