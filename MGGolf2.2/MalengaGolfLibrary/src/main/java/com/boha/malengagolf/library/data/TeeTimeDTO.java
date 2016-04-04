package com.boha.malengagolf.library.data;

import com.orm.SugarRecord;

import java.io.Serializable;

public class TeeTimeDTO  extends SugarRecord implements Serializable {
	private int teeTimeID;
	private int golfRound;
	private long teeTime;
	private int leaderBoardID;
	public int getTeeTimeID() {
		return teeTimeID;
	}
	public void setTeeTimeID(int teeTimeID) {
		this.teeTimeID = teeTimeID;
	}
	public int getGolfRound() {
		return golfRound;
	}
	public void setGolfRound(int golfRound) {
		this.golfRound = golfRound;
	}
	public long getTeeTime() {
		return teeTime;
	}
	public void setTeeTime(long teeTime) {
		this.teeTime = teeTime;
	}

    public int getLeaderBoardID() {
        return leaderBoardID;
    }

    public void setLeaderBoardID(int leaderBoardID) {
        this.leaderBoardID = leaderBoardID;
    }


}
