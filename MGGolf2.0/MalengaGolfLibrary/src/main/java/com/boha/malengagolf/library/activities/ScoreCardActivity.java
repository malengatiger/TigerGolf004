package com.boha.malengagolf.library.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.fragments.ScoreCardFragment;

/**
 * Created by aubreyM on 2014/04/15.
 */
public class ScoreCardActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);
        ctx = getApplicationContext();
        scoreCardFragment = (ScoreCardFragment)getSupportFragmentManager()
                .findFragmentById(R.id.PSC_fragment);
        leaderBoard = (LeaderBoardDTO)getIntent().getSerializableExtra("leaderBoard");
        if (leaderBoard == null) {
            throw new UnsupportedOperationException("Leaderboard passed in intent is null. Aborting!");
        }
        scoreCardFragment.setLeaderBoard(leaderBoard);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scorecard, menu);
        mMenu = menu;
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
        finish();
    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    Menu mMenu;
    Context ctx;
    LeaderBoardDTO leaderBoard;
    ScoreCardFragment scoreCardFragment;
}