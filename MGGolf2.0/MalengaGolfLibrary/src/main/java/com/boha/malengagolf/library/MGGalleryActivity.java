package com.boha.malengagolf.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.AgeGroupDTO;
import com.boha.malengagolf.library.data.AppUserDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardCarrierDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.SplashFragment;
import com.boha.malengagolf.library.fragments.StaggeredListener;
import com.boha.malengagolf.library.fragments.StaggeredPlayerGridFragment;
import com.boha.malengagolf.library.gallery.StaggeredTournamentGridFragment;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class MGGalleryActivity extends AppCompatActivity implements StaggeredListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorer_main_pager);
        ctx = getApplicationContext();

        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        administrator = (AdministratorDTO) getIntent().getSerializableExtra("administrator");
        scorer = (ScorerDTO) getIntent().getSerializableExtra("scorer");
        player = (PlayerDTO) getIntent().getSerializableExtra("player");
        appUser = (AppUserDTO) getIntent().getSerializableExtra("appUser");
        golfGroup = SharedUtil.getGolfGroup(ctx);

        mPager = (ViewPager) findViewById(R.id.pager);
        setTitle(tournament.getTourneyName());
        //setTimer();

        //TODO - stash leaderboardlist and fileNames in cache ..

    }
    Timer timer;
    private void setTimer() {
        timer = new Timer("timer", true);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e(LOG,"############ Timer fired a request to refresh at " + new Date().toString());
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        getLeaderBoard();
                    }});
            }
        };
        timer.scheduleAtFixedRate(tt, DELAY, PERIOD);
    }
    static final String LOG = "MGGalleryActivity";
    List<String> fileNames;
    List<LeaderBoardDTO> leaderBoardList;
    SplashFragment splashFragment;
    int currentPageIndex;
    ImageLoader imageLoader;
    private AdministratorDTO administrator;
    private AppUserDTO appUser;
    private ScorerDTO scorer;
    private PlayerDTO player;
    static final long DELAY = 60000, PERIOD = DELAY * 3;



    private void getLeaderBoard() {
        Log.w(LOG, ".....getLeaderBoard");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_LEADERBOARD);
        w.setTournamentID(tournament.getTournamentID());
        w.setTournamentType(tournament.getTournamentType());
        w.setSessionID(SharedUtil.getSessionID(ctx));
        if (administrator != null) {
            w.setAdministratorID(administrator.getAdministratorID());
        }
        if (appUser != null) {
            w.setAppUserID(appUser.getAppUserID());
        }
        if (scorer != null) {
            w.setScorerID(scorer.getScorerID());
        }
        if (player != null) {
            w.setPlayerID(player.getPlayerID());
        }
        setRefreshActionButtonState(true);

        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO r) {

                carriers = new ArrayList<LeaderBoardCarrierDTO>();
                if (tournament.getUseAgeGroups() == 0) {
                    LeaderBoardCarrierDTO c = new LeaderBoardCarrierDTO();
                    c.setLeaderBoardList(r.getLeaderBoardList());
                    carriers.add(c);
                } else {
                    carriers = r.getLeaderBoardCarriers();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        buildPages();
                        mPager.setCurrentItem(currentPageIndex, true);
                        //TODO = check if scoring complete - kill timer
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
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                carriers = new ArrayList<LeaderBoardCarrierDTO>();
//                if (tournament.getUseAgeGroups() == 0) {
//                    LeaderBoardCarrierDTO c = new LeaderBoardCarrierDTO();
//                    c.setLeaderBoardList(r.getLeaderBoardList());
//                    carriers.add(c);
//                } else {
//                    carriers = r.getLeaderBoardCarriers();
//                }
//                buildPages();
//                mPager.setCurrentItem(currentPageIndex, true);
//                //TODO = check if scoring complete - kill timer
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    List<LeaderBoardCarrierDTO> carriers;
    String mSessionID;

    private  void buildPages() {
        pageFragmentList = new ArrayList<MGPageFragment>();
        staggeredTournamentGridFragment = new StaggeredTournamentGridFragment();
        Bundle b = new Bundle();
        b.putSerializable("tournament", tournament);
        staggeredTournamentGridFragment.setArguments(b);

        for (LeaderBoardCarrierDTO carrier: carriers) {
            if (carrier.getLeaderBoardList() == null || carrier.getLeaderBoardList().isEmpty()) continue;
            StaggeredPlayerGridFragment spgf = new StaggeredPlayerGridFragment();
            Bundle b2 = new Bundle();
            ResponseDTO w = new ResponseDTO();
            for (LeaderBoardDTO d: carrier.getLeaderBoardList()) {
                d.setAgeGroup(carrier.getAgeGroup());
            }
            setPositions(carrier.getLeaderBoardList());
            w.setLeaderBoardList(carrier.getLeaderBoardList());
            w.setTournaments(new ArrayList<TournamentDTO>());
            w.getTournaments().add(tournament);
            b2.putSerializable("response", w);
            spgf.setArguments(b2);
            pageFragmentList.add(spgf);
        }

        pageFragmentList.add(staggeredTournamentGridFragment);
        //
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
    private void setPositions(List<LeaderBoardDTO> list) {
        Log.d(LOG, "####------------- setPositions ............");
        int mPosition = 1;
        int running = 1, score = 999;
        switch (tournament.getTournamentType()) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                Log.e(LOG, "####------------- setPositions STABLEFORD_INDIVIDUAL");
                for (LeaderBoardDTO lb : list) {
                    if (lb.isTied()) {
                        if (score == 999) {
                            score = lb.getTotalPoints();
                            mPosition = running;
                            lb.setPosition(mPosition);
                        } else {
                            if (score == lb.getTotalPoints()) {
                                lb.setPosition(mPosition);
                            } else {
                                score = lb.getTotalPoints();
                                mPosition = running;
                                lb.setPosition(mPosition);
                            }
                        }

                    } else {
                        score = 999;
                        lb.setPosition(running);
                    }

                    running++;
                }
                break;
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                Log.e(LOG, "####------------- setPositions STROKE_PLAY_INDIVIDUAL");
                for (LeaderBoardDTO lb : list) {
                    if (lb.isTied()) {
                        if (score == 999) {
                            score = lb.getParStatus();
                            mPosition = running;
                            lb.setPosition(mPosition);
                        } else {
                            if (score == lb.getParStatus()) {
                                lb.setPosition(mPosition);
                            } else {
                                score = lb.getParStatus();
                                mPosition = running;
                                lb.setPosition(mPosition);
                            }
                        }

                    } else {
                        score = 999;
                        lb.setPosition(running);
                    }

                    running++;
                }
                break;
        }

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
    public void onPlayerTapped(LeaderBoardDTO lb, int index) {
        Intent w = new Intent(this,ScoreCardActivity.class);
        w.putExtra("leaderBoard", lb);
        startActivity(w);
    }

    @Override
    public void onTournamentImagesNotFound() {
        pageFragmentList.remove(pageFragmentList.size() - 1);
        mPagerAdapter.notifyDataSetChanged();
        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tournament_images_not_found));
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
            String title = "";
            if (pageFragmentList.get(position) instanceof StaggeredPlayerGridFragment) {
                StaggeredPlayerGridFragment f = (StaggeredPlayerGridFragment)pageFragmentList.get(position);
                AgeGroupDTO ag = f.getAgeGroup();
                if (ag != null) {
                    title = ag.getGroupName();
                } else {
                    title = ctx.getResources().getString(R.string.leaderboard);
                    if (tournament.getUseAgeGroups() > 0) {
                        title = ctx.getResources().getString(R.string.combined_leaderboard);
                    }
                }
            }
            if (pageFragmentList.get(position) instanceof StaggeredTournamentGridFragment) {
                title = ctx.getResources().getString(R.string.tourn_pics);
            }
            return title;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        mMenu = menu;
        getLeaderBoard();
        if (tournament.getClosedForScoringFlag() > 0) {
            if (new Date().getTime() < (tournament.getEndDate() + CUT_OFF_TIME)) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
            } else {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(false);
            }
        }
        return true;
    }

    static final int CAMERA_REQUESTED = 5533;
    static final long CUT_OFF_TIME = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUESTED:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        staggeredTournamentGridFragment.getTournamentPictures();
                    } catch (Exception e) {
                        Log.e(LOG,"###### failed to execute method",e);
                        Intent x = new Intent(ctx, PictureActivity.class);
                        x.putExtra("tournament", tournament);
                        startActivityForResult(x, CAMERA_REQUESTED);
                    }
                }
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_camera) {
            Intent x = new Intent(ctx, PictureActivity.class);
            x.putExtra("tournament", tournament);
            startActivityForResult(x, CAMERA_REQUESTED);
            return true;

        }
        if (item.getItemId() == R.id.menu_refresh) {
            getLeaderBoard();
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
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    StaggeredTournamentGridFragment staggeredTournamentGridFragment;
    Menu mMenu;
    Context ctx;
    TournamentDTO tournament;
    GolfGroupDTO golfGroup;
    ResponseDTO response;
    PagerAdapter mPagerAdapter;
    ViewPager mPager;
    List<MGPageFragment> pageFragmentList;
}