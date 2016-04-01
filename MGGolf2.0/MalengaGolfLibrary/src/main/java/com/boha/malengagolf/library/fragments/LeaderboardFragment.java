package com.boha.malengagolf.library.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.boha.malengagolf.library.LeaderboardAdapter;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.ScoreCardActivity;
import com.boha.malengagolf.library.adapters.LeaderboardOneRoundAdapter;
import com.boha.malengagolf.library.data.ClubCourseDTO;
import com.boha.malengagolf.library.data.LeaderBoardCarrierDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.CompleteRounds;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.LeaderBoardPage;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class LeaderboardFragment extends Fragment implements LeaderBoardPage {

    public interface LeaderboardListener {
        public void onRequestRefresh();

        public void setBusy();

        public void setNotBusy();

        public void onScoreCardRequested(LeaderBoardDTO leaderBoard);
    }

    LeaderboardListener leaderboardListener;
    LeaderBoardCarrierDTO leaderBoardCarrier;
    FragmentActivity act;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof LeaderboardListener) {
            leaderboardListener = (LeaderboardListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement LeaderboardListener");
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
        Log.i(LOG, "onCreateView...................................");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_leaderboard, container, false);
        setFields();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d(LOG, "----------------onCreateView bundle has data ...");
            tournament = (TournamentDTO) bundle.getSerializable("tournament");
            leaderBoardCarrier = (LeaderBoardCarrierDTO) bundle.getSerializable("carrier");
            setLeaderBoardList(tournament, leaderBoardCarrier);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.LB_list);
        txTourneyName = (TextView) view.findViewById(R.id.LB_tournName);
        txtClubName = (TextView) view.findViewById(R.id.LB_clubName);
        txtTimeStamp = (TextView) view.findViewById(R.id.LB_timeStamp);
        txtPlayerCount = (TextView) view.findViewById(R.id.LB_count);
        txtAverage = (TextView) view.findViewById(R.id.LB_average);
        txtLive = (TextView) view.findViewById(R.id.LB_live);
        txtComplete = (TextView) view.findViewById(R.id.LB_complete);
        mSwitch = (Switch)view.findViewById(R.id.LB_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    hidePics = true;
                } else {
                    hidePics = false;
                }
                setList();
            }
        });
    }

    List<LeaderBoardDTO> goodList;

    private void setLeaderBoardLive() {


//        for (LeaderBoardDTO x : leaderBoardList) {
//            CompleteRounds.markScoringCompletion(x);
//        }
//        for (LeaderBoardDTO x : leaderBoardList) {
//            int cnt = 0;
//            for (TourneyScoreByRoundDTO v : x.getTourneyScoreByRoundList()) {
//                if (v.getScoringComplete() == 0) {
//                    cnt++;
//                }
//            }
//            if (cnt == 0) {
//                x.setScoringComplete(true);
//            }
//        }
        int cnt = 0;
        for (LeaderBoardDTO x : leaderBoardList) {
            if (x.isScoringComplete()) {
                cnt++;
            }
        }

        if (leaderBoardList.size() == (cnt)) {
            txtComplete.setVisibility(View.VISIBLE);
            txtLive.setVisibility(View.GONE);
            txtComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCloseConfirmDialog();
                }
            });
        } else {
            txtComplete.setVisibility(View.GONE);
            txtLive.setVisibility(View.VISIBLE);
        }
        if (tournament.getClosedForScoringFlag() > 0) {
            txtComplete.setText(ctx.getResources().getString(R.string.closed));
            txtComplete.setBackgroundColor(ctx.getResources().getColor(R.color.black));
            return;
        }
    }

    private void showWithdrawConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.withdraw))
                .setMessage(ctx.getResources().getString(R.string.withdraw_text)
                        + "\n\n" + leaderBoard.getPlayer().getFullName())
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        withdrawPlayer();
                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void withdrawPlayer() {

        leaderBoardList.remove(selectedIndex);
        txtPlayerCount.setText("" + leaderBoardList.size());
        if (leaderboardAdapter != null) {
            leaderboardAdapter.notifyDataSetChanged();
        }
        if (leaderboardOneRoundAdapter != null) {
            leaderboardOneRoundAdapter.notifyDataSetChanged();
        }
        setLeaderBoardLive();

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.WITHDRAW_PLAYER);
        w.setTournamentID(tournament.getTournamentID());
        w.setLeaderBoardID(leaderBoard.getLeaderBoardID());
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        leaderboardListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.withdrawn));
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
               act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                leaderboardListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.withdrawn));
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                leaderboardListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }


    private void showCloseConfirmDialog() {
        if (tournament.getClosedForScoringFlag() == 1) {
            txtComplete.setText(ctx.getResources().getString(R.string.complete));
            txtComplete.setBackgroundColor(ctx.getResources().getColor(R.color.green));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.close_leaderboard))
                .setMessage(ctx.getResources().getString(R.string.close_leaderboard_text))
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tournament.setClosedForScoringFlag(1);
                        closeLeaderBoard();
                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void closeLeaderBoard() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.CLOSE_LEADERBORD);
        w.setTournamentID(tournament.getTournamentID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        leaderboardListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }

                        txtComplete.setText(ctx.getResources().getString(R.string.closed));
                        txtComplete.setBackgroundColor(ctx.getResources().getColor(R.color.black));

                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                leaderboardListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//
//                txtComplete.setText(ctx.getResources().getString(R.string.closed));
//                txtComplete.setBackgroundColor(ctx.getResources().getColor(R.color.black));
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                leaderboardListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    boolean hidePics = false;
    int selectedIndex;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMMM yyyy HH:mm", loc);

    public void updateSingleScore(LeaderBoardDTO lb) {
        List<LeaderBoardDTO> bList = new ArrayList<LeaderBoardDTO>();
        boolean needsUpdate = false;
        int index = 0, holeCount = 0;

        for (TourneyScoreByRoundDTO tsb: lb.getTourneyScoreByRoundList()) {
            if (tsb.getScoringComplete() > 0) {
                holeCount = 0;
                continue;
            }
            if (tsb.getScore1() > 0) holeCount++;
            if (tsb.getScore2() > 0) holeCount++;
            if (tsb.getScore3() > 0) holeCount++;
            if (tsb.getScore4() > 0) holeCount++;
            if (tsb.getScore5() > 0) holeCount++;
            if (tsb.getScore6() > 0) holeCount++;
            if (tsb.getScore7() > 0) holeCount++;
            if (tsb.getScore8() > 0) holeCount++;
            if (tsb.getScore9() > 0) holeCount++;
            if (tsb.getScore10() > 0) holeCount++;
            if (tsb.getScore11() > 0) holeCount++;
            if (tsb.getScore12() > 0) holeCount++;
            if (tsb.getScore13() > 0) holeCount++;
            if (tsb.getScore14() > 0) holeCount++;
            if (tsb.getScore15() > 0) holeCount++;
            if (tsb.getScore16() > 0) holeCount++;
            if (tsb.getScore17() > 0) holeCount++;
            if (tsb.getScore18() > 0) holeCount++;
        }
        lb.setLastHole(holeCount);

        for (LeaderBoardDTO dto: leaderBoardList) {
            if (dto.getLeaderBoardID() == lb.getLeaderBoardID()) {
                bList.add(lb);
                needsUpdate = true;
            } else {
                bList.add(dto);
            }
        }

        if (needsUpdate) {
            leaderBoardList = bList;
            if (tournament.getTournamentType() == TournamentDTO.STROKE_PLAY_INDIVIDUAL) {
                calculateLeaderboard(leaderBoardList);
                setList();
            }
            if (tournament.getTournamentType() == TournamentDTO.STABLEFORD_INDIVIDUAL) {
                calculateLeaderBoardPoints(leaderBoardList);
                setList();
            }

        }
        for (LeaderBoardDTO dto: leaderBoardList) {
            if (dto.getLeaderBoardID() == lb.getLeaderBoardID()) {
                selectedIndex = index;
                Log.e(LOG,"&&&&&&&&&& selected index: " + selectedIndex);
                break;
            }
            index++;
        }

        listView.setSelection(index);
    }
    public void setList() {

        goodList = new ArrayList<LeaderBoardDTO>();
        List<LeaderBoardDTO> bList = new ArrayList<LeaderBoardDTO>();
        for (LeaderBoardDTO x : leaderBoardList) {
            if (x.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
                bList.add(x);
            } else {
                goodList.add(x);
            }
        }
        setPositions(goodList);
        goodList.addAll(bList);
        leaderBoardList = null;
        leaderBoardList = goodList;
        if (!leaderBoardList.isEmpty()) {
            long time = leaderBoardList.get(0).getTimeStamp();
            if (time == 0) {
                time = new Date().getTime();
            }
            if (tournament.getClosedForScoringFlag() == 0) {
                txtTimeStamp.setText(sdf.format(new Date(time)));
            } else {
                txtTimeStamp.setText("Tournament Closed");
                txtTimeStamp.setTextColor(ctx.getResources().getColor(R.color.black));
            }
        }
        setLeaderBoardLive();



        if (tournament.getGolfRounds() == 1) {
            leaderboardOneRoundAdapter = new LeaderboardOneRoundAdapter(ctx,
                    R.layout.leaderboard_one_round,
                    leaderBoardList,
                    SharedUtil.getGolfGroup(ctx).getGolfGroupID(),hidePics);
            listView.setAdapter(leaderboardOneRoundAdapter);
        } else {
            leaderboardAdapter = new LeaderboardAdapter(ctx,
                    R.layout.leaderboard_item,
                    leaderBoardList,
                    tournament.getGolfRounds(), tournament.getPar(), hidePics,
                    new LeaderboardAdapter.LeaderBoardListener() {
                        @Override
                        public void onScrollToItem(int index) {
                            SharedUtil.setScrollIndex(ctx, index);
                        }
                    }
            );
            listView.setAdapter(leaderboardAdapter);

        }
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
                startScorecard();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
                selectedIndex = i;
                Log.i(LOG, "leaderboard selected onItemLongClick: " + leaderBoard.getPlayer().getFullName());
                return false;
            }
        });
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
                selectedIndex = i;
                Log.i(LOG, "leaderboard selected onItemSelected: " + leaderBoard.getPlayer().getFullName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //listView.setSelection(selectedIndex);
        //listView.smoothScrollToPosition(selectedIndex);
        if (tournament.getClosedForScoringFlag() == 0) {
            final ObjectAnimator an = ObjectAnimator.ofFloat(txtTimeStamp, View.SCALE_X, 0);
            an.setRepeatCount(1);
            an.setDuration(500);
            an.setRepeatMode(ValueAnimator.REVERSE);
            an.start();

            final ObjectAnimator an2 = ObjectAnimator.ofFloat(txtLive, View.SCALE_Y, 0);
            an2.setRepeatCount(1);
            an2.setDuration(500);
            an2.setRepeatMode(ValueAnimator.REVERSE);
            an2.setStartDelay(500);
            an2.start();
        }
    }

    private int scrollIndex;

    private void setPositions(List<LeaderBoardDTO> list) {
        int mPosition = 1;
        int running = 1, score = 999;
        switch (tournament.getTournamentType()) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                Log.d(LOG, "####------------- setPositions STABLEFORD_INDIVIDUAL");
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
                Log.d(LOG, "####------------- setPositions STROKE_PLAY_INDIVIDUAL");
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

    public void setLeaderBoardList(TournamentDTO tournament, LeaderBoardCarrierDTO carrier) {
        Log.d(LOG, "########## setLeaderBoardList from carrier....");
        this.tournament = tournament;
        this.leaderBoardCarrier = carrier;
        txTourneyName.setText(tournament.getTourneyName());
        txtClubName.setText(tournament.getClubName());

        this.leaderBoardList = carrier.getLeaderBoardList();
        txtPlayerCount.setText("" + leaderBoardList.size());
        getAverage();
        int cnt = 0;
        for (LeaderBoardDTO x : leaderBoardList) {
            if (x.getAgeGroup() == null) cnt++;
        }
        if (leaderBoardList.size() == cnt) { //combined leaderboard

        }
        positionWinner();
        setList();

    }

    List<CompleteRounds> completeRoundsList;

    private void getAverage() {
        completeRoundsList = CompleteRounds.getCompletedRounds(leaderBoardList);
        if (!completeRoundsList.isEmpty()) {
            double t = 0;
            for (CompleteRounds pav : completeRoundsList) {
                t += pav.getAverage();
            }
            t = t / completeRoundsList.size();
            txtAverage.setText(df.format(t));
        } else {
            txtAverage.setText("N/A");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.leaderboard_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (leaderBoard == null) {
            throw new UnsupportedOperationException("Leaderboard is null onCreateContextMenu. aborting...");
        }
        menu.setHeaderTitle(leaderBoard.getPlayer().getFullName());
        menu.setHeaderIcon(R.drawable.winner_red32);

        if (leaderBoard.getPosition() == 1 && leaderBoard.isTied()) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());

        int winnerFlag = 0;
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.winner_countout))) {
            winnerFlag = LeaderBoardDTO.WINNER_BY_COUNT_OUT;
            updateWinner(winnerFlag);
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.winner_playoff))) {
            winnerFlag = LeaderBoardDTO.WINNER_BY_PLAYOFF;
            updateWinner(winnerFlag);
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.view_scorecard))) {
            startScorecard();
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.withdraw))) {
            showWithdrawConfirmDialog();
        }

        return true;

    }

    private void positionWinner() {
        List<LeaderBoardDTO> gList = new ArrayList<LeaderBoardDTO>();
        int index = 0;
        for (LeaderBoardDTO x : leaderBoardList) {
            if (x.getWinnerFlag() > 0 && x.isTied()) {
                gList.add(x);
                break;
            }
            index++;
        }
        if (gList.size() > 0) {
            leaderBoardList.remove(index);
            gList.addAll(leaderBoardList);
            leaderBoardList = gList;
        }
    }

    private void updateWinner(int winnerFlag) {

        //TODO set winner flag visually and put winner top of list
        leaderBoard.setWinnerFlag(winnerFlag);
        for (LeaderBoardDTO x : leaderBoardList) {
            x.setWinnerFlag(0);
        }
        List<LeaderBoardDTO> gList = new ArrayList<LeaderBoardDTO>();
        int index = 0;
        for (LeaderBoardDTO x : leaderBoardList) {
            if (leaderBoard.getLeaderBoardID() == x.getLeaderBoardID()) {
                x.setWinnerFlag(winnerFlag);
                gList.add(x);
                break;
            }
            index++;
        }
        leaderBoardList.remove(index);
        gList.addAll(leaderBoardList);
        leaderBoardList = gList;
        setList();

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_WINNER_FLAG);
        w.setLeaderBoardID(leaderBoard.getLeaderBoardID());
        w.setWinnerFlag(winnerFlag);
        w.setZippedResponse(false);
        leaderboardListener.setBusy();
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }

                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.winner_updated));
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardListener.setNotBusy();
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                leaderboardListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//
//                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.winner_updated));
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                leaderboardListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void startScorecard() {
        Log.e(LOG, "startScorecard: useDialogForScorecard = "
                + useDialogForScorecard);
        if (!useDialogForScorecard) {
            Intent s = new Intent(ctx, ScoreCardActivity.class);
            s.putExtra("leaderBoard", leaderBoard);
            startActivity(s);
        } else {
            leaderboardListener.onScoreCardRequested(leaderBoard);

        }
    }

    static final DecimalFormat df = new DecimalFormat("###,###.00");
    double overallAverage;
    LeaderboardAdapter leaderboardAdapter;
    LeaderboardOneRoundAdapter leaderboardOneRoundAdapter;
    FragmentManager fragmentManager;
    ListView listView;
    TextView txTourneyName, txtClubName, txtPlayerCount,
            txtTimeStamp, txtAverage, txtLive, txtComplete;
    static final String LOG = "LeaderboardFragment";
    Context ctx;
    View view;
    TournamentDTO tournament;
    ResponseDTO response;
    List<LeaderBoardDTO> leaderBoardList;
    LeaderBoardDTO leaderBoard;
    boolean useDialogForScorecard;
    Switch mSwitch;


    public void setUseDialogForScorecard(boolean useDialogForScorecard) {
        this.useDialogForScorecard = useDialogForScorecard;
        Log.d(LOG, "setUseDialogForScorecard " + useDialogForScorecard);
    }

    public LeaderBoardCarrierDTO getLeaderBoardCarrier() {
        return leaderBoardCarrier;
    }
    private void calculateLeaderBoardPoints(List<LeaderBoardDTO> list) {

        for (LeaderBoardDTO b: list) {
            b.setSortType(LeaderBoardDTO.SORT_POINTS);
        }
        Collections.sort(list);
        //set positions
        HashMap<Integer, Integer> map = new HashMap<>();
        int pos = 1;
        for (LeaderBoardDTO board : list) {
            if (map.containsKey(board.getTotalPoints())) {
                continue;
            }
            map.put(board.getTotalPoints(), pos);
            pos++;
        }

        for (LeaderBoardDTO b : list) {
            if (map.containsKey(b.getTotalPoints())) {
                b.setPosition(map.get(b.getTotalPoints()));
            }
        }

        //check for tied
        HashMap<Integer, Integer> map2 = new HashMap<>();
        HashMap<Integer, Integer> tied = new HashMap<>();

        for (LeaderBoardDTO d : list) {

            if (map2.containsKey(d.getTotalPoints())) {
                tied.put(d.getTotalPoints(), d.getTotalPoints());
            } else {
                map2.put(d.getTotalPoints(), d.getTotalPoints());
            }

        }
        for (Map.Entry pairs : tied.entrySet()) {
            for (LeaderBoardDTO d : list) {
                int a = (Integer) pairs.getKey();
                if (d.getTotalPoints() == a) {
                    d.setTied(true);
                }
            }
        }
        setPositions(list);
    }
    private void calculateLeaderboard(List<LeaderBoardDTO> list) {

        for (LeaderBoardDTO lb : list) {
            setParStatus(lb);
            lb.setWinnerFlag(0);
            lb.setSortType(0);
        }
        Collections.sort(list);
        //set positions
        HashMap<Integer, Integer> map = new HashMap<>();
        int pos = 1;
        for (LeaderBoardDTO board : list) {
            if (map.containsKey(board.getParStatus())) {
                continue;
            }
            map.put(board.getParStatus(), pos);
            pos++;
        }

        for (LeaderBoardDTO b : list) {
            if (map.containsKey(b.getParStatus())) {
                b.setPosition(map.get(b.getParStatus()));
            }
        }

        //check for tied
        HashMap<Integer, Integer> map2 = new HashMap<>();
        HashMap<Integer, Integer> tied = new HashMap<>();

        for (LeaderBoardDTO d : list) {
            if (d.getTotalScore() == 0) {
                continue;
            }
            if (map2.containsKey(d.getParStatus())) {
                tied.put(d.getParStatus(), d.getParStatus());
            } else {
                map2.put(d.getParStatus(), d.getParStatus());
            }

        }
        for (Map.Entry pairs : tied.entrySet()) {
            for (LeaderBoardDTO d : list) {
                int a = (Integer) pairs.getKey();
                if (d.getParStatus() == a) {
                    d.setTied(true);
                }
            }
        }
        setPositions(list);
    }
    private void setParStatus(LeaderBoardDTO lb) {
        int parStatus = 0;
        int cnt = 0;
        for (TourneyScoreByRoundDTO r : lb.getTourneyScoreByRoundList()) {
            setCurrentRoundStatus(lb, r);
            ClubCourseDTO cc = r.getClubCourse();
            if (r.getScore1() > 0) {
                parStatus += cc.getParHole1() - r.getScore1();
                lb.setLastHole(1);
                cnt++;
            }
            if (r.getScore2() > 0) {
                parStatus += cc.getParHole2() - r.getScore2();
                lb.setLastHole(2);
                cnt++;
            }
            if (r.getScore3() > 0) {
                parStatus += cc.getParHole3() - r.getScore3();
                lb.setLastHole(3);
                cnt++;
            }
            if (r.getScore4() > 0) {
                parStatus += cc.getParHole4() - r.getScore4();
                lb.setLastHole(4);
                cnt++;
            }
            if (r.getScore5() > 0) {
                parStatus += cc.getParHole5() - r.getScore5();
                lb.setLastHole(5);
                cnt++;
            }
            if (r.getScore6() > 0) {
                parStatus += cc.getParHole6() - r.getScore6();
                lb.setLastHole(6);
                cnt++;
            }
            if (r.getScore7() > 0) {
                parStatus += cc.getParHole7() - r.getScore7();
                lb.setLastHole(7);
                cnt++;
            }
            if (r.getScore8() > 0) {
                parStatus += cc.getParHole8() - r.getScore8();
                lb.setLastHole(8);
                cnt++;
            }
            if (r.getScore9() > 0) {
                parStatus += cc.getParHole9() - r.getScore9();
                lb.setLastHole(9);
                cnt++;
            }
            if (r.getScore10() > 0) {
                parStatus += cc.getParHole10() - r.getScore10();
                lb.setLastHole(10);
                cnt++;
            }
            if (r.getScore11() > 0) {
                parStatus += cc.getParHole11() - r.getScore11();
                lb.setLastHole(11);
                cnt++;
            }
            if (r.getScore12() > 0) {
                parStatus += cc.getParHole12() - r.getScore12();
                lb.setLastHole(12);
                cnt++;
            }
            if (r.getScore13() > 0) {
                parStatus += cc.getParHole13() - r.getScore13();
                lb.setLastHole(13);
                cnt++;
            }
            if (r.getScore14() > 0) {
                parStatus += cc.getParHole14() - r.getScore14();
                lb.setLastHole(14);
                cnt++;
            }
            if (r.getScore15() > 0) {
                parStatus += cc.getParHole15() - r.getScore15();
                lb.setLastHole(15);
                cnt++;
            }
            if (r.getScore16() > 0) {
                parStatus += cc.getParHole16() - r.getScore16();
                lb.setLastHole(16);
                cnt++;
            }
            if (r.getScore17() > 0) {
                parStatus += cc.getParHole17() - r.getScore17();
                lb.setLastHole(17);
                cnt++;
            }
            if (r.getScore18() > 0) {
                parStatus += cc.getParHole18() - r.getScore18();
                lb.setLastHole(18);
                cnt++;
            }
            if (cnt == 0) {
                lb.setParStatus(LeaderBoardDTO.NO_PAR_STATUS);
            } else {
                lb.setParStatus(parStatus);
            }

        }
    }
    private void setCurrentRoundStatus(LeaderBoardDTO lb, TourneyScoreByRoundDTO r) {

        int cnt = 0;
        ClubCourseDTO cc = r.getClubCourse();
        if (cc == null) throw new UnsupportedOperationException("club course is null");
        int parStatus = 0;
        if (r.getScore1() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole1() - r.getScore1();
        }
        if (r.getScore2() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole2() - r.getScore2();
        }
        if (r.getScore3() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole3() - r.getScore3();
        }
        if (r.getScore4() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole4() - r.getScore4();
        }
        if (r.getScore5() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole5() - r.getScore5();
        }
        if (r.getScore6() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole6() - r.getScore6();
        }
        if (r.getScore7() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole7() - r.getScore7();
        }
        if (r.getScore8() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole8() - r.getScore8();
        }
        if (r.getScore9() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole9() - r.getScore9();
        }
        if (r.getScore10() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole10() - r.getScore10();
        }
        if (r.getScore11() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole11() - r.getScore11();
        }
        if (r.getScore12() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole12() - r.getScore12();
        }
        if (r.getScore13() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole13() - r.getScore13();
        }
        if (r.getScore14() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole14() - r.getScore14();
        }
        if (r.getScore15() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole15() - r.getScore15();
        }
        if (r.getScore16() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole16() - r.getScore16();
        }
        if (r.getScore17() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole17() - r.getScore17();
        }
        if (r.getScore18() == 0) {
            cnt++;
        } else {
            parStatus += cc.getParHole18() - r.getScore18();
        }
        if (cnt < 18) {
            lb.setCurrentRoundStatus(parStatus);
        }

    }
}
