package com.boha.malengagolf.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.ToastUtil;

/**
 * Created by aubreyM on 2014/05/18.
 */
public class AppInvitationActivity extends AppCompatActivity implements AppInvitationFragment.AppInvitationListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ctx = getApplicationContext();

        email = getIntent().getStringExtra("email");
        pin = getIntent().getStringExtra("pin");
        member = getIntent().getStringExtra("member");
        type = getIntent().getIntExtra("type",0);
        appInvitationFragment = (AppInvitationFragment)
                getSupportFragmentManager().findFragmentById(R.id.INV_fragment);
        appInvitationFragment.setData(email, pin, member, type);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invitation, menu);
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

    Menu mMenu;
    String member;
    int type;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        if (item.getTitle().toString().equalsIgnoreCase(ctx.getResources().getString(R.string.back))) {
//            finish();
//            return true;
//        }
        if (item.getItemId() == R.id.menu_help) {
            ToastUtil.toast(ctx, "Under Construction");
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    AppInvitationFragment appInvitationFragment;
    Context ctx;
    String email, pin;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
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