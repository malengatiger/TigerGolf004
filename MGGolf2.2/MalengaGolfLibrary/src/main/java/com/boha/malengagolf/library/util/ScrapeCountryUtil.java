package com.boha.malengagolf.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
    static final String LOG = "HtmlUtil";
    static final String baseUrl = "http://www.1golf.eu/en/golf-courses/";
    static CountrySCR country = new CountrySCR();
    static CountryContainer countryContainer;
    static Context ctx;

    static String u;
    public static void scrapeStates(Context context, String countryName, String countryCode) throws IOException {
        u = baseUrl + "canada/";
        ctx = context;
        country.setCountryName("malaysia");
        country.setCountryCode("MA");
        boolean found = false;
        for (CountrySCR c: countryContainer.getCountryList()) {
            if (c.getCountryName().equalsIgnoreCase(countryName)) {
                c.setStateList(new ArrayList<State>());
                country = c;
                found = true;
            }
        }
        if (!found) {
            countryContainer.getCountryList().add(country);
        }

        new HTask().execute();

    }
   static class HTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Connection con = Jsoup.connect(u);
            Document doc = null;
            boolean start = false, end = false, firstTime = true;
            try {
                doc = con.get();
                String title = doc.title();
                Elements links = doc.select("a[href]"); // a with href
                Pattern pattern = Pattern.compile("javascript:void");

                for (Element e: links) {
                    Matcher matcher = pattern.matcher(e.attr("href"));
                    if (matcher.find()) {
                        if (e.text().equalsIgnoreCase("Region")) {
                            start = true;

                        }
                        if (e.text().equalsIgnoreCase("City")) {
                            end = true;
                            break;
                        }
                    }
                    if (start ) {
                        if (firstTime) {
                            firstTime =  false;
                            continue;
                        }
                        State st = new State();
                        st.setStateName(e.text());
                        st.setCountryID(country.getCountryID());
                        st.setCountryName(country.getCountryName());
                        country.getStateList().add(st);
                        Log.e(LOG, "State created - " + st.getStateName());
                    }
                    if (end) {
                        break;
                    }
                }
                Log.i(LOG, "States created: " + countryContainer.getCountryList().get(1).getStateList().size());
                //FileUtil.writeScrapeFile(countryContainer, ctx);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    static Gson gson = new Gson();
    static List<String> states;
    private static void loadStates() {
        states = new ArrayList<String>();
        states.add("alabama");
        states.add("alaska");
        states.add("alabama");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
        states.add("");
    }
}
