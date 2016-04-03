package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class TeamDTO implements Serializable {
    private int teamID;
    private String teamName;
    private long dateRegistered;
    private List<LeaderBoardTeamDTO> leaderBoardTeamList;
    private int golfGroupID;
    private List<TeamMemberDTO> teamMemberList;

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

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public List<LeaderBoardTeamDTO> getLeaderBoardTeamList() {
        return leaderBoardTeamList;
    }

    public void setLeaderBoardTeamList(List<LeaderBoardTeamDTO> leaderBoardTeamList) {
        this.leaderBoardTeamList = leaderBoardTeamList;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public List<TeamMemberDTO> getTeamMemberList() {
        return teamMemberList;
    }

    public void setTeamMemberList(List<TeamMemberDTO> teamMemberList) {
        this.teamMemberList = teamMemberList;
    }


}
