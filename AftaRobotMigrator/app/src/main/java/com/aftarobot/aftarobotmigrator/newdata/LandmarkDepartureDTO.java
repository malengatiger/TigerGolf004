package com.aftarobot.aftarobotmigrator.newdata;

/**
 * Created by aubreymalabie on 6/6/16.
 */
public class LandmarkDepartureDTO {
    private String departureID, landmarkID, landmarkName,
            vehicleID, associationID, associationName, countryID;
    private long dateDeparted;

    public String getDepartureID() {
        return departureID;
    }

    public void setDepartureID(String departureID) {
        this.departureID = departureID;
    }

    public String getLandmarkID() {
        return landmarkID;
    }

    public void setLandmarkID(String landmarkID) {
        this.landmarkID = landmarkID;
    }

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getAssociationID() {
        return associationID;
    }

    public void setAssociationID(String associationID) {
        this.associationID = associationID;
    }

    public String getAssociationName() {
        return associationName;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public long getDateDeparted() {
        return dateDeparted;
    }

    public void setDateDeparted(long dateDeparted) {
        this.dateDeparted = dateDeparted;
    }
}
