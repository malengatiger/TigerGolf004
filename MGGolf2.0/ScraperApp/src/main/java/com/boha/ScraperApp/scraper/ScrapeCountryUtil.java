package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.CountryDTO;
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
public class ScrapeCountryUtil {
    static final String LOG = "ScrapeCountryUtil";
    static final String baseUrl = "http://www.1golf.eu/en/golf-courses/";
    static CountryDTO country;
    static Context ctx;

    public interface ScraperListener {
        public void onFinished();
    }

    static ScraperListener scraperListener;
    static String u;

    public static void scrapeStates(Context context, CountryDTO c, ScraperListener listener) throws IOException {
        u = baseUrl + c.getCountryName() + "/";
        ctx = context;
        country = c;
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
                for (Element e : links) {
                    String href = e.attr("href");
                    Matcher matcher = pattern.matcher(href);
                    if (matcher.find()) {
                        if (e.text().equalsIgnoreCase("Region")) {
                            start = true;

                        }
                        if (e.text().equalsIgnoreCase("City")) {
                            end = true;
                            break;
                        }
                    }
                    if (start) {
                        if (firstTime) {
                            firstTime = false;
                            continue;
                        }
                        String[] strings = href.split("/");
                        ProvinceDTO st = new ProvinceDTO();
                        st.setProvinceName(e.text());
                        st.setCountryID(country.getCountryID());
                        st.setWebKey(strings[strings.length - 1]);
                        loadState(st);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        //Log.d(LOG, "State scraped - " + st.getProvinceName() + " webKey: " + st.getWebKey());
                        cnt++;
                    }
                    if (end) {
                        break;
                    }
                }
                long e = System.currentTimeMillis();
                Log.i(LOG, "States created: " + cnt + " elapsed: " + (e - s) / 1000 + " seconds");


            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer r) {
            if (r.intValue() > 0) {
                Toast.makeText(ctx, "Fucked up!", Toast.LENGTH_SHORT).show();
                return;
            }
            scraperListener.onFinished();
        }
    }
    private static void loadState(final ProvinceDTO p) {
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.LOAD_PROVINCES);
        List<ProvinceDTO> list = new ArrayList<ProvinceDTO>();
        list.add(p);
        w.setProvinceList(list);
        w.setCountryID(country.getCountryID());

        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                Log.d(LOG, "State has been loaded on db: " + p.getProvinceName());
                if (country.getProvinces() == null) {
                    country.setProvinces(new ArrayList<ProvinceDTO>());
                }
                country.getProvinces().addAll(response.getProvinceList());
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }
    static Gson gson = new Gson();
    static List<String> states;

}
