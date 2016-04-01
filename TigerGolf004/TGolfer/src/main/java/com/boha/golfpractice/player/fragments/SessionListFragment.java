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
import android.widget.TextView;

import com.boha.golfpractice.player.R;
import com.boha.golfpractice.player.activities.MonApp;
import com.boha.golfpractice.player.adapters.SessionListAdapter;
import com.boha.golfpractice.player.dto.PracticeSessionDTO;
import com.boha.golfpractice.player.dto.RequestDTO;
import com.boha.golfpractice.player.dto.ResponseDTO;
import com.boha.golfpractice.player.util.MonLog;
import com.boha.golfpractice.player.util.OKHttpException;
import com.boha.golfpractice.player.util.OKUtil;
import com.boha.golfpractice.player.util.SnappyPractice;
import com.boha.golfpractice.player.util.Util;

import java.util.List;


public class SessionListFragment extends Fragment implements PageFragment {

    public static SessionListFragment newInstance(List<PracticeSessionDTO> list) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        ResponseDTO w = new ResponseDTO();
        w.setPracticeSessionList(list);
        args.putSerializable("response", w);
        fragment.setArguments(args);
        return fragment;
    }

    List<PracticeSessionDTO> practiceSessionList;
    RecyclerView mRecyclerView;
    TextView txtCount;
    View view;
    MonApp app;
    SessionListAdapter adapter;
    FloatingActionButton fab;
    static final String LOG = SessionListFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ResponseDTO w = (ResponseDTO) getArguments().getSerializable("response");
            practiceSessionList = w.getPracticeSessionList();
        }
        app = (MonApp) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_list, container, false);
        setFields();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MonLog.d(getActivity(), LOG, "+++++++++++++ onResume ++++");
        setList();
    }


    public void refresh() {
        SnappyPractice.getPracticeSessions((MonApp) getActivity().getApplication(), new SnappyPractice.DBReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                practiceSessionList = response.getPracticeSessionList();
                setList();
            }

            @Override
            public void onError(String message) {

            }
        });
    }
    private void setList() {

        MonLog.d(getActivity(), LOG, "########## setList: " + practiceSessionList.size());
        txtCount.setText("" + practiceSessionList.size());
        adapter = new SessionListAdapter(practiceSessionList, getActivity(), new SessionListAdapter.SessionListener() {
            @Override
            public void onSessionClicked(PracticeSessionDTO session) {
                if (mListener != null) {
                    mListener.onSessionClicked(session);
                }
            }

            @Override
            public void onSessionCloseRequired(PracticeSessionDTO session) {
                closeSession(session);
            }
        });
        mRecyclerView.setAdapter(adapter);

    }

    private void setFields() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        txtCount = (TextView) view.findViewById(R.id.count);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNewSessionRequested();
            }
        });
    }

    public void setPracticeSessionList(List<PracticeSessionDTO> practiceSessionList) {
        MonLog.d(getActivity(), LOG, "+++++++++++++ setPracticeSessionList ++++");
        this.practiceSessionList = practiceSessionList;

        if (mRecyclerView != null) {
            setList();
        }

    }

    public void addPracticeSession(PracticeSessionDTO s) {
        if (practiceSessionList != null) {
            practiceSessionList.add(0, s);
            if (mRecyclerView != null) {
                setList();
            }
        }
    }


    private void closeSession(PracticeSessionDTO session) {
        RequestDTO w = new RequestDTO(RequestDTO.CLOSE_PRACTICE_SESSION);
        w.setPracticeSessionID(session.getPracticeSessionID());
        w.setPlayerID(session.getPlayerID());
        w.setZipResponse(true);

        OKUtil util = new OKUtil();
        try {
            util.sendGETRequest(getContext(), w, getActivity(), new OKUtil.OKListener() {
                @Override
                public void onResponse(final ResponseDTO response) {
                    SnappyPractice.addPracticeSessions(app, response.getPracticeSessionList(), new SnappyPractice.DBWriteListener() {
                        @Override
                        public void onDataWritten() {
                            setPracticeSessionList(response.getPracticeSessionList());
                        }

                        @Override
                        public void onError(String message) {
                            Util.showErrorToast(getActivity(), message);
                        }
                    });
                }

                @Override
                public void onError(String message) {

                }
            });
        } catch (OKHttpException e) {
            e.printStackTrace();
        }
    }

    SessionListListener mListener;
    String pageTitle = "Practice Sessions";

    @Override
    public String getPageTitle() {
        return pageTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SessionListListener) {
            mListener = (SessionListListener) context;
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
//        MonApp app = (MonApp)getActivity().getApplication();
//        RefWatcher refWatcher = app.getRefWatcher();
//        refWatcher.watch(this);
    }

    public interface SessionListListener {
        void onSessionClicked(PracticeSessionDTO session);

        void onNewSessionRequested();
    }

    public void setApp(MonApp app) {
        this.app = app;
    }


}
