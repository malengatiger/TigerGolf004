package com.boha.malengagolf.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.malengagolf.library.R;
import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.TournamentPlayerListFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

/**
 * Created by aubreyM on 2014/05/21.
 */
public class TournamentPlayerListActivity extends AppCompatActivity implements TournamentPlayerListFragment.TournamentPlayerListListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        ctx = getApplicationContext();
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        golfGroup = SharedUtil.getGolfGroup(ctx);
        tournamentPlayerListFragment = (TournamentPlayerListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        tournamentPlayerListFragment.setTournament(tournament);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tournament_players, menu);
        mMenu = menu;

        return true;
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
        if (item.getItemId() == R.id.menu_leaderboard) {
            Intent intent = new Intent(this, LeaderBoardPager.class);
            intent.putExtra("tournament", tournament);
            if (SharedUtil.getAdministrator(ctx) != null)
                intent.putExtra("administrator", SharedUtil.getAdministrator(ctx));
            if (SharedUtil.getScorer(ctx) != null)
                intent.putExtra("scorer", SharedUtil.getScorer(ctx));
            if (SharedUtil.getPlayer(ctx) != null)
                intent.putExtra("player", SharedUtil.getPlayer(ctx));
            if (SharedUtil.getAppUser(ctx) != null)
                intent.putExtra("appUser", SharedUtil.getAppUser(ctx));
            startActivity(intent);

            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

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

    TournamentPlayerListFragment tournamentPlayerListFragment;
    TournamentDTO tournament;
    GolfGroupDTO golfGroup;
    Context ctx;
    Menu mMenu;

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }

    @Override
    public void onScoringRequested(LeaderBoardDTO l) {
        Intent w = new Intent(ctx, ScoringByHoleActivity.class);
        w.putExtra("leaderBoard", l);
        w.putExtra("tournament", tournament);
        startActivityForResult(w, REQUEST_SCORING);
    }

    static final int REQUEST_SCORING = 933;
    static final String LOG = TournamentPlayerListActivity.class.getName();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG, "****** onActivityResult code: " + requestCode + " result: " + resultCode);
        switch (requestCode) {
            case REQUEST_SCORING:
                if (resultCode == RESULT_OK) {
                    Log.w(LOG, "...... about to refresh tournamentPlayerListFragment");
                    ResponseDTO w = (ResponseDTO) data.getSerializableExtra("response");
                    tournamentPlayerListFragment.refresh(w.getLeaderBoardList());
                }
                break;
        }
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

}