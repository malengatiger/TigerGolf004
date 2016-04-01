/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.golfpractice.player.dto;

/**
 * @author aubreymalabie
 */
public class RequestDTO {
    public RequestDTO(int requestType) {
        this.requestType = requestType;
    }

    //call identifiers
    public static final int
            EDIT_PLAYER = 1,
            EDIT_COACH = 2,
            SIGN_IN_PLAYER = 3,
            SIGN_IN_COACH = 4,

    ADD_GOLF_COURSE = 11,
            ADD_HOLE = 12,
            ADD_PRACTICE_SESSION = 14,
            ADD_HOLE_STAT = 15,
            CLOSE_PRACTICE_SESSION = 16,
            ADD_VIDEO = 17,

            GET_PLAYER_DATA = 21,
            GET_COACH_DATA = 22,
            GET_GOLF_COURSES_BY_LOCATION = 23,
            GET_ALL_GOLF_COURSES = 24,
            UPDATE_PRACTICE_SESSION = 25,
            GET_ALL_COACHES = 26,
            GET_SESSIONS_IN_PERIOD = 27;

    private int requestType;
    private Integer playerID,
            coachID,
            golfCourseID,
            practiceSessionID,
            clubUsedID, days,
            holeID, holeStatID, videoUploadID;
    private Double latitude, longitude;
    private Integer radius;
    private String email, password;
    private PlayerDTO player;
    private CoachDTO coach;
    private VideoUploadDTO videoUpload;
    private GolfCourseDTO golfCourse;
    private PracticeSessionDTO practiceSession;
    private HoleStatDTO holeStat;
    private HoleDTO hole;
    private Boolean isExistingUser = false;

    public Boolean getExistingUser() {
        return isExistingUser;
    }

    public void setExistingUser(Boolean existingUser) {
        isExistingUser = existingUser;
    }

    public VideoUploadDTO getVideoUpload() {
        return videoUpload;
    }

    public void setVideoUpload(VideoUploadDTO videoUpload) {
        this.videoUpload = videoUpload;
    }

    public Integer getDays() {
        if (days == null) {
            days = 90;
        }
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    private boolean zipResponse;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public boolean isZipResponse() {
        return zipResponse;
    }

    public void setZipResponse(boolean zipResponse) {
        this.zipResponse = zipResponse;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public Integer getCoachID() {
        return coachID;
    }

    public void setCoachID(Integer coachID) {
        this.coachID = coachID;
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

    public Integer getClubUsedID() {
        return clubUsedID;
    }

    public void setClubUsedID(Integer clubUsedID) {
        this.clubUsedID = clubUsedID;
    }

    public Integer getHoleID() {
        return holeID;
    }

    public void setHoleID(Integer holeID) {
        this.holeID = holeID;
    }

    public Integer getHoleStatID() {
        return holeStatID;
    }

    public void setHoleStatID(Integer holeStatID) {
        this.holeStatID = holeStatID;
    }

    public Integer getVideoUploadID() {
        return videoUploadID;
    }

    public void setVideoUploadID(Integer videoUploadID) {
        this.videoUploadID = videoUploadID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public CoachDTO getCoach() {
        return coach;
    }

    public void setCoach(CoachDTO coach) {
        this.coach = coach;
    }

    public GolfCourseDTO getGolfCourse() {
        return golfCourse;
    }

    public void setGolfCourse(GolfCourseDTO golfCourse) {
        this.golfCourse = golfCourse;
    }

    public PracticeSessionDTO getPracticeSession() {
        return practiceSession;
    }

    public void setPracticeSession(PracticeSessionDTO practiceSession) {
        this.practiceSession = practiceSession;
    }

    public HoleStatDTO getHoleStat() {
        return holeStat;
    }

    public void setHoleStat(HoleStatDTO holeStat) {
        this.holeStat = holeStat;
    }

    public HoleDTO getHole() {
        return hole;
    }

    public void setHole(HoleDTO hole) {
        this.hole = hole;
    }


}
