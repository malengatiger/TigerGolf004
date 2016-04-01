package com.boha.malengagolf.library.util;

/**
 * Created by aubreyM on 2014/05/11.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class GCMUtil {
    public interface  GCMUtilListener {
        public void onDeviceRegistered(String id);
        public void onGCMError();
    }
    static Context ctx;
    static GCMUtilListener gcmUtilListener;
    static String registrationID, msg;
    static final String LOG = "GCMUtil";
    static GoogleCloudMessaging gcm;

    public static void startGCMRegistration(Context context, GCMUtilListener listener) {
        ctx = context;
        gcmUtilListener = listener;
        new GCMTask().execute();
    }
    public static final String GCM_SENDER_ID = "327722367643";

    static class GCMTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e(LOG, "... startin GCM registration");
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(ctx);
                }
                registrationID = gcm.register(GCM_SENDER_ID);
                msg = "Device registered, registration ID = \n" + registrationID;
                storeRegistrationId(ctx, registrationID);
                RequestDTO w = new RequestDTO();
                w.setRequestType(RequestDTO.SEND_GCM_REGISTRATION);
                w.setGcmRegistrationID(registrationID);
                WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
                    @Override
                    public void onMessage(ResponseDTO response) {
                        if (response.getStatusCode() == 0) {
                            Log.w(LOG, "############ Device registered on server GCM regime");
                        }
                    }

                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onError(String message) {
                        Log.e(LOG, "############ Device failed to register on server GCM regime\n" + message);
                    }
                });
//                BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,w,ctx,new BaseVolley.BohaVolleyListener() {
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        if (response.getStatusCode() == 0) {
//                            Log.w(LOG, "############ Device registered on server GCM regime");
//                        }
//                    }
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        Log.e(LOG, "############ Device failed to register on server GCM regime");
//                    }
//                });
                Log.i(LOG, msg);

            } catch (IOException e) {
                return Constants.ERROR_SERVER_COMMS;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i(LOG, "onPostExecute... ending GCM registration");
            if (result > 0) {
                gcmUtilListener.onGCMError();
                ErrorUtil.handleErrors(ctx, result);
                return;
            }
            gcmUtilListener.onDeviceRegistered(registrationID);
            Log.i(LOG, "onPostExecute GCM device registered OK");
        }

    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int appVersion = getAppVersion(context);
        Log.i(LOG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GCM_REGISTRATION_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
        Log.e(LOG, "GCM regId saved in prefs! Yebo!!!");
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        ctx = context;
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String registrationId = prefs.getString(GCM_REGISTRATION_ID, null);
        if (registrationId == null) {
            Log.i(LOG, "GCM Registration ID not found on device.");
            return null;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG, "App version changed.");
            return null;
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static boolean checkPlayServices(Context ctx, Activity act) {
        Log.e(LOG, "checkPlayServices .................");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, act,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG, "This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }

    public static final String GCM_REGISTRATION_ID = "gcmRegID";
    public static final String APP_VERSION = "appVersion";
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = 11;

}
