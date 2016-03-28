package com.boha.golfpractice.golfer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.boha.golfpractice.golfer.R;
import com.boha.golfpractice.golfer.dto.PlayerDTO;
import com.boha.golfpractice.golfer.fragments.SessionSummaryFragment;
import com.boha.golfpractice.golfer.util.Util;

public class SessionSummaryActivity extends AppCompatActivity {

    SessionSummaryFragment sessionSummaryFragment;
    PlayerDTO player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);
        player = (PlayerDTO) getIntent().getSerializableExtra("player");
        setFragment();

        Util.setCustomActionBar(getApplicationContext(),
                getSupportActionBar(),"Player Summary","",
                ContextCompat.getDrawable(getApplicationContext(),R.drawable.golfball48));

    }
    private void setFragment () {
        sessionSummaryFragment = new SessionSummaryFragment();
        sessionSummaryFragment.setApp((MonApp)getApplication());
        sessionSummaryFragment.setPlayer(player);
        sessionSummaryFragment.setType(SessionSummaryFragment.FROM_PLAYER);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);

        ft.add(R.id.frameLayout, sessionSummaryFragment);
        ft.commit();
    }

}
