package com.boha.malengagolf.admin.com.boha.malengagolf.packs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

/**
 * Created by aubreyM on 2014/04/12.
 */
public class ScoringActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);
        ctx = getApplicationContext();
        tourneyPlayerScore = (LeaderBoardDTO)getIntent().getSerializableExtra("leaderBoard");
        tournament = (TournamentDTO)getIntent().getSerializableExtra("tournament");
        administrator = SharedUtil.getAdministrator(ctx);
        setFields();

    }

    LeaderBoardDTO tourneyPlayerScore;
    TournamentDTO tournament;
    TextView txtPlayer, txtTourn, txtScore1, txtScore2, txtScore3, txtScore4, txtScore5, txtScore6;
    TextView btnUp1, btnUp2, btnUp3, btnUp4, btnUp5, btnUp6;
    TextView btnDown1, btnDown2, btnDown3, btnDown4, btnDown5, btnDown6;
    Button btnSave;
    View vRound2, vRound3, vRound4, vRound5, vRound6;
    Context ctx;
    AdministratorDTO administrator;

    private void submitScore() {

        switch (tournament.getGolfRounds()) {
            case 1:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                break;
            case 2:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                tourneyPlayerScore.setScoreRound2(
                        Integer.parseInt(txtScore2.getText().toString()));
                break;
            case 3:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                tourneyPlayerScore.setScoreRound2(
                        Integer.parseInt(txtScore2.getText().toString()));
                tourneyPlayerScore.setScoreRound3(
                        Integer.parseInt(txtScore3.getText().toString()));
                break;
            case 4:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                tourneyPlayerScore.setScoreRound2(
                        Integer.parseInt(txtScore2.getText().toString()));
                tourneyPlayerScore.setScoreRound3(
                        Integer.parseInt(txtScore3.getText().toString()));
                tourneyPlayerScore.setScoreRound4(
                        Integer.parseInt(txtScore4.getText().toString()));
                break;
            case 5:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                tourneyPlayerScore.setScoreRound2(
                        Integer.parseInt(txtScore2.getText().toString()));
                tourneyPlayerScore.setScoreRound3(
                        Integer.parseInt(txtScore3.getText().toString()));
                tourneyPlayerScore.setScoreRound4(
                        Integer.parseInt(txtScore4.getText().toString()));
                tourneyPlayerScore.setScoreRound5(
                        Integer.parseInt(txtScore5.getText().toString()));
                break;
            case 6:
                tourneyPlayerScore.setScoreRound1(
                        Integer.parseInt(txtScore1.getText().toString()));
                tourneyPlayerScore.setScoreRound2(
                        Integer.parseInt(txtScore2.getText().toString()));
                tourneyPlayerScore.setScoreRound3(
                        Integer.parseInt(txtScore3.getText().toString()));
                tourneyPlayerScore.setScoreRound4(
                        Integer.parseInt(txtScore4.getText().toString()));
                tourneyPlayerScore.setScoreRound5(
                        Integer.parseInt(txtScore5.getText().toString()));
                tourneyPlayerScore.setScoreRound6(
                        Integer.parseInt(txtScore6.getText().toString()));
                break;
        }
        //leaderBoard.setAdministratorID(administrator.getAdministratorID());
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_TOURNAMENT_SCORE_TOTALS);
        w.setLeaderBoard(tourneyPlayerScore);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx,r)) {
                            return;
                        }
                        response = r;
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,w,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx,r)) {
//                    return;
//                }
//                response = r;
//                onBackPressed();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }
    ResponseDTO response;
    @Override
    public void onBackPressed() {
        if (response != null) {
            Intent intent = new Intent();
            intent.putExtra("response", response);
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
        super.onBackPressed();
    }
    private void setFields() {
        vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        progressBar = (ProgressBar)findViewById(R.id.SC_progress);
        progressBar.setVisibility(View.GONE);
        txtPlayer= (TextView)findViewById(R.id.SC_txtName);
        txtTourn= (TextView)findViewById(R.id.SC_txtTournament);
        txtScore1 = (TextView)findViewById(R.id.SC_txtScore1);
        txtScore2 = (TextView)findViewById(R.id.SC_txtScore2);
        txtScore3 = (TextView)findViewById(R.id.SC_txtScore3);
        txtScore4 = (TextView)findViewById(R.id.SC_txtScore4);
        txtScore5 = (TextView)findViewById(R.id.SC_txtScore5);
        txtScore6 = (TextView)findViewById(R.id.SC_txtScore6);

        vRound2 = findViewById(R.id.SC_layoutRound2);
        vRound3 = findViewById(R.id.SC_layoutRound3);
        vRound4 = findViewById(R.id.SC_layoutRound4);
        vRound5 = findViewById(R.id.SC_layoutRound5);
        vRound6 = findViewById(R.id.SC_layoutRound6);

        btnDown1 = (TextView)findViewById(R.id.SC_btnDown1);
        btnDown2 = (TextView)findViewById(R.id.SC_btnDown2);
        btnDown3 = (TextView)findViewById(R.id.SC_btnDown3);
        btnDown4 = (TextView)findViewById(R.id.SC_btnDown4);
        btnDown5 = (TextView)findViewById(R.id.SC_btnDown5);
        btnDown6 = (TextView)findViewById(R.id.SC_btnDown6);

        btnUp1 = (TextView)findViewById(R.id.SC_btnUp1);
        btnUp2 = (TextView)findViewById(R.id.SC_btnUp2);
        btnUp3 = (TextView)findViewById(R.id.SC_btnUp3);
        btnUp4 = (TextView)findViewById(R.id.SC_btnUp4);
        btnUp5 = (TextView)findViewById(R.id.SC_btnUp5);
        btnUp6 = (TextView)findViewById(R.id.SC_btnUp6);

        btnSave = (Button)findViewById(R.id.SC_btnSave);
        setDownButtons();
        setButtonsUp();
        switch (tournament.getGolfRounds()) {
            case 1:
                vRound2.setVisibility(View.GONE);
                vRound3.setVisibility(View.GONE);
                vRound4.setVisibility(View.GONE);
                vRound5.setVisibility(View.GONE);
                vRound6.setVisibility(View.GONE);
                break;
            case 2:
                vRound3.setVisibility(View.GONE);
                vRound4.setVisibility(View.GONE);
                vRound5.setVisibility(View.GONE);
                vRound6.setVisibility(View.GONE);
                break;
            case 3:
                vRound4.setVisibility(View.GONE);
                vRound5.setVisibility(View.GONE);
                vRound6.setVisibility(View.GONE);
                break;
            case 4:
                vRound5.setVisibility(View.GONE);
                vRound6.setVisibility(View.GONE);
                break;
            case 5:
                vRound6.setVisibility(View.GONE);
                break;
        }
        txtPlayer.setText(tourneyPlayerScore.getPlayer().getFullName());
        txtTourn.setText(tournament.getTourneyName());
        setScoreBasics();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitScore();
            }
        });
    }
    private void setScoreBasics() {
        if (tourneyPlayerScore.getScoreRound1() == 0) {
            txtScore1.setText("0" + tourneyPlayerScore.getScoreRound1());
        } else {
            txtScore1.setText("" + tourneyPlayerScore.getScoreRound1());
        }
        if (tourneyPlayerScore.getScoreRound2() == 0) {
            txtScore2.setText("0" + tourneyPlayerScore.getScoreRound2());
        } else {
            txtScore2.setText("" + tourneyPlayerScore.getScoreRound2());
        }
        if (tourneyPlayerScore.getScoreRound3() == 0) {
            txtScore3.setText("0" + tourneyPlayerScore.getScoreRound3());
        } else {
            txtScore3.setText("" + tourneyPlayerScore.getScoreRound3());
        }
        if (tourneyPlayerScore.getScoreRound4() == 0) {
            txtScore4.setText("0" + tourneyPlayerScore.getScoreRound4());
        } else {
            txtScore4.setText("" + tourneyPlayerScore.getScoreRound4());
        }
        if (tourneyPlayerScore.getScoreRound5() == 0) {
            txtScore5.setText("0" + tourneyPlayerScore.getScoreRound5());
        } else {
            txtScore5.setText("" + tourneyPlayerScore.getScoreRound5());
        }
        if (tourneyPlayerScore.getScoreRound6() == 0) {
            txtScore6.setText("0" + tourneyPlayerScore.getScoreRound6());
        } else {
            txtScore6.setText("" + tourneyPlayerScore.getScoreRound6());
        }

        txtScore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore1.setText("00");
                score1 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
        txtScore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore2.setText("00");
                score2 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
        txtScore3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore3.setText("00");
                score3 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
        txtScore4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore4.setText("00");
                score4 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
        txtScore5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore5.setText("00");
                score5 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
        txtScore6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtScore6.setText("00");
                score6 = BASE_SCORE_FOR_FIRST_DISPLAY;
            }
        });
    }
    static final int UPPER_LIMIT = 120;
    static final long VIBRATION_TIME = 300L;
    private void setButtonsUp() {
        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1++;
                txtScore1.setText("" + score1);
            }
        });
        btnUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score2 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score2++;
                txtScore2.setText("" + score2);
            }
        });
        btnUp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score3 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score3++;
                txtScore3.setText("" + score3);
            }
        });
        btnUp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score4 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score4++;
                txtScore4.setText("" + score4);
            }
        });
        btnUp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score5 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score5++;
                txtScore5.setText("" + score5);
            }
        });
        btnUp6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score6 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score6++;
                txtScore6.setText("" + score6);
            }
        });
    }


    private void setDownButtons() {
        btnDown1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1--;
                txtScore1.setText("" + score1);
            }
        });
        btnDown2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score2 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score2--;
                txtScore2.setText("" + score2);
            }
        });
        btnDown3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score3 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score3--;
                txtScore3.setText("" + score3);
            }
        });
        btnDown4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score4 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score4--;
                txtScore4.setText("" + score4);
            }
        });
        btnDown5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score5 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score5--;
                txtScore5.setText("" + score5);
            }
        });
        btnDown6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score6 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score6--;
                txtScore6.setText("" + score6);
            }
        });
    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG, "onResume ...nothing to be done");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        super.onSaveInstanceState(b);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scoring_menu, menu);
        mMenu = menu;
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                return true;

            case R.id.menu_help:
                ToastUtil.toast(ctx, "Under Construction");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    Menu mMenu;

    static final String LOG = "ScoringActivity";
    Vibrator vb;
    ProgressBar progressBar;
    static final int BASE_SCORE_FOR_FIRST_DISPLAY = 80;
    int score1 = BASE_SCORE_FOR_FIRST_DISPLAY,
            score2  = BASE_SCORE_FOR_FIRST_DISPLAY,
            score3  = BASE_SCORE_FOR_FIRST_DISPLAY,
            score4  = BASE_SCORE_FOR_FIRST_DISPLAY,
            score5  = BASE_SCORE_FOR_FIRST_DISPLAY,
            score6  = BASE_SCORE_FOR_FIRST_DISPLAY;
}