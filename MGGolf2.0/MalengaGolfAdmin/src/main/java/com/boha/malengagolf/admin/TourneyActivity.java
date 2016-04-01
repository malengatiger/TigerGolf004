package com.boha.malengagolf.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.TournamentFragment;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/11.
 */
public class TourneyActivity extends AppCompatActivity
        implements TournamentFragment.TournamentListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        ctx = getApplicationContext();
        int action = getIntent().getIntExtra("action", TournamentFragment.ADD_NEW);
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");

        ResponseDTO r = (ResponseDTO) getIntent().getSerializableExtra("clubs");

        tournamentFragment = (TournamentFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        Log.i(LOG, "##### onCreate: action: " + action);
        tournamentFragment.setAction(action);
        tournamentFragment.setTournament(tournament);
    }


    Context ctx;
    TournamentFragment tournamentFragment;
    List<ClubDTO> clubList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tourn_menu, menu);
        mMenu = menu;
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_help);
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

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_help:
                ToastUtil.toast(ctx, "Help is Under Construction");
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Log.w(LOG, "onBackPressed tournament: " + tournament);
        if (tournamentList != null && !tournamentList.isEmpty()) {
            Intent intent = new Intent();
            ResponseDTO w = new ResponseDTO();
            w.setTournaments(tournamentList);
            intent.putExtra("response", w);
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }


    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    Menu mMenu;
    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", locale);
    ResponseDTO response;
    TournamentDTO tournament;
    List<TournamentDTO> tournamentList = new ArrayList<TournamentDTO>();
    static final String LOG = "TourneyActivity";

    @Override
    public void onTournamentAdded(List<TournamentDTO> tournamentList) {
        this.tournamentList = tournamentList;
        onBackPressed();
    }

    @Override
    public void onTournamentUpdated(TournamentDTO tournament) {
        this.tournament = tournament;
        onBackPressed();
    }

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }
}