package com.boha.malengagolf.library.data;

import com.boha.malengagolf.library.util.PersonInterface;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ScorerDTO  extends SugarRecord implements Serializable, PersonInterface {
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
    private List<PhotoUploadDTO> photoUploads;

    public String getImageURL() {

        if (photoUploads != null) {
            if (!photoUploads.isEmpty()) {
                imageURL = photoUploads.get(0).getUrl();
            }
        } else {
            imageURL = "";
        }
        return imageURL;

    }

    public List<PhotoUploadDTO> getPhotoUploads() {
        return photoUploads;
    }

    public void setPhotoUploads(List<PhotoUploadDTO> photoUploads) {
        this.photoUploads = photoUploads;
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
