package com.boha.malengagolf.library.data;

import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.Statics;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ScorerDTO implements Serializable, PersonInterface {
    private Integer scorerID;
    private String firstName;
    private String lastName;
    private String email, imageURL;
    private String cellphone;
    private String pin;
    private long dateRegistered;
    private int golfGroupID;
    private GcmDeviceDTO gcmDevice;
    private boolean forceImageRefresh;

    public String getImageURL() {
        if (imageURL == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("golfgroup")
                    .append(golfGroupID).append("/scorer/");
            sb.append("t");
            sb.append(scorerID).append(".jpg");
            imageURL = sb.toString();
        }
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isForceImageRefresh() {
        return forceImageRefresh;
    }

    public void setForceImageRefresh(boolean forceImageRefresh) {
        this.forceImageRefresh = forceImageRefresh;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public GcmDeviceDTO getGcmDevice() {
        return gcmDevice;
    }

    public void setGcmDevice(GcmDeviceDTO gcmDevice) {
        this.gcmDevice = gcmDevice;
    }

    public Integer getScorerID() {
        return scorerID;
    }

    public void setScorerID(Integer scorerID) {
        this.scorerID = scorerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }


}
