package com.boha.malengagolf.library.util;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class Course implements Serializable, Comparable<Course>  {
    long courseID = System.currentTimeMillis();
    long cityID;
    String cityName, courseName;
    double latitude, longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public int compareTo(Course course) {
        if (courseName == null) return 0;
        if (course.getCourseName() == null) return 0;
        return courseName.compareTo(course.courseName);
    }
}
