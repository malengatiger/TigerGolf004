package com.boha.malengagolf.scorer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.library.data.GcmDeviceDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.GCMUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.boha.malengagolf.library.R.layout.sign_in_scorer_player_admin);
        ctx = getApplicationContext();
        type = SCORER;
        checkVirgin();
        setFields();
        getEmail();
    }

    private void checkVirgin() {
        ScorerDTO dto = SharedUtil.getScorer(ctx);
        if (dto != null) {
            Log.e(LOG, "Not a virgin anymore ...checking GCM registration....");
            //check that this device is registered for GCM
            String id = GCMUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                updateGCMDevice = true;
                registerGCMDevice();
            }

            Intent intent = new Intent(ctx, MainPagerActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        registerGCMDevice();

    }

    boolean updateGCMDevice;

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
                    if (updateGCMDevice) {
                        ScorerDTO s = SharedUtil.getScorer(ctx);
                        email = s.getEmail();
                        ePin.setText(s.getEmail());
                        sendSignIn();
                    }

                }

                @Override
                public void onGCMError() {
                    Log.e(LOG, "############# onGCMError --- we got GCM problems");
                }
            });
        }
    }

    private void sendSignIn() {

        if (email == null) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(com.boha.malengagolf.library.R.string.select_email));
            return;
        }
        if (ePin.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(com.boha.malengagolf.library.R.string.enter_pin));
            return;
        }


        RequestDTO r = new RequestDTO();
        switch (type) {
            case ADMIN:
                r.setRequestType(RequestDTO.ADMIN_LOGIN);
                break;
            case SCORER:
                r.setRequestType(RequestDTO.SIGN_IN_SCORER);
                break;
            case PLAYER:
                r.setRequestType(RequestDTO.SIGN_IN_PLAYER);
                break;
            case LEADERBOARD:
                r.setRequestType(RequestDTO.SIGN_IN_LEADERBOARD);
                break;
        }

        r.setEmail(email);
        r.setPin(ePin.getText().toString());
        r.setGcmDevice(gcmDevice);

        setRefreshActionButtonState(true);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(getApplicationContext(),Statics.ADMIN_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG, "########### SIGN-IN RESPONSE coming in");
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        if (response.getGolfGroup() == null) {
                            Log.e(LOG, "GolfGroup is null, ignoring .... " + response.getMessage());
                            return;
                        }

                        SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
                        switch (type) {
                            case ADMIN:
                                SharedUtil.saveAdministration(ctx, response.getAdministrator());
                                break;
                            case SCORER:
                                SharedUtil.saveScorer(ctx, response.getScorers().get(0));
                                break;
                            case PLAYER:
                                SharedUtil.savePlayer(ctx, response.getPlayers().get(0));
                                break;
                            case LEADERBOARD:
                                SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
                                break;
                        }

                        Log.i(LOG, "Shared preferences saved. GolfGroup & Person");
                        Intent intent = new Intent(ctx, MainPagerActivity.class);
                        intent.putExtra("response", response);
                        startActivity(intent);

                        if (response.getTournaments() != null && !response.getTournaments().isEmpty())
                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {

                                }

                                @Override
                                public void onDataCached() {

                                }
                            });

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
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(com.boha.malengagolf.library.R.string.error_server_comms));
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, 360, new BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
//                switch (type) {
//                    case ADMIN:
//                        SharedUtil.saveAdministration(ctx, response.getAdministrator());
//                        break;
//                    case SCORER:
//                        SharedUtil.saveScorer(ctx, response.getScorers().get(0));
//                        break;
//                    case PLAYER:
//                        SharedUtil.savePlayer(ctx, response.getPlayers().get(0));
//                        break;
//                    case LEADERBOARD:
//                        SharedUtil.saveGolfGroup(ctx, response.getGolfGroup());
//                        break;
//                }
//
//                Log.i(LOG, "Shared preferences saved. GolfGroup & Person");
//                Intent intent = new Intent(ctx, MainPagerActivity.class);
//                intent.putExtra("response", response);
//                startActivity(intent);
//
//                if (response.getTournaments() != null && !response.getTournaments().isEmpty())
//                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
//                        @Override
//                        public void onFileDataDeserialized(ResponseDTO response) {
//
//                        }
//
//                        @Override
//                        public void onDataCached() {
//
//                        }
//                    });
//
//                finish();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(com.boha.malengagolf.library.R.string.error_server_comms));
//            }
//        });

    }

    GcmDeviceDTO gcmDevice;

    private void setFields() {

        ePin = (EditText) findViewById(com.boha.malengagolf.library.R.id.SI_pin);
        spinnerEmail = (Spinner) findViewById(com.boha.malengagolf.library.R.id.SI_spinner);
        txtApp = (TextView) findViewById(R.id.SI_app);
        txtApp.setText(ctx.getResources().getString(R.string.scorer_app));
        btnSave = (Button) findViewById(com.boha.malengagolf.library.R.id.SI_btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSignIn();

            }
        });

    }

    @Override
    public void onPause() {
        overridePendingTransition(com.boha.malengagolf.library.R.anim.slide_in_left, com.boha.malengagolf.library.R.anim.slide_out_right);
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
        getMenuInflater().inflate(com.boha.malengagolf.library.R.menu.scorer_registration, menu);
        mMenu = menu;

        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(com.boha.malengagolf.library.R.id.menu_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(com.boha.malengagolf.library.R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equalsIgnoreCase(ctx.getResources().getString(com.boha.malengagolf.library.R.string.refresh))) {
            ToastUtil.toast(ctx, "Under Construction");
            return true;
        }
        if (item.getTitle().toString().equalsIgnoreCase(ctx.getResources().getString(com.boha.malengagolf.library.R.string.help_me))) {
            ToastUtil.toast(ctx, "Under Construction");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG, "onConnection failed: " + connectionResult.toString());
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            //TODO - send user to create acct
            ToastUtil.errorToast(ctx, "No Google account found. Please create one and try again");
            finish();
            return;
        }
        if (accts.length == 1) {
            email = accts[0].name;
            spinnerEmail.setVisibility(View.GONE);
            return;
        }
        final ArrayList<String> tarList = new ArrayList<String>();
        if (accts != null) {
            tarList.add(ctx.getResources().getString(R.string.select_acct));
            for (int i = 0; i < accts.length; i++) {
                tarList.add(accts[i].name);
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    com.boha.malengagolf.library.R.layout.xsimple_spinner_item, tarList);
            dataAdapter
                    .setDropDownViewResource(com.boha.malengagolf.library.R.layout.xsimple_spinner_dropdown_item);
            spinnerEmail.setAdapter(dataAdapter);
            spinnerEmail
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int index, long arg3) {
                            if (index ==  0) {
                                email = null;
                                return;
                            }
                            email = tarList.get(index);
                            Log.d("Reg", "###### Email account selected is "
                                    + email);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
        }
    }

    Button btnSave;
    EditText ePin;
    TextView txtApp;
    Spinner spinnerEmail;
    static final String LOG = "SignInActivity";
    int type;
    String email;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "### ---> PlayServices onConnected() - gotta go! >>");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static final int ADMIN = 1, SCORER = 2, PLAYER = 3, LEADERBOARD = 4;
}
