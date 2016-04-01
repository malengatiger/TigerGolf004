package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.util.Log;
import com.boha.malengagolf.library.util.CountryContainer;
import com.google.gson.Gson;

import java.io.*;

/**
 * Created by aubreyM on 2014/04/16.
 */
public class FileUtil {


    public static void writeScrapeFile(CountryContainer response, Context ctx) throws IOException {
        String json = gson.toJson(response);
        FileOutputStream outputStream;
        outputStream = ctx.openFileOutput(SCRAPE_FILE, Context.MODE_PRIVATE);
        outputStream.write(json.getBytes());
        outputStream.close();

        File file = ctx.getFileStreamPath(SCRAPE_FILE);
        Log.i(LOG, "CountryContainer json written to file: " + file.getAbsolutePath() +
                " - length: " + file.length());
    }

    public static CountryContainer getScrapeData(Context ctx) throws IOException {
        CountryContainer response = null;

        try {
            FileInputStream stream = ctx.openFileInput(SCRAPE_FILE);
            String json = getStringFromInputStream(stream);
            Log.i(LOG, "CountryContainer json retrieved: " + json.length());
            response = gson.fromJson(json, CountryContainer.class);
        } catch (Exception e) {
            response = new CountryContainer();
            Log.i(LOG, "new CountryContainer created ");
        }

        return response;
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

    static final String LOG = "FileUtil";
    static final String RESPONSE_FILENAME = "response.json",
            VIDEO_CLIP_FILENAME = "video.json", SCRAPE_FILE = "scrape003l.json";
    static final Gson gson = new Gson();
}
