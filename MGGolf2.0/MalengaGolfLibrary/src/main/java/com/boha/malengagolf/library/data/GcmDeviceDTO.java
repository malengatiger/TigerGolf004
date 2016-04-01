package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/05/15.
 */
public class GcmDeviceDTO implements Serializable {
    private int gcmDeviceID;
    private String manufacturer, gcmRegistrationID, androidVersion;
    private String model;
    private String serial;
    private String product;
    private long dateRegistered;
    private int parentID;
    private int playerID;
    private int scorerID;
    private int administratorID;
    private int golfGroupID;

    public String getGcmRegistrationID() {
        return gcmRegistrationID;
    }

    public void setGcmRegistrationID(String gcmRegistrationID) {
        this.gcmRegistrationID = gcmRegistrationID;
    }

    public int getGcmDeviceID() {
        return gcmDeviceID;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public void setGcmDeviceID(int gcmDeviceID) {
        this.gcmDeviceID = gcmDeviceID;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getScorerID() {
        return scorerID;
    }

    public void setScorerID(int scorerID) {
        this.scorerID = scorerID;
    }

    public int getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(int administratorID) {
        this.administratorID = administratorID;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

}
