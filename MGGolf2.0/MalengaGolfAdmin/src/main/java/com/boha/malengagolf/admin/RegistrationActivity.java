package com.boha.malengagolf.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.data.GcmDeviceDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.GCMUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static com.boha.malengagolf.library.volley.toolbox.BaseVolley.checkNetworkOnDevice;

public class RegistrationActivity extends AppCompatActivity implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.boha.malengagolf.admin.R.layout.register);
        ctx = getApplicationContext();
        checkVirgin();
        setFields();
    }

    private void checkVirgin() {
        
        GolfGroupDTO dto = SharedUtil.getGolfGroup(ctx);
        if (dto != null) {
            Log.i(LOG, "++++++++ Not a virgin anymore ...checking GCM registration....");
            String id = GCMUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                registerGCMDevice();
            }

            Intent intent = new Intent(ctx, MainPagerActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getCountries();

    }

    private void registerGCMDevice() {
        boolean ok = GCMUtil.checkPlayServices(getApplicationContext(), this);
        if (ok) {
            Log.e(LOG, "############# Starting Google Cloud Messaging registration");
            GCMUtil.startGCMRegistration(getApplicationContext(), new GCMUtil.GCMUtilListener() {
                @Override
                public void onDeviceRegistered(String id) {
                    Log.e(LOG, "############# GCM - we cool, cool.....: " + id);
                    gcmDevice = new GcmDeviceDTO();
                    gcmDevice.setManufacturer(Build.MANUFACTURER);
                    gcmDevice.setModel(Build.MODEL);
                    gcmDevice.setSerial(Build.SERIAL);
                    gcmDevice.setProduct(Build.PRODUCT);
                    gcmDevice.setAndroidVersion(Build.VERSION.RELEASE);
                    gcmDevice.setGcmRegistrationID(id);

                }

                @Override
                public void onGCMError() {
                    Log.e(LOG, "############# onGCMError --- we got GCM problems");

                }
            });
        }
    }
    private void sendRegistration() {

        if (eGroup.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_group_name));
            return;
        }
        if (eFirstName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_firstname));
            return;
        }
        if (eLastName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_lastname));
            return;
        }

        if (ePin.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_password));
            return;
        }
        if (country == null) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_country));
            return;
        }
        if (eMail.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_email));
            return;
        }
        AdministratorDTO a = new AdministratorDTO();
        if (!eCell.getText().toString().isEmpty())
        a.setCellphone(eCell.getText().toString());
        a.setEmail(emailString);
        a.setFirstName(eFirstName.getText().toString());
        a.setLastName(eLastName.getText().toString());
        a.setPin(ePin.getText().toString());
        a.setGcmDevice(gcmDevice);

        GolfGroupDTO g = new GolfGroupDTO();
        g.setCellphone(eCell.getText().toString());
        g.setEmail(emailString);
        g.setGolfGroupName(eGroup.getText().toString());
        g.setCountryID(country.getCountryID());

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.ADD_GOLF_GROUP);
        r.setAdministrator(a);
        r.setGolfGroup(g);

        setRefreshActionButtonState(true);

        BaseVolley.checkNetworkOnDevice(ctx);
        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.wait), 10, Gravity.CENTER);

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        if (response.getGolfGroup() == null) {
                            Log.e(LOG, "GolfGroup is null, ignoring .... " + response.getMessage());
                            return;
                        }
                        if (response.getAdministrator() == null) {
                            Log.e(LOG, "Admin is null, ignoring .... " + response.getMessage());
                            return;
                        }
                        SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
                        SharedUtil.saveAdministration(ctx, response.getAdministrator());
                        Log.i(LOG, "@@@@@@@@@@ Shared preferences saved. GolfGroup & Admin");
                        Intent intent = new Intent(ctx, MainPagerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));

                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, 360, new BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                setRefreshActionButtonState(false);
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
//                SharedUtil.saveAdministration(ctx, response.getAdministrator());
//                Log.i(LOG, "Shared preferences saved. GolfGroup & Admin");
//                Intent intent = new Intent(ctx, MainPagerActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });

    }

    private void sendSignIn() {

        if (ePin.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_password));
            return;
        }
        if (eMail.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_email));
            return;
        }
        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.ADMIN_LOGIN);
        r.setEmail(eMail.getText().toString());
        r.setPin(ePin.getText().toString());
        r.setGcmDevice(gcmDevice);

        setRefreshActionButtonState(true);
        BaseVolley.checkNetworkOnDevice(ctx);

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        if (response.getGolfGroup() == null) {
                            Log.e(LOG, "GolfGroup is null, ignoring .... " + response.getMessage());
                            return;
                        }
                        if (response.getAdministrator() == null) {
                            Log.e(LOG, "Admin is null, ignoring .... " + response.getMessage());
                            return;
                        }
                        SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
                        SharedUtil.saveAdministration(ctx, response.getAdministrator());
                        Log.i(LOG, "**************Shared preferences saved. GolfGroup & Admin");
                        Intent intent = new Intent(ctx, MainPagerActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
                    }
                });
            }
        });

//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, new BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                setRefreshActionButtonState(false);
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
//                SharedUtil.saveAdministration(ctx, response.getAdministrator());
//                Log.i(LOG, "Shared preferences saved. GolfGroup & Admin");
//                Intent intent = new Intent(ctx, MainPagerActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });

    }

    List<CountryDTO> countryList;
    CountryDTO country;
    boolean isRegistration;
    GcmDeviceDTO gcmDevice;


    private void getCountries() {
        RequestDTO request = new RequestDTO();
        request.setRequestType(RequestDTO.GET_COUNTRIES);
        request.setZippedResponse(true);

        if (!checkNetworkOnDevice(ctx)) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(
                            com.boha.malengagolf.library.R.string.error_network_unavailable
                    )
            );
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOG, "..............response received, should have country data ....");
                        setRefreshActionButtonState(false);
                        if (response.getStatusCode() > 0) {
                            Log.e(LOG, "Error: " + response.getMessage());
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        countryList = response.getCountries();
                        setSpinnerCountry();
                        Log.i(LOG, "Countries received! Get your reward!!!! - " + countryList.size());
                        registerGCMDevice();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//        getRemoteData(Statics.SERVLET_ADMIN, request, ctx,
//                new BohaVolleyListener() {
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        Log.i(LOG, "..............response received, should have country data ....");
//                        setRefreshActionButtonState(false);
//                        if (response.getStatusCode() > 0) {
//                            Log.e(LOG, "Error: " + response.getMessage());
//                            ToastUtil.errorToast(ctx, response.getMessage());
//                            return;
//                        }
//                        countryList = response.getCountries();
//                        setSpinnerCountry();
//                        Log.i(LOG, "Countries received! Get your reward!!!! - " + countryList.size());
//                        registerGCMDevice();
//
//                    }
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        setRefreshActionButtonState(false);
//                        ErrorUtil.showServerCommsError(ctx);
//                    }
//                }
//        );
    }

    private void setFields() {
        spinnerCountry = (Spinner) findViewById(R.id.EP_countrySpinner);
        eFirstName = (EditText) findViewById(R.id.EP_firstName);
        eMail = (EditText) findViewById(R.id.EP_editEmail);
        eLastName = (EditText) findViewById(R.id.EP_lastName);
        eCell = (EditText) findViewById(R.id.EP_cellphone);
        ePin = (EditText) findViewById(R.id.EP_password);
        eGroup = (EditText) findViewById(R.id.EP_groupName);
        mainEPLayout = findViewById(R.id.REG_ediLayout);
        mainRegLayout = findViewById(R.id.REG_mainLayout);
        mainEPLayout.setVisibility(View.GONE);
        final TextView txtHdr = (TextView) findViewById(R.id.EP_header);
        btnStartSignIn = (Button) findViewById(R.id.REG_btnExisting);
        btnStartNewGroup = (Button) findViewById(R.id.REG_btnNewGroup);
        btnStartNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRegLayout.setVisibility(View.GONE);
                mainEPLayout.setVisibility(View.VISIBLE);
                isRegistration = true;
                eCell.setVisibility(View.VISIBLE);
                btnSave.setText(ctx.getResources().getString(R.string.register));
                txtHdr.setText(ctx.getResources().getString(R.string.group_reg));
            }
        });
        btnStartSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRegLayout.setVisibility(View.GONE);
                mainEPLayout.setVisibility(View.VISIBLE);
                eGroup.setVisibility(View.GONE);
                eFirstName.setVisibility(View.GONE);
                eCell.setVisibility(View.GONE);
                eLastName.setVisibility(View.GONE);
                spinnerCountry.setVisibility(View.GONE);
                isRegistration = false;
                btnSave.setText(ctx.getResources().getString(R.string.sign_in));
                txtHdr.setText(ctx.getResources().getString(R.string.group_signin));
            }
        });

        btnSave = (Button) findViewById(R.id.EP_btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegistration) {
                    sendRegistration();
                } else {
                    sendSignIn();
                }
            }
        });
        Button btnCan = (Button) findViewById(R.id.EP_btnCancel);
        btnCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRegLayout.setVisibility(View.VISIBLE);
                mainEPLayout.setVisibility(View.GONE);
                eGroup.setVisibility(View.VISIBLE);
                eFirstName.setVisibility(View.VISIBLE);
                eLastName.setVisibility(View.VISIBLE);
                spinnerCountry.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setSpinnerCountry() {
        Log.i(LOG, "setting country spinner .....");
        List<String> strings = new ArrayList<String>();
        //TODO - get country code from device ---
        strings.add("Please select country");
        if (countryList == null) return;
        for (CountryDTO country : countryList) {
            strings.add(country.getCountryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, strings);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    country = null;
                    return;
                }
                country = countryList.get(i - 1);
                ResponseDTO w = new ResponseDTO();
                w.setCountry(country);
                CacheUtil.cacheData(ctx, w, CacheUtil.CACHE_COUNTRY, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached() {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG, "onResume ...nothing to be done");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        super.onSaveInstanceState(b);
    }

    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registration_menu, menu);
        mMenu = menu;


    return true;
}

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_refresh);
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

            case R.id.menu_help:
                ToastUtil.toast(ctx, "Under Construction");
                return true;
            case R.id.menu_refresh:
                getCountries();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG, "onConnection failed: " + connectionResult.toString());
    }



    @Override
    public void onStop() {

        super.onStop();
    }

    View mainRegLayout, mainEPLayout;
    Button btnStartSignIn, btnStartNewGroup, btnSave;
    EditText eFirstName, eLastName, eCell, ePin, eGroup,eMail;
    Spinner spinnerCountry;
    static final String LOG = "RegistrationActivity";

    String emailString;
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "### ---> PlayServices onConnected() - gotta go! >>");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
