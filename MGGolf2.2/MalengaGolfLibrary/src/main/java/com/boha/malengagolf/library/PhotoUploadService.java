package com.boha.malengagolf.library;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.util.CDNUploader;

public class PhotoUploadService extends IntentService {

    public PhotoUploadService() {
        super("PhotoUploadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            PhotoUploadDTO p = (PhotoUploadDTO)intent.getSerializableExtra("photo");
            uploadFile(p);
            return;
        }
    }

    private void uploadFile(PhotoUploadDTO p) {

        CDNUploader.uploadFile(getApplicationContext(), p, new CDNUploader.CDNUploaderListener() {
            @Override
            public void onFileUploaded(PhotoUploadDTO photo) {

                Intent m = new Intent(BROADCAST_PHOTO_UPLOADED);
                m.putExtra("photo",photo);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(m);
            }

            @Override
            public void onError(String message) {

            }
        });
    }


    static final String LOG = PhotoUploadService.class.getSimpleName(), BROADCAST_PHOTO_UPLOADED = "com.boha.PHOTO_UPLOADED";
}
