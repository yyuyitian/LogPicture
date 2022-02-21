/* Copyright � 2015 Oracle and/or its affiliates. All rights reserved. */
package com.example.employees;

import java.io.IOException;
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
        name = "AnalysisServlet",
        urlPatterns = {"/analysis"}
)
public class AnalysisServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "create":
                createPicture(req, resp);
                break;
        }

    }

    private void createPicture(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        System.out.println(name);
    }

}
