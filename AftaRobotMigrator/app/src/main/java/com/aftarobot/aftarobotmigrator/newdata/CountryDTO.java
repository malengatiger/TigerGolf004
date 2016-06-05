/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aftarobot.aftarobotmigrator.newdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Sipho
 */
public class CountryDTO implements Serializable {

    private String countryID;
    private String name, status;
    private double latitude, longitude;
    private long date;
    private List<ProvinceDTO> provinceList = new ArrayList<>();
    private HashMap<String, ProvinceDTO> provinces;

    public CountryDTO() {

    }

    public HashMap<String, ProvinceDTO> getProvinces() {
        return provinces;
    }

    public void setProvinces(HashMap<String, ProvinceDTO> provinces) {
        provinceList = new ArrayList<>();
        if (provinces != null){
            provinceList.addAll(provinces.values());
        }
        this.provinces = provinces;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<ProvinceDTO> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceDTO> provinceList) {
        this.provinceList = provinceList;
    }
}
