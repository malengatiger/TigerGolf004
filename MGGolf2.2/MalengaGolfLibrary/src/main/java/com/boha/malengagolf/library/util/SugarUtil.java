package com.boha.malengagolf.library.util;

import android.os.AsyncTask;
import android.util.Log;

import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreymalabie on 4/3/16.
 */
public class SugarUtil {

    static final String LOG = SugarUtil.class.getSimpleName();
    public static void edit(ResponseDTO resp, SugarListener listener) {
        if (listener != null) {
            //todo surgical removal, untilf further notice
            return;
        }
        List<ExecBag> bags = new ArrayList<>();
        if (!resp.getClubs().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_CLUB,false,listener));
        }
        if (!resp.getTournaments().isEmpty()) {
            bags.add(  new ExecBag(resp,null,ExecBag.ADD_TOURNAMENT,false,listener));
        }
        if (!resp.getAdministrators().isEmpty()) {
            bags.add(new ExecBag(resp,null,ExecBag.ADD_ADMINS,false,listener));
        }
        if (!resp.getPlayers().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_PLAYER,false,listener));
        }
        if (!resp.getScorers().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_SCORERS,false,listener));
        }
        if (!resp.getLeaderBoardList().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_LEADERBOARDS,false,listener));
        }
        if (!resp.getCountries().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_COUNTRY,false,listener));
        }
        if (!resp.getProvinces().isEmpty()) {
            bags.add( new ExecBag(resp,null,ExecBag.ADD_PROVINCE,false,listener));
        }
        if (!bags.isEmpty()) {
            for (ExecBag bag: bags) {
                new SugarTask().execute(bag);
            }
        }
    }
    public static void getPlayers(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_PLAYERS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getTournaments(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_TOURNAMENTS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getAdmins(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_ADMINS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getScorers(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_SCORERS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getCountries(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_COUNTRYS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getProvinces(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_PROVINCE,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getClubs(SugarListener listener) {
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_CLUB,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getLeaderBoards(Integer tournamentID,SugarListener listener) {
        ExecBag bag = new ExecBag(null,tournamentID,ExecBag.GET_LEADERBOARDS,true,listener);
        new SugarTask().execute(bag);
    }
    public static void getAllData(SugarListener listener) {
        if (listener != null) {
            //todo surgical removal, untilf further notice
            return;
        }
        ExecBag bag = new ExecBag(null,null,ExecBag.GET_ALL_DATA,true,listener);
        new SugarTask().execute(bag);
    }
    private static class SugarTask extends AsyncTask<ExecBag, Void, ResponseDTO> {
        ExecBag bag;

        @Override
        protected ResponseDTO doInBackground(ExecBag... params) {
            ResponseDTO response = new ResponseDTO();
            response.setStatusCode(0);
            bag = params[0];
            try {


                switch (bag.type) {
                    case ExecBag.ADD_ADMINS:
                        for (AdministratorDTO c : bag.response.getAdministrators()) {
                            AdministratorDTO x = (AdministratorDTO) AdministratorDTO.find(AdministratorDTO.class,
                                    "administratorID = " + c.getAdministratorID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing Administrator prior to refresh: " + x.getFullName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "Administrator saved on disk: " + c.getFullName());
                        }
                        List<AdministratorDTO> administrators = AdministratorDTO.listAll(AdministratorDTO.class);
                        Log.e(LOG, "****** Administrators on Sugar ORM disk: " + administrators.size());

                        break;
                    case ExecBag.ADD_PLAYER:
                        for (PlayerDTO c : bag.response.getPlayers()) {
                            PlayerDTO x = (PlayerDTO) PlayerDTO.find(PlayerDTO.class,
                                    "playerID = " + c.getPlayerID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing Player prior to refresh: " + x.getFullName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "Player saved on disk: " + c.getFullName());
                        }
                        List<PlayerDTO> players = PlayerDTO.listAll(PlayerDTO.class);
                        Log.e(LOG, "****** Players on Sugar ORM disk: " + players.size());
                        break;
                    case ExecBag.ADD_SCORERS:
                        for (ScorerDTO c : bag.response.getScorers()) {
                            ScorerDTO x = (ScorerDTO) ScorerDTO.find(ScorerDTO.class,
                                    "scorerID = " + c.getScorerID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing Scorer prior to refresh: " + x.getFullName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "Scorer saved on disk: " + c.getFullName());
                        }
                        List<ScorerDTO> scorers = ScorerDTO.listAll(ScorerDTO.class);
                        Log.e(LOG, "****** Scorers on Sugar ORM disk: " + scorers.size());
                        break;
                    case ExecBag.ADD_CLUB:
                        for (ClubDTO c : bag.response.getClubs()) {
                            ClubDTO x = (ClubDTO) ClubDTO.find(ClubDTO.class,
                                    "clubID = " + c.getClubID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing Club prior to refresh: " + x.getClubName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "club saved on disk: " + c.getClubName());
                        }
                        List<ClubDTO> clubs = ClubDTO.listAll(ClubDTO.class);
                        Log.e(LOG, "****** clubs on Sugar ORM disk: " + clubs.size());
                        break;
                    case ExecBag.ADD_LEADERBOARDS:
                        for (LeaderBoardDTO c : bag.response.getLeaderBoardList()) {
                            LeaderBoardDTO x = (LeaderBoardDTO) LeaderBoardDTO.find(LeaderBoardDTO.class,
                                    "leaderBoardID = " + c.getLeaderBoardID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing LeaderBoard prior to refresh: " + x.getPlayer().getFullName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "LeaderBoard saved on disk: " + c.getPlayer().getFullName());
                        }
                        List<LeaderBoardDTO> boards = LeaderBoardDTO.listAll(LeaderBoardDTO.class);
                        Log.e(LOG, "****** LeaderBoards on Sugar ORM disk: " + boards.size());
                        break;
                    case ExecBag.ADD_TOURNAMENT:
                        for (TournamentDTO c : bag.response.getTournaments()) {
                            TournamentDTO x = (TournamentDTO) TournamentDTO.find(TournamentDTO.class,
                                    "tournamentID = " + c.getTournamentID(), "");
                            if (x != null) {
                                Log.d(LOG, "... removing Tournament prior to refresh: " + x.getTourneyName());
                                x.delete();
                            }
                            c.save();
                            Log.w(LOG, "Tournament saved on disk: " + c.getTourneyName());
                        }
                        List<TournamentDTO> tns = TournamentDTO.listAll(TournamentDTO.class);
                        Log.e(LOG, "****** Tournaments on Sugar ORM disk: " + tns.size());
                        break;

                    /////////////////// get lists
                    case ExecBag.GET_ADMINS:
                        response.setAdministrators(AdministratorDTO.listAll(AdministratorDTO.class));
                        break;
                    case ExecBag.GET_PLAYERS:
                        response.setPlayers(PlayerDTO.listAll(PlayerDTO.class));
                        break;
                    case ExecBag.GET_CLUB:
                        response.setClubs(ClubDTO.listAll(ClubDTO.class));
                        break;
                    case ExecBag.GET_SCORERS:
                        response.setScorers(ScorerDTO.listAll(ScorerDTO.class));
                        break;
                    case ExecBag.GET_TOURNAMENTS:
                        response.setTournaments(TournamentDTO.listAll(TournamentDTO.class));
                        break;
                    case ExecBag.GET_LEADERBOARDS:
                        List<LeaderBoardDTO> list = LeaderBoardDTO.find(LeaderBoardDTO.class,"tournamentID = " + bag.tournamentID);
                        response.setLeaderBoardList(list);
                        break;
                    case ExecBag.GET_ALL_DATA:
                        response.setAdministrators(AdministratorDTO.listAll(AdministratorDTO.class));
                        response.setPlayers(PlayerDTO.listAll(PlayerDTO.class));
                        response.setClubs(ClubDTO.listAll(ClubDTO.class));
                        response.setScorers(ScorerDTO.listAll(ScorerDTO.class));
                        response.setTournaments(TournamentDTO.listAll(TournamentDTO.class));
                        response.setCountries(CountryDTO.listAll(CountryDTO.class));
                        response.setProvinces(ProvinceDTO.listAll(ProvinceDTO.class));
                        break;
                }


            } catch (Exception e) {
                response.setStatusCode(9);
                response.setMessage("Problem with data");
            }


            return response;
        }

        @Override
        protected void onPostExecute(ResponseDTO response) {
            if (response.getStatusCode() > 0) {
                if (bag.isReading) {
                    bag.listener.onError("Unable to read data from disk");
                } else {
                    bag.listener.onError("Unable to write data to disk");
                }
                return;
            }
            response.printDetails();
            if (bag.isReading) {
                bag.listener.onDataRead(response);
            } else {
                bag.listener.onDataWritten(response.getStatusCode());
            }

        }
    }


    public static class ExecBag {
        int type;
        boolean isReading;
        ResponseDTO response;
        SugarListener listener;
        Integer tournamentID;

        public ExecBag(ResponseDTO response,
                       Integer tournamentID, int type,
                       boolean isReading, SugarListener listener) {
            this.type = type;
            this.response = response;
            this.listener = listener;
            this.isReading = isReading;
            this.tournamentID = tournamentID;
        }


        public static final int
                ADD_PLAYER = 1,
                ADD_TOURNAMENT = 2,
                ADD_CLUB = 3,
                ADD_ADMINS = 4,
                ADD_SCORERS = 5,
                ADD_LEADERBOARDS = 6,
                ADD_COUNTRY = 7,
                ADD_PROVINCE = 8,
                GET_PLAYERS = 11,
                GET_TOURNAMENTS = 12,
                GET_CLUB = 13,
                GET_ADMINS = 14,
                GET_SCORERS = 15,
                GET_LEADERBOARDS = 16,
                GET_COUNTRYS = 17,
                GET_PROVINCE = 18,
                GET_ALL_DATA = 19;
    }

    public interface SugarListener {
        void onDataWritten(int statusCode);

        void onDataRead(ResponseDTO response);

        void onError(String message);
    }
}
