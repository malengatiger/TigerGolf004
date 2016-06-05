package com.boha.routebuilder;

import android.app.Application;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by aubreymalabie on 4/18/16.
 */
public class RBApp extends Application {
    private  DB snappyDB;
    public  DB getSnappyDB() {
        try {
            if (snappyDB == null || !snappyDB.isOpen()) {
                snappyDB = DBFactory.open(getApplicationContext());
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        return snappyDB;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n##############################################\n");
        sb.append("####################################################\n");
        sb.append("###\n");
        sb.append("###  RouteBuilder App has started, setting up resources ...............\n");
        sb.append("###\n");
        sb.append("###################################################\n\n");

        Log.d(LOG, sb.toString());
    }

    static final String LOG = RBApp.class.getSimpleName();
}
