package com.boha.ScraperApp.scraper;

import android.content.Context;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/02.
 */
public class CountryManager {

    public interface CountryListener {
        public void onCountriesFound(List<CountryDTO> countryList);
    }

    static Context ctx;
    static CountryListener listener;

    public static void loadCountries(Context context, final CountryListener countryListener) {
        listener = countryListener;
        ctx = context;
        buildCountryList();
        setList();
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.LOAD_COUNTRIES);
        w.setCountryList(dtoList);

        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                countryListener.onCountriesFound(response.getCountryList());
            }

            @Override
            public void onVolleyError(VolleyError error) {
                ToastUtil.errorToast(ctx, "Network Error");
            }
        });

    }

    static List<CountryDTO> dtoList = new ArrayList<CountryDTO>();

    private static void setList() {
        for (CountrySCR c : countryList) {
            CountryDTO d = new CountryDTO();
            d.setCountryCode(c.getCountryCode());
            d.setCountryName(c.getCountryName());
            dtoList.add(d);
        }
    }

    static List<CountrySCR> countryList;

    private static void buildCountryList() {

        countryList = new ArrayList<CountrySCR>();
        countryList.add(new CountrySCR("south-africa", "ZA"));
        countryList.add(new CountrySCR("united-states", "US"));
        countryList.add(new CountrySCR("canada", "CA"));
        countryList.add(new CountrySCR("united-kingdom", "GB"));
        // countryList.add(new CountrySCR("spain", "ES"));
        // countryList.add(new CountrySCR("france", "FR"));
        //countryList.add(new CountrySCR("portugal", "PT"));
        //countryList.add(new CountrySCR("germany", "DE"));
        //countryList.add(new CountrySCR("italy", "IT"));
        //countryList.add(new CountrySCR("czech-republic", "CZ"));

    }
}
