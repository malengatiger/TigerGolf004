package com.boha.malengagolf.library.util;

import android.util.Log;

import com.boha.malengagolf.library.data.ResponseDTO;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by aubreyM on 2014/08/10.
 */
public class ZipUtil {
    static final int BUFFER = 2048;

    //TODO implement in the other apps - maybe make a jar of this class
    public static void unpack(ByteBuffer bb, WebSocketUtil.WebSocketListener listener) throws ZipException {
        //notify listener
        ResponseDTO response = unpackBytes(bb);
        Log.e(LOG, "##### unpack - telling listener that response object is ready after unpack");
        listener.onMessage(response);
    }

    public static ResponseDTO unpackBytes(ByteBuffer bb) throws ZipException {
        Log.d(LOG, "##### unpack - starting to unpack byte buffer: " + bb.capacity());
        InputStream is = new ByteArrayInputStream(bb.array());
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ResponseDTO response = null;
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[BUFFER];
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                String filename = ze.getName();
                byte[] bytes = baos.toByteArray();
                String json = new String(bytes);
                Log.e(LOG, "#### Downloaded file: " + filename + ", length: " + json.length());
                response = gson.fromJson(json, ResponseDTO.class);
            }
        } catch (Exception e) {
            Log.e(LOG, "Failed to unpack byteBuffer", e);
            throw new ZipException();
        } finally {
            try {
                zis.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ZipException();
            }
        }
        if (response == null) throw new ZipException();
        return response;
    }

    static final String LOG = ZipUtil.class.getSimpleName();
    static final Gson gson = new Gson();

    public static class ZipException extends Exception {

    }
}
