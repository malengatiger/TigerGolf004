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
 * @author aubreymalabie
 */
public class PracticeSessionDTO implements Serializable, Comparable<PracticeSessionDTO> {

    private static final long serialVersionUID = 1L;
    private Integer practiceSessionID;
    private String golfCourseName;
    private Long sessionDate;
    private Integer numberOfHoles;
    private Integer totalStrokes;
    private Integer underPar;
    private Integer overPar, par;
    private Boolean closed;
    private List<HoleStatDTO> holeStatList;
    private List<VideoUploadDTO> videoUploadList;
    private Integer playerID, coachID;
    private Integer golfCourseID, totalMistakes;
    private GolfCourseDTO golfCourse;
    private Boolean needsUpload = false;
    private Short gender = 0;

    public Boolean getNeedsUpload() {
        return needsUpload;
    }

    public void setNeedsUpload(Boolean needsUpload) {
        this.needsUpload = needsUpload;
    }

    public PracticeSessionDTO() {
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Integer getCoachID() {
        return coachID;
    }

    public void setCoachID(Integer coachID) {
        this.coachID = coachID;
    }

    public PracticeSessionDTO(Integer practiceSessionID) {
        this.practiceSessionID = practiceSessionID;
    }

    public Boolean getClosed() {
        if (closed == null) {
            closed = Boolean.FALSE;
        }
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Integer getTotalMistakes() {
        if (totalMistakes == null) {
            totalMistakes = 0;
        }
        return totalMistakes;
    }

    public void setTotalMistakes(Integer totalMistakes) {
        this.totalMistakes = totalMistakes;
    }

    public GolfCourseDTO getGolfCourse() {
        return golfCourse;
    }

    public void setGolfCourse(GolfCourseDTO golfCourse) {
        this.golfCourse = golfCourse;
    }

    public String getGolfCourseName() {
        return golfCourseName;
    }

    public void setGolfCourseName(String golfCourseName) {
        this.golfCourseName = golfCourseName;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public Integer getGolfCourseID() {
        return golfCourseID;
    }

    public void setGolfCourseID(Integer golfCourseID) {
        this.golfCourseID = golfCourseID;
    }

    public Integer getPracticeSessionID() {
        return practiceSessionID;
    }

    public void setPracticeSessionID(Integer practiceSessionID) {
        this.practiceSessionID = practiceSessionID;
    }

    public Long getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Long sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getNumberOfHoles() {
        if (numberOfHoles == null) {
            numberOfHoles = 0;
        }
        return numberOfHoles;
    }

    public void setNumberOfHoles(Integer numberOfHoles) {
        this.numberOfHoles = numberOfHoles;
    }

    public Integer getTotalStrokes() {
        if (totalStrokes == null) {
            totalStrokes = 0;
        }
        return totalStrokes;
    }

    public void setTotalStrokes(Integer totalStrokes) {
        this.totalStrokes = totalStrokes;
    }

    public Integer getUnderPar() {
        if (underPar == null) {
            underPar = 0;
        }
        return underPar;
    }

    public void setUnderPar(Integer underPar) {
        this.underPar = underPar;
    }

    public Integer getOverPar() {
        if (overPar == null) {
            overPar = 0;
        }
        return overPar;
    }

    public void setOverPar(Integer overPar) {
        this.overPar = overPar;
    }

    public Integer getPar() {
        if (par == null) {
            par = 0;
        }
        return par;
    }

    public void setPar(Integer par) {
        this.par = par;
    }

    public List<HoleStatDTO> getHoleStatList() {
        if (holeStatList == null) {
            holeStatList = new ArrayList<>();
        }
        return holeStatList;
    }

    public void setHoleStatList(List<HoleStatDTO> holeStatList) {
        this.holeStatList = holeStatList;
    }

    public List<VideoUploadDTO> getVideoUploadList() {
        if (videoUploadList == null) {
            videoUploadList = new ArrayList<>();
        }
        return videoUploadList;
    }

    public void setVideoUploadList(List<VideoUploadDTO> videoUploadList) {
        this.videoUploadList = videoUploadList;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (practiceSessionID != null ? practiceSessionID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PracticeSessionDTO)) {
            return false;
        }
        PracticeSessionDTO other = (PracticeSessionDTO) object;
        if ((this.practiceSessionID == null && other.practiceSessionID != null) || (this.practiceSessionID != null && !this.practiceSessionID.equals(other.practiceSessionID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.golfpractice.data.PracticeSession[ practiceSessionID=" + practiceSessionID + " ]";
    }

    @Override
    public int compareTo(PracticeSessionDTO another) {
        if (this.sessionDate > another.sessionDate) {
            return -1;
        }
        if (this.sessionDate < another.sessionDate) {
            return 1;
        }
        return 0;
    }
}
