package com.boha.malengagolf.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.fragments.PlayerHistoryFragment;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/15.
 */
public class PlayerHistoryActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_history);
        ctx = getApplicationContext();
        playerHistoryFragment = (PlayerHistoryFragment)getSupportFragmentManager()
                .findFragmentById(R.id.PH_fragment);
        player = (PlayerDTO)getIntent().getSerializableExtra("player");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private void getPlayerHistory() {
        RequestDTO z = new RequestDTO();
        z.setRequestType(RequestDTO.GET_PLAYER_HISTORY);
        z.setPlayerID(player.getPlayerID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, z, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        leaderBoardList = response.getLeaderBoardList();
                        if (leaderBoardList == null) {
                            leaderBoardList = new ArrayList<LeaderBoardDTO>();
                        }
                        playerHistoryFragment.setLeaderBoardList(player, leaderBoardList);
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
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,z,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                leaderBoardList = response.getLeaderBoardList();
//                if (leaderBoardList == null) {
//                    leaderBoardList = new ArrayList<LeaderBoardDTO>();
//                }
//                playerHistoryFragment.setLeaderBoardList(player, leaderBoardList);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scorecard, menu);
        mMenu = menu;
        if (leaderBoardList == null) {
            getPlayerHistory();
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

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.menu_camera) {
            Intent t = new Intent(ctx, PictureActivity.class);
            t.putExtra("player", player);
            startActivity(t);
            return true;
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
    Menu mMenu;
    Context ctx;
    PlayerDTO player;
    PlayerHistoryFragment playerHistoryFragment;
    List<LeaderBoardDTO> leaderBoardList;
}