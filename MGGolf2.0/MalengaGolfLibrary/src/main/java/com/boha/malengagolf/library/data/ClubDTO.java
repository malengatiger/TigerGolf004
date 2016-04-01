/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Aubrey Malabie
 */
@SuppressWarnings(value = "unused")
public class ClubDTO implements Serializable, Comparable<ClubDTO>{

    private int clubID;
    private String address;
    private String clubName, provinceName;
    private String email;
    private double latitude;
    private double longitude, distance;
    private String telephone;
    private int provinceID;
    private List<ClubCourseDTO> clubCourses;

     public List<ClubCourseDTO> getClubCourses() {
        return clubCourses;
    }

    public void setClubCourses(List<ClubCourseDTO> clubCourses) {
        this.clubCourses = clubCourses;
    }

     public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    @Override
    public int compareTo(ClubDTO clubDTO) {
        return clubName.compareTo(clubDTO.clubName);
    }

}
