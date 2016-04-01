package com.boha.malengagolf.library;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.fragments.MessageFragment;

/**
 * Created by aubreyM on 2014/05/28.
 */
public class MessageActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageFragment = (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);

        ResponseDTO resp = (ResponseDTO)getIntent().getSerializableExtra("response");
        int index = getIntent().getIntExtra("index", -1);
        messageFragment.setPlayerList(resp.getPlayers(), index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG, "################ onCreateOptionsMenu ");
        getMenuInflater().inflate(R.menu.invitation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_help) {

            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    MessageFragment messageFragment;
    private static final String LOG = "MessageActivity";




}