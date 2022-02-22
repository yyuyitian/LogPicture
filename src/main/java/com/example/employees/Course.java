package com.example.employees;

import com.fasterxml.jackson.annotation.JsonSetter;

public class Course {
    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getCourse() {
        return course;
    }
    @JsonSetter("cource")
    public void setCourse(String course) {
        this.course = course;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }
    @JsonSetter("seats available")
    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    String classname;
    String course;
    String instructor;
    String language;
    int seatsAvailable;
    int fees;

}
