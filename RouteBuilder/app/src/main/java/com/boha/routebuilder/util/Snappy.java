package com.boha.routebuilder.util;

import android.os.AsyncTask;
import android.util.Log;

import com.boha.routebuilder.RBApp;
import com.boha.routebuilder.TRouteDTO;
import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreymalabie on 4/18/16.
 */
public class Snappy {


    public interface SnappyListener {
        void onRoutesAdded();

        void onRoutesRetrieved(List<TRouteDTO> routes);

        void onError(String message);

        void onRouteDeleted();
    }

    static SnappyListener listener;
    static final String ROUTE = "ROUTE";


    public static void addRoutes(RBApp app, List<TRouteDTO> routes,
                                 SnappyListener mSnappyListener) {
        listener = mSnappyListener;
        getDatabase(app);
        new MTask(routes).execute();
    }

    public static void getRoutes(RBApp app,
                                 SnappyListener mSnappyListener) {
        listener = mSnappyListener;
        getDatabase(app);
        new MTask().execute();
    }

    public static void deleteRoute(RBApp app, Integer routeID,
                                   SnappyListener mSnappyListener) {
        listener = mSnappyListener;
        getDatabase(app);
        new MTask(routeID).execute();
    }

    static class MTask extends AsyncTask<Void, Void, List<TRouteDTO>> {

        static final int ADD_ROUTES = 2,
                GET_ROUTES = 3, DELETE_ROUTE = 4;
        int type;
        List<TRouteDTO> routes;
        Integer routeID;

        public MTask() {
            type = GET_ROUTES;
        }

        public MTask(Integer routeID) {
            type = DELETE_ROUTE;
            this.routeID = routeID;
        }

        public MTask(List<TRouteDTO> routes) {
            type = ADD_ROUTES;
            this.routes = routes;
        }

        @Override
        protected List<TRouteDTO> doInBackground(Void... params) {
            try {
                switch (type) {
                    case ADD_ROUTES:
                        for (TRouteDTO r : routes) {
                            String json = GSON.toJson(r);
                            snappyDB.put(ROUTE + r.getRouteID(), json);
                        }
                        Log.w(LOG, "Routes added to cache: " + routes.size());
                        break;
                    case GET_ROUTES:
                        routes = new ArrayList<>();
                        String[] keys = snappyDB.findKeys(ROUTE);
                        for (String key : keys) {
                            String json = snappyDB.get(key);
                            TRouteDTO route = GSON.fromJson(json, TRouteDTO.class);
                            routes.add(route);
                        }
                        Log.w(LOG, "Routes retrived from cache: " + routes.size());
                        break;
                    case DELETE_ROUTE:
                        snappyDB.del(ROUTE + routeID);
                        Log.e(LOG, "Route removed from local cache");
                        break;

                }
            } catch (SnappydbException e) {
                Log.e(LOG, "Snappy failed", e);
                routes = null;
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<TRouteDTO> routes) {
            if (routes == null) {
                listener.onError("Snappy failed to work for you");
                return;
            }
            if (type == ADD_ROUTES) {
                listener.onRoutesAdded();
            }
            if (type == GET_ROUTES) {
                listener.onRoutesRetrieved(routes);
            }
            if (type == DELETE_ROUTE) {
                listener.onRouteDeleted();
            }
        }
    }

    static DB snappyDB;
    static final Gson GSON = new Gson();

    static void getDatabase(RBApp app) {
        snappyDB = app.getSnappyDB();
    }

    static final String LOG = Snappy.class.getSimpleName();
}
