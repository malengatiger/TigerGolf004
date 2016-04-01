package com.boha.golfpractice.player.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.boha.golfpractice.player.activities.MonApp;
import com.boha.golfpractice.player.dto.RequestDTO;
import com.boha.golfpractice.player.dto.ResponseDTO;
import com.boha.golfpractice.player.dto.VideoUploadDTO;
import com.boha.golfpractice.player.util.CDNUploader;
import com.boha.golfpractice.player.util.MonLog;
import com.boha.golfpractice.player.util.OKUtil;
import com.boha.golfpractice.player.util.SnappyVideo;
import com.boha.golfpractice.player.util.Util;
import com.cloudinary.Cloudinary;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CDNVideoService extends IntentService {
    static final String LOG = CDNVideoService.class.getSimpleName(),
            VIDEO = "video";
    
    public CDNVideoService() {
        super("CDNVideoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MonLog.w(getApplicationContext(),LOG,"--------------- onHandleIntent");
        SnappyVideo.getVideo((MonApp) getApplication(), new SnappyVideo.ReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                for (VideoUploadDTO v: response.getVideoUploadList()) {
                    if (v.getDateUploaded() == null) {
                        new VTask().execute(v);
                    }
                }
            }

            @Override
            public void onError() {

            }
        });
    }
    
    private class VTask extends AsyncTask<VideoUploadDTO,Void,Integer> {

        @Override
        protected Integer doInBackground(VideoUploadDTO... params) {
            MonLog.d(getApplicationContext(),LOG,"****** starting video upload to CDN");
            VideoUploadDTO dto = params[0];
            long start = System.currentTimeMillis();
            try {
                Map config = new HashMap();

                config.put("cloud_name", CDNUploader.CLOUD_NAME);
                config.put("api_key", CDNUploader.API_KEY);
                config.put("api_secret", CDNUploader.API_SECRET);
                config.put("resource_type", VIDEO);

                Cloudinary cloudinary = new Cloudinary(config);
                File file = new File(dto.getFilePath());
                Log.d(LOG, "## videoFile: " + file.getAbsolutePath()
                        + " length: " + file.length());

                Map map = cloudinary.uploader().upload(file, config);

                long end = System.currentTimeMillis();
                MonLog.d(getApplicationContext(),LOG, "---- video uploaded: " + map.get("url") + " elapsed: "
                        + Util.getElapsed(start, end) + " seconds\n" + map.get("secure_url"));

                dto.setUrl((String) map.get("secure_url"));
                dto.setDateUploaded(new Date().getTime());

                RequestDTO w = new RequestDTO(RequestDTO.ADD_VIDEO);
                w.setVideoUpload(dto);
                w.setZipResponse(false);
                OKUtil util = new OKUtil();
                ResponseDTO resp = util.sendSynchronousGET(getApplicationContext(),w);
                if (resp.getStatusCode() == 0) {
                    VideoUploadDTO v = resp.getVideoUploadList().get(0);
                    v.setFilePath(dto.getFilePath());
                    v.setThumbnailPath(dto.getThumbnailPath());
                    v.setDateUploaded(new Date().getTime());
                    SnappyVideo.addVideo((MonApp) getApplication(), v, new SnappyVideo.WriteListener() {
                        @Override
                        public void onDataWritten() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            } catch (Exception e) {
                Log.e(LOG,"Failed to upload video");
                return 9;
            }

            return 0;
        }
        @Override
        protected void onPostExecute(Integer v) {
            if (v == 0) {
                MonLog.w(getApplicationContext(),LOG,"video metadata sent to server, OK");
            }
        }
    }
}
