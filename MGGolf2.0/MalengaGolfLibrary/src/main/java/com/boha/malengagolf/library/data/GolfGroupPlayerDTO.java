package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class GolfGroupPlayerDTO implements Serializable {
    private long dateRegistered;

    private PlayerDTO player;

    private int golfGroupID;

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

}
