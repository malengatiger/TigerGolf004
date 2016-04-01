package com.boha.malengagolf.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.fragments.ImportFragment;
import com.boha.malengagolf.library.util.SharedUtil;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class ImportActivity extends AppCompatActivity implements ImportFragment.ImportFragmentListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = getApplicationContext();
        golfGroup = SharedUtil.getGolfGroup(ctx);

        importFragment = (ImportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.IMP_fragment);

        setTitle(ctx.getResources().getString(R.string.import_players));



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.boha.malengagolf.library.R.menu.import_players, menu);
        mMenu = menu;
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(com.boha.malengagolf.library.R.id.menu_help);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(com.boha.malengagolf.library.R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.menu_help) {

        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (playersImported) {
            Intent dd = new Intent();
            dd.putExtra("playersImported", playersImported);
            setResult(RESULT_OK, dd);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }
    @Override
    public void onPause() {
        overridePendingTransition(com.boha.malengagolf.library.R.anim.slide_in_left,
                com.boha.malengagolf.library.R.anim.slide_out_right);
        super.onPause();
    }
    Menu mMenu;
    Context ctx;
    GolfGroupDTO golfGroup;
    ImportFragment importFragment;
    boolean playersImported;
    static final String LOG = "ImportActivity";

    @Override
    public void onPlayersImported() {
        Log.e(LOG, "onPlayersImported - ************ set boolean true");
        playersImported = true;
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
