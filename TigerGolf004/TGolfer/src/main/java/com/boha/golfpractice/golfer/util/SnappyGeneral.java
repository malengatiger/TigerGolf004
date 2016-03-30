package com.boha.golfpractice.golfer.util;

import android.os.AsyncTask;
import android.util.Log;

import com.boha.golfpractice.golfer.activities.MonApp;
import com.boha.golfpractice.golfer.dto.ClubDTO;
import com.boha.golfpractice.golfer.dto.PlayerDTO;
import com.boha.golfpractice.golfer.dto.ResponseDTO;
import com.boha.golfpractice.golfer.dto.ShotShapeDTO;
import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.List;

/**
 * Created by aubreymalabie on 3/18/16.
 */
public class SnappyGeneral {

    private static DB snappydb;
    private static MonApp app;
    private static DBReadListener dbReadListener;
    private static DBWriteListener dbWriteListener;
    private static String LOG = SnappyGeneral.class.getSimpleName();


    static final int
            ADD_CLUBS = 1,
            ADD_SHOT_SHAPES = 2,
            GET_ALL_LOOKUPS = 3,
            ADD_PLAYERS = 4;

    static final String
            CLUB = "CLUB",
            PLAYER = "PLAYER",
            SHOT_SHAPE = "SHOT_SHAPE";


    public static void addPlayers(MonApp monApp, List<PlayerDTO> list, DBWriteListener listener) {
        getDatabase(monApp);
        dbWriteListener = listener;
        GeneralTask task = new GeneralTask(list, 1);
        task.execute();
    }

    public static void addClubs(MonApp monApp, List<ClubDTO> list, DBWriteListener listener) {
        getDatabase(monApp);
        dbWriteListener = listener;
        GeneralTask task = new GeneralTask(list, true);
        task.execute();
    }

    public static void addShotShapes(MonApp monApp, List<ShotShapeDTO> list, DBWriteListener listener) {
        getDatabase(monApp);
        dbWriteListener = listener;
        GeneralTask task = new GeneralTask(list);
        task.execute();
    }


    public static void getLookups(MonApp monApp, DBReadListener listener) {
        getDatabase(monApp);
        dbReadListener = listener;
        GeneralTask task = new GeneralTask();
        task.execute();
    }

    static Gson gson = new Gson();
    private static class GeneralTask extends AsyncTask<Void, Void, ResponseDTO> {
        List<ClubDTO> clubList;
        List<ShotShapeDTO> shotShapeList;
        List<PlayerDTO> playerList;
        int type;
        boolean isError;

        public GeneralTask() {
            type = GET_ALL_LOOKUPS;
        }


        public GeneralTask(List<ClubDTO> list, boolean x) {
            this.clubList = list;
            type = ADD_CLUBS;
        }

        public GeneralTask(List<ShotShapeDTO> list) {
            this.shotShapeList = list;
            type = ADD_SHOT_SHAPES;
        }

        public GeneralTask(List<PlayerDTO> list, int isAddingPlayers) {
            this.playerList = list;
            type = ADD_PLAYERS;
        }


        @Override
        protected ResponseDTO doInBackground(Void... params) {
            ResponseDTO resp = new ResponseDTO();
            switch (type) {
                case ADD_PLAYERS:
                    for (PlayerDTO gc : playerList) {
                        try {
                            String json = gson.toJson(gc);
                            snappydb.put(PLAYER + gc.getPlayerID(), json);
                        } catch (SnappydbException e) {
                            Log.e(LOG, "Failed player write", e);
                            isError = true;
                            break;
                        }
                    }
                    MonLog.d(app.getApplicationContext(), LOG, "players added to cache: " + playerList.size());
                    break;
                case ADD_CLUBS:
                    for (ClubDTO gc : clubList) {
                        try {
                            String json = gson.toJson(gc);
                            snappydb.put(CLUB + gc.getClubID(), json);
                        } catch (SnappydbException e) {
                            Log.e(LOG, "Failed club write", e);
                            isError = true;
                            break;
                        }
                    }
                    MonLog.d(app.getApplicationContext(), LOG, "clubs added to cache: " + clubList.size());
                    break;
                case ADD_SHOT_SHAPES:
                    for (ShotShapeDTO gc : shotShapeList) {
                        try {
                            String json = gson.toJson(gc);
                            snappydb.put(SHOT_SHAPE + gc.getShotShapeID(), json);
                        } catch (SnappydbException e) {
                            Log.e(LOG, "Failed shotShape write", e);
                            isError = true;
                            break;
                        }
                    }
                    MonLog.d(app.getApplicationContext(), LOG, "shotShapes added to cache: " + shotShapeList.size());
                    break;

                case GET_ALL_LOOKUPS:
                    try {

                        String[] keys = snappydb.findKeys(CLUB);
                        for (String key : keys) {
                            String json = snappydb.get(key);
                            resp.getClubList().add(gson.fromJson(json,ClubDTO.class));
                        }
                        keys = snappydb.findKeys(SHOT_SHAPE);
                        for (String key : keys) {
                            String json = snappydb.get(key);
                            resp.getShotShapeList().add(gson.fromJson(json,ShotShapeDTO.class));
                        }
                        keys = snappydb.findKeys(PLAYER);
                        for (String key : keys) {
                            String json = snappydb.get(key);
                            resp.getPlayerList().add(gson.fromJson(json,PlayerDTO.class));
                        }
                    } catch (SnappydbException e) {
                        Log.e(LOG, "Failed to get lookups list", e);
                        isError = true;
                        break;
                    }
                    MonLog.d(app.getApplicationContext(), LOG, "clubs and shotShapes list from cache: ");
                    break;

            }

            return resp;
        }

        @Override
        protected void onPostExecute(ResponseDTO resp) {
            switch (type) {
                case ADD_PLAYERS:
                    if (dbWriteListener != null) {
                        if (isError) {
                            dbWriteListener.onError("Failed to add players to cache");
                        } else {
                            dbWriteListener.onDataWritten();
                        }
                    }
                    break;
                case ADD_CLUBS:
                    if (dbWriteListener != null) {
                        if (isError) {
                            dbWriteListener.onError("Failed to add clubs to cache");
                        } else {
                            dbWriteListener.onDataWritten();
                        }

                    }
                    break;
                case ADD_SHOT_SHAPES:
                    if (dbWriteListener != null) {
                        if (isError) {
                            dbWriteListener.onError("Failed to add shotShapes to cache");
                        } else {
                            dbWriteListener.onDataWritten();
                        }

                    }
                    break;

                case GET_ALL_LOOKUPS:
                    if (dbReadListener != null) {
                        if (isError) {
                            dbReadListener.onError("Failed to get practiceSessions from cache");
                        } else {
                            dbReadListener.onDataRead(resp);
                        }

                    }

            }
        }
    }

    public interface DBWriteListener {
        void onDataWritten();

        void onError(String message);
    }

    public interface DBReadListener {
        void onDataRead(ResponseDTO response);

        void onError(String message);
    }

    private static void getDatabase(MonApp monApp) {
        app = monApp;
        try {
            if (snappydb == null || !snappydb.isOpen()) {
                snappydb = app.getSnappyDB();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
