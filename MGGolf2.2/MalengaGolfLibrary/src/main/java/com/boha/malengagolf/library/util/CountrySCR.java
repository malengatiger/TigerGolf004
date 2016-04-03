package com.boha.malengagolf.library.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class CountrySCR implements Serializable{
    long countryID = System.currentTimeMillis();
    String countryName, countryCode;
    List<State> stateList = new ArrayList<State>();
    public CountrySCR(String countryName, String countryCode) {
        this.countryCode = countryCode;
        this.countryName = countryName;
    }
    public CountrySCR()  {}
    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<State> getStateList() {
        return stateList;
    }

    public void setStateList(List<State> stateList) {
        this.stateList = stateList;
    }
}
