package com.boha.malengagolf.library.util;

import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.TournamentDTO;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class GalleryRow implements Serializable, Comparable<GalleryRow>{

    TournamentDTO tournament;
    GolfGroupDTO golfGroup;
    private String fileName;
    private String detail;

    public TournamentDTO getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
    }

    public GolfGroupDTO getGolfGroup() {
        return golfGroup;
    }

    public void setGolfGroup(GolfGroupDTO golfGroup) {
        this.golfGroup = golfGroup;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int compareTo(GalleryRow galleryRow) {
        return this.fileName.compareTo(galleryRow.fileName) * -1;
    }
}
