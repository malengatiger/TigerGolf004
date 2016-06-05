package com.aftarobot.aftarobotmigrator;

import android.app.Application;
import android.util.Log;

import com.firebase.client.Firebase;

/**
 * Created by aubreymalabie on 6/3/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(getApplicationContext());
        Log.i("App","########################### Migrator App has started: Firebase initialized");
    }
}
