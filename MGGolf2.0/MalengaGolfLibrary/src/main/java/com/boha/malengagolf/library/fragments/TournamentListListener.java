package com.boha.malengagolf.library.fragments;

import com.boha.malengagolf.library.data.TournamentDTO;

/**
 * Created by aubreyM on 2014/06/10.
 */
public interface TournamentListListener {

    public void manageTournamentPlayersScores(TournamentDTO t);
    public void editTournament(TournamentDTO t);
    public void viewLeaderBoard(TournamentDTO t);

    public void takePictures(TournamentDTO t);
    public void viewGallery(TournamentDTO t);
    public void manageTournamentTees(TournamentDTO t);

    public void inviteAppUser();
    public void uploadVideo(TournamentDTO t);

    public void sendPlayerTextMessage(TournamentDTO t);
    public void sendPlayerEmail(TournamentDTO t);

    public void removeTournament(TournamentDTO t);
    public void deleteSampleTournaments();
}
