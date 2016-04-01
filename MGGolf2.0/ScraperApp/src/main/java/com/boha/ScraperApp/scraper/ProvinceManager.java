package com.boha.ScraperApp.scraper;

import android.content.Context;
import com.boha.malengagolf.library.data.CountryDTO;

import java.io.IOException;

/**
 * Created by aubreyM on 2014/05/02.
 */
public class ProvinceManager {

    public interface ProvinceListener {
        public void onProvincesFound();
    }
    static Context ctx;
    static  ProvinceListener listener;
    public static void loadProvinces(Context context, final CountryDTO country,
                   final ProvinceListener provinceListener) throws IOException {
        listener = provinceListener;
        ctx = context;
        getStates(country);


    }
    private static void getStates(final CountryDTO country) throws IOException {

        ScrapeCountryUtil.scrapeStates(ctx, country, new ScrapeCountryUtil.ScraperListener() {
            @Override
            public void onFinished() {

               listener.onProvincesFound();
            }
        });
    }



    static final String LOG = "ProvinceManager";
}
