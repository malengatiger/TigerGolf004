package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/20.
 */
public class PhotoUploadDTO implements Serializable {
    private Integer photoUploadID;
    private Long dateTaken;
    private String url, filePath;
    private Long dateUploaded;
    private Integer tournamentID;
    private Integer playerID, golfGroupID, administratorID, scorerID;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getPhotoUploadID() {
        return photoUploadID;
    }

    public void setPhotoUploadID(Integer photoUploadID) {
        this.photoUploadID = photoUploadID;
    }

    public Long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Long dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Integer getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(Integer tournamentID) {
        this.tournamentID = tournamentID;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public Integer getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(Integer golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public Integer getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(Integer administratorID) {
        this.administratorID = administratorID;
    }

    public Integer getScorerID() {
        return scorerID;
    }

    public void setScorerID(Integer scorerID) {
        this.scorerID = scorerID;
    }
}
