package com.boha.malengagolf.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.TourneyPlayerActivity;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.AdministratorListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.ClubListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.PageFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.ParentListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.PlayerListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.ScorerListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.SplashFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.TournamentFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.TournamentListFragment;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.listeners.BusyListener;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.util.PersonEditDialog;
import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.GolfCourseMapActivity;
import com.boha.malengagolf.library.MGApp;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ParentDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class MainPagerActivity extends AppCompatActivity
        implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BusyListener, TournamentListFragment.TournamentListener,
        PlayerListFragment.PlayerListener, ParentListFragment.ParentListener,
        ScorerListFragment.ScorerListener, AdministratorListFragment.AdministratorListener,
        ClubListFragment.ClubListener {

    Context ctx;
    ViewPager mPager;
    GolfGroupDTO golfGroup;
    ImageLoader imageLoader;
    LocationRequest mLocationRequest;
    private DrawerLayout mDrawerLayout;
    ImageView navImage;
    TextView navText;
    Location location;
    NavigationView navigationView;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "onCreate ..........................");
        setContentView(R.layout.main_pager);
        ctx = getApplicationContext();
        golfGroup = SharedUtil.getGolfGroup(ctx);
//        ACRA.getErrorReporter().putCustomData("golfGroupID", "" + golfGroup.getGolfGroupID());
//        ACRA.getErrorReporter().putCustomData("golfGroupName", golfGroup.getGolfGroupName());
        mPager = (ViewPager) findViewById(R.id.pager);
        MGApp app = (MGApp) getApplication();
        setTitle("mgGolf");
        setSplashFrament();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navImage = (ImageView) findViewById(R.id.NAVHEADER_image);
        navText = (TextView) findViewById(R.id.NAVHEADER_text);
        navText.setText(SharedUtil.getAdministrator(ctx).getFullName());

        setTitle(golfGroup.getGolfGroupName());
        AdministratorDTO a = SharedUtil.getAdministrator(ctx);
        getSupportActionBar().setSubtitle(a.getFullName());
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.ic_action_event);

        setMenuDestinations();
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void setMenuDestinations() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_import_players:
                        Intent dd = new Intent(ctx, ImportActivity.class);
                        startActivityForResult(dd, REQUEST_IMPORT_PLAYERS);
                        return true;
                    case R.id.nav_find_courses:
                        Intent z1 = new Intent(ctx, GolfCourseMapActivity.class);
                        z1.putExtra("requestOrigin", GolfCourseMapActivity.ORIGIN_SEARCH);
                        startActivity(z1);
                        return true;

                    case R.id.nav_invite:
                        Intent x = new Intent(ctx, AppInvitationActivity.class);
                        x.putExtra("type", AppInvitationFragment.APP_USER);
                        startActivity(x);
                        return true;
                    case R.id.nav_add_tournament:
                        Intent intent = new Intent(ctx, TourneyActivity.class);
                        intent.putExtra("action", TournamentFragment.ADD_NEW);
                        ResponseDTO r = new ResponseDTO();
                        r.setClubs(response.getClubs());
                        intent.putExtra("clubs", r);
                        startActivityForResult(intent, NEW_TOURNEY_REQUEST);
                        return true;
                    case R.id.nav_add_admin:
                        mPager.setCurrentItem(ADMINS,true);
                        administratorListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        return true;
                    case R.id.nav_add_player:
                        mPager.setCurrentItem(PLAYERS,true);
                        playerListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        return true;
                    case R.id.nav_add_scorer:
                        mPager.setCurrentItem(SCORERS,true);
                        scorerListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        return true;

                    case R.id.nav_refresh:
                        getGolfGroupData();
                        return true;


                }

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

//        if (mLocationClient.isConnected()) {
//            stopPeriodicUpdates();
//        }
//
//        // After disconnect() is called, the client is considered "dead".
//        mLocationClient.disconnect();
//        Log.e("map", "### onStop - locationClient disconnected: "
//                + mLocationClient.isConnected());
//        //WebSocketUtil.disconnectSession();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.e(LOG, "### .........Location changed, lat: "
                + loc.getLatitude() + " lng: "
                + loc.getLongitude()
                + " -- getting nearby clubs");
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        mCurrentLocation = loc;
        double[] x = SharedUtil.getLastLocation(ctx);
        SharedUtil.saveLocation(ctx, latitude, longitude);

    }

    SplashFragment splashFragment;


    private void getNearbyClubs() {
        if (latitude == 0 && longitude == 0) return;

        RequestDTO w = new RequestDTO();
        if (w != null) { //TODO remove after testing
            return;
        }
        w.setRequestType(RequestDTO.GET_CLUBS_NEARBY);
        w.setLatitude(latitude);
        w.setLongitude(longitude);
        w.setRadius(RequestDTO.RADIUS);

        //TODO - check if Canada and UK use miles or km
        if (golfGroup.getCountryName().contains("United States")) {
            w.setRadiusType(MILES);
        } else {
            w.setRadiusType(KILOMETRES);
        }
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        Log.e(LOG, "Have found " + r.getClubs().size() + " clubs nearby");
                        response.setClubs(r.getClubs());
                        CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_NEAREST_CLUBS, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                Log.e(LOG, "Have found " + r.getClubs().size() + " clubs nearby");
//                response.setClubs(r.getClubs());
//                CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_NEAREST_CLUBS, new CacheUtil.CacheUtilListener() {
//                    @Override
//                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onDataCached() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void setSplashFrament() {
        pageFragmentList = new ArrayList<PageFragment>();
        splashFragment = new SplashFragment();
        pageFragmentList.add(splashFragment);
        initializeAdapter();

    }

    private void getGolfGroupData() {
        mPager.setCurrentItem(0, true);
        splashFragment.setLoading(true);

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.GET_GOLF_GROUP_DATA);
        r.setGolfGroupID(golfGroup.getGolfGroupID());
        r.setCountryID(golfGroup.getCountryID());
        r.setZippedResponse(true);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            goOffLine();
            return;
        }
        setBusy();

        if (useWebSocket) {
            WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(ResponseDTO r) {
                    response = r;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNotBusy();
                            splashFragment.setLoading(false);
                            setSplashFrament();
                            buildPages();
                            getNearbyClubs();
                        }
                    });

                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_ADMINS, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO response) {
                        }

                        @Override
                        public void onDataCached() {
                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_PLAYERS, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {
                                }

                                @Override
                                public void onDataCached() {
                                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_SCORERS, new CacheUtil.CacheUtilListener() {
                                        @Override
                                        public void onFileDataDeserialized(ResponseDTO response) {
                                        }

                                        @Override
                                        public void onDataCached() {
                                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
                                                @Override
                                                public void onFileDataDeserialized(ResponseDTO response) {
                                                }

                                                @Override
                                                public void onDataCached() {
                                                }
                                            });
                                        }
                                    });
                                }
                            });
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
                            ToastUtil.errorToast(ctx, message);
                        }
                    });

                }


            });

        } else {
            MGApp app = (MGApp) getApplication();
            BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, app, new BaseVolley.BohaVolleyListener() {
                @Override
                public void onResponseReceived(ResponseDTO r) {
                    setNotBusy();
                    splashFragment.setLoading(false);
                    if (r.getStatusCode() > 0) {
                        ToastUtil.errorToast(ctx, r.getMessage());
                        return;
                    }
                    response = r;
                    setSplashFrament();
                    buildPages();
                    getNearbyClubs();
                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_ADMINS, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO response) {
                        }

                        @Override
                        public void onDataCached() {
                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_PLAYERS, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {
                                }

                                @Override
                                public void onDataCached() {
                                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_SCORERS, new CacheUtil.CacheUtilListener() {
                                        @Override
                                        public void onFileDataDeserialized(ResponseDTO response) {
                                        }

                                        @Override
                                        public void onDataCached() {
                                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
                                                @Override
                                                public void onFileDataDeserialized(ResponseDTO response) {
                                                }

                                                @Override
                                                public void onDataCached() {
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });


                }

                @Override
                public void onVolleyError(VolleyError error) {
                    setNotBusy();
                    splashFragment.setLoading(false);
                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
                }
            });
        }
    }

    boolean isUsingCachedData, useWebSocket = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_pager_menu, menu);
        mMenu = menu;

        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_ADMINS, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                if (r != null) {
                    response = r;
                    CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TOURNAMENTS, 0, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO r) {
                            if (r != null) {
                                response.setTournaments(r.getTournaments());
                                CacheUtil.getCachedData(ctx, CacheUtil.CACHE_PLAYERS, 0, new CacheUtil.CacheUtilListener() {
                                    @Override
                                    public void onFileDataDeserialized(ResponseDTO r) {
                                        if (r != null) {
                                            response.setPlayers(r.getPlayers());
                                            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_SCORERS, 0, new CacheUtil.CacheUtilListener() {
                                                @Override
                                                public void onFileDataDeserialized(ResponseDTO r) {
                                                    if (r != null) {
                                                        response.setScorers(r.getScorers());
                                                        setSplashFrament();
                                                        isUsingCachedData = true;
                                                        buildPages();
                                                    } else {
                                                        getGolfGroupData();
                                                    }


                                                }

                                                @Override
                                                public void onDataCached() {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onDataCached() {

                                    }
                                });
                            } else {
                                getGolfGroupData();
                            }
                        }

                        @Override
                        public void onDataCached() {

                        }
                    });
                } else {
                    getGolfGroupData();
                }

            }

            @Override
            public void onDataCached() {
            }
        });


        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_add);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    static final int REQUEST_IMPORT_PLAYERS = 3733;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;

            case R.id.menu_import:
                Intent dd = new Intent(ctx, ImportActivity.class);
                startActivityForResult(dd, REQUEST_IMPORT_PLAYERS);
                return true;
            case R.id.menu_invite_leaderboard_viewer:
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("type", AppInvitationFragment.APP_USER);
                startActivity(x);
                return true;
            case R.id.menu_subscribe:
                Intent a = new Intent(ctx, MgPaymentActivity.class);
                startActivityForResult(a, PAYMENT_REQUEST);
                return true;

            case R.id.menu_search:
                Intent z1 = new Intent(ctx, GolfCourseMapActivity.class);
                z1.putExtra("requestOrigin", GolfCourseMapActivity.ORIGIN_SEARCH);
                startActivity(z1);
                return true;
            case R.id.menu_add:
                switch (currentPageIndex) {
                    case TOURNAMENT:
                        Intent intent = new Intent(ctx, TourneyActivity.class);
                        intent.putExtra("action", TournamentFragment.ADD_NEW);
                        ResponseDTO r = new ResponseDTO();
                        r.setClubs(response.getClubs());
                        intent.putExtra("clubs", r);
                        startActivityForResult(intent, NEW_TOURNEY_REQUEST);
                        break;
                    case PLAYERS:
                        mPager.setCurrentItem(1,true);
                        playerListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        break;
                    case SCORERS:
                        mPager.setCurrentItem(3,true);
                        scorerListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        break;
                    case ADMINS:
                        mPager.setCurrentItem(2,true);
                        administratorListFragment.showPersonDialog(PersonEditDialog.ACTION_ADD);
                        break;

                }
                return true;

            case R.id.menu_refresh:
                getGolfGroupData();
                return true;

            case R.id.menu_help:
                ToastUtil.toast(ctx, "Under Construction");
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goOffLine() {
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_ADMINS, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                if (r != null) {
                    response = r;
                    CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TOURNAMENTS, 0, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO r) {
                            if (r != null) {
                                response.setTournaments(r.getTournaments());
                                CacheUtil.getCachedData(ctx, CacheUtil.CACHE_PLAYERS, 0, new CacheUtil.CacheUtilListener() {
                                    @Override
                                    public void onFileDataDeserialized(ResponseDTO r) {
                                        if (r != null) {
                                            response.setPlayers(r.getPlayers());
                                            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_SCORERS, 0, new CacheUtil.CacheUtilListener() {
                                                @Override
                                                public void onFileDataDeserialized(ResponseDTO r) {
                                                    if (r != null) {
                                                        response.setScorers(r.getScorers());
                                                    }
                                                    setSplashFrament();
                                                    buildPages();
                                                }

                                                @Override
                                                public void onDataCached() {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onDataCached() {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onDataCached() {

                        }
                    });
                }
            }

            @Override
            public void onDataCached() {

            }
        });
    }

    TournamentListFragment tournamentListFragment;
    PlayerListFragment playerListFragment;
    ScorerListFragment scorerListFragment;
    AdministratorListFragment administratorListFragment;

    private void buildPages() {

        tournamentListFragment = new TournamentListFragment();
        ResponseDTO r1 = new ResponseDTO();
        Bundle data1 = new Bundle();
        r1.setTournaments(response.getTournaments());
        r1.setPlayers(response.getPlayers());
        data1.putSerializable("response", r1);
        tournamentListFragment.setArguments(data1);

        playerListFragment = new PlayerListFragment();
        Bundle data2 = new Bundle();
        ResponseDTO r2 = new ResponseDTO();
        r2.setPlayers(response.getPlayers());
        data2.putSerializable("response", r2);
        playerListFragment.setArguments(data2);

        scorerListFragment = new ScorerListFragment();
        Bundle data3 = new Bundle();
        ResponseDTO r3 = new ResponseDTO();
        r3.setScorers(response.getScorers());
        data3.putSerializable("response", r3);
        scorerListFragment.setArguments(data3);

        administratorListFragment = new AdministratorListFragment();
        Bundle data5 = new Bundle();
        ResponseDTO r5 = new ResponseDTO();
        r5.setAdministrators(response.getAdministrators());
        data5.putSerializable("response", r5);
        administratorListFragment.setArguments(data5);

        pageFragmentList.add(tournamentListFragment);
        pageFragmentList.add(playerListFragment);
        pageFragmentList.add(administratorListFragment);
        pageFragmentList.add(scorerListFragment);

        initializeAdapter();
        mPager.setCurrentItem(1, true);
        if (isUsingCachedData) {
            isUsingCachedData = false;
            getGolfGroupData();
        }

    }

    private void initializeAdapter() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                currentPageIndex = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(LOG, "@@@@@@@@@@@@@@@@@ onActivityResult requestCode = " + requestCode + " resultCode: " + resultCode);
        switch (requestCode) {
            case NEW_TOURNEY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    ResponseDTO t = (ResponseDTO) data.getSerializableExtra("response");
                    tournamentListFragment.refreshList(t.getTournaments());
                }
                break;
            case MANAGE_TOURNAMENT:
                tournamentListFragment.onTournamentChange(requestCode, resultCode, data);
                break;
            case MANAGE_TOURNAMENT_PLAYERS:
                tournamentListFragment.onTournamentChange(requestCode, resultCode, data);
                break;
            case REQUEST_IMPORT_PLAYERS:
                if (resultCode == Activity.RESULT_OK) {
                    boolean pImported = data.getBooleanExtra("playersImported", false);
                    if (pImported) {
                        Log.w(LOG, "...refresh by getGolfGroupData");
                        getGolfGroupData();
                    }

                }
                break;
            case ADMIN_PICTURE_REQUESTED:
                if (resultCode == Activity.RESULT_OK) {
                    administratorListFragment.setList(true, selectedIndex);
                }
                break;
            case SCORER_PICTURE_REQUESTED:
                if (resultCode == Activity.RESULT_OK) {
                    scorerListFragment.setList(true, selectedIndex);
                }

                break;
            case PLAYER_PICTURE_REQUESTED:
                if (resultCode == Activity.RESULT_OK) {
                    playerListFragment.setList(true, selectedIndex);
                }

                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "### ---> PlayServices onConnected() - gotta start something! >>");
//        mCurrentLocation = mLocationClient.getLastLocation();
//        if (mCurrentLocation != null) {
//            latitude = mCurrentLocation.getLatitude();
//            longitude = mCurrentLocation.getLongitude();
//            Log.e(LOG, "######### onConnected() - starting ServerTask");
//            if (response == null) return;
//            onLocationChanged(mCurrentLocation);
//        } else {
//            Log.e("map", "$$$$ mCurrentLocation is NULL");
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onScorerPictureRequested(ScorerDTO scorer, int index) {
        selectedIndex = index;
        Intent dd = new Intent(this, PictureActivity.class);
        dd.putExtra("scorer", scorer);
        startActivityForResult(dd, SCORER_PICTURE_REQUESTED);
    }

    @Override
    public void onPlayerPictureRequested(PlayerDTO player, int index) {
        selectedIndex = index;
        Intent dd = new Intent(this, PictureActivity.class);
        dd.putExtra("player", player);
        startActivityForResult(dd, PLAYER_PICTURE_REQUESTED);
    }

    @Override
    public void onAdministratorPictureRequested(AdministratorDTO administrator, int index) {
        selectedIndex = index;
        Intent dd = new Intent(this, PictureActivity.class);
        dd.putExtra("administrator", administrator);
        startActivityForResult(dd, ADMIN_PICTURE_REQUESTED);
    }


    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";

            switch (position) {
                case SPLASH:
                    title = golfGroup.getGolfGroupName();
                    break;
                case TOURNAMENT:
                    title = ctx.getResources().getString(R.string.tournaments);
                    break;
                case PLAYERS:
                    title = ctx.getResources().getString(R.string.players);
                    break;
                case SCORERS:
                    title = ctx.getResources().getString(R.string.scorers);
                    break;
                case ADMINS:
                    title = ctx.getResources().getString(R.string.admins);
                    break;
                default:
                    break;
            }
            return title;
        }
    }

    private int selectedIndex;
    public static final int
            SPLASH = 0,
            TOURNAMENT = 1,
            PLAYERS = 2,
            ADMINS = 3,
            SCORERS = 4,
            PARENTS = 5,
            SCORER_PICTURE_REQUESTED = 903, ADMIN_PICTURE_REQUESTED = 904, PLAYER_PICTURE_REQUESTED = 905, PARENT_PICTURE_REQUESTED = 906;

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
        splashFragment.setLoading(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
        splashFragment.setLoading(false);
    }

    @Override
    public void onParentPicked(ParentDTO parent) {

    }

    @Override
    public void onClubPicked(ClubDTO club) {

    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        WebSocketUtil.disconnectSession();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.e(LOG, "---------------- onResume ...........");
        super.onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "-------------- onSaveInstanceState .............................");
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    @Override
    public void onManageTournamentRequest(TournamentDTO t) {
        startTournamentManager(t);
    }

    @Override
    public void onManagePlayersRequest(TournamentDTO t, List<PlayerDTO> list) {
        startPlayerManager(t, list);
    }

    private void startTournamentManager(TournamentDTO t) {
        Intent x = new Intent(ctx, TourneyActivity.class);
        x.putExtra("action", TournamentFragment.UPDATE);
        x.putExtra("tournament", t);
        ResponseDTO r = new ResponseDTO();
        r.setClubs(response.getClubs());
        x.putExtra("clubs", r);
        startActivityForResult(x, MANAGE_TOURNAMENT);
    }

    private void startPlayerManager(TournamentDTO t, List<PlayerDTO> list) {
        Intent intent = new Intent(ctx, TourneyPlayerActivity.class);
        intent.putExtra("tournament", t);
        ResponseDTO w = new ResponseDTO();
        w.setPlayers(list);
        intent.putExtra("response", w);
        startActivityForResult(intent, MANAGE_TOURNAMENT_PLAYERS);

    }

    Menu mMenu;
    ResponseDTO response, response2;
    PagerAdapter mPagerAdapter;
    int currentPageIndex;
    List<PageFragment> pageFragmentList;
    static final String LOG = "MainPagerActivity";
    public static final int MANAGE_TOURNAMENT = 3366,
            MANAGE_TOURNAMENT_PLAYERS = 3377;
//    LocationClient mLocationClient;
    Location mCurrentLocation;
    double latitude, longitude;
    public static final int ADD_NEW = 0, NEW_TOURNEY_REQUEST = 3543, PAYMENT_REQUEST = 3391;
    public static final int KILOMETRES = 1, MILES = 2;

}