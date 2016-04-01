package com.boha.malengagolf.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.ScoringByHoleFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.util.List;

/**
 * Hosts fragment ScoringByHoleFragment
 * Created by aubreyM on 2014/04/12.
 */
public class ScoringByHoleActivity extends AppCompatActivity
        implements ScoringByHoleFragment.ScoringByHoleListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.activity_sbh);
        scoringByHoleFragment = (ScoringByHoleFragment) getSupportFragmentManager()
                .findFragmentById(R.id.SBH_fragment);
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        leaderBoard = (LeaderBoardDTO) getIntent().getSerializableExtra("leaderBoard");
        leaderBoard.setTournamentID(tournament.getTournamentID());
        if (leaderBoard == null)
            throw new UnsupportedOperationException("Leaderboard is null. Why da fuck?");
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administrator = SharedUtil.getAdministrator(ctx);

        scoringByHoleFragment.setGolfGroup(golfGroup);
        scoringByHoleFragment.setAdministrator(administrator);
        scoringByHoleFragment.setTournament(tournament);
        scoringByHoleFragment.setTourneyPlayerScore(leaderBoard);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (carrier == null) {
            Log.e(LOG, "onBackPressed, carrier is NULL ...");
            setResult(Activity.RESULT_CANCELED);
        } else {
            Log.w(LOG, "## onBackPressed, carrier is LOADED, leaderBoardList size: " + carrier.getLeaderBoardList().size());
            Intent intent = new Intent();
            intent.putExtra("response", carrier);
            setResult(Activity.RESULT_OK, intent);
        }
        //
        Log.i(LOG, "onBackPressed - about to finish()");
        finish();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG, "onResume ...nothing to be done");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        super.onSaveInstanceState(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scoring_menu, menu);
        mMenu = menu;
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_camera);
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


        if (item.getItemId() == R.id.menu_camera) {
            Intent z = new Intent(ctx, PictureActivity.class);
            z.putExtra("golfGroup", golfGroup);
            z.putExtra("tournament", tournament);
            startActivity(z);
            return true;
        }

        if (item.getItemId() == R.id.menu_help) {
            ToastUtil.toast(ctx, "Under Construction");
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    Menu mMenu;

    static final String LOG = "ScoringByHoleActivity";
    Vibrator vb;
    TournamentDTO tournament;
    LeaderBoardDTO leaderBoard;
    GolfGroupDTO golfGroup;
    AdministratorDTO administrator;

    ScoringByHoleFragment scoringByHoleFragment;
    Context ctx;

    @Override
    public void setBusy() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRefreshActionButtonState(true);
            }
        });
    }

    @Override
    public void setNotBusy() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRefreshActionButtonState(false);
            }
        });

    }

    ResponseDTO carrier;

    @Override
    public void scoringSubmitted(List<LeaderBoardDTO> lbList) {
        if (lbList == null) {
            Log.e(LOG, "*** scoringSubmitted leaderBoardList is null");
            onBackPressed();
            return;
        } else {
            Log.w(LOG, "*** scoringSubmitted leaderBoardList is OK, size: " + lbList.size());
        }
        carrier = new ResponseDTO();
        carrier.setLeaderBoardList(lbList);
        onBackPressed();

    }

    @Override
    public void onError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRefreshActionButtonState(false);
                ToastUtil.errorToast(ctx, message);
            }
        });

    }


    List<LeaderBoardDTO> updatedTeeList;

}