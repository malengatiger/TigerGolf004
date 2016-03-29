package com.boha.golfpractice.golfer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.golfpractice.golfer.R;
import com.boha.golfpractice.golfer.dto.GolfCourseDTO;
import com.boha.golfpractice.golfer.dto.HoleDTO;
import com.boha.golfpractice.golfer.dto.HoleStatDTO;
import com.boha.golfpractice.golfer.dto.PracticeSessionDTO;
import com.boha.golfpractice.golfer.dto.RequestDTO;
import com.boha.golfpractice.golfer.dto.ResponseDTO;
import com.boha.golfpractice.golfer.fragments.GolfCourseListFragment;
import com.boha.golfpractice.golfer.fragments.PageFragment;
import com.boha.golfpractice.golfer.fragments.SessionListFragment;
import com.boha.golfpractice.golfer.fragments.SessionSummaryFragment;
import com.boha.golfpractice.golfer.util.DepthPageTransformer;
import com.boha.golfpractice.golfer.util.MonLog;
import com.boha.golfpractice.golfer.util.OKHttpException;
import com.boha.golfpractice.golfer.util.OKUtil;
import com.boha.golfpractice.golfer.util.SharedUtil;
import com.boha.golfpractice.golfer.util.SnappyGeneral;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerMainActivity extends AppCompatActivity
        implements SessionListFragment.SessionListListener,
        GolfCourseListFragment.GolfCourseListListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private CharSequence mTitle;

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    TextView navText;
    ImageView navImage;
    CircleImageView circleImage;
    Context ctx;
    ViewPager mPager;
    StaffPagerAdapter adapter;
    int currentPageIndex;
    ImageView hero;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        ctx = getApplicationContext();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mPager = (ViewPager) findViewById(R.id.viewpager);
        hero = (ImageView) findViewById(R.id.image);

        Util.setCustomActionBar(ctx, getSupportActionBar(), "TGolf", getString(R.string.record_prac),
                ContextCompat.getDrawable(ctx, R.drawable.golfball48));
        snackbar = Snackbar.make(mPager, "Refreshing data from cloud server, hang on a second ...", Snackbar.LENGTH_INDEFINITE);

        getCachedPractices();
    }

    @Override
    public void onResume() {
        super.onResume();
        hero.setImageDrawable(Util.getRandomBackgroundImage(getApplicationContext()));
    }
    static List<PageFragment> pageFragmentList;
    SessionListFragment sessionListFragment;
    SessionSummaryFragment sessionSummaryFragment;
    GolfCourseListFragment golfCourseListFragment;
    List<PracticeSessionDTO> practiceSessionList = new ArrayList<>();
    List<GolfCourseDTO> golfCourseList = new ArrayList<>();

    private void buildPages() {
        MonLog.w(ctx,"PlayerMainActivity","########### ----------------buildPages");
        pageFragmentList = new ArrayList<>();
        sessionListFragment = SessionListFragment.newInstance(practiceSessionList);
        sessionListFragment.setApp((MonApp) getApplication());

        sessionSummaryFragment = new SessionSummaryFragment();
        sessionSummaryFragment.setApp((MonApp) getApplication());
        sessionSummaryFragment.setPlayer(SharedUtil.getPlayer(ctx));
        sessionSummaryFragment.setType(SessionSummaryFragment.FROM_CACHE);

        golfCourseListFragment = GolfCourseListFragment.newInstance(golfCourseList);
        golfCourseListFragment.setListener(this);

        pageFragmentList.add(sessionListFragment);
        pageFragmentList.add(sessionSummaryFragment);
        pageFragmentList.add(golfCourseListFragment);

        adapter = new StaffPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());

        mPager.setCurrentItem(currentPageIndex, true);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex = position;
                //pageFragmentList.get(position).animateHeroHeight();
                PageFragment pf = pageFragmentList.get(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    Menu mMenu;
    Snackbar snackbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            setRefreshActionButtonState(true);
            snackbar.show();
            getPracticeSessions();
            return true;
        }
        if (id == R.id.action_add) {
            onNewSessionRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static final int PRACTICE_SESSION_START = 123;
    static final float ACCURACY_THRESHOLD = 50;

    @Override
    public void onLocationChanged(Location location) {
        MonLog.d(getApplicationContext(), LOG, "------ onLocationChanged " + location.getLatitude()
                + " " + location.getLongitude() + " " + location.getAccuracy());

        if (location.getAccuracy() <= ACCURACY_THRESHOLD) {
            mCurrentLocation = location;
            stopLocationUpdates();
            if (snackbar != null)
                snackbar.dismiss();
            if (directionsRequired) {
                directionsRequired = false;
                Util.startDirections(getApplicationContext(),
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(),
                        golfCourse.getLatitude(),
                        golfCourse.getLongitude());

            } else {
                getCourses();
            }
        }
    }

    int radius = Util.GOLFCOURSE_SEARCH_RADIUS;
    private void getCourses() {
        MonLog.w(getApplicationContext(), LOG, "============== getCourses .............");
        RequestDTO w = new RequestDTO(RequestDTO.GET_GOLF_COURSES_BY_LOCATION);
        w.setLatitude(mCurrentLocation.getLatitude());
        w.setLongitude(mCurrentLocation.getLongitude());
        w.setRadius(radius);
        w.setZipResponse(true);
        setRefreshActionButtonState(true);
        snackbar.show();
        OKUtil okUtil = new OKUtil();
        try {
            okUtil.sendGETRequest(getApplicationContext(), w, this, new OKUtil.OKListener() {
                @Override
                public void onResponse(ResponseDTO response) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    MonLog.i(getApplicationContext(), LOG, "Golf Courses found: " + response.getGolfCourseList().size());
                    golfCourseList = response.getGolfCourseList();
                    if (golfCourseListFragment == null) {
                        buildPages();
                    } else {
                        golfCourseListFragment.setGolfCourseList(golfCourseList);
                    }

                    SnappyGolfCourse.addFavoriteGolfCourses((MonApp) getApplication(), golfCourseList, null);
                }

                @Override
                public void onError(String message) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    if (golfCourseList == null || golfCourseList.isEmpty()) {
                        Util.showErrorToast(getApplicationContext(), "No golf courses found. There may be a problem with the server");
                    }
                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSessionClicked(PracticeSessionDTO session) {
        if (session.getClosed() == Boolean.FALSE) {
            Intent m = new Intent(ctx, SessionControllerActivity.class);
            m.putExtra("session", session);
            startActivityForResult(m, PRACTICE_SESSION_START);
        } else {
            Intent m = new Intent(ctx, HoleStatViewerActivity.class);
            m.putExtra("session", session);
            startActivity(m);
        }

    }

    static final int NEW_SESSION_REQUESTED = 657;

    @Override
    public void onNewSessionRequested() {
//        Intent m = new Intent(ctx, GolfCourseListActivity.class);
//        startActivityForResult(m, NEW_SESSION_REQUESTED);
        mPager.setCurrentItem(2);
        Util.showToast(getApplicationContext(),"Select the Golf Course action to start the process");
    }


    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case NEW_SESSION_REQUESTED:
                if (resCode == RESULT_OK) {
                    practiceSession = (PracticeSessionDTO) data.getSerializableExtra("practiceSession");
                    sessionListFragment.addPracticeSession(practiceSession);
                }
        }
    }

    @Override
    public void onGolfCourseClicked(GolfCourseDTO course) {
        MonLog.d(getApplicationContext(), LOG, "####### onGolfCourseClicked");
    }

    @Override
    public void onCourseSearchRequired(int radius) {
        this.radius = radius;
        setBusy(true);
        startLocationUpdates();
    }

    PracticeSessionDTO practiceSession;

    @Override
    public void onStartSession(GolfCourseDTO c) {
        MonLog.d(getApplicationContext(), LOG, "++++++ onStartSession: "
                + c.getGolfCourseName());
        practiceSession = new PracticeSessionDTO();
        practiceSession.setGolfCourseID(c.getGolfCourseID());
        practiceSession.setGolfCourseName(c.getGolfCourseName());
        practiceSession.setGolfCourse(c);
        practiceSession.setPlayerID(SharedUtil.getPlayer(getApplicationContext()).getPlayerID());
        practiceSession.setSessionDate(new Date().getTime());
        for (HoleDTO hole : c.getHoleList()) {
            HoleStatDTO m = new HoleStatDTO();
            m.setHole(hole);
            practiceSession.getHoleStatList().add(m);
        }
        sendPracticeSession(practiceSession);
    }
    private void sendPracticeSession(PracticeSessionDTO s) {

        snackbar = Snackbar.make(mPager, "Sending data for Practice Session, hang on a sec", Snackbar.LENGTH_INDEFINITE);

        RequestDTO w = new RequestDTO(RequestDTO.ADD_PRACTICE_SESSION);
        w.setPracticeSession(s);
        w.setZipResponse(false);

        if (WebCheck.checkNetworkAvailability(getApplicationContext()).isNetworkUnavailable()) {
            cacheSession(practiceSession);
            startHoleStat();
            return;
        }
        snackbar.show();
        setRefreshActionButtonState(true);
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
                    startHoleStat();
                }

                @Override
                public void onError(String message) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    cacheSession(practiceSession);
                    startHoleStat();

                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }

    static final int EDIT_HOLE_STATS_REQUIRED = 1765;

    private void startHoleStat() {
        Intent m = new Intent(getApplicationContext(), SessionControllerActivity.class);
        m.putExtra("session", practiceSession);
        startActivityForResult(m, EDIT_HOLE_STATS_REQUIRED);
        if (sessionListFragment != null) {
            sessionListFragment.addPracticeSession(practiceSession);
            mPager.setCurrentItem(0,true);
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

    @Override
    public void onGetSessions(GolfCourseDTO course) {
        Util.showToast(getApplicationContext(), "Under Construction");
    }

    boolean directionsRequired;
    GolfCourseDTO golfCourse;

    @Override
    public void onGetDirections(GolfCourseDTO course) {
        directionsRequired = true;
        golfCourse = course;
        snackbar = Snackbar.make(mPager, "Confirming device location before starting directions", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        startLocationUpdates();
    }

    @Override
    public void setBusy(boolean busy) {
        setRefreshActionButtonState(true);
    }

    private static class StaffPagerAdapter extends FragmentStatePagerAdapter {

        public StaffPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {

            return (android.support.v4.app.Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            PageFragment pf = pageFragmentList.get(position);
            return pf.getPageTitle();
        }
    }

    private void setMenuDestinations() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_session) {
                    mPager.setCurrentItem(0, true);
                    return true;
                }

                if (menuItem.getItemId() == R.id.nav_profile) {
                    mPager.setCurrentItem(3, true);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_messaging) {
                    mPager.setCurrentItem(1, true);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_coaches) {
                    mPager.setCurrentItem(2, true);
                    return true;
                }


                return false;
            }
        });
    }

    private void getCachedPractices() {
        SnappyPractice.getPracticeSessions((MonApp) getApplication(), new SnappyPractice.DBReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {

                if (!response.getPracticeSessionList().isEmpty()) {
                    practiceSessionList = response.getPracticeSessionList();
                    snackbar.dismiss();
                    Log.e(LOG,"SnappyPractice.getPracticeSessions onDataRead ........starting buildPages.");
                    buildPages();
                }

                getCachedCourses();
                getPracticeSessions();
            }

            @Override
            public void onError(String message) {

            }
        });
    }


    private void getCachedCourses() {
        SnappyGolfCourse.getFavouriteGolfCourses((MonApp) getApplication(), new SnappyGolfCourse.DBReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                golfCourseList = response.getGolfCourseList();
                buildPages();
            }

            @Override
            public void onError(String message) {

            }
        });
    }
    private void getPracticeSessions() {

        RequestDTO w = new RequestDTO(RequestDTO.GET_PLAYER_DATA);
        w.setPlayerID(SharedUtil.getPlayer(getApplicationContext()).getPlayerID());
        w.setZipResponse(true);
        OKUtil okUtil = new OKUtil();
        try {
            okUtil.sendGETRequest(ctx, w, this, new OKUtil.OKListener() {
                @Override
                public void onResponse(final ResponseDTO response) {
                    setRefreshActionButtonState(false);
                    snackbar.dismiss();
                    practiceSessionList = response.getPracticeSessionList();
                    if (sessionListFragment == null) {
                        buildPages();
                    } else {
                        sessionListFragment.setPracticeSessionList(practiceSessionList);
                    }

                    SnappyPractice.addPracticeSessions((MonApp) getApplication(), response.getPracticeSessionList(), new SnappyPractice.DBWriteListener() {
                        @Override
                        public void onDataWritten() {
                            SnappyGeneral.addClubs((MonApp) getApplication(), response.getClubList(), null);
                            SnappyGeneral.addShotShapes((MonApp) getApplication(), response.getShotShapeList(), null);
                        }

                        @Override
                        public void onError(String message) {

                        }
                    });

                }

                @Override
                public void onError(String message) {
                    setRefreshActionButtonState(false);
                    Util.showErrorToast(ctx, message);
                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
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
    @Override
    public void onBackPressed() {
        MonLog.w(getApplicationContext(),"PlayerMainActivity",
                "---------- onBackPressed");

    }
    public void onStart() {
        MonLog.d(getApplicationContext(), LOG,
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
            MonLog.e(getApplicationContext(), LOG, "### onStop - locationClient disconnecting ");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MonLog.e(getApplicationContext(), LOG,
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
        snackbar = Snackbar.make(mPager, "Getting GPS location so we can find golf courses around you", Snackbar.LENGTH_INDEFINITE);
        MonLog.d(getApplicationContext(), LOG, "### startLocationUpdates ....");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);


            MonLog.d(getApplicationContext(), LOG, "## GoogleApiClient connected, requesting location updates ...");
        } else {
            MonLog.e(getApplicationContext(), LOG, "------- GoogleApiClient is NOT connected, not sure where we are...");
            mGoogleApiClient.connect();

        }
    }


    protected void stopLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            MonLog.w(getApplicationContext(), LOG, "### stopLocationUpdates ...removeLocationUpdates fired");
        } else {
            MonLog.e(getApplicationContext(), LOG, "##################### stopLocationUpdates googleApiClient is NULL ...");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    static final String LOG = PlayerMainActivity.class.getSimpleName();
}
