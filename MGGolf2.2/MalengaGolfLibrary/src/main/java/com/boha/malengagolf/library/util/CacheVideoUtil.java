package com.boha.malengagolf.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.boha.malengagolf.library.data.VideoClipContainer;
import com.google.gson.Gson;

import java.io.*;

/**
 * Created by aubreyM on 2014/04/16.
 */
public class CacheVideoUtil {

    public interface CacheVideoListener {
        public void onDataDeserialized(VideoClipContainer vcc);
    }

    static VideoClipContainer videoClipContainer;
    static CacheVideoListener listener;
    static Context ctx;

    public static void cacheVideo(Context context, VideoClipContainer r) {
        videoClipContainer = r;
        ctx = context;
        new CacheTask().execute();
    }
    public static void getCachedVideo(Context context, CacheVideoListener cacheVideoListener) {
        listener = cacheVideoListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }

    static class CacheTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String json = null;
            File file = null;
            FileOutputStream outputStream;
            try {
                json = gson.toJson(videoClipContainer);
                outputStream = ctx.openFileOutput(VIDEO_CLIP_FILENAME, Context.MODE_PRIVATE);
                outputStream.write(json.getBytes());
                outputStream.close();

                file = ctx.getFileStreamPath(VIDEO_CLIP_FILENAME);
                Log.i(LOG, "VideoClipContainer json written to file: " + file.getAbsolutePath() +
                        " - length: " + file.length());
            } catch (IOException e) {
                Log.e(LOG, "Failed to cache data", e);
            }
            return null;
        }
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, VideoClipContainer> {


        @Override
        protected VideoClipContainer doInBackground(Void... voids) {
            VideoClipContainer vcc = null;
            FileInputStream stream;
            try {

                try {
                     stream = ctx.openFileInput(VIDEO_CLIP_FILENAME);
                    String json = getStringFromInputStream(stream);
                    Log.i(LOG, "VideoClipContainer json retrieved: " + json.length());
                    vcc = gson.fromJson(json, VideoClipContainer.class);
                } catch (FileNotFoundException e) {
                    return new VideoClipContainer();
                }
                return vcc;

            } catch (IOException e) {

            }
            return vcc;
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


        @Override
        protected void onPostExecute(VideoClipContainer v) {
            listener.onDataDeserialized(v);
        }
    }



    static final String LOG = "CacheVideoUtil";
    static final String
            VIDEO_CLIP_FILENAME = "video.json";
    static final Gson gson = new Gson();
}
