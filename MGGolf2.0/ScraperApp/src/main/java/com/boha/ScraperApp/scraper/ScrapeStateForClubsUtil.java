package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.util.LoaderRequestDTO;
import com.boha.malengagolf.library.util.LoaderResponseDTO;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class ScrapeStateForClubsUtil {
    static final String LOG = "ScrapeStateForClubsUtil";
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
    static ProvinceDTO province;

    public static void scrapeClubsInState(Context context, ProvinceDTO s, String countryName,
                                          CityScraperListener listener) throws IOException {
        u = baseUrl + countryName + "/" + s.getWebKey();
        Log.e(LOG, "===============================> processing state for clubs: " + s.getProvinceName() + " country: "
                + countryName);
        ctx = context;
        province = s;
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

            counter = 0;
            for (int i = 0; i < 15; i++) {
                if (i == 0) {
                    url = u + "/?rpp=100";
                } else {
                    url = u + "/?rpp=100&offset=" + (i * 100);
                }
                try {
                    Log.w(LOG, "...CONNECTING.....CONNECTING TO: " + url);
                    List<WorkerBee.Coords> coordsList = WorkerBee.startScan(url);
                    for (WorkerBee.Coords c : coordsList) {
                        if (c.getKey() == 0) {
                            continue;
                        }

                        ClubDTO cc = new ClubDTO();
                        cc.setProvinceID(province.getProvinceID());
                        cc.setClubName(c.getClubName());
                        cc.setLatitude(c.getLat());
                        cc.setLongitude(c.getLng());
                        sendClub(cc);
                        counter++;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            long e = System.currentTimeMillis();
            Log.d(LOG, "TOTAL CLUBS SCRAPED & ADDED TO DATABASE: " + (province.getClubs().size()) + ". Elapsed time: "
                    + (e - s) / 1000 + " seconds");

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


    private static void sendClub(final ClubDTO club) {
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.LOAD_CITY_CLUBS);
        w.setProvinceID(province.getProvinceID());
        List<ClubDTO> cList = new ArrayList<ClubDTO>();
        cList.add(club);
        w.setClubList(cList);

        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                Log.d(LOG, "Club loaded on database... " + club.getClubName());
                if (province.getClubs() == null) {
                    province.setClubs(new ArrayList<ClubDTO>());
                }
                if (response.getClubList() != null) {
                    province.getClubs().addAll(response.getClubList());
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    static int counter;
    static int currentPageIndex = 0;
    static final int ROWS_PER_PAGE = 100, RADIUS = 20, ONE_PAGER = 21;

}
