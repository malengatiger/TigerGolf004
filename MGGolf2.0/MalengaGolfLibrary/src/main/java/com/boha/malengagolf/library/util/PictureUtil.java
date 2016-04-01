package com.boha.malengagolf.library.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.data.ResponseDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureUtil {

    public static void uploadImage(int golfGroupID, int tournamentID, String uri,
               int type, Context ctx,final PhotoUploadDTO.PhotoUploadedListener listener) {

        File imageFile = new File(Uri.parse(uri).getPath());
        Log.i("pic", "Uri for upload: " + uri);
        Log.w(LOG, "File to be uploaded - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            PhotoUploadDTO dto = new PhotoUploadDTO();
            dto.setGolfGroupID(golfGroupID);
            dto.setTournamentID(tournamentID);
            dto.setType(type);
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener() {
                        @Override
                        public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG,
                                    "Error uploading - onUploadError");
                        }

                        @Override
                        public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG,
                                        "Error uploading - "
                                                + response.getMessage()
                                );
                            }
                        }
                    }
            );
        }
    }
    public static void uploadPlayerImage(int golfGroupID, int playerID, String uri,
                                   int type, Context ctx, final PhotoUploadDTO.PhotoUploadedListener listener) {

        File imageFile = new File(Uri.parse(uri).getPath());
        Log.i("pic", "Uri for upload: " + uri);
        Log.w(LOG, "File to be uploaded - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            PhotoUploadDTO dto = new PhotoUploadDTO();
            dto.setGolfGroupID(golfGroupID);
            dto.setPlayerID(playerID);
            dto.setType(type);
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener() {
                        @Override
                        public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG,
                                    "Error uploading - onUploadError");
                        }

                        @Override
                        public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG,
                                        "Error uploading - "
                                                + response.getMessage()
                                );
                            }
                        }
                    }
            );
        }
    }
    public static void uploadAdminImage(int golfGroupID, int adminID, String uri,
                                         int type, Context ctx, final PhotoUploadDTO.PhotoUploadedListener listener) {

        File imageFile = new File(Uri.parse(uri).getPath());
        Log.i("pic", "Uri for upload: " + uri);
        Log.w(LOG, "File to be uploaded - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            PhotoUploadDTO dto = new PhotoUploadDTO();
            dto.setGolfGroupID(golfGroupID);
            dto.setAdministratorID(adminID);
            dto.setType(type);
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener() {
                        @Override
                        public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG,
                                    "Error uploading - onUploadError");
                        }

                        @Override
                        public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG,
                                        "Error uploading - "
                                                + response.getMessage()
                                );
                            }
                        }
                    }
            );
        }
    }
    public static void uploadParentImage(int golfGroupID, int parentID, String uri,
                                         int type, Context ctx,final PhotoUploadDTO.PhotoUploadedListener listener) {

        File imageFile = new File(Uri.parse(uri).getPath());
        Log.i("pic", "Uri for upload: " + uri);
        Log.w(LOG, "File to be uploaded - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            PhotoUploadDTO dto = new PhotoUploadDTO();
            dto.setGolfGroupID(golfGroupID);
            dto.setParentID(parentID);
            dto.setType(type);
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener() {
                        @Override
                        public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG,
                                    "Error uploading - onUploadError");
                        }

                        @Override
                        public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG,
                                        "Error uploading - "
                                                + response.getMessage()
                                );
                            }
                        }
                    }
            );
        }
    }

    public static void uploadScorerImage(int golfGroupID, int scorerID, String uri,
                                         int type, Context ctx, final PhotoUploadDTO.PhotoUploadedListener listener) {

        File imageFile = new File(Uri.parse(uri).getPath());
        Log.i("pic", "Uri for upload: " + uri);
        Log.w(LOG, "File to be uploaded - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            PhotoUploadDTO dto = new PhotoUploadDTO();
            dto.setGolfGroupID(golfGroupID);
            dto.setScorerID(scorerID);
            dto.setType(type);
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener() {
                        @Override
                        public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG,
                                    "Error uploading - onUploadError");
                        }

                        @Override
                        public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG,
                                        "Error uploading - "
                                                + response.getMessage()
                                );
                            }
                        }
                    }
            );
        }
    }
    private static final String LOG = "PictureUtil";


}
