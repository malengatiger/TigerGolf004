package com.boha.malengagolf.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.TimeSheetFragment;
import com.boha.malengagolf.library.util.ToastUtil;

/**
 * Created by aubreyM on 2014/04/20.
 */
public class TimeSheetActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        ctx = getApplicationContext();
        timeSheetFragment = (TimeSheetFragment)
                getSupportFragmentManager().findFragmentById(
                        R.id.TEE_fragment);
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        response = (ResponseDTO) getIntent().getSerializableExtra("response");
        round = getIntent().getIntExtra("round", 1);
        timeSheetFragment.setData(tournament, response.getLeaderBoardList(), round);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timesheet, menu);
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
        if (item.getTitle() == null) {
            Log.e("","menuItem.getTitle() is NULL");
            return false;
        }
        if (item.getTitle().toString().equalsIgnoreCase(
                ctx.getResources().getString(R.string.send_timesheet))) {
            ToastUtil.toast(ctx, "Will be sending TimeSheet to all registered Players ...");
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
        finish();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    ResponseDTO response;
    TournamentDTO tournament;
    int round;
    TimeSheetFragment timeSheetFragment;
    Menu mMenu;
    Context ctx;
}