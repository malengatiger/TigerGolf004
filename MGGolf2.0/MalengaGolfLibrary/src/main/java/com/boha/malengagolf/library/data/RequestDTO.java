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
@SuppressWarnings(value = "unused")
public class RequestDTO implements Serializable {

    public static final int ADMIN = 1, PLAYER = 2, SCORER = 3, PARENT = 4, VOLUNTEER = 5, APP_USER = 6;


    private String email, pin, gcmRegistrationID, sessionID;
    private Integer golfGroupID;
    private Integer winnerFlag, leaderBoardID, appUserID;
    private Integer requestType;
    private Boolean zippedResponse;
    private Integer personalPlayerID, type, tournamentType, viewerType;
    private Double latitude, longitude;
    private Integer radius, radiusType, page;
    private Integer tournamentID, playerID, countryID,scorerID, administratorID, provinceID, clubCourseID;

    private GcmDeviceDTO gcmDevice;
    private List<Integer> idList;
    private PersonalPlayerDTO personalPlayer;
    private PersonalScoreDTO personalScore;
    private ClubCourseDTO clubCourse;
    private List<LeaderBoardDTO> leaderBoardList;
    private VideoClipDTO videoClip;
    private GolfGroupDTO golfGroup;
    private TournamentDTO tournament;
    private PlayerDTO player;
    private TourneyScoreByRoundDTO tourneyScoreByRound;
    private AdministratorDTO administrator;
    private List<PlayerDTO> playerList;
    private LeaderBoardDTO leaderBoard;
    private ParentDTO parent;
    private ClubDTO club;
    private ScorerDTO scorer;
    private List<LeaderBoardDTO> players;
    private List<TourneyScoreByRoundDTO> tourneyScoreByRoundList;
    private TeamDTO team;
    private TourneyScoreByRoundTeamDTO tourneyScoreByRoundTeam;

    //
    public static final Integer KILOMETRES = 1, MILES = 2, RADIUS = 30;
    public static final Integer ADMIN_LOGIN = 1;
    public static final Integer ADD_GOLF_GROUP = 2;
    public static final Integer UPDATE_GOLF_GROUP = 3;
    public static final int ADD_TOURNAMENT = 4;
    public static final int UPDATE_TOURNAMENT = 5;
    public static final int ADD_TOURNAMENT_PLAYER = 6;
    public static final int ADD_TOURNAMENT_PLAYERS = 616;

    public static final int UPDATE_TOURNAMENT_SCORES = 7;
    public static final int ADD_PARENT = 8;
    public static final int UPDATE_PARENT = 9;
    public static final int GET_LEADERBOARD = 10;
    public static final int ADD_CLUB = 11;
    public static final int UPDATE_CLUB = 12;
    public static final int ADD_ADMINISTRATOR = 13;
    public static final int UPDATE_ADMINISTRATOR = 14;
    public static final int ADD_PLAYER = 15;
    public static final int UPDATE_PLAYER = 16;
    public static final int UPDATE_PLAYER_PARENT = 17;

    public static final int GET_CLUBS_IN_PROVINCE = 18;
    public static final int GET_CLUBS_IN_COUNTRY = 19;
    public static final int GET_AGE_GROUPS = 20;
    public static final int GET_AGE_GROUPS_BOYS = 21;
    public static final int GET_AGE_GROUPS_GIRLS = 22;
    public static final int GET_LEADERBOARD_BOYS = 23;
    public static final int GET_LEADERBOARD_GIRLS = 24;
    public static final int GET_COUNTRIES = 25;
    public static final int ADD_CLUB_COURSE = 30;
    public static final int UPDATE_CLUB_COURSE = 31;

    public static final int ADD_TEE_TIMES = 32;
    public static final int GET_TOURNAMENT_SCORES = 33;
    public static final int UPDATE_TEE_TIMES = 34;

    public static final int REMOVE_TOURNAMENT_PLAYER = 35;
    public static final int UPDATE_TOURNAMENT_SCORE_TOTALS = 36;
    public static final int GET_TEE_TIMES = 37;
    public static final int GET_GOLF_GROUP_DATA = 38;
    public static final int ADD_SCORER = 39;
    public static final int UPDATE_SCORER = 40;
    public static final int GET_TOURNAMENT_PLAYERS = 41;
    public static final int CLOSE_TOURNAMENT = 42;
    public static final int GET_PLAYER_HISTORY = 43;

    public static final int ADD_PERSONAL_PLAYER = 44;
    public static final int UPDATE_PERSONAL_PLAYER = 45;
    public static final int ADD_PERSONAL_SCORE = 46;
    public static final int GET_PERSONAL_SCORES = 47;
    public static final int PERSONAL_PLAYER_LOGIN = 48;
    public static final int UPDATE_WINNER_FLAG = 50;

    public static final int GET_GOLFGROUP_THUMBNAILS = 60;
    public static final int GET_TOURNAMENT_THUMBNAILS = 61;
    public static final int GET_GOLFGROUP_PICTURES = 62;
    public static final int GET_TOURNAMENT_PICTURES = 63;

    public static final int ADD_VIDEO_CLIP = 64;
    public static final int GET_VIDEO_CLIPS_BY_GOLF_GROUP = 66;
    public static final int CLOSE_LEADERBORD = 67;
    public static final int WITHDRAW_PLAYER = 69;
    public static final int DELETE_TOURNAMENT = 70;
    public static final int GET_PROVINCES = 71;
    public static final int SEND_GCM_REGISTRATION = 72;

    public static final int GET_ERROR_REPORTS = 73;
    public static final int SIGN_IN_SCORER = 75;
    public static final int SIGN_IN_PLAYER = 76;
    public static final int SIGN_IN_LEADERBOARD = 77;

    public static final int GET_TOURNAMENTS = 78;
    public static final int GET_PLAYER_GROUPS = 80;

    public static final int REGISTER_APP_USER = 81;
    public static final int SIGNIN_APP_USER = 82;

    public static final int GET_APPUSER_GROUPS = 83;
    public static final int DELETE_SAMPLE_TOURNAMENTS = 84;
    public static final int IMPORT_PLAYERS = 85;

    public static final int GET_CLUBS_NEARBY = 119;
    public static final int GET_CLUBS_IN_STATE = 120;
    public static final int REGISTER_ADMIN_FOR_TOURNAMENT_UPDATES = 121;
    public static final int REGISTER_APPUSER_FOR_TOURNAMENT_UPDATES = 122;
    public static final int REGISTER_SCORER_FOR_TOURNAMENT_UPDATES = 123;
    public static final int REGISTER_PLAYER_FOR_TOURNAMENT_UPDATES = 124;

    public static final int STROKE_PLAY_INDIVIDUAL = 1;
    public static final int STABLEFORD_INDIVIDUAL = 2;
    public static final int BETTER_BALL_STROKEPLAY = 3;
    public static final int BETTER_BALL_STABLEFORD = 4;
    public static final int ALLIANCE_STABLEFORD = 5;

    private ImportPlayerDTO importPlayer;
    private List<ImportPlayerDTO> importPlayers;

    public Integer getViewerType() {
        return viewerType;
    }

    public void setViewerType(Integer viewerType) {
        this.viewerType = viewerType;
    }

    public Integer getScorerID() {
        return scorerID;
    }

    public void setScorerID(Integer scorerID) {
        this.scorerID = scorerID;
    }

    public Integer getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(Integer administratorID) {
        this.administratorID = administratorID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public List<ImportPlayerDTO> getImportPlayers() {
        return importPlayers;
    }

    public void setImportPlayers(List<ImportPlayerDTO> importPlayers) {
        this.importPlayers = importPlayers;
    }

    public ImportPlayerDTO getImportPlayer() {
        return importPlayer;
    }

    public void setImportPlayer(ImportPlayerDTO importPlayer) {
        this.importPlayer = importPlayer;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public TourneyScoreByRoundTeamDTO getTourneyScoreByRoundTeam() {
        return tourneyScoreByRoundTeam;
    }

    public void setTourneyScoreByRoundTeam(TourneyScoreByRoundTeamDTO tourneyScoreByRoundTeam) {
        this.tourneyScoreByRoundTeam = tourneyScoreByRoundTeam;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public Integer getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(Integer tournamentType) {
        this.tournamentType = tournamentType;
    }

    public Integer getAppUserID() {
        return appUserID;
    }

    public void setAppUserID(Integer appUserID) {
        this.appUserID = appUserID;
    }

    public String getGcmRegistrationID() {
        return gcmRegistrationID;
    }

    public void setGcmRegistrationID(String gcmRegistrationID) {
        this.gcmRegistrationID = gcmRegistrationID;
    }

    public GcmDeviceDTO getGcmDevice() {
        return gcmDevice;
    }

    public void setGcmDevice(GcmDeviceDTO gcmDevice) {
        this.gcmDevice = gcmDevice;
    }

    public ClubCourseDTO getClubCourse() {
        return clubCourse;
    }

    public void setClubCourse(ClubCourseDTO clubCourse) {
        this.clubCourse = clubCourse;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public double getLatitude() {
        return latitude.doubleValue();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude.doubleValue();
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Integer getRadiusType() {
        return radiusType;
    }

    public void setRadiusType(Integer radiusType) {
        this.radiusType = radiusType;
    }

    public List<LeaderBoardDTO> getLeaderBoardList() {
        return leaderBoardList;
    }

    public void setLeaderBoardList(List<LeaderBoardDTO> leaderBoardList) {
        this.leaderBoardList = leaderBoardList;
    }

    public VideoClipDTO getVideoClip() {
        return videoClip;
    }

    public void setVideoClip(VideoClipDTO videoClip) {
        this.videoClip = videoClip;
    }

    public Integer getWinnerFlag() {
        return winnerFlag;
    }

    public void setWinnerFlag(Integer winnerFlag) {
        this.winnerFlag = winnerFlag;
    }

    public Integer getLeaderBoardID() {
        return leaderBoardID;
    }

    public void setLeaderBoardID(Integer leaderBoardID) {
        this.leaderBoardID = leaderBoardID;
    }

    public Integer getPersonalPlayerID() {
        return personalPlayerID;
    }

    public void setPersonalPlayerID(Integer personalPlayerID) {
        this.personalPlayerID = personalPlayerID;
    }

    public TourneyScoreByRoundDTO getTourneyScoreByRound() {
        return tourneyScoreByRound;
    }

    public void setTourneyScoreByRound(TourneyScoreByRoundDTO tourneyScoreByRound) {
        this.tourneyScoreByRound = tourneyScoreByRound;
    }

    public PersonalPlayerDTO getPersonalPlayer() {
        return personalPlayer;
    }

    public void setPersonalPlayer(PersonalPlayerDTO personalPlayer) {
        this.personalPlayer = personalPlayer;
    }

    public PersonalScoreDTO getPersonalScore() {
        return personalScore;
    }

    public void setPersonalScore(PersonalScoreDTO personalScore) {
        this.personalScore = personalScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(Integer golfGroupID) {
        this.golfGroupID = golfGroupID;
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

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

    public Integer getClubCourseID() {
        return clubCourseID;
    }

    public void setClubCourseID(Integer clubCourseID) {
        this.clubCourseID = clubCourseID;
    }

    public GolfGroupDTO getGolfGroup() {
        return golfGroup;
    }

    public void setGolfGroup(GolfGroupDTO golfGroup) {
        this.golfGroup = golfGroup;
    }

    public TournamentDTO getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public AdministratorDTO getAdministrator() {
        return administrator;
    }

    public void setAdministrator(AdministratorDTO administrator) {
        this.administrator = administrator;
    }

    public List<PlayerDTO> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerDTO> playerList) {
        this.playerList = playerList;
    }

    public ParentDTO getParent() {
        return parent;
    }

    public void setParent(ParentDTO parent) {
        this.parent = parent;
    }

    public LeaderBoardDTO getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(LeaderBoardDTO leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public List<LeaderBoardDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<LeaderBoardDTO> players) {
        this.players = players;
    }

    public ClubDTO getClub() {
        return club;
    }

    public void setClub(ClubDTO club) {
        this.club = club;
    }

    public List<TourneyScoreByRoundDTO> getTourneyScoreByRoundList() {
        return tourneyScoreByRoundList;
    }

    public void setTourneyScoreByRoundList(List<TourneyScoreByRoundDTO> tourneyScoreByRoundList) {
        this.tourneyScoreByRoundList = tourneyScoreByRoundList;
    }

    public ScorerDTO getScorer() {
        return scorer;
    }

    public void setScorer(ScorerDTO scorer) {
        this.scorer = scorer;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public boolean isZippedResponse() {
        return zippedResponse;
    }

    public void setZippedResponse(boolean zippedResponse) {
        this.zippedResponse = zippedResponse;
    }

}
