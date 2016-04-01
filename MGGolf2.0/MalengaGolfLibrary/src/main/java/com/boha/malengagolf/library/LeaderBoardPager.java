package com.boha.malengagolf.library;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.AppUserDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardCarrierDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.LeaderBoardSplashFragment;
import com.boha.malengagolf.library.fragments.LeaderboardFragment;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.LeaderBoardPage;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/19.
 */
public class LeaderBoardPager extends AppCompatActivity
        implements LeaderboardFragment.LeaderboardListener {
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "################ onCreate .......");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_pager);
        ctx = getApplicationContext();
        mPager = (ViewPager) findViewById(R.id.pager);
        LeaderBoardDTO lb = (LeaderBoardDTO)getIntent().getSerializableExtra("leaderBoard");
        if (lb != null) {
            Log.e(LOG,"@@@@@@@ LeaderBoard from GCM has come in");
            tournament = new TournamentDTO();
            tournament.setTournamentID(lb.getTournamentID());
            tournament.setTournamentType(lb.getTournamentType());
            tournament.setGolfRounds(lb.getRounds());
            tournament.setClubName(lb.getClubName());
            tournament.setTourneyName(lb.getTournamentName());
            administrator = SharedUtil.getAdministrator(ctx);
            scorer = SharedUtil.getScorer(ctx);
            appUser = SharedUtil.getAppUser(ctx);
            player = SharedUtil.getPlayer(ctx);
        } else {
            tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
            administrator = (AdministratorDTO) getIntent().getSerializableExtra("administrator");
            scorer = (ScorerDTO) getIntent().getSerializableExtra("scorer");
            player = (PlayerDTO) getIntent().getSerializableExtra("player");
            appUser = (AppUserDTO) getIntent().getSerializableExtra("appUser");

        }
        if (administrator == null && scorer == null && player == null && appUser == null) {
            throw new UnsupportedOperationException("User not found for Leaderboard");
        }
        golfGroup = SharedUtil.getGolfGroup(ctx);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        pagerTitleStrip = (PagerTitleStrip)findViewById(R.id.pager_title_strip);

    }

    private ImageLoader imageLoader;
    private GolfGroupDTO golfGroup;
    private AdministratorDTO administrator;
    private AppUserDTO appUser;
    private ScorerDTO scorer;
    private PlayerDTO player;
    private List<LeaderBoardCarrierDTO> carrierList;
    LeaderBoardSplashFragment splashFragment;

    private void setSplashFrament() {
        Log.e(LOG, "################ setSplashFrament ");
        leaderBoardPages = new ArrayList<LeaderBoardPage>();
        splashFragment = new LeaderBoardSplashFragment();
        leaderBoardPages.add(splashFragment);
        initializeAdapter();

    }

    private void buildPages() {
        Log.w(LOG, "#########################..................buildPages........");
        carrierList = new ArrayList<LeaderBoardCarrierDTO>();
        if (response.getLeaderBoardCarriers() == null && response.getLeaderBoardList() == null) {
            return;
        }
        if (response.getLeaderBoardCarriers() == null) {
            response.setLeaderBoardCarriers(new ArrayList<LeaderBoardCarrierDTO>());
            LeaderBoardCarrierDTO carrier = new LeaderBoardCarrierDTO();
            carrier.setLeaderBoardList(response.getLeaderBoardList());
            response.getLeaderBoardCarriers().add(carrier);
            if (response.getLeaderBoardList().isEmpty()) {
                ToastUtil.toast(ctx,ctx.getResources().getString(R.string.scoring_not_started));
                onBackPressed();
                return;
            }
        }
        for (LeaderBoardCarrierDTO lc : response.getLeaderBoardCarriers()) {
            if (lc.getLeaderBoardList().isEmpty()) {
                continue;
            }
            carrierList.add(lc);
        }

        Collections.sort(carrierList);
        if (carrierList.size() > 1) {
            pagerTitleStrip.setVisibility(View.VISIBLE);
        } else {
            pagerTitleStrip.setVisibility(View.GONE);
        }
        for (LeaderBoardCarrierDTO carrierDTO : carrierList) {
            LeaderboardFragment fragment = new LeaderboardFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("tournament", tournament);
            bundle.putSerializable("carrier", carrierDTO);
            fragment.setArguments(bundle);
            leaderBoardPages.add(fragment);

        }

        initializeAdapter();
        mPager.setCurrentItem(1, true);
    }

    private void initializeAdapter() {
        Log.e(LOG, "################ initializeAdapter ");
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    boolean isActivityFound;
    public boolean isActivityRunning() {

        ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        isActivityFound = false;
        for (int i = 0; i < services.size(); i++) {
            Log.i(LOG,"#### topActivity: " + services.get(i).topActivity.toString());
            if (services.get(i).topActivity.toString().equalsIgnoreCase(
                    "ComponentInfo{com.boha.malengagolf.admin/com.boha.malengagolf.library.LeaderBoardPager}")) {
                isActivityFound = true;
            }
            if (services.get(i).topActivity.toString().equalsIgnoreCase(
                    "ComponentInfo{com.boha.malengagolf.scorer/com.boha.malengagolf.library.LeaderBoardPager}")) {
                isActivityFound = true;
            }
            if (services.get(i).topActivity.toString().equalsIgnoreCase(
                    "ComponentInfo{com.boha.malengagolf.player/com.boha.malengagolf.library.LeaderBoardPager}")) {
                isActivityFound = true;
            }
        }
        return isActivityFound;
    }

    private void refreshLeaderBoard() {
        Log.w(LOG, "################,.......... refreshLeaderBoard ");
        mPager.setCurrentItem(0, true);
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_LEADERBOARD);
        w.setTournamentID(tournament.getTournamentID());
        w.setTournamentType(tournament.getTournamentType());
        w.setSessionID(SharedUtil.getSessionID(ctx));
        if (administrator != null) {
            w.setAdministratorID(administrator.getAdministratorID());
            Log.w(LOG, "################ user is administrator ");
        }
        if (appUser != null) {
            w.setAppUserID(appUser.getAppUserID());
            Log.w(LOG, "################ user is appUser ");
        }
        if (scorer != null) {
            w.setScorerID(scorer.getScorerID());
            Log.w(LOG, "################ user is scorer ");
        }
        if (player != null) {
            w.setPlayerID(player.getPlayerID());
            Log.w(LOG, "################ user is administrator ");
        }

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setRefreshActionButtonState(false);
                        if (r.getLeaderBoard() != null) {
                            Log.i(LOG, "### onMessage received ... about to update 1 score from server push");
                            for (LeaderBoardPage lf : leaderBoardPages) {
                                if (lf instanceof LeaderboardFragment) {
                                    LeaderboardFragment fragment = (LeaderboardFragment) lf;
                                    r.getLeaderBoard().setTimeStamp(new Date().getTime());
                                    fragment.updateSingleScore(r.getLeaderBoard());
                                    if (!isActivityRunning()) {
                                        ToastUtil.toast(ctx, tournament.getTourneyName()
                                                + "\nLeaderboard update just came in");
                                    }
                                }

                            }
                        } else {
                            Log.i(LOG, "### onMessage received ... about to build pages");
                            setSplashFrament();
                            response = r;
                            buildPages();
                        }
                    }
                });

                response = r;

                if (tournament.getClosedForScoringFlag() == 0) {
                    if (tournament.getUseAgeGroups() == 0) {
                        if (response.getLeaderBoard() != null) {
                            response.getLeaderBoard().setTimeStamp(new Date().getTime());
                        }
                        if (response.getLeaderBoardList() != null) {
                            for (LeaderBoardDTO d : response.getLeaderBoardList()) {
                                d.setTimeStamp(new Date().getTime());
                            }
                        }

                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });
                    } else {
                        for (LeaderBoardCarrierDTO c : response.getLeaderBoardCarriers()) {
                            for (LeaderBoardDTO d : c.getLeaderBoardList()) {
                                d.setTimeStamp(new Date().getTime());
                            }
                        }
                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADERBOARD_CARRIERS, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });
                    }

                }
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


//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                response = r;
//
//                if (tournament.getUseAgeGroups() == 0) {
//                    for (LeaderBoardDTO d : response.getLeaderBoardList()) {
//                        d.setTimeStamp(new Date().getTime());
//                    }
//
//                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
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
//                } else {
//                    for (LeaderBoardCarrierDTO c : response.getLeaderBoardCarriers()) {
//                        for (LeaderBoardDTO d : c.getLeaderBoardList()) {
//                            d.setTimeStamp(new Date().getTime());
//                        }
//                    }
//                    CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADERBOARD_CARRIERS, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
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
//                }
//
//                setSplashFrament();
//                buildPages();
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });

    }

    String mSessionID;

    @Override
    public void onRequestRefresh() {
        refreshLeaderBoard();
    }

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }

    @Override
    public void onScoreCardRequested(LeaderBoardDTO leaderBoard) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) leaderBoardPages.get(i);
        }

        @Override
        public int getCount() {
            return leaderBoardPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";
            if (position == 0 && tournament != null) {
                return tournament.getTourneyName();
            }
            if (carrierList == null) return "";
            try {
                LeaderBoardCarrierDTO x = (LeaderBoardCarrierDTO)
                        carrierList.get(position - 1);
                if (x == null) {
                    Log.e(LOG, "carrier is null at position " + position);
                } else {
                    if (x.getAgeGroup() == null) {
                        if (tournament.getUseAgeGroups() == 0) {
                            title = ctx.getResources().getString(R.string.leaderboard);
                        } else {
                            title = ctx.getResources().getString(R.string.combined_leaderboard);
                        }
                    } else {
                        title = x.getAgeGroup().getGroupName();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                title = "";
            }
            return title;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG, "################ onCreateOptionsMenu ");
        getMenuInflater().inflate(R.menu.leaderboard_pager, menu);
        mMenu = menu;
        if (tournament.getClosedForScoringFlag() > 0) {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).setVisible(false);
        }
        setSplashFrament();
        refreshLeaderBoard();
//        if (tournament.getUseAgeGroups() == 0) {
//            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
//                @Override
//                public void onFileDataDeserialized(ResponseDTO r) {
//                    if (r != null) {
//                        response = r;
//                        response.setLeaderBoardCarriers(new ArrayList<LeaderBoardCarrierDTO>());
//                        LeaderBoardCarrierDTO carrier = new LeaderBoardCarrierDTO();
//                        carrier.setLeaderBoardList(r.getLeaderBoardList());
//                        response.getLeaderBoardCarriers().add(carrier);
//                        buildPages();
//
//                    }
//                    refreshLeaderBoard();
//
//                }
//
//                @Override
//                public void onDataCached() {
//
//                }
//            });
//        } else {
//            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_LEADERBOARD_CARRIERS, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
//                @Override
//                public void onFileDataDeserialized(ResponseDTO r) {
//                    response = r;
//                    if (r != null) {
//                        buildPages();
//                    }
//                    refreshLeaderBoard();
//                }
//
//                @Override
//                public void onDataCached() {
//
//                }
//            });
//        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_camera) {
            Intent z = new Intent(ctx, PictureActivity.class);
            z.putExtra("tournament", tournament);
            startActivity(z);
            return true;
        }
        if (item.getItemId() == R.id.menu_refresh) {
            refreshLeaderBoard();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
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
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG, "!!!!!!!!!! onStop");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG, "!!!!!!!!!! onDestroy reset scrollIndex");
        SharedUtil.setScrollIndex(ctx, 0);
        super.onDestroy();
    }

    static final String LOG = LeaderBoardPager.class.getName();
    ResponseDTO response;
    Menu mMenu;
    TournamentDTO tournament;
    Context ctx;
    List<LeaderBoardPage> leaderBoardPages;
    List<LeaderBoardCarrierDTO> carriers;
    ViewPager mPager;
    PagerAdapter pagerAdapter;
    PagerTitleStrip pagerTitleStrip;
}