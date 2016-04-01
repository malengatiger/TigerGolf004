package com.boha.ScraperApp.scraper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/29.
 */
public class ScrapeString {

    List<String> stringList = new ArrayList<String>();
    String country, state, city, course;

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
