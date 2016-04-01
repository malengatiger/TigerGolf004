package com.boha.golfpractice.player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.boha.golfpractice.player.R;
import com.boha.golfpractice.player.dto.ClubDTO;
import com.boha.golfpractice.player.dto.PracticeSessionDTO;
import com.boha.golfpractice.player.dto.ShotShapeDTO;
import com.boha.golfpractice.player.fragments.HoleStatFragment;
import com.boha.golfpractice.player.util.SnappyPractice;
import com.boha.golfpractice.player.util.ThemeChooser;
import com.boha.golfpractice.player.util.Util;

public class SessionControllerActivity extends AppCompatActivity implements HoleStatFragment.HoleStatListener {

    HoleStatFragment holeStatFragment;
    FrameLayout frameLayout;
    PracticeSessionDTO practiceSession;
    static final String LOG = SessionControllerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChooser.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hole_stat);
        practiceSession = (PracticeSessionDTO) getIntent().getSerializableExtra("session");
        addFragment();


        String subTitle = "Practice Session";
        Util.setCustomActionBar(getApplicationContext(),
                getSupportActionBar(), practiceSession.getGolfCourseName(), subTitle,
                ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.golfball48));

    }

    private void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        holeStatFragment = HoleStatFragment.newInstance(practiceSession);
        holeStatFragment.setApp((MonApp) getApplication());

        ft.add(R.id.frameLayout, holeStatFragment);
        ft.commit();
    }

    @Override
    public void onHoleStatUpdated(PracticeSessionDTO session) {
        session.setNeedsUpload(true);
        practiceSession = session;
        isUpdated = true;

        SnappyPractice.addCurrentPracticeSession((MonApp) getApplication(), session, new SnappyPractice.DBWriteListener() {
            @Override
            public void onDataWritten() {
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    static final int
            TEE_SHOT_CLUB_REQUIRED = 621,
            GREEN_SHOT_REQUIRED = 732;

    @Override
    public void onTeeClubSelectionRequired() {
        Intent m = new Intent(getApplicationContext(),ClubAndShapeActivity.class);
        startActivityForResult(m,TEE_SHOT_CLUB_REQUIRED);
    }

    @Override
    public void onGreenClubSelectionRequired() {
        Intent m = new Intent(getApplicationContext(),ClubAndShapeActivity.class);
        startActivityForResult(m,GREEN_SHOT_REQUIRED);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case TEE_SHOT_CLUB_REQUIRED:
                if (resCode == RESULT_OK) {
                    ClubDTO club = (ClubDTO)data.getSerializableExtra("club");
                    ShotShapeDTO ss = (ShotShapeDTO)data.getSerializableExtra("shotShape");
                    holeStatFragment.setTeeClubAndShape(club,ss);
                }
                break;
            case GREEN_SHOT_REQUIRED:
                if (resCode == RESULT_OK) {
                    ClubDTO club = (ClubDTO)data.getSerializableExtra("club");
                    ShotShapeDTO ss = (ShotShapeDTO)data.getSerializableExtra("shotShape");
                    holeStatFragment.setGreenClubAndShape(club,ss);
                }
                break;
        }
    }

    boolean isUpdated;

    @Override
    public void onBackPressed() {
//        if (isUpdated) {
//            Intent m = new Intent(getApplicationContext(), PracticeUploadService.class);
//            startService(m);
//        }
        finish();
    }
}
