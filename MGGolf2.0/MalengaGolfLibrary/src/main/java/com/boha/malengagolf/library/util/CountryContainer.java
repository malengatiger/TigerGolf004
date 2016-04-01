package com.boha.malengagolf.library.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class CountryContainer implements Serializable{
    List<CountrySCR> countryList = new ArrayList<CountrySCR>();

    public List<CountrySCR> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountrySCR> countryList) {
        this.countryList = countryList;
    }
}
