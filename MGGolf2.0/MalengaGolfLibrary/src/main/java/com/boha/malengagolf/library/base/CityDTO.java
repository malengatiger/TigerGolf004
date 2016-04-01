package com.boha.malengagolf.library.base;

import com.boha.malengagolf.library.data.ClubDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/01.
 */
public class CityDTO implements Serializable, Comparable<CityDTO>{
    private int cityID;
    private String cityName, webKey;
    private double latitude;
    private double longitude;
    private List<ClubDTO> clubList;

    private int provinceID;

    public int getCityID() {
        return cityID;
    }

    public String getWebKey() {
        return webKey;
    }

    public void setWebKey(String webKey) {
        this.webKey = webKey;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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

    public List<ClubDTO> getClubList() {
        return clubList;
    }

    public void setClubList(List<ClubDTO> clubList) {
        this.clubList = clubList;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    @Override
    public int compareTo(CityDTO cityDTO) {
        return cityName.compareTo(cityDTO.cityName);
    }
}
