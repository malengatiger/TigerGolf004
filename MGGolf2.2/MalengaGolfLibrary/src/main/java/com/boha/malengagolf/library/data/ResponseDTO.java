/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.malengagolf.library.data;

import android.util.Log;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aubrey Malabie
 */
public class ResponseDTO  extends SugarRecord implements Serializable {
    private Integer statusCode, totalPages, totalClubs;
    private String message, sessionID;
    private LeaderBoardDTO leaderBoard;
    private AppUserDTO appUser;
    private CountryDTO country;
    private PersonalPlayerDTO personalPlayer;
    private List<PersonalPlayerDTO> personalPlayerList;
    private List<PersonalScoreDTO> personalScoreList;
    private List<GolfGroupDTO> golfGroups;
    private List<ParentDTO> parents;
    private List<PlayerDTO> players;
    private List<LeaderBoardDTO> tourneyPlayers;
    private List<TourneyScoreByRoundDTO> tourneyScoreByRoundList;
    private List<TeeTimeDTO> teeTimeList;
    private List<TournamentDTO> tournaments;
    private List<LeaderBoardDTO> leaderBoardList;
    private List<LeaderBoardTeamDTO> leaderBoardTeamList;
    private List<CountryDTO> countries;
    private List<ProvinceDTO> provinces;
    private List<ClubDTO> clubs;
    private List<AgeGroupDTO> ageGroups;
    private List<AdministratorDTO> administrators;
    private List<GolfGroupPlayerDTO> golfGroupPlayers;
    private List<GolfGroupParentDTO> golfGroupParents;
    private List<PhotoUploadDTO> photoUploads;
    private List<ScorerDTO> scorers;
    private GolfGroupDTO golfGroup;
    private AdministratorDTO administrator;
    private List<LeaderBoardCarrierDTO> leaderBoardCarriers;
    private List<ErrorStoreAndroidDTO> errorStoreAndroidList;
    private List<ErrorStoreDTO> errorStoreList;
    private List<String> imageFileNames;
    private List<VideoClipDTO> videoClips;
    private UploadBlobDTO uploadBlob;
    private UploadUrlDTO uploadUrl;

    public String printDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("#########################################\n");
        sb.append("*** ").append("ResponseDTO").append("\n");
        sb.append("*** Tournaments: ").append(getTournaments().size()).append("\n");
        sb.append("*** Admins: ").append(getAdministrators().size()).append("\n");
        sb.append("*** Players: ").append(getPlayers().size()).append("\n");
        sb.append("*** Scorers: ").append(getScorers().size()).append("\n");
        sb.append("*** Clubs: ").append(getClubs().size()).append("\n");
        sb.append("*** LeaderBoards: ").append(getLeaderBoardList().size()).append("\n");
        sb.append("*** Provinces: ").append(getProvinces().size()).append("\n");
        sb.append("*** Countries: ").append(getCountries().size()).append("\n");
        sb.append("#########################################\n");
        sb.append("*** Status Code: ").append(getStatusCode()).append("\n");
        sb.append("*** Message: ").append(getMessage()).append("\n");
        sb.append("#########################################\n");

        Log.w("ResponseDTO",sb.toString());
        return sb.toString();
    }
    public List<PhotoUploadDTO> getPhotoUploads() {
        if (photoUploads == null) {
            photoUploads = new ArrayList<>();
        }
        return photoUploads;
    }

    public void setPhotoUploads(List<PhotoUploadDTO> photoUploads) {
        this.photoUploads = photoUploads;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public UploadBlobDTO getUploadBlob() {
        return uploadBlob;
    }

    public void setUploadBlob(UploadBlobDTO uploadBlob) {
        this.uploadBlob = uploadBlob;
    }

    public UploadUrlDTO getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(UploadUrlDTO uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public List<LeaderBoardCarrierDTO> getLeaderBoardCarriers() {
        if (leaderBoardCarriers == null) {
            leaderBoardCarriers = new ArrayList<>();
        }
        return leaderBoardCarriers;
    }

    public void setLeaderBoardCarriers(List<LeaderBoardCarrierDTO> leaderBoardCarriers) {
        this.leaderBoardCarriers = leaderBoardCarriers;
    }

    //
    public static final int LOGIN_EXCEPTION = 101;
    public static final int DATA_EXCEPTION = 102;
    public static final int DUPLICATE_EXCEPTION = 103;
    public static final int GENERIC_EXCEPTION = 109;

    private String log;

    public List<LeaderBoardTeamDTO> getLeaderBoardTeamList() {
        if (leaderBoardTeamList == null) {
            leaderBoardTeamList = new ArrayList<>();
        }
        return leaderBoardTeamList;
    }

    public void setLeaderBoardTeamList(List<LeaderBoardTeamDTO> leaderBoardTeamList) {
        this.leaderBoardTeamList = leaderBoardTeamList;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Integer getTotalClubs() {
        return totalClubs;
    }

    public void setTotalClubs(Integer totalClubs) {
        this.totalClubs = totalClubs;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<String> getImageFileNames() {
        return imageFileNames;
    }

    public void setImageFileNames(List<String> imageFileNames) {
        this.imageFileNames = imageFileNames;
    }

    public List<ScorerDTO> getScorers() {
        if (scorers == null) {
            scorers = new ArrayList<>();
        }
        return scorers;
    }

    public void setScorers(List<ScorerDTO> scorers) {
        this.scorers = scorers;
    }

    public List<ErrorStoreAndroidDTO> getErrorStoreAndroidList() {
        return errorStoreAndroidList;
    }

    public void setErrorStoreAndroidList(List<ErrorStoreAndroidDTO> errorStoreAndroidList) {
        this.errorStoreAndroidList = errorStoreAndroidList;
    }

    public AppUserDTO getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUserDTO appUser) {
        this.appUser = appUser;
    }

    public List<ErrorStoreDTO> getErrorStoreList() {
        return errorStoreList;
    }

    public void setErrorStoreList(List<ErrorStoreDTO> errorStoreList) {
        this.errorStoreList = errorStoreList;
    }

    public LeaderBoardDTO getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(LeaderBoardDTO leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public List<LeaderBoardDTO> getTourneyPlayers() {
        if (tourneyPlayers == null) {
            tourneyPlayers = new ArrayList<>();
        }
        return tourneyPlayers;
    }

    public void setTourneyPlayers(List<LeaderBoardDTO> tourneyPlayers) {
        this.tourneyPlayers = tourneyPlayers;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public PersonalPlayerDTO getPersonalPlayer() {
        return personalPlayer;
    }

    public void setPersonalPlayer(PersonalPlayerDTO personalPlayer) {
        this.personalPlayer = personalPlayer;
    }

    public List<PersonalPlayerDTO> getPersonalPlayerList() {
        if (personalPlayerList == null) {
            personalPlayerList = new ArrayList<>();
        }
        return personalPlayerList;
    }

    public void setPersonalPlayerList(List<PersonalPlayerDTO> personalPlayerList) {
        this.personalPlayerList = personalPlayerList;
    }

    public List<PersonalScoreDTO> getPersonalScoreList() {
        if (personalScoreList == null) {
            personalScoreList = new ArrayList<>();
        }
        return personalScoreList;
    }

    public void setPersonalScoreList(List<PersonalScoreDTO> personalScoreList) {
        this.personalScoreList = personalScoreList;
    }

    public List<LeaderBoardDTO> getLeaderBoardList() {
        if (leaderBoardList == null) {
            leaderBoardList = new ArrayList<>();
        }
        return leaderBoardList;
    }

    public void setLeaderBoardList(List<LeaderBoardDTO> leaderBoardList) {
        this.leaderBoardList = leaderBoardList;
    }

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GolfGroupDTO> getGolfGroups() {
        if (golfGroups == null) {
            golfGroups = new ArrayList<>();
        }
        return golfGroups;
    }

    public void setGolfGroups(List<GolfGroupDTO> golfGroups) {
        this.golfGroups = golfGroups;
    }

    public List<ParentDTO> getParents() {
        if (parents == null) {
            parents = new ArrayList<>();
        }
        return parents;
    }

    public void setParents(List<ParentDTO> parents) {
        this.parents = parents;
    }

    public List<PlayerDTO> getPlayers() {
        if (players == null) {
            players = new ArrayList<>();
        }
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }


    public List<TourneyScoreByRoundDTO> getTourneyScoreByRoundList() {
        if (tourneyScoreByRoundList == null) {
            tourneyScoreByRoundList = new ArrayList<>();
        }
        return tourneyScoreByRoundList;
    }

    public void setTourneyScoreByRoundList(List<TourneyScoreByRoundDTO> tourneyScoreByRoundList) {
        this.tourneyScoreByRoundList = tourneyScoreByRoundList;
    }

    public List<TeeTimeDTO> getTeeTimeList() {
        if (teeTimeList == null) {
            teeTimeList = new ArrayList<>();
        }
        return teeTimeList;
    }

    public void setTeeTimeList(List<TeeTimeDTO> teeTimeList) {
        this.teeTimeList = teeTimeList;
    }

    public List<TournamentDTO> getTournaments() {
        if (tournaments == null) {
            tournaments = new ArrayList<>();
        }
        return tournaments;
    }

    public void setTournaments(List<TournamentDTO> tournaments) {
        this.tournaments = tournaments;
    }


    public List<CountryDTO> getCountries() {
        if (countries == null) {
            countries = new ArrayList<>();
        }
        return countries;
    }

    public void setCountries(List<CountryDTO> countries) {
        this.countries = countries;
    }

    public List<ProvinceDTO> getProvinces() {
        if (provinces == null) {
            provinces = new ArrayList<>();
        }
        return provinces;
    }

    public void setProvince(List<ProvinceDTO> provinces) {
        this.provinces = provinces;
    }

    public List<ClubDTO> getClubs() {
        if (clubs == null) {
            clubs = new ArrayList<>();
        }
        return clubs;
    }

    public void setClubs(List<ClubDTO> clubs) {
        this.clubs = clubs;
    }

    public List<AgeGroupDTO> getAgeGroups() {
        if (ageGroups == null) {
            ageGroups = new ArrayList<>();
        }
        return ageGroups;
    }

    public void setAgeGroups(List<AgeGroupDTO> ageGroups) {
        this.ageGroups = ageGroups;
    }

    public List<AdministratorDTO> getAdministrators() {
        if (administrators == null) {
            administrators = new ArrayList<>();
        }
        return administrators;
    }

    public void setAdministrators(List<AdministratorDTO> administrators) {
        this.administrators = administrators;
    }

    public List<GolfGroupPlayerDTO> getGolfGroupPlayers() {
        if (golfGroupPlayers == null) {
            golfGroupPlayers = new ArrayList<>();
        }
        return golfGroupPlayers;
    }

    public void setGolfGroupPlayers(List<GolfGroupPlayerDTO> golfGroupPlayers) {
        this.golfGroupPlayers = golfGroupPlayers;
    }

    public List<GolfGroupParentDTO> getGolfGroupParents() {
        return golfGroupParents;
    }

    public void setGolfGroupParents(List<GolfGroupParentDTO> golfGroupParents) {
        this.golfGroupParents = golfGroupParents;
    }

    public void setProvinces(List<ProvinceDTO> provinces) {
        this.provinces = provinces;
    }

    public GolfGroupDTO getGolfGroup() {
        return golfGroup;
    }

    public void setGolfGroup(GolfGroupDTO golfGroup) {
        this.golfGroup = golfGroup;
    }

    public AdministratorDTO getAdministrator() {
        return administrator;
    }

    public void setAdministrator(AdministratorDTO administrator) {
        this.administrator = administrator;
    }

    public List<VideoClipDTO> getVideoClips() {
        if (videoClips == null) {
            videoClips = new ArrayList<>();
        }
        return videoClips;
    }

    public void setVideoClips(List<VideoClipDTO> videoClips) {
        this.videoClips = videoClips;
    }

    public ResponseDTO() {
    }

}
