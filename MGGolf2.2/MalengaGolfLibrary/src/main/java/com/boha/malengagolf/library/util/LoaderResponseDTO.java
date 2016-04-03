package com.boha.malengagolf.library.util;

import com.boha.malengagolf.library.base.CityDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;

import java.util.List;

/**
 *
 * @author aubreyM
 */
public class LoaderResponseDTO {

    private int statusCode;
    private String message;
    private int numberStates, numberCities, numberClubs;
    private String countryName;

    List<ProvinceDTO> provinceList;
    List<CityDTO> cityList;
    List<ClubDTO> clubList;
    List<CountryDTO> countryList;

    public List<CountryDTO> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryDTO> countryList) {
        this.countryList = countryList;
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumberStates() {
        return numberStates;
    }

    public void setNumberStates(int numberStates) {
        this.numberStates = numberStates;
    }

    public int getNumberCities() {
        return numberCities;
    }

    public void setNumberCities(int numberCities) {
        this.numberCities = numberCities;
    }

    public int getNumberClubs() {
        return numberClubs;
    }

    public void setNumberClubs(int numberClubs) {
        this.numberClubs = numberClubs;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public List<ProvinceDTO> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceDTO> provinceList) {
        this.provinceList = provinceList;
    }

    public List<CityDTO> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityDTO> cityList) {
        this.cityList = cityList;
    }

    public List<ClubDTO> getClubList() {
        return clubList;
    }

    public void setClubList(List<ClubDTO> clubList) {
        this.clubList = clubList;
    }


}
