package com.boha.malengagolf.library;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.volley.toolbox.OkHttpStack;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import java.io.File;

/**
 * Created by aubreyM on 2014/05/17.
 */

/**
 * Created by aubreyM on 2014/05/17.
 */

@ReportsCrashes(
        formKey = "",
        formUri = Statics.CRASH_REPORTS_URL,
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.STACK_TRACE,
                ReportField.PACKAGE_NAME,
                ReportField.CUSTOM_DATA,
                ReportField.LOGCAT},
        socketTimeout = 3000
)
public class MGApp extends Application {

    public static Picasso picasso;
    static final long MAX_CACHE_SIZE = 1024 * 1024 * 1024;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "############################ onCreate MalengaGolf has started ---------------->");

        boolean isDebuggable = 0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
        if (!isDebuggable) {
            ACRA.init(this);
            GolfGroupDTO golfGroup = SharedUtil.getGolfGroup(getApplicationContext());
            AdministratorDTO mon = SharedUtil.getAdministrator(getApplicationContext());
            PlayerDTO player = SharedUtil.getPlayer(getApplicationContext());
            if (golfGroup != null) {
                ACRA.getErrorReporter().putCustomData("groupID", "" + golfGroup.getGolfGroupID());
            }
            if (mon != null) {
                ACRA.getErrorReporter().putCustomData("administrator", "" + mon.getAdministratorID());
            }
            Log.e(LOG, "###### ACRA Crash Reporting has been initiated");
        }
        // create Picasso.Builder object
        File picassoCacheDir = getCacheDir();

        Picasso.Builder picassoBuilder = new Picasso.Builder(getApplicationContext());
        picassoBuilder.downloader(new OkHttpDownloader(picassoCacheDir, MAX_CACHE_SIZE));
        picasso = picassoBuilder.build();
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            // Picasso instance was already set
        }

        if (isDebuggable) {
            Picasso.with(getApplicationContext())
                    .setIndicatorsEnabled(true);
            Picasso.with(getApplicationContext())
                    .setLoggingEnabled(true);
        }

        Log.w(LOG, "####### images in picasso cache: " + picassoCacheDir.listFiles().length);

        initializeVolley(getApplicationContext());


           }

    /**
     * Set up Volley Networking; create RequestQueue and ImageLoader
     *
     * @param context
     */
    public void initializeVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context, new OkHttpStack());

        Log.i(LOG, "********** Yebo! Volley Networking has been initialized");

    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    RequestQueue requestQueue;
    static final String LOG = "MGApplication";
}

