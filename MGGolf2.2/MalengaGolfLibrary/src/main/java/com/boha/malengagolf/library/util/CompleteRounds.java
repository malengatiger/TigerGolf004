package com.boha.malengagolf.library.util;

import android.util.Log;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/17.
 */
public class CompleteRounds {
    int round;
    int numberOfPlayers;
    int totalScores;
    double average;

    public CompleteRounds(int round, int numberOfPlayers, int totalScores) {
        this.round = round;
        this.numberOfPlayers = numberOfPlayers;
        this.totalScores = totalScores;
        getAverage();
    }

    public static List<CompleteRounds> getCompletedRounds(List<LeaderBoardDTO> leaderBoardList) {
        int total = 0;
        for (LeaderBoardDTO b : leaderBoardList) {
            markScoringCompletion(b);
        }
        List<CompleteRounds> completeRoundsList = new ArrayList<CompleteRounds>();
        if (leaderBoardList.isEmpty()) {
            return completeRoundsList;
        }
        //find round where all players are complete, calculate average for only these
        if (leaderBoardList.get(0) == null) return completeRoundsList;
        for (int i = 0; i < leaderBoardList.get(0).getRounds(); i++) {
            switch (i) {
                case 0:    //round 1
                    int cnt = 0, totScore = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(0);
                        if (tbs.getScoringComplete() == 1) {
                            cnt++;
                            totScore += tbs.getTotalScore();
                        }
                    }
                    if (cnt == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(1, cnt, totScore));
                    }
                    break;
                case 1:    //round 2
                    int cnt2 = 0, totScore2 = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(1);
                        if (tbs.getScoringComplete() == 1) {
                            cnt2++;
                            totScore2 += tbs.getTotalScore();
                        }
                    }
                    if (cnt2 == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(2, cnt2, totScore2));
                    }
                    break;
                case 2:    //round 3
                    int cnt3 = 0, totScore3 = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(2);
                        if (tbs.getScoringComplete() == 1) {
                            cnt3++;
                            totScore3 += tbs.getTotalScore();
                        }
                    }
                    if (cnt3 == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(3, cnt3, totScore3));
                    }
                    break;
                case 3:    //round 4
                    int cnt4 = 0, totScore4 = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(3);
                        if (tbs.getScoringComplete() == 1) {
                            cnt4++;
                            totScore4 += tbs.getTotalScore();
                        }
                    }
                    if (cnt4 == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(4, cnt4, totScore4));
                    }
                    break;
                case 4:    //round 5
                    int cnt5 = 0, totScore5 = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(4);
                        if (tbs.getScoringComplete() == 1) {
                            cnt5++;
                            totScore5 += tbs.getTotalScore();
                        }
                    }
                    if (cnt5 == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(5, cnt5, totScore5));
                    }
                    break;
                case 5:    //round 6
                    int cnt6 = 0, totScore6 = 0;
                    for (LeaderBoardDTO x : leaderBoardList) {
                        TourneyScoreByRoundDTO tbs = x.getTourneyScoreByRoundList().get(5);
                        if (tbs.getScoringComplete() == 1) {
                            cnt6++;
                            totScore6 += tbs.getTotalScore();
                        }
                    }
                    if (cnt6 == leaderBoardList.size()) {
                        completeRoundsList.add(new CompleteRounds(6, cnt6, totScore6));
                    }
                    break;
            }
        }
        return completeRoundsList;
    }

    public static int countHolesMissed(TourneyScoreByRoundDTO r) {
        int missingScores = 0;
        if (r.getScore1() == 0) missingScores++;
        if (r.getScore2() == 0) missingScores++;
        if (r.getScore3() == 0) missingScores++;
        if (r.getScore4() == 0) missingScores++;
        if (r.getScore5() == 0) missingScores++;
        if (r.getScore6() == 0) missingScores++;
        if (r.getScore7() == 0) missingScores++;
        if (r.getScore8() == 0) missingScores++;
        if (r.getScore9() == 0) missingScores++;
        if (r.getScore10() == 0) missingScores++;
        if (r.getScore11() == 0) missingScores++;
        if (r.getScore12() == 0) missingScores++;
        if (r.getScore13() == 0) missingScores++;
        if (r.getScore14() == 0) missingScores++;
        if (r.getScore15() == 0) missingScores++;
        if (r.getScore16() == 0) missingScores++;
        if (r.getScore17() == 0) missingScores++;
        if (r.getScore18() == 0) missingScores++;
        return missingScores;
    }
    public static int countHolesScored(TourneyScoreByRoundDTO r) {
        Log.e("CompleteRounds", "countHolesScored tsbr round: " + r.getGolfRound());
        int scores = 0;
        if (r.getScore1() > 0) scores++;
        if (r.getScore2() > 0) scores++;
        if (r.getScore3() > 0) scores++;
        if (r.getScore4() > 0) scores++;
        if (r.getScore5() > 0) scores++;
        if (r.getScore6() > 0) scores++;
        if (r.getScore7() > 0) scores++;
        if (r.getScore8() > 0) scores++;
        if (r.getScore9() > 0) scores++;
        if (r.getScore10() > 0) scores++;
        if (r.getScore11() > 0) scores++;
        if (r.getScore12() > 0) scores++;
        if (r.getScore13() > 0) scores++;
        if (r.getScore14() > 0) scores++;
        if (r.getScore15() > 0) scores++;
        if (r.getScore16() > 0) scores++;
        if (r.getScore17() > 0) scores++;
        if (r.getScore18() > 0) scores++;
        Log.e("CompleteRounds", "scores counted: " + scores);
        return scores;
    }
    public static void markScoringCompletion(LeaderBoardDTO dto) {

        for (TourneyScoreByRoundDTO r: dto.getTourneyScoreByRoundList()) {
            int noScores = 0, scores = 0;
            if (r.getScore1() == 0) noScores++; else scores++;
            if (r.getScore2() == 0) noScores++; else scores++;
            if (r.getScore3() == 0) noScores++; else scores++;
            if (r.getScore4() == 0) noScores++; else scores++;
            if (r.getScore5() == 0) noScores++; else scores++;
            if (r.getScore6() == 0) noScores++; else scores++;
            if (r.getScore7() == 0) noScores++; else scores++;
            if (r.getScore8() == 0) noScores++; else scores++;
            if (r.getScore9() == 0) noScores++; else scores++;
            if (r.getScore10() == 0) noScores++; else scores++;
            if (r.getScore11() == 0) noScores++; else scores++;
            if (r.getScore12() == 0) noScores++; else scores++;
            if (r.getScore13() == 0) noScores++; else scores++;
            if (r.getScore14() == 0) noScores++; else scores++;
            if (r.getScore15() == 0) noScores++; else scores++;
            if (r.getScore16() == 0) noScores++; else scores++;
            if (r.getScore17() == 0) noScores++; else scores++;
            if (r.getScore18() == 0) noScores++; else scores++;

            if (scores == dto.getHolesPerRound()) { //scoring complete
                r.setScoringComplete(1);
            }
        }
        int completeRounds = 0;
        for (TourneyScoreByRoundDTO r: dto.getTourneyScoreByRoundList()) {
            if (r.getScoringComplete() == 1) {
                completeRounds++;
            }
        }
        if (completeRounds == dto.getTourneyScoreByRoundList().size()) {
            dto.setScoringComplete(true);
        }


    }

    public double getAverage() {
        average = Double.valueOf(totalScores) / Double.valueOf(numberOfPlayers);
        return average;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getTotalScores() {
        return totalScores;
    }

    public void setTotalScores(int totalScores) {
        this.totalScores = totalScores;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}