package com.boha.malengagolf.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.cloudinary.Cloudinary;

import org.acra.ACRA;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the image uploadToYouTube to the Cloudinary CDN.
 * The uploadToYouTube runs in a Thread and returns its response via CDNUploaderListener
 * <p/>
 * Created by aubreyM on 15/06/08.
 */
public class CDNUploader {
    public interface CDNUploaderListener {
        void onFileUploaded(PhotoUploadDTO photo);

        void onError(String message);
    }

    static CDNUploaderListener mListener;
    static final String LOG = CDNUploader.class.getSimpleName();
    static Context ctx;
    public static final String
            API_KEY = "397571984789619",
            API_SECRET = "2RBq1clEHC5X_0eQlNP-K3yhA8U",
            CLOUD_NAME = "bohatmx";




    /**
     * Upload photo to CDN (Cloudinary at this time). On return of the CDN response, a call is made
     * to the backend to add the metadata of the photo to the backend database
     *
     * @param context
     * @param dto
     * @param uploaderListener
     * @see PhotoUploadDTO
     */
    public static void uploadFile(final Context context, final PhotoUploadDTO dto, CDNUploaderListener uploaderListener) {
        mListener = uploaderListener;
        ctx = context;
        Log.d(LOG, "##### starting CDNUploader uploadFile: " + dto.getFilePath());
        new CDNTask().execute(dto);
    }

    static class CDNTask extends AsyncTask<PhotoUploadDTO, Void, PhotoUploadDTO> {

        @Override
        protected PhotoUploadDTO doInBackground(PhotoUploadDTO... params) {
            final PhotoUploadDTO dto = params[0];
            final long start = System.currentTimeMillis();
            Map config = new HashMap();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);

            Cloudinary cloudinary = new Cloudinary(config);
            File file = new File(dto.getFilePath());
            Map map;
            try {
                map = cloudinary.uploader().upload(file, config);
                long end = System.currentTimeMillis();
                Log.i(LOG, "----> photo uploaded: " + map.get("url") + " elapsed: "
                        + Util.getElapsed(start, end) + " seconds");

                dto.setUrl((String) map.get("secure_url"));
                dto.setDateUploaded(new Date().getTime());

            } catch (Exception e) {
                Log.e(LOG, "CDN uploadToYouTube Failed", e);
                try {
                    ACRA.getErrorReporter().handleException(e, false);
                } catch (Exception ex) {//ignore}
                }
                return null;
            }
            return dto;
        }

        @Override
        protected void onPostExecute(final PhotoUploadDTO dto) {
            if (dto == null) {
                mListener.onError("Error uploading image to CDN");
                return;
            }
            Log.e(LOG,"****** uploading photo metadata");
            RequestDTO w = new RequestDTO(RequestDTO.ADD_PHOTO);
            w.setPhoto(dto);

            BaseVolley.getRemoteData(Statics.ADMIN_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
                @Override
                public void onResponseReceived(ResponseDTO response) {
                    if (response.getStatusCode() == 0) {
                        Log.e(LOG,"******  photo metadata uploaded OK");
                        mListener.onFileUploaded(response.getPhotoUploads().get(0));
                    } else {
                        mListener.onError(response.getMessage());
                    }
                }

                @Override
                public void onVolleyError(VolleyError error) {
                    mListener.onError(error.getMessage());
                }
            });
        }
    }
}
