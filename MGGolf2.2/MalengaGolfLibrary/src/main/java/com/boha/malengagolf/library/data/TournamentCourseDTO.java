package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/17.
 */
public class TournamentCourseDTO implements Serializable {
    private int tournamentCourseID;
    private int round;
    private int tournamentID;
    private ClubCourseDTO clubCourse;

    public int getTournamentCourseID() {
        return tournamentCourseID;
    }

    public void setTournamentCourseID(int tournamentCourseID) {
        this.tournamentCourseID = tournamentCourseID;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public ClubCourseDTO getClubCourse() {
        return clubCourse;
    }

    public void setClubCourse(ClubCourseDTO clubCourse) {
        this.clubCourse = clubCourse;
    }

}
