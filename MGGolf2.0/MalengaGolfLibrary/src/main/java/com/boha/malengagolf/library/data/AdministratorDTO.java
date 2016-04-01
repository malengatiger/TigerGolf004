/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.malengagolf.library.data;

import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.Statics;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Aubrey Malabie
 */
@SuppressWarnings(value = "unused")
public class AdministratorDTO implements Serializable, PersonInterface {

    private int administratorID, superUserFlag;
    private String cellphone;
    private Date dateRegistered;
    private String email, imageURL;
    private String firstName;
    private String lastName;
    private String pin;
    private int golfGroupID;
    private GcmDeviceDTO gcmDevice;
    private boolean forceImageRefresh;

    public String getImageURL() {

        if (imageURL == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("golfgroup")
                    .append(golfGroupID).append("/admin/");
            sb.append("t");
            sb.append(administratorID).append(".jpg");
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

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getSuperUserFlag() {
        return superUserFlag;
    }

    public void setSuperUserFlag(int superUserFlag) {
        this.superUserFlag = superUserFlag;
    }

    private GolfGroupDTO golfGroup;

    public GcmDeviceDTO getGcmDevice() {
        return gcmDevice;
    }

    public void setGcmDevice(GcmDeviceDTO gcmDevice) {
        this.gcmDevice = gcmDevice;
    }


    public GolfGroupDTO getGolfGroup() {
        return golfGroup;
    }

    public void setGolfGroup(GolfGroupDTO golfGroup) {
        this.golfGroup = golfGroup;
    }

    public int getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(int administratorID) {
        this.administratorID = administratorID;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }


}
