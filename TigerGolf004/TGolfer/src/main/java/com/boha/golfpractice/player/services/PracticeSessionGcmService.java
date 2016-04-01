package com.boha.golfpractice.player.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;


public class PracticeSessionGcmService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.i("PracticeGcmService","----------------%%%%%%%%%%% onRunTask");
        Intent m = new Intent(getApplicationContext(), PracticeUploadService.class);
        getApplicationContext().startService(m);
        return 0;
    }

}
