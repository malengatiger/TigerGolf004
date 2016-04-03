package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class GolfGroupParentDTO implements Serializable {
    private int golfGroupParentID;

    private long dateRegistered;

    private int golfGroupID;

    public int getGolfGroupParentID() {
        return golfGroupParentID;
    }

    public void setGolfGroupParentID(int golfGroupParentID) {
        this.golfGroupParentID = golfGroupParentID;
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
