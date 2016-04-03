package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;

/**
  * Created by aubreyM on 2014/05/25.
  */
 public class AppUserDTO implements Serializable {
    private int appUserID;
    private String email;
    private long dateRegistered;
    private List<GolfGroupDTO> golfGroupList;

    public int getAppUserID() {
        return appUserID;
    }

    public void setAppUserID(int appUserID) {
        this.appUserID = appUserID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public List<GolfGroupDTO> getGolfGroupList() {
        return golfGroupList;
    }

    public void setGolfGroupList(List<GolfGroupDTO> golfGroupList) {
        this.golfGroupList = golfGroupList;
    }


}
