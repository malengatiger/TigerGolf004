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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.GolfCourseMapActivity;
import com.boha.malengagolf.library.MGApp;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.GolfGroupTournamentListFragment;
import com.boha.malengagolf.library.fragments.SplashFragment;
import com.boha.malengagolf.library.gallery.StaggeredTournamentGridFragment;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/20.
 */
public class MainPagerActivity extends FragmentActivity implements GolfGroupTournamentListFragment.TournamentListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorer_main_pager);
        ctx = getApplicationContext();
        golfGroup = SharedUtil.getGolfGroup(ctx);
        ACRA.getErrorReporter().putCustomData("golfGroupID", "" + golfGroup.getGolfGroupID());
        ACRA.getErrorReporter().putCustomData("golfGroupName", golfGroup.getGolfGroupName());
        mPager = (ViewPager) findViewById(R.id.pager);
        MGApp app = (MGApp) getApplication();
        setTitle(ctx.getResources().getString(R.string.app_name));
    }

    private void setSplashFrament(boolean build) {
        pageFragmentList = new ArrayList<MGPageFragment>();
        splashFragment = new SplashFragment();
        pageFragmentList.add(splashFragment);
        if (build) {
            buildPages();
        }
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


    private void buildPages() {

        tournamentListFragment = new GolfGroupTournamentListFragment();
        ResponseDTO w = new ResponseDTO();
        w.setTournaments(tournamentList);
        Bundle b = new Bundle();
        b.putSerializable("response", w);
        b.putInt("type", GolfGroupTournamentListFragment.APP_USER);
        b.putSerializable("appUser",SharedUtil.getAppUser(ctx));
        tournamentListFragment.setArguments(b);

        pageFragmentList.add(tournamentListFragment);
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

    @Override
    public void onGalleryRequested(TournamentDTO t) {
        tournament = t;
        getTournamentPictures(t);

    }

    List<String> fileNames;

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
                    Log.e(LOG, "getTournamentPictures fileNames : " + fileNames.size());
                    getTournamentPLayersPictures(t);
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                ErrorUtil.showServerCommsError(ctx);
            }
        });
    }

    private void getTournamentPLayersPictures(final TournamentDTO t) {
        Log.e(LOG, "getTournamentPLayersPictures");
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_LEADERBOARD);
        w.setTournamentID(t.getTournamentID());
        w.setTournamentType(t.getTournamentType());

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ErrorUtil.checkServerError(ctx, r)) {
                            leaderBoardList = r.getLeaderBoardList();
                            Log.e(LOG, "getTournamentPlayersPictures leaderBoardList: " + leaderBoardList.size());
                            staggeredTournamentGridFragment = new StaggeredTournamentGridFragment();
                            Bundle b = new Bundle();
                            b.putSerializable("tournament", t);
                            staggeredTournamentGridFragment.setArguments(b);
                            staggeredTournamentGridFragment.setFileNames(fileNames);
                            try {
                                if (pageFragmentList.size() > 2) {
                                    pageFragmentList.remove(pageFragmentList.size() - 1);
                                    pageFragmentList.remove(pageFragmentList.size() - 1);
                                    mPagerAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                            }
                            pageFragmentList.add(staggeredTournamentGridFragment);


                            mPagerAdapter.notifyDataSetChanged();
                            mPager.setCurrentItem(2);
                        }
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

                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                if (ErrorUtil.checkServerError(ctx, r)) {
//                    leaderBoardList = r.getLeaderBoardList();
//                    Log.e(LOG, "getTournamentPlayersPictures leaderBoardList: " + leaderBoardList.size());
//                    staggeredTournamentGridFragment = new StaggeredTournamentGridFragment();
//                    Bundle b = new Bundle();
//                    b.putSerializable("tournament", t);
//                    staggeredTournamentGridFragment.setArguments(b);
//                    staggeredTournamentGridFragment.setFileNames(fileNames);
//                    try {
//                        if (pageFragmentList.size() > 2) {
//                            pageFragmentList.remove(pageFragmentList.size() - 1);
//                            pageFragmentList.remove(pageFragmentList.size() - 1);
//                            mPagerAdapter.notifyDataSetChanged();
//                        }
//                    } catch (Exception e) {
//                    }
//                    pageFragmentList.add(staggeredTournamentGridFragment);
//
//
//                    mPagerAdapter.notifyDataSetChanged();
//                    mPager.setCurrentItem(2);
//                }
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//
//            }
//        });
    }

    StaggeredTournamentGridFragment staggeredTournamentGridFragment;
    //TournamentPlayersPicturesFragment tournamentPlayersPicturesFragment;
    List<LeaderBoardDTO> leaderBoardList;
    static final String LOG = "MainPagerActivity-scorer";

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
                    title = ctx.getResources().getString(R.string.tournaments);
                    break;
                case 2:
                    title = ctx.getResources().getString(R.string.tourn_player_pics);
                    break;
                case 3:
                    title = ctx.getResources().getString(R.string.tourn_pics);
                    break;

                default:
                    break;
            }
            return title;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scoring_main, menu);
        mMenu = menu;
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TOURNAMENTS, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null)
                    tournamentList = response.getTournaments();
            }

            @Override
            public void onDataCached() {

            }
        });

        if (!tournamentList.isEmpty()) {
            setSplashFrament(true);
        } else {
            setSplashFrament(false);
        }
        if (response == null) {
            getTournaments();
        }
        return true;
    }

    private void getTournaments() {
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
                setSplashFrament(true);

                    CacheUtil.cacheData(ctx,response, CacheUtil.CACHE_TOURNAMENTS, new CacheUtil.CacheUtilListener() {
                        @Override
                        public void onFileDataDeserialized(ResponseDTO response) {

                        }

                        @Override
                        public void onDataCached() {

                        }
                    });

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
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_clubs);
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

            case R.id.menu_refresh:
                getTournaments();
                return true;
            case R.id.menu_camera:
                Intent z = new Intent(ctx, PictureActivity.class);
                z.putExtra("tournament", tournament);
                startActivity(z);
                return true;

            case R.id.menu_clubs:
                Intent x = new Intent(ctx, GolfCourseMapActivity.class);
                startActivity(x);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Menu mMenu;
    Context ctx;
    ViewPager mPager;
    GolfGroupDTO golfGroup;
    ImageLoader imageLoader;
    List<MGPageFragment> pageFragmentList;
    PagerAdapter mPagerAdapter;
    GolfGroupTournamentListFragment tournamentListFragment;
    SplashFragment splashFragment;
    ResponseDTO response;
    int currentPageIndex;
    List<TournamentDTO> tournamentList;
}