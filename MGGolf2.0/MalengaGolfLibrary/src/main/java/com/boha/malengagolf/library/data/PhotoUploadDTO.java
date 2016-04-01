package com.boha.malengagolf.library.data;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by aubreyM on 2014/04/20.
 */
public class PhotoUploadDTO implements Serializable {

    public interface PhotoUploadedListener {
        public void onPhotoUploaded();
        public void onPhotoUploadFailed();
    }
    static final Logger logger = Logger.getLogger("PhotoServlet");
    public static final String GOLF_GROUP_PREFIX = "golfgroup";
    public static final String TOURNAMENT_PREFIX = "tournament";
    public static final String PLAYER_PREFIX = "player";
    public static final String PARENT_PREFIX = "parent";
    public static final String SCORER_PREFIX = "scorer";
    public static final String THUMB_PREFIX = "thumbnails";

    public static final int PICTURES_FULL_SIZE = 1;
    public static final int PICTURES_THUMBNAILS = 2;

    private int golfGroupID, tournamentID, type, playerID, parentID, scorerID, administratorID;
    private List<String> tags;

    public int getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(int administratorID) {
        this.administratorID = administratorID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public int getScorerID() {
        return scorerID;
    }

    public void setScorerID(int scorerID) {
        this.scorerID = scorerID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


}
