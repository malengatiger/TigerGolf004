package com.boha.malengagolf.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.boha.malengagolf.library.data.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aubreyM on 2014/04/16.
 */
public class CacheUtil {

    public static final int CACHE_PLAYERS = 1, CACHE_GG_DATA = 10, CACHE_TOURN_PLAYERS = 11,
            CACHE_SCORERS = 2, CACHE_ADMINS = 3, CACHE_TOURNAMENTS = 4, CACHE_LEADER_BOARD = 5,
            CACHE_GOLFGROUPS = 6, CACHE_NEAREST_CLUBS = 7, CACHE_LEADERBOARD_CARRIERS = 8, CACHE_COUNTRY = 9;

    public interface CacheUtilListener {
        public void onFileDataDeserialized(ResponseDTO response);
        public void onDataCached();
    }

    static int dataType, tournamentID;
    static ResponseDTO response;
    static CacheUtilListener listener;
    static Context ctx;

    public static void cacheData(Context context, ResponseDTO r, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        listener = cacheUtilListener;
        ctx = context;
        new CacheTask().execute();
    }
    public static void cacheData(Context context, ResponseDTO r, int type, int tourneyID, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        listener = cacheUtilListener;
        ctx = context;
        tournamentID = tourneyID;
        new CacheTask().execute();
    }
    public static void getCachedData(Context context, int type, int tourneyID, CacheUtilListener cacheUtilListener) {
        Log.e(LOG, "################ getting cached data ..................");
        dataType = type;
        listener = cacheUtilListener;
        ctx = context;
        tournamentID = tourneyID;
        new CacheRetrieveTask().execute();
    }

    static class CacheTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String json = null;
            File file = null;
            FileOutputStream outputStream;
            try {
                switch (dataType) {

                    case CACHE_COUNTRY:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.COUNTRY_JSON, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.COUNTRY_JSON);
                        break;
                    case CACHE_PLAYERS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.PLAYER_LIST, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.PLAYER_LIST);
                        if (file != null) {
                            Log.w(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_SCORERS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.SCORER_LIST, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.SCORER_LIST);
                        if (file != null) {
                            Log.w(LOG, ".....Scorers Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length() );
                        }
                        break;
                    case CACHE_ADMINS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.ADMIN_LIST, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.ADMIN_LIST);
                        if (file != null) {
                            Log.w(LOG, "......Admins Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_TOURNAMENTS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.TOURNAMENT_LIST, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.TOURNAMENT_LIST);
                        if (file != null) {
                            Log.w(LOG, ".....Tournament Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_GOLFGROUPS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.GOLF_GROUPS, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.GOLF_GROUPS);
                        if (file != null) {
                            Log.w(LOG, "......GolfGroup Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_LEADER_BOARD:

                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.LEADERBOARD + tournamentID, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.LEADERBOARD + tournamentID);
                        if (file != null) {
                            Log.w(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_TOURN_PLAYERS:
                        for (LeaderBoardDTO d: response.getLeaderBoardList()) {
                            d.setTimeStamp(new Date().getTime());
                        }
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.TOURNAMENT_PLAYER_LIST + tournamentID, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.TOURNAMENT_PLAYER_LIST + tournamentID);
                        if (file != null) {
                            Log.w(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length() + " items: " + response.getLeaderBoardList().size());
                        }
                        break;
                    case CACHE_LEADERBOARD_CARRIERS:

                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.LEADERBOARD_CARRIERS + tournamentID, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.LEADERBOARD_CARRIERS + tournamentID);
                        if (file != null) {
                            Log.w(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length() + " items: " + response.getLeaderBoardCarriers().size());
                        }
                        break;
                    case CACHE_NEAREST_CLUBS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(Constants.NEAREST_CLUBS, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(Constants.NEAREST_CLUBS);
                        if (file != null) {
                            Log.w(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    default:
                        Log.e(LOG, "######### NOTHING done ...");
                        break;

                }

            } catch (IOException e) {
                Log.w(LOG, "Failed to cache data", e);
            }
            return null;
        }

        private void write(FileOutputStream outputStream, String json) throws IOException {
            outputStream.write(json.getBytes());
            outputStream.close();
        }
        @Override
        protected void onPostExecute(Void v) {
            listener.onDataCached();
        }
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            if (response == null) {
                response = new ResponseDTO();
            }
            return response;
        }
        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = null;
            FileInputStream stream;
            Log.e(LOG, "########### doInBackground: getting cached data ....");
            try {
                switch (dataType) {
                    case CACHE_COUNTRY:
                        stream = ctx.openFileInput(Constants.COUNTRY_JSON);
                        response = getData(stream);
                        break;
                    case CACHE_PLAYERS:
                        stream = ctx.openFileInput(Constants.PLAYER_LIST);
                        response = getData(stream);
                        if (response.getPlayers() == null) {
                            response.setPlayers(new ArrayList<PlayerDTO>());
                        }
                        break;
                    case CACHE_SCORERS:
                        stream = ctx.openFileInput(Constants.SCORER_LIST);
                        response = getData(stream);
                        if (response.getScorers() == null) {
                            response.setScorers(new ArrayList<ScorerDTO>());
                        }
                        break;
                    case CACHE_ADMINS:
                        stream = ctx.openFileInput(Constants.ADMIN_LIST);
                        response = getData(stream);
                        if (response.getAdministrators() == null) {
                            response.setAdministrators(new ArrayList<AdministratorDTO>());
                        }
                        break;
                    case CACHE_TOURNAMENTS:
                        stream = ctx.openFileInput(Constants.TOURNAMENT_LIST);
                        response = getData(stream);
                        if (response.getTournaments() == null) {
                            response.setTournaments(new ArrayList<TournamentDTO>());
                        }
                        break;
                    case CACHE_GOLFGROUPS:
                        stream = ctx.openFileInput(Constants.GOLF_GROUPS);
                        response = getData(stream);
                        if (response.getGolfGroups() == null) {
                            response.setGolfGroups(new ArrayList<GolfGroupDTO>());
                        }
                        break;
                    case CACHE_LEADER_BOARD:
                        stream = ctx.openFileInput(Constants.LEADERBOARD + tournamentID);
                        response = getData(stream);
                        if (response.getLeaderBoardList() == null) {
                            response.setLeaderBoardList(new ArrayList<LeaderBoardDTO>());
                        }
                        break;
                    case CACHE_TOURN_PLAYERS:
                        stream = ctx.openFileInput(Constants.TOURNAMENT_PLAYER_LIST + tournamentID);
                        response = getData(stream);
                        if (response.getLeaderBoardList() == null) {
                            response.setLeaderBoardList(new ArrayList<LeaderBoardDTO>());
                        }
                        break;
                    case CACHE_LEADERBOARD_CARRIERS:
                        stream = ctx.openFileInput(Constants.LEADERBOARD_CARRIERS + tournamentID);
                        response = getData(stream);
                        if (response.getLeaderBoardCarriers() == null) {
                            response.setLeaderBoardCarriers(new ArrayList<LeaderBoardCarrierDTO>());
                        }
                        break;
                    case CACHE_NEAREST_CLUBS:
                        stream = ctx.openFileInput(Constants.NEAREST_CLUBS);
                        response = getData(stream);
                        if (response.getClubs() == null) {
                            response.setClubs(new ArrayList<ClubDTO>());
                        }
                        break;
                }

            } catch (IOException e) {
                Log.e(LOG, "Failed to retrieve cache: " + e.getMessage());
                return null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(ResponseDTO v) {
            if (v != null) {
                Log.w(LOG, "$$$$$$$$$$$$ cached data retrieved");
            } else {
                Log.w(LOG, "No existing cache file was found ....must be virgin signin");
            }

            listener.onFileDataDeserialized(v);
        }
    }


    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;

    }

    static final String LOG = "CacheUtil";
    static final Gson gson = new Gson();
}
