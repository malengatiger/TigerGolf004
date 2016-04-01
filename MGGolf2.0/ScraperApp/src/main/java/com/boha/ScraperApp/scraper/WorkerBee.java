package com.boha.ScraperApp.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage the addition of 'scraped' country, state, city and course data
 *
 * @author aubreyM
 */
public class WorkerBee {

    static final String baseUrl = "http://www.1golf.eu/en/golf-courses/south-africa/cities/midrand/?r=20&rpp=100";

        public static List<Coords> startScan(String url) throws IOException {
        Connection con = Jsoup.connect(url);
        if (con == null) {
            throw new IOException();
        }


        Document doc;
        doc = con.get();
        if (doc == null) {
            throw new IOException();
        }
        processScript(doc);
        Coords cityCoords = getCityCoordinates(doc);
        coordsList.add(cityCoords);

       // System.out.println("Coordinate matrix done, found: " + coordsList.size()
       //         + " city, lat: " + cityCoords.lat + " lng: " + cityCoords.lng);


        return coordsList;
    }

    private static List<String> getHREF(Document doc) {
        List<String> list = new ArrayList<String>();
        Elements links = doc.select("a[href]"); // a with href
        Pattern pattern = Pattern.compile("/en/club/");

        for (Element e : links) {
            String href = e.attr("href");
            Matcher matcher = pattern.matcher(href);
            if (matcher.find()) {
                if (e.text().isEmpty()) {
                    continue;
                }
                if (e.text().contains("Review")) {
                    continue;
                }
                list.add(e.text());
            }
        }
        return list;
    }

    private static List<Coords> processScript(Document doc) {
        coordsList = new ArrayList<Coords>();
        Elements scripts = doc.getElementsByTag("script");
        Pattern px = Pattern.compile("\\},");
        String pattern1 = "setItemList({";
        String pattern2 = "}})";
        Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        for (Element z : scripts) {
            for (DataNode node : z.dataNodes()) {
                String ss = node.getWholeData();
                Matcher m = p.matcher(ss);
                if (m.find()) {
                    String x = m.group(1);
                    String[] subs = px.split(x);
                    for (String string : subs) {
                        Coords c = new Coords(string);
                        coordsList.add(c);
                        //System.out.println(c.lat + " " + c.lng + " key: " + c.key);
                    }
                }

            }
        }
        for (Coords c : coordsList) {
            Elements divs = doc.select("#item-" + c.key);
            for (Element e : divs) {
                c.address = e.text();
            }

        }
        List<String> list = getHREF(doc);
        for (String string : list) {
            for (Coords c : coordsList) {
                if (c.address.contains(string)) {
                    c.clubName = string;
                }

            }

        }
        return coordsList;
    }

    public  static Coords getCityCoordinates(Document doc) {
        //page.localResults.setMapCenter(-33.9581, 25.6192);
        Coords c = null;
        Elements scripts = doc.getElementsByTag("script");
        for (Element z : scripts) {
            for (DataNode node : z.dataNodes()) {
                String ss = node.getWholeData();

                String pattern1 = "setMapCenter(";
                String pattern2 = ")";
                Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                Matcher m = p.matcher(ss);
                if (m.find()) {
                    String s = m.group(1);
                    int i = s.indexOf(",");
                    String slat = s.substring(0, i);
                    String slng = s.substring(i + 1);
                    double xLat = Double.parseDouble(slat);
                    double xLng = Double.parseDouble(slng);
                    c = new Coords(0, xLat, xLng);
                }

            }
        }
        return c;
    }

    static List<Coords> coordsList = new ArrayList<Coords>();

    public static class Coords {

        public Coords(int key, double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
            this.key = key;
        }

        public Coords(String s) {
            //31284:{m:'O',n:14,lat:-26.233519,lng:28.152319
            int a = s.indexOf(":");
            String sKey = s.substring(0, a);
            int d = s.indexOf("lat:");
            String cc = s.substring(d + 4);
            int i = cc.indexOf(",");

            String slat = cc.substring(0, i);
            String slng = cc.substring(i + 5);
            double xLat = Double.parseDouble(slat);
            double xLng = Double.parseDouble(slng);
            lat = xLat;
            lng = xLng;
            key = Integer.parseInt(sKey);

        }

        double lat, lng;
        int key;
        String clubName, address;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getClubName() {
            return clubName;
        }

        public void setClubName(String clubName) {
            this.clubName = clubName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

}
