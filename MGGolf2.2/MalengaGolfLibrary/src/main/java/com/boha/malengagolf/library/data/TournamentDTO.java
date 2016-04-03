/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Aubrey Malabie
 */
public class TournamentDTO implements Serializable, Comparable<TournamentDTO> {
    private int tournamentID, tournamentType;
    private long closingDate;
    private long endDate;
    private int golfRounds, numberOfRegisteredPlayers;
    private int closedForScoringFlag;
    private int closedForRegistrationFlag;
    private long startDate;
    private int scoringCommencedFlag;
    private int useAgeGroups;
    private String tourneyName, clubName;
    private int clubID, provinceID;
    private int clubCourseID, sortType, exampleFlag;
    private int golfGroupID, par = 72, holesPerRound;
    private List<LeaderBoardDTO> scores;
    private List<VolunteerDTO> volunteers;
    private List<TournamentCourseDTO> tournamentCourses;
    public static final int SORT_BY_DATE_DESC = 1,
            SORT_BY_DATE_ASC = 2,
            SORT_BY_NEWEST_TOURNAMENT_ENTERED = 3;
    public static final int STROKE_PLAY_INDIVIDUAL = 1;
    public static final int STABLEFORD_INDIVIDUAL = 2;
    public static final int BETTER_BALL_STROKEPLAY = 3;
    public static final int BETTER_BALL_STABLEFORD = 4;
    public static final int ALLIANCE_STABLEFORD = 5;

    public int getScoringCommencedFlag() {
        return scoringCommencedFlag;
    }

    public void setScoringCommencedFlag(int scoringCommencedFlag) {
        this.scoringCommencedFlag = scoringCommencedFlag;
    }

    public int getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(int tournamentType) {
        this.tournamentType = tournamentType;
    }

    public int getExampleFlag() {
        return exampleFlag;
    }

    public void setExampleFlag(int exampleFlag) {
        this.exampleFlag = exampleFlag;
    }

    public int getUseAgeGroups() {
        return useAgeGroups;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public void setUseAgeGroups(int useAgeGroups) {
        this.useAgeGroups = useAgeGroups;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public List<TournamentCourseDTO> getTournamentCourses() {
        return tournamentCourses;
    }

    public void setTournamentCourses(List<TournamentCourseDTO> tournamentCourses) {
        this.tournamentCourses = tournamentCourses;
    }

    public int getClosedForRegistrationFlag() {
        return closedForRegistrationFlag;
    }

    public void setClosedForRegistrationFlag(int closedForRegistrationFlag) {
        this.closedForRegistrationFlag = closedForRegistrationFlag;
    }

    public int getHolesPerRound() {
        return holesPerRound;
    }

    public void setHolesPerRound(int holesPerRound) {
        this.holesPerRound = holesPerRound;
    }

    public int getClosedForScoringFlag() {
        return closedForScoringFlag;
    }

    public void setClosedForScoringFlag(int closedForScoringFlag) {
        this.closedForScoringFlag = closedForScoringFlag;
    }

    public int getNumberOfRegisteredPlayers() {
        return numberOfRegisteredPlayers;
    }

    public void setNumberOfRegisteredPlayers(int numberOfRegisteredPlayers) {
        this.numberOfRegisteredPlayers = numberOfRegisteredPlayers;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(long closingDate) {
        this.closingDate = closingDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getGolfRounds() {
        return golfRounds;
    }

    public void setGolfRounds(int golfRounds) {
        this.golfRounds = golfRounds;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getTourneyName() {
        return tourneyName;
    }

    public void setTourneyName(String tourneyName) {
        this.tourneyName = tourneyName;
    }


    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    @Override
    public int compareTo(TournamentDTO t) {

        switch (sortType) {
            case 0:
                if (startDate > t.startDate) {
                    return -1;
                }
                if (startDate < t.startDate) {
                    return 1;
                }
                break;
            case SORT_BY_DATE_ASC:
                if (startDate > t.startDate) {
                    return 1;
                }
                if (startDate < t.startDate) {
                    return -1;
                }
                break;
            case SORT_BY_DATE_DESC:
                if (startDate > t.startDate) {
                    return 1;
                }
                if (startDate < t.startDate) {
                    return -1;
                }
                break;
            case SORT_BY_NEWEST_TOURNAMENT_ENTERED:
                if (tournamentID > t.tournamentID) {
                    return -1;
                }
                if (tournamentID < t.tournamentID) {
                    return 1;
                }
                break;
        }


        return 0;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public int getClubCourseID() {
        return clubCourseID;
    }

    public void setClubCourseID(int clubCourseID) {
        this.clubCourseID = clubCourseID;
    }

    public List<LeaderBoardDTO> getScores() {
        return scores;
    }

    public void setScores(List<LeaderBoardDTO> scores) {
        this.scores = scores;
    }

    public List<VolunteerDTO> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<VolunteerDTO> volunteers) {
        this.volunteers = volunteers;
    }

    public TournamentDTO() {
    }
}
