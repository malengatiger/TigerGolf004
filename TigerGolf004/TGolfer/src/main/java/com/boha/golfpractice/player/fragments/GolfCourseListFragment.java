package com.boha.golfpractice.player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boha.golfpractice.player.R;
import com.boha.golfpractice.player.activities.MonApp;
import com.boha.golfpractice.player.adapters.GolfCourseListAdapter;
import com.boha.golfpractice.player.dto.GolfCourseDTO;
import com.boha.golfpractice.player.dto.ResponseDTO;
import com.boha.golfpractice.player.util.MonLog;
import com.boha.golfpractice.player.util.SnappyGolfCourse;
import com.boha.golfpractice.player.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GolfCourseListFragment extends Fragment implements PageFragment {

    public static GolfCourseListFragment newInstance(List<GolfCourseDTO> list) {
        GolfCourseListFragment fragment = new GolfCourseListFragment();
        Bundle args = new Bundle();
        ResponseDTO w = new ResponseDTO();
        w.setGolfCourseList(list);
        args.putSerializable("response", w);
        fragment.setArguments(args);
        return fragment;
    }

    List<GolfCourseDTO> golfCourseList = new ArrayList<>();
    RecyclerView mRecyclerView;
    TextView txtCount, radiusLabel;
    SeekBar seekBar;
    View view;
    GolfCourseListAdapter adapter;
    FloatingActionButton fab;
    static final String LOG = GolfCourseListFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ResponseDTO w = (ResponseDTO) getArguments().getSerializable("response");
            golfCourseList = w.getGolfCourseList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_golfcourse_list, container, false);
        setFields();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MonLog.d(getActivity(), LOG, "+++++++++++++ onResume ++++");
        SnappyGolfCourse.getFavouriteGolfCourses((MonApp) getActivity().getApplication(), new SnappyGolfCourse.DBReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                golfCourseList = response.getGolfCourseList();
                setList();
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void setList() {
        if (golfCourseList == null || golfCourseList.isEmpty()) {
            return;
        }
        MonLog.d(getActivity(), LOG, "########## setList: " + golfCourseList.size());
        txtCount.setText("" + golfCourseList.size());
        Collections.sort(golfCourseList);
        adapter = new GolfCourseListAdapter(golfCourseList, getActivity(),
                new GolfCourseListAdapter.GolfCourseListener() {
                    @Override
                    public void onCourseClicked(GolfCourseDTO course) {
                        if (mListener != null) {
                            mListener.onGolfCourseClicked(course);
                        }
                    }

                    @Override
                    public void onStartSession(GolfCourseDTO course) {
                        mListener.onStartSession(course);
                    }

                    @Override
                    public void onGetSessions(GolfCourseDTO course) {
                        mListener.onGetSessions(course);
                    }

                    @Override
                    public void onGetDirections(GolfCourseDTO course) {
                        mListener.onGetDirections(course);
                    }
                });

        mRecyclerView.setAdapter(adapter);

    }

    private void setFields() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        txtCount = (TextView) view.findViewById(R.id.count);
        radiusLabel = (TextView) view.findViewById(R.id.radius);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        radiusLabel.setText("" + Util.GOLFCOURSE_SEARCH_RADIUS);
        seekBar.setMax(Util.GOLFCOURSE_MAX_RADIUS);
        seekBar.setProgress(Util.GOLFCOURSE_SEARCH_RADIUS);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setBusy(true);
                mListener.onCourseSearchRequired(seekBar.getProgress());
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusLabel.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void setGolfCourseList(List<GolfCourseDTO> golfCourseList) {
        this.golfCourseList = golfCourseList;

            if (mRecyclerView != null) {
                setList();
            }

    }

    GolfCourseListListener mListener;

    public void setListener(GolfCourseListListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GolfCourseListListener) {
            mListener = (GolfCourseListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GolfCourseListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MonApp app = (MonApp) getActivity().getApplication();
//        RefWatcher refWatcher = app.getRefWatcher();
//        refWatcher.watch(this);
    }

    @Override
    public String getPageTitle() {
        return "Golf Courses Around Me";
    }

    public interface GolfCourseListListener {
        void onGolfCourseClicked(GolfCourseDTO course);

        void onCourseSearchRequired(int radius);

        void onStartSession(GolfCourseDTO course);

        void onGetSessions(GolfCourseDTO course);

        void onGetDirections(GolfCourseDTO course);

        void setBusy(boolean busy);
    }
}
