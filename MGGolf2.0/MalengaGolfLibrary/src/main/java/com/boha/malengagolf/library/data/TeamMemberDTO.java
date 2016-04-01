package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class TeamMemberDTO implements Serializable {

    private int teamMemberID;
    private int teamID;
    private String teamName;
    private PlayerDTO player;

    public int getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(int teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }


}
