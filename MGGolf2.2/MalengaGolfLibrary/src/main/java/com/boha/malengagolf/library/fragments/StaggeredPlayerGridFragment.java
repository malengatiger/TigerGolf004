package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.StaggeredGridPlayerAdapter;
import com.boha.malengagolf.library.data.AgeGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/06/18.
 */
public class StaggeredPlayerGridFragment extends Fragment implements MGPageFragment {

    StaggeredListener listener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof StaggeredListener) {
            listener = (StaggeredListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() + " must implement StaggeredListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.staggered_grid, container, false);
        ctx = getActivity();
        mGridView = (StaggeredGridView) view.findViewById(R.id.staggeredGrid);
        Bundle b = getArguments();
        if (b != null) {
            ResponseDTO w = (ResponseDTO) b.getSerializable("response");
            tournament = w.getTournaments().get(0);
            setLeaderBoardList(w.getLeaderBoardList());
        }
        return view;
    }


    Context ctx;
    private View view;
    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private List<LeaderBoardDTO> leaderBoardList;

    public AgeGroupDTO getAgeGroup() {
        if (leaderBoardList == null) return null;
        AgeGroupDTO c = leaderBoardList.get(0).getAgeGroup();
        return c;
    }

    public void setLeaderBoardList(final List<LeaderBoardDTO> lb) {
        leaderBoardList = new ArrayList<>();
        for (LeaderBoardDTO b: lb) {
            if (b.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
                continue;
            }
            leaderBoardList.add(b);
        }
        mAdapter = new StaggeredGridPlayerAdapter(leaderBoardList, ctx, listener);

        mGridView.setGridPadding(0, 10, 0, 10);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                firstVisibleItem = i;
                visibleItemCount = i2;
                totalItemCount = i3;
//                Log.w(LOG, "--- onScroll firstVisibleItem:" + firstVisibleItem +
//                        " visibleItemCount:" + visibleItemCount +
//                        " totalItemCount:" + totalItemCount);
                // our handling
                //if (!mHasRequestedMore) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if (lastInScreen >= totalItemCount) {
                        Log.d(LOG, "onScroll lastInScreen - so load more");
                        mHasRequestedMore = true;
                        onLoadMoreItems();

                    }

                //}
            }
        });

    }

    boolean ismHasRequestedMore;

    private void onLoadMoreItems() {
        Log.e(LOG, "------------onLoadMoreItems ---------");
//        final ArrayList<String> sampleData = SampleData.generateSampleData();
//        for (String data : sampleData) {
//            mAdapter.add(data);
//        }
//        // stash all the data in our backing store
//        mData.addAll(sampleData);
//        // notify the adapter that we can update now
//        mAdapter.notifyDataSetChanged();
//        mHasRequestedMore = false;
    }

    //TODO - think about pull to refresh
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(LOG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }

    static final String LOG = "StaggeredGridFragment";
    StaggeredGridPlayerAdapter mAdapter;
    TournamentDTO tournament;
    int firstVisibleItem, visibleItemCount, totalItemCount;
}
