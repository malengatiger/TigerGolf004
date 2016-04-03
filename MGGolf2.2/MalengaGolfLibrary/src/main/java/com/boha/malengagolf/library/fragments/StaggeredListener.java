package com.boha.malengagolf.library.fragments;

import com.boha.malengagolf.library.data.LeaderBoardDTO;

/**
 * Created by aubreyM on 2014/06/20.
 */
public interface StaggeredListener {
    public void setBusy();
    public void setNotBusy();
    public void onPlayerTapped(LeaderBoardDTO lb, int index);
    public void onTournamentImagesNotFound();
}
