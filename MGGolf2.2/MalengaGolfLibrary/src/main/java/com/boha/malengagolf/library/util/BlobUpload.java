package com.boha.malengagolf.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.UploadBlobDTO;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import khandroid.ext.apache.http.HttpEntity;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.util.ByteArrayBuffer;

public class BlobUpload {

    public interface BlobUploadListener {
        public void onImageUploaded(UploadBlobDTO response);

        public void onUploadError();
    }

    static final String LOGTAG = "BlobUpload";

    static BlobUploadListener blobUploadListener;
    static ResponseDTO response;
    static String uploadUrl;
    static File file;

    public static void upload(String url, File f, Context c,
                              BlobUploadListener listener) {
        uploadUrl = url;
        file = f;
        blobUploadListener = listener;
        if (!BaseVolley.checkNetworkOnDevice(c)) {
            return;
        }
        Log.i(LOGTAG, "....starting image upload");
        new ImageUploadTask().execute();
    }

    static class ImageUploadTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            InputStream is = null;
            String responseJSON = null;
            try {
                response = new ResponseDTO();
                MultipartEntity reqEntity = null;
                try {
                    reqEntity = new MultipartEntity();
                } catch (Exception e) {
                    Log.e(LOGTAG, "MultiPartEntity Error - some null pointer!",
                            e);
                    throw new Exception();
                }
                Log.e(LOGTAG, "URL: " + uploadUrl);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(uploadUrl);
                Log.d(LOGTAG, "sending image upload to " + uploadUrl);
                FileBody fileBody = new FileBody(file);
                reqEntity.addPart("file", fileBody);

                httppost.setEntity(reqEntity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                HttpEntity resEntity = httpResponse.getEntity();

                is = resEntity.getContent();
                int size = 0;
                ByteArrayBuffer bab = new ByteArrayBuffer(8192);
                byte[] buffer = new byte[8192];
                while ((size = is.read(buffer, 0, buffer.length)) != -1) {
                    bab.append(buffer, 0, size);
                }
                responseJSON = new String(bab.toByteArray());
                if (responseJSON != null) {
                    Log.w(LOGTAG, "Response from upload:\n" + responseJSON);
                    response = gson.fromJson(responseJSON, ResponseDTO.class);
                }

            } catch (Exception e) {
                Log.e(LOGTAG, "Upload failed", e);
                return 9997;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i(LOGTAG, "....ending image upload");
            if (result > 0) {
                blobUploadListener.onUploadError();
                return;
            }

            blobUploadListener.onImageUploaded(response.getUploadBlob());
        }

    }

    static Gson gson = new Gson();
}
