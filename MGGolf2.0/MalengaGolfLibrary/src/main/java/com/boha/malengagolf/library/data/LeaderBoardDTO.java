/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.malengagolf.library.data;

import android.content.Context;

import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Aubrey Malabie
 */
public class LeaderBoardDTO implements Comparable<LeaderBoardDTO>, Serializable {
    private int leaderBoardID, position, parStatus, tournamentID;
    private PlayerDTO player;
    private boolean tied, scoringComplete;
    private AgeGroupDTO ageGroup;
    private int rounds, lastHole, holesPerRound, age,
            currentRoundStatus, sortType, round = 1;
    private long startDate, timeStamp;
    private String tournamentName, clubName, imageURL;
    private List<TourneyScoreByRoundDTO> tourneyScoreByRoundList;
    private int winnerFlag, orderOfMeritPoints;
    private int withDrawn, tournamentType;
    private int scoreRound1,
            scoreRound2,
            scoreRound3,
            scoreRound4,
            scoreRound5,
            scoreRound6,
            totalScore;
    private int pointsRound1,
            pointsRound2,
            pointsRound3,
            pointsRound4,
            pointsRound5,
            pointsRound6,
            totalPoints;

    public int getTournamentType() {
        return tournamentType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setTournamentType(int tournamentType) {
        this.tournamentType = tournamentType;
    }

    public int getPointsRound1() {
        return pointsRound1;
    }

    public void setPointsRound1(int pointsRound1) {
        this.pointsRound1 = pointsRound1;
    }

    public int getPointsRound2() {
        return pointsRound2;
    }

    public void setPointsRound2(int pointsRound2) {
        this.pointsRound2 = pointsRound2;
    }

    public int getPointsRound3() {
        return pointsRound3;
    }

    public void setPointsRound3(int pointsRound3) {
        this.pointsRound3 = pointsRound3;
    }

    public int getPointsRound4() {
        return pointsRound4;
    }

    public void setPointsRound4(int pointsRound4) {
        this.pointsRound4 = pointsRound4;
    }

    public int getPointsRound5() {
        return pointsRound5;
    }

    public void setPointsRound5(int pointsRound5) {
        this.pointsRound5 = pointsRound5;
    }

    public int getPointsRound6() {
        return pointsRound6;
    }

    public void setPointsRound6(int pointsRound6) {
        this.pointsRound6 = pointsRound6;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getImageURL(Context ctx) {
        if (imageURL == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("golfgroup")
                    .append(SharedUtil.getGolfGroup(ctx).getGolfGroupID()).append("/player/");
            sb.append("t");
            sb.append(player.getPlayerID()).append(".jpg");
            imageURL = sb.toString();
        }
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getAge() {
        return age;
    }

    public int getOrderOfMeritPoints() {
        return orderOfMeritPoints;
    }

    public int getWithDrawn() {
        return withDrawn;
    }

    public void setWithDrawn(int withDrawn) {
        this.withDrawn = withDrawn;
    }

    public void setOrderOfMeritPoints(int orderOfMeritPoints) {
        this.orderOfMeritPoints = orderOfMeritPoints;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isScoringComplete() {
        return scoringComplete;
    }

    public void setScoringComplete(boolean scoringComplete) {
        this.scoringComplete = scoringComplete;
    }

    public int getLeaderBoardID() {
        return leaderBoardID;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public AgeGroupDTO getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroupDTO ageGroup) {
        this.ageGroup = ageGroup;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public void setLeaderBoardID(int leaderBoardID) {
        this.leaderBoardID = leaderBoardID;
    }

    public int getWinnerFlag() {
        return winnerFlag;
    }

    public void setWinnerFlag(int winnerFlag) {
        this.winnerFlag = winnerFlag;
    }

    public int getCurrentRoundStatus() {
        return currentRoundStatus;
    }

    public void setCurrentRoundStatus(int currentRoundStatus) {
        this.currentRoundStatus = currentRoundStatus;
    }

    public int getLastHole() {
        return lastHole;
    }

    public void setLastHole(int lastHole) {
        this.lastHole = lastHole;
    }

    public List<TourneyScoreByRoundDTO> getTourneyScoreByRoundList() {
        return tourneyScoreByRoundList;
    }

    public void setTourneyScoreByRoundList(List<TourneyScoreByRoundDTO> tourneyScoreByRoundList) {
        this.tourneyScoreByRoundList = tourneyScoreByRoundList;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isTied() {
        return tied;
    }

    public int getParStatus() {

        return parStatus;
    }

    public void setParStatus(int parStatus) {
        this.parStatus = parStatus;
    }

    public int getHolesPerRound() {
        return holesPerRound;
    }

    public void setHolesPerRound(int holesPerRound) {
        this.holesPerRound = holesPerRound;
    }

    public void setTied(boolean tied) {
        this.tied = tied;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getScoreRound5() {
        return scoreRound5;
    }

    public void setScoreRound5(int scoreRound5) {
        this.scoreRound5 = scoreRound5;
    }

    public int getScoreRound6() {
        return scoreRound6;
    }

    public void setScoreRound6(int scoreRound6) {
        this.scoreRound6 = scoreRound6;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public int getScoreRound1() {
        return scoreRound1;
    }

    public void setScoreRound1(int scoreRound1) {
        this.scoreRound1 = scoreRound1;
    }

    public int getScoreRound2() {
        return scoreRound2;
    }

    public void setScoreRound2(int scoreRound2) {
        this.scoreRound2 = scoreRound2;
    }

    public int getScoreRound3() {
        return scoreRound3;
    }

    public void setScoreRound3(int scoreRound3) {
        this.scoreRound3 = scoreRound3;
    }

    public int getScoreRound4() {
        return scoreRound4;
    }

    public void setScoreRound4(int scoreRound4) {
        this.scoreRound4 = scoreRound4;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public int compareTo(LeaderBoardDTO t) {
        switch (sortType) {
            case 0:
                if (this.getParStatus() < t.getParStatus()) {
                    return 1;
                }
                if (this.getParStatus() > t.getParStatus()) {
                    return -1;
                }
                break;
            case SORT_POINTS:
                if (this.totalPoints < t.totalPoints) {
                    return 1;
                }
                if (this.totalPoints > t.totalPoints) {
                    return -1;
                }

                break;
            case SORT_AGE_GROUP:
                StringBuilder sba = new StringBuilder(), sbb = new StringBuilder();
                if (ageGroup != null) {
                    sba.append(ageGroup.getGroupName());
                    TourneyScoreByRoundDTO xa = tourneyScoreByRoundList.get(round - 1);
                    sba.append(xa.getTeeTime());
                    //
                    sbb.append(t.getAgeGroup().getGroupName());
                    TourneyScoreByRoundDTO xb = t.getTourneyScoreByRoundList().get(round - 1);
                    sbb.append(xb.getTeeTime());
                }
                return (sba.toString().compareTo(sbb.toString()));
            case SORT_TEE_HOLES:
                StringBuilder sba1 = new StringBuilder();
                TourneyScoreByRoundDTO xa1 = tourneyScoreByRoundList.get(round - 1);
                sba1.append(xa1.getTee());
                sba1.append(xa1.getTeeTime());
                //
                StringBuilder sbb1 = new StringBuilder();
                TourneyScoreByRoundDTO xb1 = t.getTourneyScoreByRoundList().get(round - 1);
                sbb1.append(xb1.getTee());
                sbb1.append(xb1.getTeeTime());
                return (sba1.toString().compareTo(sbb1.toString()));
            case SORT_TEE_TIME:
                StringBuilder sba2 = new StringBuilder();
                TourneyScoreByRoundDTO xa2 = tourneyScoreByRoundList.get(round - 1);
                sba2.append(xa2.getTeeTime());
                sba2.append(xa2.getTee());
                //
                StringBuilder sbb2 = new StringBuilder();
                TourneyScoreByRoundDTO xb2 = t.getTourneyScoreByRoundList().get(round - 1);
                sbb2.append(xb2.getTeeTime());
                sbb2.append(xb2.getTee());

                return (sba2.toString().compareTo(sbb2.toString()));
            case SORT_PLAYER_NAME:
                StringBuilder x = new StringBuilder();
                x.append(this.getPlayer().getLastName()).append(this.getPlayer().getFirstName());
                StringBuilder x1 = new StringBuilder();
                x1.append(t.getPlayer().getLastName()).append(t.getPlayer().getFirstName());

                return x.toString().compareTo(x1.toString());
            case SORT_PLAYER_AGE:
                if (player.getAge() == t.getPlayer().getAge()) return 0;
                if (player.getAge() < t.getPlayer().getAge()) return -1;
                if (player.getAge() > t.getPlayer().getAge()) return 1;
                break;

        }
        return 0;
    }

    public static final int NO_PAR_STATUS = 9999,
            WINNER_BY_PLAYOFF = 2,
            WINNER_BY_COUNT_OUT = 1;

    public static final int SORT_AGE_GROUP = 1,
            SORT_TEE_TIME = 2, SORT_POINTS = 6,
            SORT_TEE_HOLES = 3, SORT_PLAYER_NAME = 4, SORT_PLAYER_AGE = 5;

}
