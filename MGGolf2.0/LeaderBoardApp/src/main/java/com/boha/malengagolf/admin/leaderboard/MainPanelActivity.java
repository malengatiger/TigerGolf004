package com.boha.malengagolf.admin.leaderboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.GolfCourseMapActivity;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.*;
import com.boha.malengagolf.library.fragments.GolfGroupTournamentListFragment;
import com.boha.malengagolf.library.fragments.SplashFragment;
import com.boha.malengagolf.library.gallery.StaggeredTournamentGridFragment;
import com.boha.malengagolf.library.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/26.
 */
public class MainPanelActivity extends FragmentActivity implements GolfGroupTournamentListFragment.TournamentListener {
    public void onCreate(Bundle savedInstanceState) {
        Log.w(LOG, "################## onCreate");
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.main_split_panel);
        appUser = SharedUtil.getAppUser(ctx);

        setFields();
        setSplashFrament();
    }

    private void setFields() {
        tournamentListFragment = (GolfGroupTournamentListFragment) getSupportFragmentManager().findFragmentById(R.id.PANEL_fragment);
        mPager = (ViewPager) findViewById(R.id.PANEL_pager);
        spinner = (Spinner) findViewById(R.id.PANEL_spinner);


        if (appUser.getGolfGroupList().size() == 1) {
            golfGroup = appUser.getGolfGroupList().get(0);
            SharedUtil.saveGolfGroup(ctx, golfGroup);
            spinner.setVisibility(View.GONE);
            getTournaments();
        } else {
            setGroupSpinner();
        }
    }

    private void setGroupSpinner() {
        List<String> list = new ArrayList<String>();
        for (GolfGroupDTO g : appUser.getGolfGroupList()) {
            list.add(g.getGolfGroupName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                golfGroup = appUser.getGolfGroupList().get(i);
                SharedUtil.saveGolfGroup(ctx, golfGroup);
                getTournaments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }

    TournamentDTO tournament;
    SplashFragment splashFragment;

    @Override
    public void onGalleryRequested(TournamentDTO t) {
        Log.e(LOG, "onGalleryRequested ............");
        tournament = t;
        setTitle(t.getTourneyName());
        getTournamentPictures(t);

    }

    private void setSplashFrament() {
        Log.w(LOG, "############### setSplashFrament ............");
        pageFragmentList = new ArrayList<MGPageFragment>();
        splashFragment = new SplashFragment();
        pageFragmentList.add(splashFragment);

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

    private void getTournamentPictures(final TournamentDTO t) {
        Log.e(LOG, "getTournamentPictures ##########");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TOURNAMENT_THUMBNAILS);
        w.setTournamentID(t.getTournamentID());
        w.setGolfGroupID(golfGroup.getGolfGroupID());

        BaseVolley.getRemoteData(Statics.SERVLET_PHOTO, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                if (ErrorUtil.checkServerError(ctx, r)) {
                    fileNames = r.getImageFileNames();
                    getTournamentPLayersPictures(t);
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    private void getTournamentPLayersPictures(final TournamentDTO t) {
        Log.e(LOG, "getTournamentPLayersPictures");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_LEADERBOARD);
        w.setTournamentID(t.getTournamentID());
        w.setTournamentType(t.getTournamentType());

        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                if (ErrorUtil.checkServerError(ctx, r)) {
                    leaderBoardList = r.getLeaderBoardList();
                    setSplashFrament();
                    buildPages(t);
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }

    private void buildPages(TournamentDTO t) {
        Log.w(LOG, "######### buildPages ..........");
        staggeredTournamentGridFragment = new StaggeredTournamentGridFragment();
        Bundle b = new Bundle();
        b.putSerializable("tournament", t);
        staggeredTournamentGridFragment.setArguments(b);
        staggeredTournamentGridFragment.setFileNames(fileNames);


        if (leaderBoardList == null) {
            Log.e(LOG, "buildPages ######### leaderBoardList list is NULL");
        }
//        tournamentPlayersPicturesFragment = new TournamentPlayersPicturesFragment();
//        Bundle b2 = new Bundle();
//        b2.putSerializable("tournament", t);
//        tournamentPlayersPicturesFragment.setArguments(b2);
//        tournamentPlayersPicturesFragment.setLeaderBoardList(leaderBoardList);
//
//        pageFragmentList.add(tournamentPlayersPicturesFragment);
        pageFragmentList.add(staggeredTournamentGridFragment);


        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_panel, menu);
        mMenu = menu;

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.w(LOG, "onSaveInstanceState..........");
        b.putSerializable("tournament", tournament);
        ResponseDTO w = new ResponseDTO();
        w.setImageFileNames(fileNames);
        w.setLeaderBoardList(leaderBoardList);
        w.setTournaments(tournamentList);
        b.putSerializable("response", w);
        super.onSaveInstanceState(b);
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        Log.w(LOG, "onRestoreInstanceState..........");
        tournament = (TournamentDTO) b.getSerializable("tournament");
        ResponseDTO w = (ResponseDTO) b.getSerializable("response");
        leaderBoardList = w.getLeaderBoardList();
        fileNames = w.getImageFileNames();
        tournamentList = w.getTournaments();
        if (tournamentListFragment != null) {
            tournamentListFragment.refreshList(tournamentList, GolfGroupTournamentListFragment.APP_USER);
        }
        //setSplashFrament();
        //buildPages(tournament);
        super.onRestoreInstanceState(b);
    }

    List<TournamentDTO> tournamentList;

    private void getTournaments() {
        Log.w(LOG, "getTournaments ............");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TOURNAMENTS);
        w.setGolfGroupID(golfGroup.getGolfGroupID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                setRefreshActionButtonState(false);
                if (!ErrorUtil.checkServerError(ctx, response)) {
                    return;
                }
                tournamentList = response.getTournaments();
                for (int i = 0; i < tournamentList.size(); i++) {
                    TournamentDTO tournamentDTO = tournamentList.get(i);
                    tournamentDTO.setSortType(TournamentDTO.SORT_BY_NEWEST_TOURNAMENT_ENTERED);
                }
                Collections.sort(tournamentList);
                tournamentListFragment.refreshList(tournamentList, GolfGroupTournamentListFragment.APP_USER);
                if (!tournamentList.isEmpty()) {
                    setTitle(tournamentList.get(0).getTourneyName());
                    getTournamentPictures(tournamentList.get(0));

                    ResponseDTO w = new ResponseDTO();
                    w.setTournaments(tournamentList);
                    CacheUtil.cacheData(ctx, w, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO response) {

                        }

                        @Override
                        public void onDataCached() {

                        }
                    });

                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                setRefreshActionButtonState(false);
                ErrorUtil.showServerCommsError(ctx);
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_refresh:
                getTournaments();
                return true;
            case R.id.action_camera:
                Intent z = new Intent(ctx, PictureActivity.class);
                z.putExtra("tournament", tournament);
                startActivity(z);
                return true;

            case R.id.action_clubs:
                Intent x = new Intent(ctx, GolfCourseMapActivity.class);
                startActivity(x);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Menu mMenu;
    GolfGroupTournamentListFragment tournamentListFragment;
    ViewPager mPager;
    AppUserDTO appUser;
    Spinner spinner;
    Context ctx;
    GolfGroupDTO golfGroup;
    StaggeredTournamentGridFragment staggeredTournamentGridFragment;
    //TournamentPlayersPicturesFragment tournamentPlayersPicturesFragment;
    List<LeaderBoardDTO> leaderBoardList;
    List<MGPageFragment> pageFragmentList;
    PagerAdapter mPagerAdapter;

    List<String> fileNames;
    int currentPageIndex;
    static final String LOG = "MainPanelActivity";

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
            String title = ctx.getResources().getString(R.string.tourn_pics);

            switch (position) {
                case 0:
                    title = golfGroup.getGolfGroupName();
                    break;

                case 1:
                    title = ctx.getResources().getString(R.string.tourn_player_pics);
                    break;
                case 2:
                    title = ctx.getResources().getString(R.string.tourn_pics);
                    break;

                default:
                    break;
            }
            return title;
        }
    }

}