package com.boha.ScraperApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.boha.ScraperApp.scraper.CityAdapter;
import com.boha.ScraperApp.scraper.CountryManager;
import com.boha.ScraperApp.scraper.ProvinceManager;
import com.boha.ScraperApp.scraper.ScrapeForCityCoordinatesUtil;
import com.boha.ScraperApp.scraper.ScrapeStateForCitiesUtil;
import com.boha.ScraperApp.scraper.ScrapeStateForClubsUtil;
import com.boha.malengagolf.library.base.CityDTO;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.util.LoaderRequestDTO;
import com.boha.malengagolf.library.util.LoaderResponseDTO;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScraperActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = getApplicationContext();
        setFields();


    }


    private void setList() {
        adapter = new CityAdapter(ctx, R.layout.city_item, selectedState.getCityList());
        listView.setAdapter(adapter);
    }

    CityAdapter adapter;

    private void setStateSpinner() {
        List<String> list = new ArrayList<String>();
        list.add("Please select State");
        for (ProvinceDTO c : selectedCountry.getProvinces()) {
            list.add(c.getProvinceName());
        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, list);
        a.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        stateSpinner.setAdapter(a);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedState = null;
                    return;
                }
                selectedState = selectedCountry.getProvinces().get(i - 1);
                stateIndex = (i - 1);
                try {
                    if (selectedState.getCityList() == null || selectedState.getCityList().isEmpty()) {
                        ScrapeStateForCitiesUtil.scrapeCities(ctx, selectedState, selectedCountry.getCountryName(), new ScrapeStateForCitiesUtil.StateScraperListener() {
                            @Override
                            public void onFinished() {
                                setList();
                                txtCount.setText("" + selectedState.getCityList().size());
                                getClubs();
                            }
                        });
                    } else {
                        setList();
                        txtCount.setText("" + selectedState.getCityList().size());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void getClubs() {
        try {
            ScrapeStateForClubsUtil.scrapeClubsInState(ctx,
                    selectedState, selectedCountry.getCountryName(),
                    new ScrapeStateForClubsUtil.CityScraperListener() {
                        @Override
                        public void onFinished() {
                            mIndex = 0;
                            getStateCities();
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int mIndex = 0;

    private void getStateCities() {
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.GET_STATE_CITIES);
        w.setProvinceID(selectedState.getProvinceID());
        setRefreshActionButtonState(true);
        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {
                setRefreshActionButtonState(false);
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                getCityCoordinates(response.getCityList());
            }

            @Override
            public void onVolleyError(VolleyError error) {
                setRefreshActionButtonState(false);
            }
        });
    }

    private void getCityCoordinates(final List<CityDTO> cityList) {
        try {
            ScrapeForCityCoordinatesUtil.scrapeCourses(ctx, cityList,
                    selectedCountry.getCountryName(),
                    new ScrapeForCityCoordinatesUtil.CityScraperListener() {
                        @Override
                        public void onFinished() {

                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCountries() {
        setRefreshActionButtonState(true);
        CountryManager.loadCountries(ctx, new CountryManager.CountryListener() {
            @Override
            public void onCountriesFound(List<CountryDTO> cList) {
                countryList = cList;
                setCountrySpinner(countryList);
                setRefreshActionButtonState(false);

            }
        });
    }

    private void setCountrySpinner(final List<CountryDTO> countryList) {
        List<String> list = new ArrayList<String>();
        list.add("Please select country");
        for (CountryDTO c : countryList) {
            list.add(c.getCountryCode() + " - " + c.getCountryName());
        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, list);
        a.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        countrySpinner.setAdapter(a);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedCountry = null;
                    return;
                }
                selectedCountry = countryList.get(i - 1);
                countryIndex = (i - 1);
                try {
                    if (selectedCountry.getProvinces() == null
                            || selectedCountry.getProvinces().isEmpty()) {
                        Log.e(LOG, "getting provinces ...");
                        ProvinceManager.loadProvinces(ctx, selectedCountry,
                                new ProvinceManager.ProvinceListener() {
                                    @Override
                                    public void onProvincesFound() {
                                        setStateSpinner();
                                    }
                                }
                        );

                    } else {
                        setStateSpinner();
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void setFields() {
        View x = findViewById(R.id.SA_totalLayout);

        btnLoad = (Button) findViewById(R.id.SA_btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadDialog();
            }
        });
        listView = (ListView) findViewById(R.id.SA_list);
        countrySpinner = (Spinner) findViewById(R.id.SA_spinnerCountry);
        stateSpinner = (Spinner) findViewById(R.id.SA_spinnerState);
        citySpinner = (Spinner) findViewById(R.id.SA_spinnerCity);
        txtCount = (TextView) findViewById(R.id.SA_txtCount);
        txtLabel = (TextView) findViewById(R.id.SA_txtLabel);
        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedState != null) {
                    startMap();
                }
            }
        });
        txtLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedState != null) {
                    startMap();
                }
            }
        });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedState != null) {
                    startMap();
                }
            }
        });
    }

    private void startMap() {
        Intent i = new Intent(ctx, GolfCourseMapActivity.class);
        i.putExtra("state", selectedState);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scraper, menu);
        mMenu = menu;
        getCountries();
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_add_country_states);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_add_country_states:

                return true;

            case R.id.menu_get_cities:

                return true;

            case R.id.menu_get_courses:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    List<CountryDTO> countryList;
    Menu mMenu;
    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", locale);
    private Button btnStart, btnEnd, btnSave;
    private TextView txtLabel, txtCount;
    private Spinner countrySpinner, stateSpinner, citySpinner;
    private ListView listView;
    private CountryDTO selectedCountry;
    private ProvinceDTO selectedState;
    Button btnLoad;
    int countryIndex = 0, stateIndex, cityIndex;
    static final String LOG = "ScraperActivity";


    private void showLoadDialog() {
        AlertDialog.Builder diag = new AlertDialog.Builder(this);
        diag.setTitle("Country Loader")
                .setIcon(ctx.getResources().getDrawable(R.drawable.clipboard32))
                .setMessage("Do you really, absolutely have to load " + selectedCountry.getCountryName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO do something here?
                    }
                })
                .setNegativeButton("Nyet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }


}
