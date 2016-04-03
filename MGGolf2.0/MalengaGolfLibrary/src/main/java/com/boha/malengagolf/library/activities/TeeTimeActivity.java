package com.boha.malengagolf.library.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.TeeTimeRequestFragment;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.List;

/**
 * Hosts fragment LeaderboardFragment
 * Created by aubreyM on 2014/04/13.
 */
public class TeeTimeActivity extends AppCompatActivity
        implements TeeTimeRequestFragment.TeeTimeFragmentListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teetime);
        ctx = getApplicationContext();
        teeTimeRequestFragment = (TeeTimeRequestFragment)getSupportFragmentManager()
                .findFragmentById(R.id.TEE_fragment);

        if (savedInstanceState != null) {
            Log.i(LOG, "refreshing from saved state");
            tournament = (TournamentDTO)savedInstanceState.getSerializable("tournament");
            return;
        }

        tournament = (TournamentDTO)getIntent().getSerializableExtra("tournament");
        leaderBoard = (LeaderBoardDTO)getIntent().getSerializableExtra("leaderBoard");
        currentRound = getIntent().getIntExtra("currentRound", 1);
        teeTimeRequestFragment.setCurrentRound(currentRound);
        teeTimeRequestFragment.setLeaderBoardFromScoringFragment(leaderBoard);
        setTitle(tournament.getTourneyName());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private int currentRound;
    private LeaderBoardDTO leaderBoard;
    private void refreshPlayerList() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TOURNAMENT_PLAYERS);
        w.setTournamentID(tournament.getTournamentID());

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
                        if (!ErrorUtil.checkServerError(ctx,r)) {
                            return;
                        }
                        response = r;
                        teeTimeRequestFragment.setLeaderBoardList(tournament,response.getLeaderBoardList());

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
                        setRefreshActionButtonState(false);
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,w,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx,r)) {
//                    return;
//                }
//                response = r;
//                teeTimeRequestFragment.setLeaderBoardList(tournament,response.getLeaderBoardList());
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });

    }

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }

    List<LeaderBoardDTO> leaderBoardListFromFragment;
    @Override
    public void onTeeTimesCompleted(List<LeaderBoardDTO> leaderBoardList) {
        leaderBoardListFromFragment = leaderBoardList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leaderboard, menu);
        mMenu = menu;
        if (response == null) {
            refreshPlayerList();
        }
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.refresh))) {
            refreshPlayerList();
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.back))) {
            onBackPressed();
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (leaderBoardListFromFragment != null) {
            Intent c = new Intent();
            ResponseDTO response = new ResponseDTO();
            response.setLeaderBoardList(leaderBoardListFromFragment);
            c.putExtra("leaderBoardList", response);
            setResult(RESULT_OK, c);
        }
        finish();
        super.onBackPressed();
    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        b.putSerializable("tournament", tournament);
        super.onSaveInstanceState(b);
    }
    static final String LOG = "TeeTimeActivity";
    ResponseDTO response;
    Menu mMenu;
    Context ctx;

    TeeTimeRequestFragment teeTimeRequestFragment;
    TournamentDTO tournament;
}