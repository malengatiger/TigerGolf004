package com.boha.golfpractice.golfer.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.boha.golfpractice.golfer.R;
import com.boha.golfpractice.golfer.dto.GolfCourseDTO;
import com.boha.golfpractice.golfer.dto.HoleDTO;
import com.boha.golfpractice.golfer.dto.HoleStatDTO;
import com.boha.golfpractice.golfer.dto.PracticeSessionDTO;
import com.boha.golfpractice.golfer.dto.RequestDTO;
import com.boha.golfpractice.golfer.dto.ResponseDTO;
import com.boha.golfpractice.golfer.fragments.GolfCourseListFragment;
import com.boha.golfpractice.golfer.util.MonLog;
import com.boha.golfpractice.golfer.util.OKHttpException;
import com.boha.golfpractice.golfer.util.OKUtil;
import com.boha.golfpractice.golfer.util.SharedUtil;
import com.boha.golfpractice.golfer.util.SnappyGolfCourse;
import com.boha.golfpractice.golfer.util.SnappyPractice;
import com.boha.golfpractice.golfer.util.Util;
import com.boha.golfpractice.golfer.util.WebCheck;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GolfCourseListActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GolfCourseListFragment.GolfCourseListListener {

    GolfCourseListFragment golfCourseListFragment;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    Context ctx;

    FloatingActionButton fab;
    FrameLayout frameLayout;
    static final String LOG = GolfCourseListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golf_course_list);
        ctx = getApplicationContext();
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
            }
        });
        snackbar = Snackbar.make(fab, "Getting device GPS location to search for golf courses", Snackbar.LENGTH_INDEFINITE);

        Util.setCustomActionBar(ctx, getSupportActionBar(), "GolfPractice", getString(R.string.record_prac),
                ContextCompat.getDrawable(ctx, R.drawable.golfball48));

        getCachedCourses();
        golfCourseList = new ArrayList<>();
        addFragment();

        boolean isFirstTime = SharedUtil.getFirstTime(ctx);
        if (isFirstTime) {
            SharedUtil.setFirstTime(ctx);

        }

    }

    private void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        golfCourseListFragment = GolfCourseListFragment.newInstance(golfCourseList);
        golfCourseListFragment.setListener(this);

        ft.add(R.id.frameLayout, golfCourseListFragment);
        ft.commit();
    }

    List<GolfCourseDTO> golfCourseList;

    private void getCachedCourses() {
        SnappyGolfCourse.getFavouriteGolfCourses((MonApp) getApplication(), new SnappyGolfCourse.DBReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                if (!response.getGolfCourseList().isEmpty()) {
                    golfCourseList = response.getGolfCourseList();
                    if (golfCourseListFragment != null) {
                        golfCourseListFragment.setGolfCourseList(response.getGolfCourseList());
                    } else {
                        addFragment();
                    }

                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void getCourses() {
        MonLog.w(ctx, LOG, "============== getCourses .............");
        RequestDTO w = new RequestDTO(RequestDTO.GET_GOLF_COURSES_BY_LOCATION);
        w.setLatitude(mCurrentLocation.getLatitude());
        w.setLongitude(mCurrentLocation.getLongitude());
        w.setRadius(radius);
        w.setZipResponse(true);

        OKUtil okUtil = new OKUtil();
        try {
            okUtil.sendGETRequest(ctx, w, this, new OKUtil.OKListener() {
                @Override
                public void onResponse(ResponseDTO response) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    MonLog.i(ctx, LOG, "Golf Courses found: " + response.getGolfCourseList().size());
                    golfCourseList = response.getGolfCourseList();
                    if (golfCourseListFragment != null) {
                        golfCourseListFragment.setGolfCourseList(golfCourseList);
                    } else {
                        addFragment();
                    }
                    SnappyGolfCourse.addFavoriteGolfCourses((MonApp) getApplication(), golfCourseList, null);
                }

                @Override
                public void onError(String message) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    if (golfCourseList == null || golfCourseList.isEmpty()) {
                        Util.showErrorToast(ctx, "No golf courses found. There may be a problem with the server");
                    }
                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        MonLog.d(ctx, LOG,
                "##******************* onStart - GoogleApiClient connecting ... ");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            MonLog.e(ctx, LOG, "### onStop - locationClient disconnecting ");
        }

    }

    Menu mMenu;
    Snackbar snackbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.golfcourse_list, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            setRefreshActionButtonState(true);

            snackbar.show();
            startLocationUpdates();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MonLog.e(ctx, LOG,
                "+++  GoogleApiClient onConnected() ...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG, "Manifest.permission.ACCESS_FINE_LOCATION, is a problemo, returning ...");
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        MonLog.d(ctx, LOG, "### startLocationUpdates ....");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            setRefreshActionButtonState(true);
            snackbar.show();

            MonLog.d(ctx, LOG, "## GoogleApiClient connected, requesting location updates ...");
        } else {
            MonLog.e(ctx, LOG, "------- GoogleApiClient is NOT connected, not sure where we are...");
            mGoogleApiClient.connect();

        }
    }


    protected void stopLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            MonLog.w(ctx, LOG, "### stopLocationUpdates ...removeLocationUpdates fired");
        } else {
            MonLog.e(ctx, LOG, "##################### stopLocationUpdates googleApiClient is NULL ...");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    static final float ACCURACY_THRESHOLD = 50;

    @Override
    public void onLocationChanged(Location location) {
        MonLog.d(ctx, LOG, "------ onLocationChanged " + location.getLatitude()
                + " " + location.getLongitude() + " " + location.getAccuracy());

        if (location.getAccuracy() <= ACCURACY_THRESHOLD) {
            mCurrentLocation = location;
            stopLocationUpdates();
            if (snackbar != null)
                snackbar.dismiss();
            if (directionsRequired) {
                directionsRequired = false;
                Util.startDirections(ctx,
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(),
                        golfCourse.getLatitude(),
                        golfCourse.getLongitude());

            } else {
                getCourses();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onGolfCourseClicked(final GolfCourseDTO course) {

        List<GolfCourseDTO> list = new ArrayList<>();
        list.add(course);
        SnappyGolfCourse.addFavoriteGolfCourses((MonApp) getApplication(), list, null);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Practice Session Confirmation")
                .setMessage("Do you want to start a Practice Session and record your greatness?\n\nThe session to be performed at: " + course.getGolfCourseName())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addSessionRequest(course);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    int radius;
    @Override
    public void onCourseSearchRequired(int radius) {
        this.radius = radius;
        startLocationUpdates();
    }

    @Override
    public void onStartSession(GolfCourseDTO course) {
        addSessionRequest(course);
    }

    @Override
    public void onGetSessions(GolfCourseDTO course) {
        Util.showToast(getApplicationContext(),"Under Construction");
    }

    boolean directionsRequired;
    GolfCourseDTO golfCourse;

    @Override
    public void onGetDirections(GolfCourseDTO course) {
        directionsRequired = true;
        golfCourse = course;
        snackbar = Snackbar.make(fab, "Confirming device location before starting directions", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        startLocationUpdates();
    }

    @Override
    public void setBusy(boolean busy) {
        setRefreshActionButtonState(busy);
    }

    private void addSessionRequest(GolfCourseDTO c) {
        practiceSession = new PracticeSessionDTO();
        practiceSession.setGolfCourseID(c.getGolfCourseID());
        practiceSession.setGolfCourseName(c.getGolfCourseName());
        practiceSession.setGolfCourse(c);
        practiceSession.setPlayerID(SharedUtil.getPlayer(ctx).getPlayerID());
        practiceSession.setSessionDate(new Date().getTime());
        for (HoleDTO hole : c.getHoleList()) {
            HoleStatDTO m = new HoleStatDTO();
            m.setHole(hole);
            practiceSession.getHoleStatList().add(m);
        }
        sendPracticeSession(practiceSession);


    }

    private void sendPracticeSession(PracticeSessionDTO s) {

        RequestDTO w = new RequestDTO(RequestDTO.ADD_PRACTICE_SESSION);
        w.setPracticeSession(s);
        w.setZipResponse(false);

        if (WebCheck.checkNetworkAvailability(getApplicationContext()).isNetworkUnavailable()) {
            cacheSession(practiceSession);
            onBackPressed();
            return;
        }
        OKUtil util = new OKUtil();
        setRefreshActionButtonState(true);
        try {
            util.sendPOSTRequest(this, w, this, new OKUtil.OKListener() {
                @Override
                public void onResponse(final ResponseDTO response) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    practiceSession = response.getPracticeSessionList().get(0);
                    cacheSession(practiceSession);
                    onBackPressed();
                }

                @Override
                public void onError(String message) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    cacheSession(practiceSession);
                    onBackPressed();

                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }

    private void cacheSession(PracticeSessionDTO practiceSession) {
        this.practiceSession = practiceSession;
        SnappyPractice.addCurrentPracticeSession((MonApp) getApplication(),
                practiceSession, new SnappyPractice.DBWriteListener() {
                    @Override
                    public void onDataWritten() {
                        onBackPressed();
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    PracticeSessionDTO practiceSession;

    @Override
    public void onBackPressed() {
        if (practiceSession != null) {
            Intent m = new Intent();
            m.putExtra("practiceSession", practiceSession);
            setResult(RESULT_OK, m);
        }
        finish();

    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
