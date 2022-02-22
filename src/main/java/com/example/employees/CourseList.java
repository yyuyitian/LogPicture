package com.example.employees;

import java.util.ArrayList;
import java.util.List;

public class CourseList {
    private static final List<Course> courseList = new ArrayList();

    private CourseList(){
    }

    public static List <Course> getInstance(){
        return courseList;
    }
}
