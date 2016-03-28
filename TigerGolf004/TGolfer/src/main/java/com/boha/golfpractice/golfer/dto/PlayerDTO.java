/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.golfpractice.golfer.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aubreymalabie
 */
public class PlayerDTO implements Serializable, Comparable<PlayerDTO> {

    private static final long serialVersionUID = 1L;
    private Integer playerID;
    private String firstName;
    private String lastName;
    private String email;
    private String cellphone;
    private String pin, photoUrl;
    private String filePath;
    private Short gender;
    private Long dateRegistered;
    private List<VideoUploadDTO> videoUploadList;
    private List<PracticeSessionDTO> practiceSessionList;
    private List<CoachPlayerDTO> coachPlayerList;

    public PlayerDTO() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Short getGender() {
        return gender;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public List<VideoUploadDTO> getVideoUploadList() {
        return videoUploadList;
    }

    public void setVideoUploadList(List<VideoUploadDTO> videoUploadList) {
        this.videoUploadList = videoUploadList;
    }

    public String getFullName() {
        if (firstName == null) {
            return email;
        }
        return firstName + " " + lastName;
    }
    public List<PracticeSessionDTO> getPracticeSessionList() {
        if (practiceSessionList == null) {
            practiceSessionList = new ArrayList<>();
        }
        return practiceSessionList;
    }

    public void setPracticeSessionList(List<PracticeSessionDTO> practiceSessionList) {
        this.practiceSessionList = practiceSessionList;
    }

    public List<CoachPlayerDTO> getCoachPlayerList() {
        return coachPlayerList;
    }

    public void setCoachPlayerList(List<CoachPlayerDTO> coachPlayerList) {
        this.coachPlayerList = coachPlayerList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (playerID != null ? playerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerDTO)) {
            return false;
        }
        PlayerDTO other = (PlayerDTO) object;
        if ((this.playerID == null && other.playerID != null) || (this.playerID != null && !this.playerID.equals(other.playerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.golfpractice.data.Player[ playerID=" + playerID + " ]";
    }

    @Override
    public int compareTo(PlayerDTO another) {
        return this.getFullName().compareTo(another.getFullName());
    }
}
