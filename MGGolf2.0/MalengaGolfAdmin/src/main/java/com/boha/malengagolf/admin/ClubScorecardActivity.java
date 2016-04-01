package com.boha.malengagolf.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments.ClubScorecardFragment;
import com.boha.malengagolf.library.data.ClubCourseDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.util.ToastUtil;

/**
 * Created by aubreyM on 2014/05/10.
 */
public class ClubScorecardActivity extends AppCompatActivity implements ClubScorecardFragment.ClubScorecardListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_scorecard);
        clubScorecardFragment = (ClubScorecardFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        club = (ClubDTO) getIntent().getSerializableExtra("club");
        clubScorecardFragment.setClub(club);

    }


    ClubScorecardFragment clubScorecardFragment;
    ClubDTO club;
    ClubCourseDTO clubCourse;
    @Override
    public void onScorecardUpdated(ClubCourseDTO cc) {
        this.clubCourse = cc;
        onBackPressed();

    }
    @Override
    public void onBackPressed() {
        Intent d = new Intent();
        d.putExtra("clubCourse", clubCourse);
        setResult(RESULT_OK, d);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.club_scorecard, menu);
        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.menu_help:
                ToastUtil.toast(getApplicationContext(), "Under Construction");
                return true;
            case R.id.menu_back:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
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
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    Menu mMenu;
}