package com.boha.malengagolf.library.volley.toolbox;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.boha.malengagolf.library.util.LoaderResponseDTO;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import khandroid.ext.apache.http.util.ByteArrayBuffer;

public class BohaLoaderRequest extends Request<LoaderResponseDTO> {

    private Listener<LoaderResponseDTO> listener;
    private ErrorListener errorListener;
    private long start, end;

    public BohaLoaderRequest(int method, String url, ErrorListener listener) {
        super(method, url, listener);
    }

    public BohaLoaderRequest(int method, String url,
                             Listener<LoaderResponseDTO> responseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = responseListener;
        this.errorListener = errorListener;
        start = System.currentTimeMillis();
        Log.e(LOG, "...Cloud Server communication started ...");

    }

    @Override
    protected Response<LoaderResponseDTO> parseNetworkResponse(
            NetworkResponse response) {
        LoaderResponseDTO dto = new LoaderResponseDTO();

        try {
            Gson gson = new Gson();
            String resp = new String(response.data);
            Log.d(LOG, resp);
            try {
                JsonReader reader = new JsonReader(new StringReader(resp));
                reader.setLenient(true);
                dto = gson.fromJson(reader,LoaderResponseDTO.class);
                //dto = gson.fromJson(resp, LoaderResponseDTO.class);
                if (dto != null) {
                    return Response.success(dto,
                            HttpHeaderParser.parseCacheHeaders(response));
                }
            } catch (Exception e) {
                Log.e(LOG, "JSON fucked?", e);
                // ignore, it's a zipped response or it failed to parse
            }

            InputStream is = new ByteArrayInputStream(response.data);
            ZipInputStream zis = new ZipInputStream(is);
            @SuppressWarnings("unused")
            ZipEntry entry;
            ByteArrayBuffer bab = new ByteArrayBuffer(2048);

            while ((entry = zis.getNextEntry()) != null) {
                int size = 0;
                byte[] buffer = new byte[2048];
                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                    bab.append(buffer, 0, size);
                }
                resp = new String(bab.toByteArray());
                dto = gson.fromJson(resp, LoaderResponseDTO.class);
            }
        } catch (Exception e) {
            VolleyError ve = new VolleyError("Exception parsing server data", e);
            errorListener.onErrorResponse(ve);
            Log.e(LOG, "Unable to complete request: " + dto.getMessage(), e);
            return Response.error(new VolleyError(dto.getMessage()));
        }
        return Response.success(dto,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(LoaderResponseDTO response) {
        end = System.currentTimeMillis();
        listener.onResponse(response);
    }

    public static double getElapsed(long start, long end) {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));
        return m.doubleValue();
    }

    static final String LOG = "BohaLoaderRequest";
}
