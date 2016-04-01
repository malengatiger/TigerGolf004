package com.boha.malengagolf.library;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by aubreyM on 2014/04/24.
 */
public class YouTubeService extends IntentService {
    public YouTubeService(String name) {
        super(name);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
