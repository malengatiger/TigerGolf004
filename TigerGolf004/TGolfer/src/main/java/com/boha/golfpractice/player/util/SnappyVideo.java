package com.boha.golfpractice.player.util;

import android.os.AsyncTask;
import android.util.Log;

import com.boha.golfpractice.player.activities.MonApp;
import com.boha.golfpractice.player.dto.ResponseDTO;
import com.boha.golfpractice.player.dto.VideoUploadDTO;
import com.google.gson.Gson;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aubreymalabie on 3/27/16.
 */
public class SnappyVideo {

    public interface ReadListener {
        void onDataRead(ResponseDTO response);
        void onError();
    }
    public interface WriteListener {
        void onDataWritten();
        void onError();
    }
    static WriteListener writeListener;
    static ReadListener readListener;
    static MonApp app;
    static DB snappydb;
    static final String VIDEO = "VIDEO", LOG = SnappyVideo.class.getSimpleName();

    public static void addVideo(MonApp app, List<VideoUploadDTO> list, WriteListener listener) {
        writeListener = listener;
        getDatabase(app);

        VTask task = new VTask(list);
        task.execute();
    }
    public static void addVideo(MonApp app, VideoUploadDTO video, WriteListener listener) {
        writeListener = listener;
        getDatabase(app);

        List<VideoUploadDTO> list = new ArrayList<>();
        list.add(video);
        VTask task = new VTask(list);
        task.execute();
    }
    public static void getVideo(MonApp app, ReadListener listener) {
        readListener = listener;
        getDatabase(app);

        VTask task = new VTask();
        task.execute();
    }

    static class VTask extends AsyncTask<Void,Void,ResponseDTO> {
        private List<VideoUploadDTO> videoUploadList;

        public VTask(List<VideoUploadDTO> list) {
            videoUploadList = list;
        }
        public VTask() {

        }
        @Override
        protected ResponseDTO doInBackground(Void... params) {
            ResponseDTO w = new ResponseDTO();
            try {
                if (videoUploadList != null) {
                    for (VideoUploadDTO v: videoUploadList) {
                        String json = gson.toJson(v);
                        snappydb.put(VIDEO + v.getDateTaken(),json);
                        Log.w(LOG,"++++++ video added to cache: " + new Date(v.getDateTaken()));
                    }

                } else {
                    String[] keys = snappydb.findKeys(VIDEO);
                    for (String key: keys) {
                        String json = snappydb.get(key);
                        w.getVideoUploadList().add(gson.fromJson(json,VideoUploadDTO.class));
                    }
                    Log.d(LOG,"+++++++ video from cache: " + w.getVideoUploadList().size());

                }
            } catch (Exception e) {
                w.setStatusCode(9);
                w.setMessage("Unable to work with cache video");
            }

            return w;
        }
        @Override
        protected void onPostExecute(ResponseDTO r) {
            if (r.getStatusCode() > 0) {
                if (videoUploadList == null) {
                    readListener.onError();
                    return;
                } else {
                    writeListener.onError();
                    return;
                }
            } else {
                if (videoUploadList == null) {
                    readListener.onDataRead(r);
                } else {
                    writeListener.onDataWritten();
                }
            }
        }
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
    static Gson gson = new Gson();
}
