package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.base.CityDTO;
import com.boha.malengagolf.library.util.LoaderRequestDTO;
import com.boha.malengagolf.library.util.LoaderResponseDTO;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class ScrapeForCityCoordinatesUtil {
    static final String LOG = "ScrapeCityUtil";
    static final String baseUrl = "http://www.1golf.eu/en/golf-courses/";
    static Context ctx;

    static class Parms {
        public Parms(int r, int p) {
            radius = r;
            rowsPerPage = p;
        }

        int radius = 20;
        int rowsPerPage = 100;
        int offSet = 0;
    }

    static Parms parms = new Parms(0, 0);

    public interface CityScraperListener {
        public void onFinished();
    }

    static CityScraperListener scraperListener;
    static String u;
    static List<CityDTO> cityList;

    public static void scrapeCourses(Context context, List<CityDTO> list, String countryName,
                                     CityScraperListener listener) throws IOException {
        u = baseUrl + countryName + "/cities/";
        ctx = context;
        cityList = list;
        scraperListener = listener;

        parms.radius = RADIUS;
        parms.offSet = 0;
        parms.rowsPerPage = ROWS_PER_PAGE;
        new HTask().execute(parms);

    }

    static class HTask extends AsyncTask<Parms, Void, Integer> {
        @Override
        protected Integer doInBackground(Parms... parmses) {
            long s = System.currentTimeMillis();
            Parms parms = parmses[0];
            String url = null;
            Document doc;
            try {
                if (cityList == null) {
                    Log.e(LOG, "cityList is NULL, what the fuck?????");
                    return 3;
                }
                for (CityDTO city : cityList) {
                    url = u + city.getWebKey() + "/?rpp=20";
                    Log.w(LOG, ".....CONNECTING...CONNECTING TO: " + url);
                    try {
                        Connection con = Jsoup.connect(url);
                        if (con == null) {
                            throw new IOException();
                        }
                        doc = con.get();
                        if (doc == null) {
                            throw new IOException();
                        }
                        WorkerBee.Coords coords = WorkerBee.getCityCoordinates(doc);
                        city.setLatitude(coords.getLat());
                        city.setLongitude(coords.getLng());
                        updateCity(city);
                        Thread.sleep(500);

                    } catch (Exception e) {
                        Log.e(LOG, "***** Failed while getting coordinates for "
                                + city.getCityName());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.e(LOG,"getting coords",e);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer r) {
            if (r.intValue() > 0) {
                Toast.makeText(ctx, "Fucked up!", Toast.LENGTH_SHORT).show();
            }
            scraperListener.onFinished();

        }
    }

    private static void updateCity(final CityDTO c) {
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.UPDATE_CITY_COORDINATES);
        w.setCityID(c.getCityID());
        w.setLatitude(c.getLatitude());
        w.setLongitude(c.getLongitude());

        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                Log.d(LOG, "city coordinates updated: " + c.getCityName());
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }


    static int counter;
    static int currentPageIndex = 0;
    static final int ROWS_PER_PAGE = 20, RADIUS = 20, ONE_PAGER = 21;

}
