package com.boha.golfpractice.golfer.services;

import android.app.IntentService;
import android.content.Intent;

import com.boha.golfpractice.golfer.activities.MonApp;
import com.boha.golfpractice.golfer.dto.RequestDTO;
import com.boha.golfpractice.golfer.dto.ResponseDTO;
import com.boha.golfpractice.golfer.util.MonLog;
import com.boha.golfpractice.golfer.util.OKHttpException;
import com.boha.golfpractice.golfer.util.OKUtil;
import com.boha.golfpractice.golfer.util.SnappyGolfCourse;
import com.boha.golfpractice.golfer.util.WebCheck;


public class GolfCourseRefreshService extends IntentService {

    static final String LOG = GolfCourseRefreshService.class.getSimpleName();
    public GolfCourseRefreshService() {
        super("GolfCourseRefreshService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        MonLog.i(getApplicationContext(),LOG,"######## onHandleIntent");

        if (!WebCheck.checkNetworkAvailability(getApplicationContext()).isWifiConnected()) {
            MonLog.e(getApplicationContext(),LOG,"WIFI is NOT connected, quittin");
            return;
        }
        getGolfCourses();
    }

    private void getGolfCourses() {
        RequestDTO w = new RequestDTO(RequestDTO.GET_ALL_GOLF_COURSES);
        w.setZipResponse(true);

        OKUtil okUtil = new OKUtil();
        try {
            okUtil.sendGETRequest(getApplicationContext(), w, null, new OKUtil.OKListener() {
                @Override
                public void onResponse(ResponseDTO response) {
                    SnappyGolfCourse.addGolfCourses((MonApp) getApplication(), response.getGolfCourseList(), new SnappyGolfCourse.DBWriteListener() {
                        @Override
                        public void onDataWritten() {
                            MonLog.i(getApplicationContext(),LOG,"************* GolfCourse list refreshed on cache");
                        }

                        @Override
                        public void onError(String message) {

                        }
                    });

                }

                @Override
                public void onError(String message) {

                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }
}
