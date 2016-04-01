package com.boha.malengagolf.admin.leaderboard.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.boha.malengagolf.admin.leaderboard.R;
import com.boha.malengagolf.library.fragments.LeaderboardFragment;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardCarrierDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.LeaderBoardPage;
import com.boha.malengagolf.library.util.SharedUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/26.
 */
public class ContainerFragment extends Fragment
        implements LeaderBoardPage, LeaderboardFragment.LeaderboardListener{
    Context ctx;
    View view;

    public interface ContainerFragmentListener {
        public void onScorecardRequested(LeaderBoardDTO leaderBoard);
    }
    ContainerFragmentListener containerFragmentListener;
    @Override
    public void onAttach(Activity a) {
        if (a instanceof ContainerFragmentListener) {
            containerFragmentListener = (ContainerFragmentListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement ContainerFragmentListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_container, container, false);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        splash = (ImageView) view.findViewById(R.id.splash);
        view.setBackgroundColor(ctx.getResources().getColor(R.color.grey));
        //TODO - change this when done
        GolfGroupDTO s = new GolfGroupDTO();
        s.setGolfGroupID(10);
        s.setCountryID(1);
        s.setGolfGroupName("Woodmead Golfers");
        SharedUtil.saveGolfGroup(ctx,s);
        return view;
    }

    private void buildPages() {
       Collections.sort(carrierList);
        for (LeaderBoardCarrierDTO carrierDTO : carrierList) {
            LeaderboardFragment fragment = new LeaderboardFragment();
            fragment.setUseDialogForScorecard(true);
            Bundle bundle = new Bundle();
            bundle.putSerializable("tournament", tournament);
            bundle.putSerializable("carrier", carrierDTO);
            fragment.setArguments(bundle);
            leaderBoardPages.add(fragment);

        }

        initializeAdapter();
        makeSplashGone();
    }

    private void initializeAdapter() {
        mAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
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

    private void makeSplashVisible() {
        splash.setVisibility(View.VISIBLE); //TODO animate
        mPager.setVisibility(View.GONE);
    }
    private void makeSplashGone() {
        splash.setVisibility(View.GONE); //TODO animate
        mPager.setVisibility(View.VISIBLE);
    }

    public void showSplash() {
        splash.setImageDrawable(SharedUtil.getRandomSplash(ctx));
        makeSplashVisible();
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
            if (position == 0) {
                return tournament.getTourneyName();
            }
            LeaderBoardCarrierDTO x = (LeaderBoardCarrierDTO)
                    carrierList.get(position);
            if (x == null) {
                Log.e(LOG, "carrier is null at position " + position);
            } else {
                if (x.getAgeGroup() == null) {
                    title = ctx.getResources().getString(com.boha.malengagolf.library.R.string.combined_leaderboard);
                } else {
                    title = x.getAgeGroup().getGroupName();
                }
            }
            return title;
        }
    }

    TournamentDTO tournament;
    List<LeaderBoardPage> leaderBoardPages = new ArrayList<LeaderBoardPage>();
    ViewPager mPager;
    PagerAdapter mAdapter;
    List<LeaderBoardCarrierDTO> carrierList;
    ImageView splash;

    public void setCarrierList(TournamentDTO tournament,List<LeaderBoardCarrierDTO> carrierList) {
        leaderBoardPages = new ArrayList<LeaderBoardPage>();
        this.carrierList = carrierList;
        this.tournament = tournament;
        Log.d(LOG, "setCarrierList, tournament: " + tournament.getTourneyName());
        buildPages();
    }
    @Override
    public void onRequestRefresh() {

    }

    @Override
    public void setBusy() {

    }

    @Override
    public void setNotBusy() {

    }

    @Override
    public void onScoreCardRequested(LeaderBoardDTO leaderBoard) {
        Log.i(LOG,"onScorecardRequested telling listener");
        containerFragmentListener.onScorecardRequested(leaderBoard);
    }

    static final String LOG = "ContainerFragment";
}
