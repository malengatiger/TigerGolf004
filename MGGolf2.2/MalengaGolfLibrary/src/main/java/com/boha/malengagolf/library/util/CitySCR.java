package com.boha.malengagolf.library.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class CitySCR implements Serializable  {
    long stateID, countryID, cityID = System.currentTimeMillis();
    String cityName, stateName, countryName, urlSnippet;
    List<Course> courseList = new ArrayList<Course>();
    double latitude, longitude;
    boolean coursesSearchedFor;

    public boolean isCoursesSearchedFor() {
        return coursesSearchedFor;
    }

    public void setCoursesSearchedFor(boolean coursesSearchedFor) {
        this.coursesSearchedFor = coursesSearchedFor;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getCityID() {
        return cityID;
    }

    public void setCityID(long cityID) {
        this.cityID = cityID;
    }

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

    public String getUrlSnippet() {
        return urlSnippet;
    }

    public void setUrlSnippet(String urlSnippet) {
        this.urlSnippet = urlSnippet;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public long getStateID() {
        return stateID;
    }

    public void setStateID(long stateID) {
        this.stateID = stateID;
    }

    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
