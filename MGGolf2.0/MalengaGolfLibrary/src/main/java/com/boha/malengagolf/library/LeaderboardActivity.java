package com.boha.malengagolf.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.LeaderboardFragment;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

/**
 * Hosts fragment LeaderboardFragment
 * Created by aubreyM on 2014/04/13.
 */
public class LeaderboardActivity extends AppCompatActivity
        implements LeaderboardFragment.LeaderboardListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        MGApp app = (MGApp)getApplication();
        ctx = getApplicationContext();
        leaderboardFragment = (LeaderboardFragment)getSupportFragmentManager()
                .findFragmentById(R.id.LB_fragment);

        if (savedInstanceState != null) {
            Log.i(LOG, "refreshing from saved state.............what's in state?");
            response = (ResponseDTO)savedInstanceState.getSerializable("response");
            tournament = (TournamentDTO)savedInstanceState.getSerializable("tournament");
            return;
        }

        tournament = (TournamentDTO)getIntent().getSerializableExtra("tournament");

    }
    private void refreshLeaderBoard() {

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_LEADERBOARD);
        w.setTournamentID(tournament.getTournamentID());
        w.setTournamentType(tournament.getTournamentType());

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
        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,w,ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                setRefreshActionButtonState(false);
                if (!ErrorUtil.checkServerError(ctx,r)) {
                    return;
                }
                response = r;
                //leaderboardFragment.setLeaderBoardList(tournament,r.getLeaderBoardList());
            }

            @Override
            public void onVolleyError(VolleyError error) {
                setRefreshActionButtonState(false);
                ErrorUtil.showServerCommsError(ctx);
            }
        });

    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leaderboard, menu);
        mMenu = menu;
        if (response == null) {
            refreshLeaderBoard();
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
            refreshLeaderBoard();
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.back))) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        b.putSerializable("response",response);
        b.putSerializable("tournament", tournament);
        super.onSaveInstanceState(b);
    }
    static final String LOG = "LeaderboardActivity";
    ResponseDTO response;
    Menu mMenu;
    Context ctx;
    LeaderboardFragment leaderboardFragment;
    TournamentDTO tournament;
}