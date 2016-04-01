package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.base.CityDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.util.LoaderRequestDTO;
import com.boha.malengagolf.library.util.LoaderResponseDTO;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class ScrapeStateForCitiesUtil {
    static final String LOG = "ScrapeStateForCitiesUtil";
    static final String baseUrl = "http://www.1golf.eu/en/golf-courses/";
    static ProvinceDTO state;
    static Context ctx;
    public interface StateScraperListener {
        public void onFinished() throws IOException;
    }
    static StateScraperListener scraperListener;
    static String u;
    public static void scrapeCities(Context context, ProvinceDTO s, String countryName, StateScraperListener listener) throws IOException {
        u = baseUrl + countryName + "/" + s.getWebKey();
        Log.e(LOG,"Url: " + u);
        ctx = context;
        state = s;
        scraperListener = listener;
        new HTask().execute();

    }
   static class HTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            long s = System.currentTimeMillis();
            Connection con = Jsoup.connect(u);
            Document doc = null;
            int cnt = 0;
            boolean start = false, end = false, firstTime = true;
            try {
                doc = con.get();
                String title = doc.title();
                Elements links = doc.select("a[href]"); // a with href
                Pattern pattern = Pattern.compile("javascript:void");
                Pattern pattern2 = Pattern.compile("/");
                for (Element e: links) {
                    String href = e.attr("href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        if (e.text().equalsIgnoreCase("City")) {
                            start = true;

                        }
                        if (e.text().equalsIgnoreCase("Course")) {
                            break;
                        }
                    }
                    if (start ) {
                        if (firstTime) {
                            firstTime =  false;
                            continue;
                        }
                        String[] strings = href.split("/");
                        CityDTO city = new CityDTO();
                        city.setCityName(e.text());
                        city.setProvinceID(state.getProvinceID());
                        city.setWebKey(strings[strings.length - 1]);
                        sendCity(city);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        //Log.d(LOG, "City scraped - " + city.getCityName() + " webKey: " + city.getWebKey());
                        cnt++;
                    }
                    if (end) {
                        break;
                    }
                }
                long e = System.currentTimeMillis();
                Log.i(LOG, "Cities processed: " + cnt + " elapsed: " + (e - s) / 1000 + " seconds");


            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }

            return 0;
        }
       @Override
       protected void onPostExecute(Integer r) {
           if (r.intValue() > 0) {
               Log.e(LOG, "Failed to scrape cities");
               Toast.makeText(ctx,"Fucked up!", Toast.LENGTH_SHORT).show();
               return;
           }
           try {
               scraperListener.onFinished();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    private static void sendCity(final CityDTO city) {
        LoaderRequestDTO  w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.LOAD_PROVINCE_CITIES);
        w.setProvinceID(state.getProvinceID());
        List<CityDTO> list = new ArrayList<CityDTO>();
        list.add(city);
        w.setCityList(list);

        BaseVolley.loadData(Statics.SERVLET_LOADER,w,ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                Log.d(LOG, "City loaded on db: " + city.getCityName());
                if (state.getCityList() == null) state.setCityList(new ArrayList<CityDTO>());
                state.getCityList().addAll(response.getCityList());

            }

            @Override
            public void onVolleyError(VolleyError error) {
                ToastUtil.errorToast(ctx, "Network Error");
            }
        });
    }
    static ScrapeString scrapeString = new ScrapeString();
    static Gson gson = new Gson();
    static List<String> states;

}
