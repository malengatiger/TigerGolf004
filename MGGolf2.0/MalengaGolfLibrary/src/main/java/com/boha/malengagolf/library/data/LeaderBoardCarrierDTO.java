package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/18.
 */
public class LeaderBoardCarrierDTO implements Serializable, Comparable<LeaderBoardCarrierDTO>{
    private AgeGroupDTO ageGroup;
    private List<LeaderBoardDTO> leaderBoardList;
    private List<LeaderBoardTeamDTO> leaderBoardTeamList;

    public List<LeaderBoardTeamDTO> getLeaderBoardTeamList() {
        return leaderBoardTeamList;
    }

    public void setLeaderBoardTeamList(List<LeaderBoardTeamDTO> leaderBoardTeamList) {
        this.leaderBoardTeamList = leaderBoardTeamList;
    }

    public AgeGroupDTO getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroupDTO ageGroup) {
        this.ageGroup = ageGroup;
    }

    public List<LeaderBoardDTO> getLeaderBoardList() {
        return leaderBoardList;
    }

    public void setLeaderBoardList(List<LeaderBoardDTO> leaderBoardList) {
        this.leaderBoardList = leaderBoardList;
    }

    @Override
    public int compareTo(LeaderBoardCarrierDTO carrierDTO) {
        if (ageGroup == null || carrierDTO.getAgeGroup() == null) return 1;
        return ageGroup.getGroupName().compareTo(carrierDTO.ageGroup.getGroupName());
    }


}
