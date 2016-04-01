package com.boha.malengagolf.library.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class State implements Serializable {
    long countryID;
    long stateID = System.currentTimeMillis();
    String stateName, countryName, urlSnippet;
    List<CitySCR> cityList = new ArrayList<CitySCR>();

    public String getUrlSnippet() {
        return urlSnippet;
    }

    public void setUrlSnippet(String urlSnippet) {
        this.urlSnippet = urlSnippet;
    }

    public List<CitySCR> getCityList() {
        return cityList;
    }

    public void setCityList(List<CitySCR> cityList) {
        this.cityList = cityList;
    }

    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public long getStateID() {
        return stateID;
    }

    public void setStateID(long stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
