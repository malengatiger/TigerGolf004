package com.boha.golfpractice.golfer.services;

import android.app.IntentService;
import android.content.Intent;

public class SesssionCloserService extends IntentService {
    public SesssionCloserService() {
        super("SesssionCloserService");
    }

    static final String LOG = SesssionCloserService.class.getSimpleName();
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
        }
    }

}
