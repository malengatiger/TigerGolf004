package com.boha.malengagolf.admin.leaderboard;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.*;
import com.boha.malengagolf.library.util.*;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import static com.boha.malengagolf.library.volley.toolbox.BaseVolley.BohaVolleyListener;

public class SignInActivity extends FragmentActivity implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_leaderboard);
        ctx = getApplicationContext();
        type = APP_USER;
        checkVirgin();
        setFields();
        getEmail();
    }

    private void checkVirgin() {
        AppUserDTO dto = SharedUtil.getAppUser(ctx);
        if (dto != null) {
            Log.e(LOG, "Not a virgin anymore ...checking GCM registration....");
            //check that this device is registered for GCM
            String id = GCMUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                updateGCMDevice = true;
                registerGCMDevice();
            }

            Intent intent = new Intent(ctx, MainPanelActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        registerGCMDevice();

    }

    private boolean updateGCMDevice;

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
                    gcmDevice.setGcmRegistrationID(id);
                    if (updateGCMDevice) {
                        PlayerDTO s = SharedUtil.getPlayer(ctx);
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

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.SIGNIN_APP_USER);
        r.setEmail(email);
        r.setGcmDevice(gcmDevice);

        setRefreshActionButtonState(true);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, 360, new BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                setRefreshActionButtonState(false);
                Log.e(LOG, "response status = " + response.getStatusCode());
                if (!ErrorUtil.checkServerError(ctx, response)) {
                    return;
                }

                SharedUtil.saveAppUser(ctx, response.getAppUser());
                Log.i(LOG, "## AppUser Shared preferences saved.");
                Intent intent = new Intent(ctx, MainPanelActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);

                finish();
                if (gcmDevice == null) {

                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                setRefreshActionButtonState(false);
                ToastUtil.errorToast(ctx, ctx.getResources().getString(com.boha.malengagolf.library.R.string.error_server_comms));
            }
        });

    }

    GcmDeviceDTO gcmDevice;

    private void setFields() {

        spinnerEmail = (Spinner) findViewById(R.id.LBSIGN_spinner);
        btnSave = (Button) findViewById(R.id.LBSIGN_btnSave);
        txtApp = (TextView) findViewById(R.id.LBSIGN_app);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSignIn();

            }
        });

        txtApp.setText(ctx.getResources().getString(R.string.leaderbd_app));

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

        if (item.getItemId() == R.id.menu_refresh) {
            ToastUtil.toast(ctx, "Under Construction");
            return true;
        }
        if (item.getItemId() == R.id.menu_help) {
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
        Account[] accts = am
                .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
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
    TextView txtApp;
    EditText ePin;
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

    public static final int ADMIN = 1, SCORER = 2, PLAYER = 3, APP_USER = 4;
}
