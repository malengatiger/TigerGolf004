package com.boha.golfpractice.player.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aubreymalabie on 3/27/16.
 */
public class BagDTO implements Serializable{
    private List<PracticeSessionDTO> practiceSessionList;
    private String description;

    public BagDTO(List<PracticeSessionDTO> list, String description) {
        this.description = description;
        this.practiceSessionList = list;
    }

    public BagDTO() {

    }

    public List<PracticeSessionDTO> getPracticeSessionList() {
        return practiceSessionList;
    }

    public void setPracticeSessionList(List<PracticeSessionDTO> practiceSessionList) {
        this.practiceSessionList = practiceSessionList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
