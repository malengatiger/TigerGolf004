package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.MGApp;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.CompleteRounds;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment is hosted by ScoringByHoleActivity
 * Created by aubreyM on 2014/04/12.
 */
public class ScoringByHoleFragment extends Fragment {

    public interface ScoringByHoleListener {
        public void scoringSubmitted(List<LeaderBoardDTO> leaderBoardList);

        public void onError(String message);

        public void setBusy();

        public void setNotBusy();
    }

    ScoringByHoleListener scoringByHoleListener;

    @Override
    public void onAttach(Activity a) {

        if (a instanceof ScoringByHoleListener) {
            scoringByHoleListener = (ScoringByHoleListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host activity " + a.getLocalClassName() + " must implement ScoringByHoleListener"
            );
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "---------------------------> onCreateView ...");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_scoring_by_hole, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administrator = SharedUtil.getAdministrator(ctx);

        setFields();
        setUpButtons();
        setDownButtons();

        return view;
    }

    TextView reminder;

    private void setFields() {

        frontNine = (RelativeLayout) view.findViewById(R.id.SBH_frontNine);
        backNine = (RelativeLayout) view.findViewById(R.id.SBH_backNine);
        spinner = (Spinner) view.findViewById(R.id.SBH_spinnerRound);
        vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        txtPlayer = (TextView) view.findViewById(R.id.SBH_txtName);
        txtTotal = (TextView) view.findViewById(R.id.SBH_txtTotal);
        btnSave = (Button) view.findViewById(R.id.SBH_btnSave);
        txtScore1 = (TextView) view.findViewById(R.id.SBH_txtScore1);
        txtScore2 = (TextView) view.findViewById(R.id.SBH_txtScore2);
        txtScore3 = (TextView) view.findViewById(R.id.SBH_txtScore3);
        txtScore4 = (TextView) view.findViewById(R.id.SBH_txtScore4);
        txtScore5 = (TextView) view.findViewById(R.id.SBH_txtScore5);
        txtScore6 = (TextView) view.findViewById(R.id.SBH_txtScore6);

        txtScore7 = (TextView) view.findViewById(R.id.SBH_txtScore7);
        txtScore8 = (TextView) view.findViewById(R.id.SBH_txtScore8);
        txtScore9 = (TextView) view.findViewById(R.id.SBH_txtScore9);
        txtScore10 = (TextView) view.findViewById(R.id.SBH_txtScore10);
        txtScore11 = (TextView) view.findViewById(R.id.SBH_txtScore11);
        txtScore12 = (TextView) view.findViewById(R.id.SBH_txtScore12);

        txtScore13 = (TextView) view.findViewById(R.id.SBH_txtScore13);
        txtScore14 = (TextView) view.findViewById(R.id.SBH_txtScore14);
        txtScore15 = (TextView) view.findViewById(R.id.SBH_txtScore15);
        txtScore16 = (TextView) view.findViewById(R.id.SBH_txtScore16);
        txtScore17 = (TextView) view.findViewById(R.id.SBH_txtScore17);
        txtScore18 = (TextView) view.findViewById(R.id.SBH_txtScore18);
        //
        txtPoints1 = (TextView) view.findViewById(R.id.SBH_txtPoints1);
        txtPoints2 = (TextView) view.findViewById(R.id.SBH_txtPoints2);
        txtPoints3 = (TextView) view.findViewById(R.id.SBH_txtPoints3);
        txtPoints4 = (TextView) view.findViewById(R.id.SBH_txtPoints4);
        txtPoints5 = (TextView) view.findViewById(R.id.SBH_txtPoints5);
        txtPoints6 = (TextView) view.findViewById(R.id.SBH_txtPoints6);

        txtPoints7 = (TextView) view.findViewById(R.id.SBH_txtPoints7);
        txtPoints8 = (TextView) view.findViewById(R.id.SBH_txtPoints8);
        txtPoints9 = (TextView) view.findViewById(R.id.SBH_txtPoints9);
        txtPoints10 = (TextView) view.findViewById(R.id.SBH_txtPoints10);
        txtPoints11 = (TextView) view.findViewById(R.id.SBH_txtPoints11);
        txtPoints12 = (TextView) view.findViewById(R.id.SBH_txtPoints12);

        txtPoints13 = (TextView) view.findViewById(R.id.SBH_txtPoints13);
        txtPoints14 = (TextView) view.findViewById(R.id.SBH_txtPoints14);
        txtPoints15 = (TextView) view.findViewById(R.id.SBH_txtPoints15);
        txtPoints16 = (TextView) view.findViewById(R.id.SBH_txtPoints16);
        txtPoints17 = (TextView) view.findViewById(R.id.SBH_txtPoints17);
        txtPoints18 = (TextView) view.findViewById(R.id.SBH_txtPoints18);

        vPoints1 = view.findViewById(R.id.SBH_layoutPoints1);
        vPoints2 = view.findViewById(R.id.SBH_layoutPoints2);
        vPoints3 = view.findViewById(R.id.SBH_layoutPoints3);
        vPoints4 = view.findViewById(R.id.SBH_layoutPoints4);
        vPoints5 = view.findViewById(R.id.SBH_layoutPoints5);
        vPoints6 = view.findViewById(R.id.SBH_layoutPoints6);
        vPoints7 = view.findViewById(R.id.SBH_layoutPoints7);
        vPoints8 = view.findViewById(R.id.SBH_layoutPoints8);
        vPoints9 = view.findViewById(R.id.SBH_layoutPoints9);
        vPoints10 = view.findViewById(R.id.SBH_layoutPoints10);
        vPoints11 = view.findViewById(R.id.SBH_layoutPoints11);
        vPoints12 = view.findViewById(R.id.SBH_layoutPoints12);
        vPoints13 = view.findViewById(R.id.SBH_layoutPoints13);
        vPoints14 = view.findViewById(R.id.SBH_layoutPoints14);
        vPoints15 = view.findViewById(R.id.SBH_layoutPoints15);
        vPoints16 = view.findViewById(R.id.SBH_layoutPoints16);
        vPoints17 = view.findViewById(R.id.SBH_layoutPoints17);
        vPoints18 = view.findViewById(R.id.SBH_layoutPoints18);

        reminder = (TextView) view.findViewById(R.id.SBH_txt9Holes);

        txtScore1.setTag("txtScore1");
        txtScore2.setTag("txtScore2");
        txtScore3.setTag("txtScore3");
        txtScore4.setTag("txtScore4");
        txtScore5.setTag("txtScore5");
        txtScore6.setTag("txtScore6");
        txtScore7.setTag("txtScore7");
        txtScore8.setTag("txtScore8");
        txtScore9.setTag("txtScore9");
        txtScore10.setTag("txtScore10");
        txtScore11.setTag("txtScore11");
        txtScore12.setTag("txtScore12");
        txtScore13.setTag("txtScore13");
        txtScore14.setTag("txtScore14");
        txtScore15.setTag("txtScore15");
        txtScore16.setTag("txtScore16");
        txtScore17.setTag("txtScore17");
        txtScore18.setTag("txtScore18");
        //
        txtPoints1.setTag("txtPoints1");
        txtPoints2.setTag("txtPoints2");
        txtPoints3.setTag("txtPoints3");
        txtPoints4.setTag("txtPoints4");
        txtPoints5.setTag("txtPoints5");
        txtPoints6.setTag("txtPoints6");
        txtPoints7.setTag("txtPoints7");
        txtPoints8.setTag("txtPoints8");
        txtPoints9.setTag("txtPoints9");
        txtPoints10.setTag("txtPoints10");
        txtPoints11.setTag("txtPoints11");
        txtPoints12.setTag("txtPoints12");
        txtPoints13.setTag("txtPoints13");
        txtPoints14.setTag("txtPoints14");
        txtPoints15.setTag("txtPoints15");
        txtPoints16.setTag("txtPoints16");
        txtPoints17.setTag("txtPoints17");
        txtPoints18.setTag("txtPoints18");

        setScoreFieldsForCancel();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScoringCompleteDialog();
            }
        });

    }

    int index;
    TourneyScoreByRoundDTO tsbr;

    private void setScoreFieldsForCancel() {

        for (index = 0; index < 9; index++) {
            RelativeLayout hole = (RelativeLayout) frontNine.getChildAt(index); //regular score
            RelativeLayout holex = (RelativeLayout) frontNine.getChildAt(index + 1); //points score

            final TextView score = (TextView) hole.getChildAt(1);
            final TextView points = (TextView) holex.getChildAt(1);
            score.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    score.setText("00");
                    if (view.getTag().toString().equalsIgnoreCase("txtScore1")) {
                        tsbr.setScore1(0);
                        score1 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore2")) {
                        tsbr.setScore2(0);
                        score2 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore3")) {
                        tsbr.setScore3(0);
                        score3 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore4")) {
                        tsbr.setScore4(0);
                        score4 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore5")) {
                        tsbr.setScore5(0);
                        score5 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore6")) {
                        tsbr.setScore6(0);
                        score6 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore7")) {
                        tsbr.setScore7(0);
                        score7 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore8")) {
                        tsbr.setScore8(0);
                        score8 = BASE_SCORE;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtScore9")) {
                        tsbr.setScore9(0);
                        score9 = BASE_SCORE;
                    }

                    calculateTotal();
                }
            });
            points.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    points.setText("00");
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints1")) {
                        tsbr.setPoints1(0);
                        points1 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtSPoints2")) {
                        tsbr.setPoints2(0);
                        points2 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints3")) {
                        tsbr.setPoints3(0);
                        points3 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints4")) {
                        tsbr.setPoints4(0);
                        points4 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints5")) {
                        tsbr.setPoints5(0);
                        points5 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints6")) {
                        tsbr.setPoints6(0);
                        points6 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints7")) {
                        tsbr.setPoints7(0);
                        points7 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints8")) {
                        tsbr.setPoints8(0);
                        points8 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints9")) {
                        tsbr.setPoints9(0);
                        points9 = 0;
                    }

                    calculateTotal();
                }
            });

        }
        //
        for (index = 0; index < 9; index++) {
            RelativeLayout hole = (RelativeLayout) backNine.getChildAt(index);
            RelativeLayout holex = (RelativeLayout) backNine.getChildAt(index + 1);
            final TextView score = (TextView) hole.getChildAt(1);
            final TextView points = (TextView) holex.getChildAt(1);
            score.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             score.setText("00");
                                             if (view.getTag().toString().equalsIgnoreCase("txtPoints10")) {
                                                 tsbr.setScore10(0);
                                                 score10 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore11")) {
                                                 tsbr.setScore11(0);
                                                 score11 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore12")) {
                                                 tsbr.setScore12(0);
                                                 score12 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore13")) {
                                                 tsbr.setScore13(0);
                                                 score13 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore14")) {
                                                 tsbr.setScore14(0);
                                                 score14 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore15")) {
                                                 tsbr.setScore15(0);
                                                 score15 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore16")) {
                                                 tsbr.setScore16(0);
                                                 score16 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore17")) {
                                                 tsbr.setScore17(0);
                                                 score17 = BASE_SCORE;
                                             }
                                             if (view.getTag().toString().equalsIgnoreCase("txtScore18")) {
                                                 tsbr.setScore18(0);
                                                 score18 = BASE_SCORE;
                                             }
                                             calculateTotal();

                                         }
                                     }
            );
            points.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    points.setText("00");
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints10")) {
                        tsbr.setPoints10(0);
                        points10 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtSPoints11")) {
                        tsbr.setPoints11(0);
                        points11 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints12")) {
                        tsbr.setPoints12(0);
                        points12 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints13")) {
                        tsbr.setPoints13(0);
                        points13 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints14")) {
                        tsbr.setPoints14(0);
                        points14 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints15")) {
                        tsbr.setPoints15(0);
                        points15 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints16")) {
                        tsbr.setPoints16(0);
                        points16 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints17")) {
                        tsbr.setPoints17(0);
                        points17 = 0;
                    }
                    if (view.getTag().toString().equalsIgnoreCase("txtPoints18")) {
                        tsbr.setPoints18(0);
                        points18 = 0;
                    }

                    calculateTotal();
                }
            });

        }

    }

    private void submitScore() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_TOURNAMENT_SCORES);
        w.setTournamentType(tournament.getTournamentType());
        if (SharedUtil.getAdministrator(ctx) != null) {
            w.setAdministratorID(SharedUtil.getAdministrator(ctx).getAdministratorID());
        }
        if (SharedUtil.getScorer(ctx) != null) {
            w.setScorerID(SharedUtil.getScorer(ctx).getScorerID());
        }
        w.setSessionID(SharedUtil.getSessionID(ctx));

        LeaderBoardDTO lb = new LeaderBoardDTO();
        lb.setTournamentID(tournament.getTournamentID());
        lb.setLeaderBoardID(leaderBoard.getLeaderBoardID());

        lb.setTourneyScoreByRoundList(new ArrayList<TourneyScoreByRoundDTO>());
        for (TourneyScoreByRoundDTO tsbr : leaderBoard.getTourneyScoreByRoundList()) {
            TourneyScoreByRoundDTO cc = new TourneyScoreByRoundDTO();
            cc = tsbr;
            //cc.setClubCourse(tsbr.getClubCourse());
            lb.getTourneyScoreByRoundList().add(cc);
        }
        w.setLeaderBoard(lb);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        scoringByHoleListener.setBusy();

        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                scoringByHoleListener.setNotBusy();
                if (r.getLeaderBoard() != null) {
                    Log.w(LOG, "##### onMessage: scoring update coming in, ignored");
                    return;
                }
                response = r;

                Log.i(LOG, "Scores submitted OK, telling listener -> tourneyPlayerScoreList");
                scoringByHoleListener.scoringSubmitted(response.getLeaderBoardList());


            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                scoringByHoleListener.setNotBusy();
                Log.e(LOG, message);
                scoringByHoleListener.onError(message);


            }


        });

//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                scoringByHoleListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                response = r;
//                Log.i(LOG, "Scores submitted OK, telling listener -> tourneyPlayerScoreList");
//                scoringByHoleListener.scoringSubmitted(r.getLeaderBoardList());
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                scoringByHoleListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    String mSessionID;

    private void showScoringCompleteDialog() {

        CompleteRounds.markScoringCompletion(leaderBoard);
        final TourneyScoreByRoundDTO tsbr = leaderBoard.getTourneyScoreByRoundList().get(currentRound - 1);
        int scoredHoles = CompleteRounds.countHolesScored(tsbr);
        Log.e(LOG, "scoredHoles - " + scoredHoles);
        if (tournament.getHolesPerRound() == 9) {
            if (scoredHoles > 9) {
                ToastUtil.toast(ctx, ctx.getResources()
                        .getString(R.string.too_many_holes_scored), 4, Gravity.CENTER);
                return;
            }
        }


        if (tsbr.getScoringComplete() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(ctx.getResources().getString(R.string.round_complete))
                    .setIcon(ctx.getResources().getDrawable(R.drawable.ic_action_edit))
                    .setMessage(ctx.getResources().getString(R.string.round_complete_question))
                    .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tsbr.setScoringComplete(1);
                            submitScore();
                        }
                    })
                    .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.continue_scoring));
                        }
                    }).show();
        } else {
            submitScore();
        }


    }

    private void setRoundSpinner() {
        Log.e(LOG, "setRoundSpinner ....");

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < tournament.getGolfRounds(); i++) {
            switch (i) {
                case 0:
                    list.add(ctx.getResources().getString(R.string.round1)
                            + " - " + tournament.getTourneyName());
                    break;
                case 1:
                    list.add(ctx.getResources().getString(R.string.round2)
                            + " - " + tournament.getTourneyName());
                    break;
                case 2:
                    list.add(ctx.getResources().getString(R.string.round3)
                            + " - " + tournament.getTourneyName());
                    break;
                case 3:
                    list.add(ctx.getResources().getString(R.string.round4)
                            + " - " + tournament.getTourneyName());
                    break;
                case 4:
                    list.add(ctx.getResources().getString(R.string.round5)
                            + " - " + tournament.getTourneyName());
                    break;
                case 5:
                    list.add(ctx.getResources().getString(R.string.round6)
                            + " - " + tournament.getTourneyName());
                    break;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ctx, R.layout.xxsimple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentRound = i + 1;
                Log.w(LOG, "currentRound selected: " + currentRound);
                fillScoreCard();
                tsbr = leaderBoard.getTourneyScoreByRoundList().get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    static final int UPPER_LIMIT = 20, UPPER_LIMIT_POINTS = 12;
    static final long VIBRATION_TIME = 300L;

    private void enableButtons() {
        int cnt = frontNine.getChildCount();
        btnSave.setEnabled(true);
        btnSave.setAlpha(1.0f);
        for (int i = 0; i < 9; i++) {
            RelativeLayout hole = (RelativeLayout) frontNine.getChildAt(i);
            TextView score = (TextView) hole.getChildAt(1);
            score.setEnabled(true);
            TextView btnUp = (TextView) hole.getChildAt(2);
            TextView btnDown = (TextView) hole.getChildAt(3);
            btnUp.setEnabled(true);
            btnDown.setEnabled(true);
            btnUp.setAlpha(1.0f);
            btnDown.setAlpha(1.0f);
            //
            RelativeLayout holex = (RelativeLayout) frontNine.getChildAt(i + 1);
            TextView points = (TextView) holex.getChildAt(1);
            points.setEnabled(true);
            TextView btnUpX = (TextView) holex.getChildAt(2);
            TextView btnDownX = (TextView) holex.getChildAt(3);
            btnUpX.setEnabled(true);
            btnDownX.setEnabled(true);
            btnUpX.setAlpha(1.0f);
            btnDownX.setAlpha(1.0f);

        }
        for (int i = 0; i < 9; i++) {
            RelativeLayout hole = (RelativeLayout) backNine.getChildAt(i);
            TextView score = (TextView) hole.getChildAt(1);
            score.setEnabled(true);
            TextView btnUp = (TextView) hole.getChildAt(2);
            TextView btnDown = (TextView) hole.getChildAt(3);
            btnUp.setEnabled(true);
            btnDown.setEnabled(true);
            btnUp.setAlpha(1.0f);
            btnDown.setAlpha(1.0f);
            //
            RelativeLayout holex = (RelativeLayout) backNine.getChildAt(i + 1);
            TextView points = (TextView) holex.getChildAt(1);
            points.setEnabled(true);
            TextView btnUpX = (TextView) holex.getChildAt(2);
            TextView btnDownX = (TextView) holex.getChildAt(3);
            btnUpX.setEnabled(true);
            btnDownX.setEnabled(true);
            btnUpX.setAlpha(1.0f);
            btnDownX.setAlpha(1.0f);

        }

    }

    private void disableButtons() {
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.2f);

        for (int i = 0; i < 9; i++) {
            RelativeLayout hole = (RelativeLayout) frontNine.getChildAt(i);
            TextView score = (TextView) hole.getChildAt(1);
            score.setEnabled(false);
            TextView btnUp = (TextView) hole.getChildAt(2);
            TextView btnDown = (TextView) hole.getChildAt(3);
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
            btnUp.setAlpha(0.2f);
            btnDown.setAlpha(0.2f);

        }
        for (int i = 0; i < 9; i++) {
            RelativeLayout hole = (RelativeLayout) backNine.getChildAt(i);
            TextView score = (TextView) hole.getChildAt(1);
            score.setEnabled(false);
            TextView btnUp = (TextView) hole.getChildAt(2);
            TextView btnDown = (TextView) hole.getChildAt(3);
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
            btnUp.setAlpha(0.2f);
            btnDown.setAlpha(0.2f);

        }

    }

    private void fillScoreCard() {
        if (leaderBoard == null) return;
        calculateTotal();
        clearNumbers();
        if (leaderBoard.getTourneyScoreByRoundList() == null) {
            Log.e(LOG, "is == null, exiting");
            return;
        }
        switch (currentRound) {
            case 1:
                TourneyScoreByRoundDTO round1 = leaderBoard.getTourneyScoreByRoundList().get(0);
                if (round1.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round1.getScore1());
                format(txtScore2, round1.getScore2());
                format(txtScore3, round1.getScore3());
                format(txtScore4, round1.getScore4());
                format(txtScore5, round1.getScore5());
                format(txtScore6, round1.getScore6());
                format(txtScore7, round1.getScore7());
                format(txtScore8, round1.getScore8());
                format(txtScore9, round1.getScore9());
                format(txtScore10, round1.getScore10());
                format(txtScore11, round1.getScore11());
                format(txtScore12, round1.getScore12());
                format(txtScore13, round1.getScore13());
                format(txtScore14, round1.getScore14());
                format(txtScore15, round1.getScore15());
                format(txtScore16, round1.getScore16());
                format(txtScore17, round1.getScore17());
                format(txtScore18, round1.getScore18());
                //
                format(txtPoints1, round1.getPoints1());
                format(txtPoints2, round1.getPoints2());
                format(txtPoints3, round1.getPoints3());
                format(txtPoints4, round1.getPoints4());
                format(txtPoints5, round1.getPoints5());
                format(txtPoints6, round1.getPoints6());
                format(txtPoints7, round1.getPoints7());
                format(txtPoints8, round1.getPoints8());
                format(txtPoints9, round1.getPoints9());
                format(txtPoints10, round1.getPoints10());
                format(txtPoints11, round1.getPoints11());
                format(txtPoints12, round1.getPoints12());
                format(txtPoints13, round1.getPoints13());
                format(txtPoints14, round1.getPoints14());
                format(txtPoints15, round1.getPoints15());
                format(txtPoints16, round1.getPoints16());
                format(txtPoints17, round1.getPoints17());
                format(txtPoints18, round1.getPoints18());

                break;
            case 2:
                TourneyScoreByRoundDTO round2 = leaderBoard.getTourneyScoreByRoundList().get(1);
                if (round2.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round2.getScore1());
                format(txtScore2, round2.getScore2());
                format(txtScore3, round2.getScore3());
                format(txtScore4, round2.getScore4());
                format(txtScore5, round2.getScore5());
                format(txtScore6, round2.getScore6());
                format(txtScore7, round2.getScore7());
                format(txtScore8, round2.getScore8());
                format(txtScore9, round2.getScore9());
                format(txtScore10, round2.getScore10());
                format(txtScore11, round2.getScore11());
                format(txtScore12, round2.getScore12());
                format(txtScore13, round2.getScore13());
                format(txtScore14, round2.getScore14());
                format(txtScore15, round2.getScore15());
                format(txtScore16, round2.getScore16());
                format(txtScore17, round2.getScore17());
                format(txtScore18, round2.getScore18());
                //
                format(txtPoints1, round2.getPoints1());
                format(txtPoints2, round2.getPoints2());
                format(txtPoints3, round2.getPoints3());
                format(txtPoints4, round2.getPoints4());
                format(txtPoints5, round2.getPoints5());
                format(txtPoints6, round2.getPoints6());
                format(txtPoints7, round2.getPoints7());
                format(txtPoints8, round2.getPoints8());
                format(txtPoints9, round2.getPoints9());
                format(txtPoints10, round2.getPoints10());
                format(txtPoints11, round2.getPoints11());
                format(txtPoints12, round2.getPoints12());
                format(txtPoints13, round2.getPoints13());
                format(txtPoints14, round2.getPoints14());
                format(txtPoints15, round2.getPoints15());
                format(txtPoints16, round2.getPoints16());
                format(txtPoints17, round2.getPoints17());
                format(txtPoints18, round2.getPoints18());
                break;
            case 3:
                TourneyScoreByRoundDTO round3 = leaderBoard.getTourneyScoreByRoundList().get(2);
                if (round3.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round3.getScore1());
                format(txtScore2, round3.getScore2());
                format(txtScore3, round3.getScore3());
                format(txtScore4, round3.getScore4());
                format(txtScore5, round3.getScore5());
                format(txtScore6, round3.getScore6());
                format(txtScore7, round3.getScore7());
                format(txtScore8, round3.getScore8());
                format(txtScore9, round3.getScore9());
                format(txtScore10, round3.getScore10());
                format(txtScore11, round3.getScore11());
                format(txtScore12, round3.getScore12());
                format(txtScore13, round3.getScore13());
                format(txtScore14, round3.getScore14());
                format(txtScore15, round3.getScore15());
                format(txtScore16, round3.getScore16());
                format(txtScore17, round3.getScore17());
                format(txtScore18, round3.getScore18());
                //
                format(txtPoints1, round3.getPoints1());
                format(txtPoints2, round3.getPoints2());
                format(txtPoints3, round3.getPoints3());
                format(txtPoints4, round3.getPoints4());
                format(txtPoints5, round3.getPoints5());
                format(txtPoints6, round3.getPoints6());
                format(txtPoints7, round3.getPoints7());
                format(txtPoints8, round3.getPoints8());
                format(txtPoints9, round3.getPoints9());
                format(txtPoints10, round3.getPoints10());
                format(txtPoints11, round3.getPoints11());
                format(txtPoints12, round3.getPoints12());
                format(txtPoints13, round3.getPoints13());
                format(txtPoints14, round3.getPoints14());
                format(txtPoints15, round3.getPoints15());
                format(txtPoints16, round3.getPoints16());
                format(txtPoints17, round3.getPoints17());
                format(txtPoints18, round3.getPoints18());
                break;
            case 4:
                TourneyScoreByRoundDTO round4 = leaderBoard.getTourneyScoreByRoundList().get(3);
                if (round4.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round4.getScore1());
                format(txtScore2, round4.getScore2());
                format(txtScore3, round4.getScore3());
                format(txtScore4, round4.getScore4());
                format(txtScore5, round4.getScore5());
                format(txtScore6, round4.getScore6());
                format(txtScore7, round4.getScore7());
                format(txtScore8, round4.getScore8());
                format(txtScore9, round4.getScore9());
                format(txtScore10, round4.getScore10());
                format(txtScore11, round4.getScore11());
                format(txtScore12, round4.getScore12());
                format(txtScore13, round4.getScore13());
                format(txtScore14, round4.getScore14());
                format(txtScore15, round4.getScore15());
                format(txtScore16, round4.getScore16());
                format(txtScore17, round4.getScore17());
                format(txtScore18, round4.getScore18());
                //
                format(txtPoints1, round4.getPoints1());
                format(txtPoints2, round4.getPoints2());
                format(txtPoints3, round4.getPoints3());
                format(txtPoints4, round4.getPoints4());
                format(txtPoints5, round4.getPoints5());
                format(txtPoints6, round4.getPoints6());
                format(txtPoints7, round4.getPoints7());
                format(txtPoints8, round4.getPoints8());
                format(txtPoints9, round4.getPoints9());
                format(txtPoints10, round4.getPoints10());
                format(txtPoints11, round4.getPoints11());
                format(txtPoints12, round4.getPoints12());
                format(txtPoints13, round4.getPoints13());
                format(txtPoints14, round4.getPoints14());
                format(txtPoints15, round4.getPoints15());
                format(txtPoints16, round4.getPoints16());
                format(txtPoints17, round4.getPoints17());
                format(txtPoints18, round4.getPoints18());
                break;
            case 5:
                TourneyScoreByRoundDTO round5 = leaderBoard.getTourneyScoreByRoundList().get(4);
                if (round5.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round5.getScore1());
                format(txtScore2, round5.getScore2());
                format(txtScore3, round5.getScore3());
                format(txtScore4, round5.getScore4());
                format(txtScore5, round5.getScore5());
                format(txtScore6, round5.getScore6());
                format(txtScore7, round5.getScore7());
                format(txtScore8, round5.getScore8());
                format(txtScore9, round5.getScore9());
                format(txtScore10, round5.getScore10());
                format(txtScore11, round5.getScore11());
                format(txtScore12, round5.getScore12());
                format(txtScore13, round5.getScore13());
                format(txtScore14, round5.getScore14());
                format(txtScore15, round5.getScore15());
                format(txtScore16, round5.getScore16());
                format(txtScore17, round5.getScore17());
                format(txtScore18, round5.getScore18());
                //
                format(txtPoints1, round5.getPoints1());
                format(txtPoints2, round5.getPoints2());
                format(txtPoints3, round5.getPoints3());
                format(txtPoints4, round5.getPoints4());
                format(txtPoints5, round5.getPoints5());
                format(txtPoints6, round5.getPoints6());
                format(txtPoints7, round5.getPoints7());
                format(txtPoints8, round5.getPoints8());
                format(txtPoints9, round5.getPoints9());
                format(txtPoints10, round5.getPoints10());
                format(txtPoints11, round5.getPoints11());
                format(txtPoints12, round5.getPoints12());
                format(txtPoints13, round5.getPoints13());
                format(txtPoints14, round5.getPoints14());
                format(txtPoints15, round5.getPoints15());
                format(txtPoints16, round5.getPoints16());
                format(txtPoints17, round5.getPoints17());
                format(txtPoints18, round5.getPoints18());
                break;
            case 6:
                TourneyScoreByRoundDTO round6 = leaderBoard.getTourneyScoreByRoundList().get(5);
                if (round6.getScoringComplete() == 1) {
                    disableButtons();
                } else {
                    enableButtons();
                }
                format(txtScore1, round6.getScore1());
                format(txtScore2, round6.getScore2());
                format(txtScore3, round6.getScore3());
                format(txtScore4, round6.getScore4());
                format(txtScore5, round6.getScore5());
                format(txtScore6, round6.getScore6());
                format(txtScore7, round6.getScore7());
                format(txtScore8, round6.getScore8());
                format(txtScore9, round6.getScore9());
                format(txtScore10, round6.getScore10());
                format(txtScore11, round6.getScore11());
                format(txtScore12, round6.getScore12());
                format(txtScore13, round6.getScore13());
                format(txtScore14, round6.getScore14());
                format(txtScore15, round6.getScore15());
                format(txtScore16, round6.getScore16());
                format(txtScore17, round6.getScore17());
                format(txtScore18, round6.getScore18());
                //
                format(txtPoints1, round6.getPoints1());
                format(txtPoints2, round6.getPoints2());
                format(txtPoints3, round6.getPoints3());
                format(txtPoints4, round6.getPoints4());
                format(txtPoints5, round6.getPoints5());
                format(txtPoints6, round6.getPoints6());
                format(txtPoints7, round6.getPoints7());
                format(txtPoints8, round6.getPoints8());
                format(txtPoints9, round6.getPoints9());
                format(txtPoints10, round6.getPoints10());
                format(txtPoints11, round6.getPoints11());
                format(txtPoints12, round6.getPoints12());
                format(txtPoints13, round6.getPoints13());
                format(txtPoints14, round6.getPoints14());
                format(txtPoints15, round6.getPoints15());
                format(txtPoints16, round6.getPoints16());
                format(txtPoints17, round6.getPoints17());
                format(txtPoints18, round6.getPoints18());
                break;
        }
    }

    private void clearNumbers() {
        score1 = BASE_SCORE;
        score2 = BASE_SCORE;
        score3 = BASE_SCORE;
        score4 = BASE_SCORE;
        score5 = BASE_SCORE;
        score6 = BASE_SCORE;
        score7 = BASE_SCORE;
        score8 = BASE_SCORE;
        score9 = BASE_SCORE;
        score10 = BASE_SCORE;
        score11 = BASE_SCORE;
        score12 = BASE_SCORE;
        score13 = BASE_SCORE;
        score14 = BASE_SCORE;
        score15 = BASE_SCORE;
        score16 = BASE_SCORE;
        score17 = BASE_SCORE;
        score18 = BASE_SCORE;
        //
        points1 = 0;
        points2 = 0;
        points3 = 0;
        points4 = 0;
        points5 = 0;
        points6 = 0;
        points7 = 0;
        points8 = 0;
        points9 = 0;
        points10 = 0;
        points11 = 0;
        points12 = 0;
        points13 = 0;
        points14 = 0;
        points15 = 0;
        points16 = 0;
        points17 = 0;
        points18 = 0;
    }

    private void setUpButtons() {
        //
        btnUpX1 = (TextView) view.findViewById(R.id.SBH_btnPointsUp1);
        btnUpX2 = (TextView) view.findViewById(R.id.SBH_btnPointsUp2);
        btnUpX3 = (TextView) view.findViewById(R.id.SBH_btnPointsUp3);
        btnUpX4 = (TextView) view.findViewById(R.id.SBH_btnPointsUp4);
        btnUpX5 = (TextView) view.findViewById(R.id.SBH_btnPointsUp5);
        btnUpX6 = (TextView) view.findViewById(R.id.SBH_btnPointsUp6);
        btnUpX7 = (TextView) view.findViewById(R.id.SBH_btnPointsUp7);
        btnUpX8 = (TextView) view.findViewById(R.id.SBH_btnPointsUp8);
        btnUpX9 = (TextView) view.findViewById(R.id.SBH_btnPointsUp9);

        btnUpX10 = (TextView) view.findViewById(R.id.SBH_btnPointsUp10);
        btnUpX11 = (TextView) view.findViewById(R.id.SBH_btnPointsUp11);
        btnUpX12 = (TextView) view.findViewById(R.id.SBH_btnPointsUp12);
        btnUpX13 = (TextView) view.findViewById(R.id.SBH_btnPointsUp13);
        btnUpX14 = (TextView) view.findViewById(R.id.SBH_btnPointsUp14);
        btnUpX15 = (TextView) view.findViewById(R.id.SBH_btnPointsUp15);
        btnUpX16 = (TextView) view.findViewById(R.id.SBH_btnPointsUp16);
        btnUpX17 = (TextView) view.findViewById(R.id.SBH_btnPointsUp17);
        btnUpX18 = (TextView) view.findViewById(R.id.SBH_btnPointsUp18);
        //
        btnUp1 = (TextView) view.findViewById(R.id.SBH_btnUp1);
        btnUp2 = (TextView) view.findViewById(R.id.SBH_btnUp2);
        btnUp3 = (TextView) view.findViewById(R.id.SBH_btnUp3);
        btnUp4 = (TextView) view.findViewById(R.id.SBH_btnUp4);
        btnUp5 = (TextView) view.findViewById(R.id.SBH_btnUp5);
        btnUp6 = (TextView) view.findViewById(R.id.SBH_btnUp6);
        btnUp7 = (TextView) view.findViewById(R.id.SBH_btnUp7);
        btnUp8 = (TextView) view.findViewById(R.id.SBH_btnUp8);
        btnUp9 = (TextView) view.findViewById(R.id.SBH_btnUp9);

        btnUp10 = (TextView) view.findViewById(R.id.SBH_btnUp10);
        btnUp11 = (TextView) view.findViewById(R.id.SBH_btnUp11);
        btnUp12 = (TextView) view.findViewById(R.id.SBH_btnUp12);
        btnUp13 = (TextView) view.findViewById(R.id.SBH_btnUp13);
        btnUp14 = (TextView) view.findViewById(R.id.SBH_btnUp14);
        btnUp15 = (TextView) view.findViewById(R.id.SBH_btnUp15);
        btnUp16 = (TextView) view.findViewById(R.id.SBH_btnUp16);
        btnUp17 = (TextView) view.findViewById(R.id.SBH_btnUp17);
        btnUp18 = (TextView) view.findViewById(R.id.SBH_btnUp18);


        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1++;
                format(txtScore1, score1);
                processHole1();
                calculateTotal();
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
                format(txtScore2, score2);
                processHole2();
                calculateTotal();
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
                format(txtScore3, score3);
                processHole3();
                calculateTotal();
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
                format(txtScore4, score4);
                processHole4();
                calculateTotal();
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
                format(txtScore5, score5);
                processHole5();
                calculateTotal();
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
                format(txtScore6, score6);
                processHole6();
                calculateTotal();
            }
        });
        btnUp7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score7 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score7++;
                format(txtScore7, score7);
                processHole7();
                calculateTotal();
            }
        });

        btnUp8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score8 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score8++;
                format(txtScore8, score8);
                processHole8();
                calculateTotal();
            }
        });
        btnUp9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score9 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score9++;
                format(txtScore9, score9);
                processHole9();
                calculateTotal();
            }
        });
        btnUp10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score10 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score10++;
                format(txtScore10, score10);
                processHole10();
                calculateTotal();
            }
        });
        btnUp11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score11 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score11++;
                format(txtScore11, score11);
                processHole11();
                calculateTotal();
            }
        });
        btnUp12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score12 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score12++;
                format(txtScore12, score12);
                processHole12();
                calculateTotal();
            }
        });
        btnUp13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score13 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score13++;
                format(txtScore13, score13);
                processHole13();
                calculateTotal();
            }
        });
        btnUp14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score14 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score14++;
                format(txtScore14, score14);
                processHole14();
                calculateTotal();
            }
        });
        btnUp15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score15 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score15++;
                format(txtScore15, score15);
                processHole15();
                calculateTotal();
            }
        });
        btnUp16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score16 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score16++;
                format(txtScore16, score16);
                processHole16();
                calculateTotal();
            }
        });
        btnUp17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score17 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score17++;
                format(txtScore17, score17);
                processHole17();
                calculateTotal();
            }
        });
        btnUp18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score18 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score18++;
                format(txtScore18, score18);
                processHole18();
                calculateTotal();
            }
        });
        //################################################## points
        btnUpX1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points1 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points1++;
                format(txtPoints1, points1);
                processHole1();
                calculateTotal();
            }
        });
        btnUpX2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points2 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points2++;
                format(txtPoints2, points2);
                processHole2();
                calculateTotal();
            }
        });
        btnUpX3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points3 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points3++;
                format(txtPoints3, points3);
                processHole3();
                calculateTotal();
            }
        });
        btnUpX4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points4 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points4++;
                format(txtPoints4, points4);
                processHole4();
                calculateTotal();
            }
        });
        btnUpX5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points5 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points5++;
                format(txtPoints5, points5);
                processHole5();
                calculateTotal();
            }
        });
        btnUpX6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points6 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points6++;
                format(txtPoints6, points6);
                processHole6();
                calculateTotal();
            }
        });
        btnUpX7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points7 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points7++;
                format(txtPoints7, points7);
                processHole7();
                calculateTotal();
            }
        });

        btnUpX8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points8 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points8++;
                format(txtPoints8, points8);
                processHole8();
                calculateTotal();
            }
        });
        btnUpX9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points9 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points9++;
                format(txtPoints9, points9);
                processHole9();
                calculateTotal();
            }
        });
        btnUpX10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points10 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points10++;
                format(txtPoints10, points10);
                processHole10();
                calculateTotal();
            }
        });
        btnUpX11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points11 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points11++;
                format(txtPoints11, points11);
                processHole11();
                calculateTotal();
            }
        });
        btnUpX12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points12 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points12++;
                format(txtPoints12, points12);
                processHole12();
                calculateTotal();
            }
        });
        btnUpX13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points13 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points13++;
                format(txtPoints13, points13);
                processHole13();
                calculateTotal();
            }
        });
        btnUpX14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points14 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points14++;
                format(txtPoints14, points14);
                processHole14();
                calculateTotal();
            }
        });
        btnUpX15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points15 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points15++;
                format(txtPoints15, points15);
                processHole15();
                calculateTotal();
            }
        });
        btnUpX16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points16 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points16++;
                format(txtPoints16, points16);
                processHole16();
                calculateTotal();
            }
        });
        btnUpX17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points17 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points17++;
                format(txtPoints17, points17);
                processHole17();
                calculateTotal();
            }
        });
        btnUpX18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points18 == UPPER_LIMIT_POINTS) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points18++;
                format(txtPoints18, points18);
                processHole18();
                calculateTotal();
            }
        });
    }


    private void setDownButtons() {
        //
        btnDownX1 = (TextView) view.findViewById(R.id.SBH_btnPointsDown1);
        btnDownX2 = (TextView) view.findViewById(R.id.SBH_btnPointsDown2);
        btnDownX3 = (TextView) view.findViewById(R.id.SBH_btnPointsDown3);
        btnDownX4 = (TextView) view.findViewById(R.id.SBH_btnPointsDown4);
        btnDownX5 = (TextView) view.findViewById(R.id.SBH_btnPointsDown5);
        btnDownX6 = (TextView) view.findViewById(R.id.SBH_btnPointsDown6);
        btnDownX7 = (TextView) view.findViewById(R.id.SBH_btnPointsDown7);
        btnDownX8 = (TextView) view.findViewById(R.id.SBH_btnPointsDown8);
        btnDownX9 = (TextView) view.findViewById(R.id.SBH_btnPointsDown9);

        btnDownX10 = (TextView) view.findViewById(R.id.SBH_btnPointsDown10);
        btnDownX11 = (TextView) view.findViewById(R.id.SBH_btnPointsDown11);
        btnDownX12 = (TextView) view.findViewById(R.id.SBH_btnPointsDown12);
        btnDownX13 = (TextView) view.findViewById(R.id.SBH_btnPointsDown13);
        btnDownX14 = (TextView) view.findViewById(R.id.SBH_btnPointsDown14);
        btnDownX15 = (TextView) view.findViewById(R.id.SBH_btnPointsDown15);
        btnDownX16 = (TextView) view.findViewById(R.id.SBH_btnPointsDown16);
        btnDownX17 = (TextView) view.findViewById(R.id.SBH_btnPointsDown17);
        btnDownX18 = (TextView) view.findViewById(R.id.SBH_btnPointsDown18);
        //
        btnDown1 = (TextView) view.findViewById(R.id.SBH_btnDown1);
        btnDown2 = (TextView) view.findViewById(R.id.SBH_btnDown2);
        btnDown3 = (TextView) view.findViewById(R.id.SBH_btnDown3);
        btnDown4 = (TextView) view.findViewById(R.id.SBH_btnDown4);
        btnDown5 = (TextView) view.findViewById(R.id.SBH_btnDown5);
        btnDown6 = (TextView) view.findViewById(R.id.SBH_btnDown6);
        btnDown7 = (TextView) view.findViewById(R.id.SBH_btnDown7);
        btnDown8 = (TextView) view.findViewById(R.id.SBH_btnDown8);
        btnDown9 = (TextView) view.findViewById(R.id.SBH_btnDown9);

        btnDown10 = (TextView) view.findViewById(R.id.SBH_btnDown10);
        btnDown11 = (TextView) view.findViewById(R.id.SBH_btnDown11);
        btnDown12 = (TextView) view.findViewById(R.id.SBH_btnDown12);
        btnDown13 = (TextView) view.findViewById(R.id.SBH_btnDown13);
        btnDown14 = (TextView) view.findViewById(R.id.SBH_btnDown14);
        btnDown15 = (TextView) view.findViewById(R.id.SBH_btnDown15);
        btnDown16 = (TextView) view.findViewById(R.id.SBH_btnDown16);
        btnDown17 = (TextView) view.findViewById(R.id.SBH_btnDown17);
        btnDown18 = (TextView) view.findViewById(R.id.SBH_btnDown18);
        btnDown1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1--;
                format(txtScore1, score1);
                processHole1();
                calculateTotal();
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
                format(txtScore2, score2);
                processHole2();
                calculateTotal();
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
                format(txtScore3, score3);
                processHole3();
                calculateTotal();
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
                format(txtScore4, score4);
                processHole4();
                calculateTotal();
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
                format(txtScore5, score5);
                processHole5();
                calculateTotal();
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
                format(txtScore6, score6);
                processHole6();
                calculateTotal();
            }
        });
        btnDown7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score7 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score7--;
                format(txtScore7, score7);
                processHole7();
                calculateTotal();
            }
        });
        btnDown8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score8 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score8--;
                format(txtScore8, score8);
                processHole8();
                calculateTotal();
            }
        });
        btnDown9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score9 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score9--;
                format(txtScore9, score9);
                processHole9();
                calculateTotal();
            }
        });
        btnDown10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score10 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score10--;
                format(txtScore10, score10);
                processHole10();
                calculateTotal();
            }
        });
        btnDown11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score11 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score11--;
                format(txtScore11, score11);
                processHole11();
                calculateTotal();
            }
        });
        btnDown12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score12 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score12--;
                format(txtScore12, score12);
                processHole12();
                calculateTotal();
            }
        });
        btnDown13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score13 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score13--;
                format(txtScore13, score13);
                processHole13();
                calculateTotal();
            }
        });
        btnDown14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score14 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score14--;
                format(txtScore14, score14);
                processHole14();
                calculateTotal();
            }
        });
        btnDown15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score15 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score15--;
                format(txtScore15, score15);
                processHole15();
                calculateTotal();
            }
        });
        btnDown16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score16 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score16--;
                format(txtScore16, score16);
                processHole16();
                calculateTotal();
            }
        });
        btnDown17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score17 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score17--;
                format(txtScore17, score17);
                processHole17();
                calculateTotal();
            }
        });
        btnDown18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score18 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score18--;
                format(txtScore18, score18);
                processHole18();
                calculateTotal();
            }
        });
        //################################################## points
        btnDownX1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points1 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points1--;
                format(txtPoints1, points1);
                processHole1();
                calculateTotal();
            }
        });
        btnDownX2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points2 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points2--;
                format(txtPoints2, points2);
                processHole2();
                calculateTotal();
            }
        });
        btnDownX3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points3 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points3--;
                format(txtPoints3, points3);
                processHole3();
                calculateTotal();
            }
        });
        btnDownX4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points4 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points4--;
                format(txtPoints4, points4);
                processHole4();
                calculateTotal();
            }
        });
        btnDownX5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points5 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points5--;
                format(txtPoints5, points5);
                processHole5();
                calculateTotal();
            }
        });
        btnDownX6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points6 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points6--;
                format(txtPoints6, points6);
                processHole6();
                calculateTotal();
            }
        });
        btnDownX7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points7 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points7--;
                format(txtPoints7, points7);
                processHole7();
                calculateTotal();
            }
        });
        btnDownX8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points8 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points8--;
                format(txtPoints8, points8);
                processHole8();
                calculateTotal();
            }
        });
        btnDownX9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points9 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points9--;
                format(txtPoints9, points9);
                processHole9();
                calculateTotal();
            }
        });
        btnDownX10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points10 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points10--;
                format(txtPoints10, points10);
                processHole10();
                calculateTotal();
            }
        });
        btnDownX11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points11 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points11--;
                format(txtPoints11, points11);
                processHole11();
                calculateTotal();
            }
        });
        btnDownX12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points12 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points12--;
                format(txtPoints12, points12);
                processHole12();
                calculateTotal();
            }
        });
        btnDownX13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points13 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points13--;
                format(txtPoints13, points13);
                processHole13();
                calculateTotal();
            }
        });
        btnDownX14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points14 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points14--;
                format(txtPoints14, points14);
                processHole14();
                calculateTotal();
            }
        });
        btnDownX15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points15 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points15--;
                format(txtPoints15, points15);
                processHole15();
                calculateTotal();
            }
        });
        btnDownX16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points16 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points16--;
                format(txtPoints16, points16);
                processHole16();
                calculateTotal();
            }
        });
        btnDownX17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points17 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points17--;
                format(txtPoints17, points17);
                processHole17();
                calculateTotal();
            }
        });
        btnDownX18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points18 == 0) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                points18--;
                format(txtPoints18, points18);
                processHole18();
                calculateTotal();
            }
        });

    }

    private void processHole1() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().get(0).setScore1(score1);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().get(1).setScore1(score1);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().get(2).setScore1(score1);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().get(3).setScore1(score1);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().get(4).setScore1(score1);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().get(5).setScore1(score1);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints1(points1);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints1(points1);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints1(points1);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints1(points1);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints1(points1);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints1(points1);
                break;
        }
    }

    private void processHole2() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().get(0).setScore2(score2);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().get(1).setScore2(score2);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().get(2).setScore2(score2);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().get(3).setScore2(score2);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().get(4).setScore2(score2);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().get(5).setScore2(score2);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints2(points2);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints2(points2);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints2(points2);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints2(points2);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints2(points2);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints2(points2);
                break;
        }
    }

    private void processHole3() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore3(score3);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore3(score3);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore3(score3);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore3(score3);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore3(score3);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore3(score3);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints3(points3);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints3(points3);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints3(points3);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints3(points3);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints3(points3);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints3(points3);
                break;
        }
    }

    private void processHole4() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore4(score4);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore4(score4);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore4(score4);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore4(score4);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore4(score4);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore4(score4);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints4(points4);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints4(points4);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints4(points4);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints4(points4);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints4(points4);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints4(points4);
                break;
        }
    }

    private void processHole5() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore5(score5);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore5(score5);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore5(score5);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore5(score5);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore5(score5);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore5(score5);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints5(points5);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints5(points5);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints5(points5);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints5(points5);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints5(points5);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints5(points5);
                break;
        }
    }

    private void processHole6() {
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore6(score6);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore6(score6);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore6(score6);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore6(score6);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore6(score6);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore6(score6);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints6(points6);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints6(points6);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints6(points6);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints6(points6);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints6(points6);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints6(points6);
                break;
        }
    }

    private void processHole7() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore7(score7);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore7(score7);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore7(score7);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore7(score7);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore7(score7);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore7(score7);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints7(points7);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints7(points7);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints7(points7);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints7(points7);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints7(points7);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints7(points7);
                break;
        }
    }

    private void processHole8() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore8(score8);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore8(score8);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore8(score8);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore8(score8);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore8(score8);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore8(score8);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints8(points8);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints8(points8);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints8(points8);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints8(points8);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints8(points8);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints8(points8);
                break;
        }
    }

    private void processHole9() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore9(score9);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore9(score9);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore9(score9);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore9(score9);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore9(score9);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore9(score9);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints9(points9);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints9(points9);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints9(points9);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints9(points9);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints9(points9);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints9(points9);
                break;
        }
    }

    private void processHole10() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore10(score10);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore10(score10);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore10(score10);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore10(score10);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore10(score10);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore10(score10);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints10(points10);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints10(points10);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints10(points10);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints10(points10);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints10(points10);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints10(points10);
                break;
        }
    }

    private void processHole11() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore11(score11);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore11(score11);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore11(score11);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore11(score11);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore11(score11);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore11(score11);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints11(points11);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints11(points11);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints11(points11);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints11(points11);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints11(points11);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints11(points11);
                break;
        }
    }

    private void processHole12() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore12(score12);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore12(score12);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore12(score12);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore12(score12);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore12(score12);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore12(score12);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints12(points12);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints12(points12);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints12(points12);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints12(points12);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints12(points12);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints12(points12);
                break;
        }
    }

    private void processHole13() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore13(score13);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore13(score13);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore13(score13);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore13(score13);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore13(score13);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore13(score13);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints13(points13);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints13(points13);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints13(points13);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints13(points13);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints13(points13);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints13(points13);
                break;
        }
    }

    private void processHole14() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore14(score14);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore14(score14);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore14(score14);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore14(score14);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore14(score14);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore14(score14);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints14(points14);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints14(points14);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints14(points14);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints14(points14);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints14(points14);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints14(points14);
                break;
        }
    }

    private void processHole15() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore15(score15);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore15(score15);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore15(score15);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore15(score15);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore15(score15);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore15(score15);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints15(points15);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints15(points15);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints15(points15);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints15(points15);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints15(points15);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints15(points15);
                break;
        }
    }

    private void processHole16() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore16(score16);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore16(score16);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore16(score16);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore16(score16);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore16(score16);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore16(score16);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints16(points16);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints16(points16);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints16(points16);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints16(points16);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints16(points16);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints16(points16);
                break;
        }
    }

    private void processHole17() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore17(score17);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore17(score17);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore17(score17);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore17(score17);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore17(score17);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore17(score17);
                break;
        }
        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints17(points17);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints17(points17);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints17(points17);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints17(points17);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints17(points17);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints17(points17);
                break;
        }
    }

    private void processHole18() {

        switch (currentRound) {
            case 1:
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(0).setPoints18(points18);
                break;
            case 2:
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(1).setPoints18(points18);
                break;
            case 3:
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(2).setPoints18(points18);
                break;
            case 4:
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(3).setPoints18(points18);
                break;
            case 5:
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(4).setPoints18(points18);
                break;
            case 6:
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setScore18(score18);
                leaderBoard.getTourneyScoreByRoundList().
                        get(5).setPoints18(points18);
                break;
        }
    }

    private void format(TextView txt, int score) {
        if (score < 10) {
            txt.setText("0" + score);
        } else {
            txt.setText("" + score);
        }
    }

    private void calculateTotal() {
        if (leaderBoard == null) return;
        roundTotal = 0;
        roundPointTotal = 0;
        if (leaderBoard.getTourneyScoreByRoundList() == null) {
            Log.e(LOG, "this is == null");
            return;
        }
        switch (currentRound) {
            case 1:

                TourneyScoreByRoundDTO tsb = leaderBoard.getTourneyScoreByRoundList().get(0);
                roundTotal += tsb.getScore1();
                roundTotal += tsb.getScore2();
                roundTotal += tsb.getScore3();
                roundTotal += tsb.getScore4();
                roundTotal += tsb.getScore5();
                roundTotal += tsb.getScore6();
                roundTotal += tsb.getScore7();
                roundTotal += tsb.getScore8();
                roundTotal += tsb.getScore9();
                roundTotal += tsb.getScore10();
                roundTotal += tsb.getScore11();
                roundTotal += tsb.getScore12();
                roundTotal += tsb.getScore13();
                roundTotal += tsb.getScore14();
                roundTotal += tsb.getScore15();
                roundTotal += tsb.getScore16();
                roundTotal += tsb.getScore17();
                roundTotal += tsb.getScore18();
                //
                roundPointTotal += tsb.getPoints1();
                roundPointTotal += tsb.getPoints2();
                roundPointTotal += tsb.getPoints3();
                roundPointTotal += tsb.getPoints4();
                roundPointTotal += tsb.getPoints5();
                roundPointTotal += tsb.getPoints6();
                roundPointTotal += tsb.getPoints7();
                roundPointTotal += tsb.getPoints8();
                roundPointTotal += tsb.getPoints9();
                roundPointTotal += tsb.getPoints10();
                roundPointTotal += tsb.getPoints11();
                roundPointTotal += tsb.getPoints12();
                roundPointTotal += tsb.getPoints13();
                roundPointTotal += tsb.getPoints14();
                roundPointTotal += tsb.getPoints15();
                roundPointTotal += tsb.getPoints16();
                roundPointTotal += tsb.getPoints17();
                roundPointTotal += tsb.getPoints18();
                break;

            case 2:
                TourneyScoreByRoundDTO tsb2 = leaderBoard.getTourneyScoreByRoundList().get(1);
                roundTotal += tsb2.getScore1();
                roundTotal += tsb2.getScore2();
                roundTotal += tsb2.getScore3();
                roundTotal += tsb2.getScore4();
                roundTotal += tsb2.getScore5();
                roundTotal += tsb2.getScore6();
                roundTotal += tsb2.getScore7();
                roundTotal += tsb2.getScore8();
                roundTotal += tsb2.getScore9();
                roundTotal += tsb2.getScore10();
                roundTotal += tsb2.getScore11();
                roundTotal += tsb2.getScore12();
                roundTotal += tsb2.getScore13();
                roundTotal += tsb2.getScore14();
                roundTotal += tsb2.getScore15();
                roundTotal += tsb2.getScore16();
                roundTotal += tsb2.getScore17();
                roundTotal += tsb2.getScore18();
                //
                roundPointTotal += tsb2.getPoints1();
                roundPointTotal += tsb2.getPoints2();
                roundPointTotal += tsb2.getPoints3();
                roundPointTotal += tsb2.getPoints4();
                roundPointTotal += tsb2.getPoints5();
                roundPointTotal += tsb2.getPoints6();
                roundPointTotal += tsb2.getPoints7();
                roundPointTotal += tsb2.getPoints8();
                roundPointTotal += tsb2.getPoints9();
                roundPointTotal += tsb2.getPoints10();
                roundPointTotal += tsb2.getPoints11();
                roundPointTotal += tsb2.getPoints12();
                roundPointTotal += tsb2.getPoints13();
                roundPointTotal += tsb2.getPoints14();
                roundPointTotal += tsb2.getPoints15();
                roundPointTotal += tsb2.getPoints16();
                roundPointTotal += tsb2.getPoints17();
                roundPointTotal += tsb2.getPoints18();
                break;
            case 3:
                TourneyScoreByRoundDTO tsb3 = leaderBoard.getTourneyScoreByRoundList().get(2);
                roundTotal += tsb3.getScore1();
                roundTotal += tsb3.getScore2();
                roundTotal += tsb3.getScore3();
                roundTotal += tsb3.getScore4();
                roundTotal += tsb3.getScore5();
                roundTotal += tsb3.getScore6();
                roundTotal += tsb3.getScore7();
                roundTotal += tsb3.getScore8();
                roundTotal += tsb3.getScore9();
                roundTotal += tsb3.getScore10();
                roundTotal += tsb3.getScore11();
                roundTotal += tsb3.getScore12();
                roundTotal += tsb3.getScore13();
                roundTotal += tsb3.getScore14();
                roundTotal += tsb3.getScore15();
                roundTotal += tsb3.getScore16();
                roundTotal += tsb3.getScore17();
                roundTotal += tsb3.getScore18();
                //
                roundPointTotal += tsb3.getPoints1();
                roundPointTotal += tsb3.getPoints2();
                roundPointTotal += tsb3.getPoints3();
                roundPointTotal += tsb3.getPoints4();
                roundPointTotal += tsb3.getPoints5();
                roundPointTotal += tsb3.getPoints6();
                roundPointTotal += tsb3.getPoints7();
                roundPointTotal += tsb3.getPoints8();
                roundPointTotal += tsb3.getPoints9();
                roundPointTotal += tsb3.getPoints10();
                roundPointTotal += tsb3.getPoints11();
                roundPointTotal += tsb3.getPoints12();
                roundPointTotal += tsb3.getPoints13();
                roundPointTotal += tsb3.getPoints14();
                roundPointTotal += tsb3.getPoints15();
                roundPointTotal += tsb3.getPoints16();
                roundPointTotal += tsb3.getPoints17();
                roundPointTotal += tsb3.getPoints18();
                break;
            case 4:
                TourneyScoreByRoundDTO tsb4 = leaderBoard.getTourneyScoreByRoundList().get(3);
                roundTotal += tsb4.getScore1();
                roundTotal += tsb4.getScore2();
                roundTotal += tsb4.getScore3();
                roundTotal += tsb4.getScore4();
                roundTotal += tsb4.getScore5();
                roundTotal += tsb4.getScore6();
                roundTotal += tsb4.getScore7();
                roundTotal += tsb4.getScore8();
                roundTotal += tsb4.getScore9();
                roundTotal += tsb4.getScore10();
                roundTotal += tsb4.getScore11();
                roundTotal += tsb4.getScore12();
                roundTotal += tsb4.getScore13();
                roundTotal += tsb4.getScore14();
                roundTotal += tsb4.getScore15();
                roundTotal += tsb4.getScore16();
                roundTotal += tsb4.getScore17();
                roundTotal += tsb4.getScore18();
                //
                roundPointTotal += tsb4.getPoints1();
                roundPointTotal += tsb4.getPoints2();
                roundPointTotal += tsb4.getPoints3();
                roundPointTotal += tsb4.getPoints4();
                roundPointTotal += tsb4.getPoints5();
                roundPointTotal += tsb4.getPoints6();
                roundPointTotal += tsb4.getPoints7();
                roundPointTotal += tsb4.getPoints8();
                roundPointTotal += tsb4.getPoints9();
                roundPointTotal += tsb4.getPoints10();
                roundPointTotal += tsb4.getPoints11();
                roundPointTotal += tsb4.getPoints12();
                roundPointTotal += tsb4.getPoints13();
                roundPointTotal += tsb4.getPoints14();
                roundPointTotal += tsb4.getPoints15();
                roundPointTotal += tsb4.getPoints16();
                roundPointTotal += tsb4.getPoints17();
                roundPointTotal += tsb4.getPoints18();
                break;
            case 5:
                TourneyScoreByRoundDTO tsb5 = leaderBoard.getTourneyScoreByRoundList().get(4);
                roundTotal += tsb5.getScore1();
                roundTotal += tsb5.getScore2();
                roundTotal += tsb5.getScore3();
                roundTotal += tsb5.getScore4();
                roundTotal += tsb5.getScore5();
                roundTotal += tsb5.getScore6();
                roundTotal += tsb5.getScore7();
                roundTotal += tsb5.getScore8();
                roundTotal += tsb5.getScore9();
                roundTotal += tsb5.getScore10();
                roundTotal += tsb5.getScore11();
                roundTotal += tsb5.getScore12();
                roundTotal += tsb5.getScore13();
                roundTotal += tsb5.getScore14();
                roundTotal += tsb5.getScore15();
                roundTotal += tsb5.getScore16();
                roundTotal += tsb5.getScore17();
                roundTotal += tsb5.getScore18();
                //
                roundPointTotal += tsb5.getPoints1();
                roundPointTotal += tsb5.getPoints2();
                roundPointTotal += tsb5.getPoints3();
                roundPointTotal += tsb5.getPoints4();
                roundPointTotal += tsb5.getPoints5();
                roundPointTotal += tsb5.getPoints6();
                roundPointTotal += tsb5.getPoints7();
                roundPointTotal += tsb5.getPoints8();
                roundPointTotal += tsb5.getPoints9();
                roundPointTotal += tsb5.getPoints10();
                roundPointTotal += tsb5.getPoints11();
                roundPointTotal += tsb5.getPoints12();
                roundPointTotal += tsb5.getPoints13();
                roundPointTotal += tsb5.getPoints14();
                roundPointTotal += tsb5.getPoints15();
                roundPointTotal += tsb5.getPoints16();
                roundPointTotal += tsb5.getPoints17();
                roundPointTotal += tsb5.getPoints18();
                break;
            case 6:
                TourneyScoreByRoundDTO tsb6 = leaderBoard.getTourneyScoreByRoundList().get(5);
                roundTotal += tsb6.getScore1();
                roundTotal += tsb6.getScore2();
                roundTotal += tsb6.getScore3();
                roundTotal += tsb6.getScore4();
                roundTotal += tsb6.getScore5();
                roundTotal += tsb6.getScore6();
                roundTotal += tsb6.getScore7();
                roundTotal += tsb6.getScore8();
                roundTotal += tsb6.getScore9();
                roundTotal += tsb6.getScore10();
                roundTotal += tsb6.getScore11();
                roundTotal += tsb6.getScore12();
                roundTotal += tsb6.getScore13();
                roundTotal += tsb6.getScore14();
                roundTotal += tsb6.getScore15();
                roundTotal += tsb6.getScore16();
                roundTotal += tsb6.getScore17();
                roundTotal += tsb6.getScore18();
                //
                roundPointTotal += tsb6.getPoints1();
                roundPointTotal += tsb6.getPoints2();
                roundPointTotal += tsb6.getPoints3();
                roundPointTotal += tsb6.getPoints4();
                roundPointTotal += tsb6.getPoints5();
                roundPointTotal += tsb6.getPoints6();
                roundPointTotal += tsb6.getPoints7();
                roundPointTotal += tsb6.getPoints8();
                roundPointTotal += tsb6.getPoints9();
                roundPointTotal += tsb6.getPoints10();
                roundPointTotal += tsb6.getPoints11();
                roundPointTotal += tsb6.getPoints12();
                roundPointTotal += tsb6.getPoints13();
                roundPointTotal += tsb6.getPoints14();
                roundPointTotal += tsb6.getPoints15();
                roundPointTotal += tsb6.getPoints16();
                roundPointTotal += tsb6.getPoints17();
                roundPointTotal += tsb6.getPoints18();
                break;

        }
        switch (tournament.getTournamentType()) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                txtTotal.setText("" + roundPointTotal);
                break;

            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                txtTotal.setText("" + roundTotal);
                break;
        }


    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    Vibrator vb;
    TextView txtPlayer, txtScore1, txtScore2, txtScore3, txtScore4, txtScore5, txtScore6,
            txtScore7, txtScore8, txtScore9, txtScore10, txtScore11, txtScore12,
            txtScore13, txtScore14, txtScore15, txtScore16, txtScore17, txtScore18;

    TextView txtPoints1, txtPoints2, txtPoints3, txtPoints4, txtPoints5, txtPoints6,
            txtPoints7, txtPoints8, txtPoints9, txtPoints10, txtPoints11, txtPoints12,
            txtPoints13, txtPoints14, txtPoints15, txtPoints16, txtPoints17, txtPoints18;

    TextView btnUp1, btnUp2, btnUp3, btnUp4, btnUp5, btnUp6, btnUp7, btnUp8, btnUp9, btnUp10,
            btnUp11, btnUp12, btnUp13, btnUp14, btnUp15, btnUp16, btnUp17, btnUp18;

    TextView btnUpX1, btnUpX2, btnUpX3, btnUpX4, btnUpX5, btnUpX6, btnUpX7, btnUpX8, btnUpX9, btnUpX10,
            btnUpX11, btnUpX12, btnUpX13, btnUpX14, btnUpX15, btnUpX16, btnUpX17, btnUpX18;


    TextView btnDown1, btnDown2, btnDown3, btnDown4, btnDown5, btnDown6, btnDown7, btnDown8, btnDown9,
            btnDown10, btnDown11, btnDown12, btnDown13, btnDown14, btnDown15, btnDown16, btnDown17,
            btnDown18, txtTotal;

    TextView btnDownX1, btnDownX2, btnDownX3, btnDownX4, btnDownX5, btnDownX6, btnDownX7, btnDownX8, btnDownX9,
            btnDownX10, btnDownX11, btnDownX12, btnDownX13, btnDownX14, btnDownX15, btnDownX16, btnDownX17,
            btnDownX18;
    View vPoints1, vPoints2, vPoints3, vPoints4, vPoints5, vPoints6, vPoints7, vPoints8, vPoints9,
            vPoints10, vPoints11, vPoints12, vPoints13, vPoints14, vPoints15, vPoints16, vPoints17, vPoints18;

    private void showPoints() {
        vPoints1.setVisibility(View.VISIBLE);
        vPoints2.setVisibility(View.VISIBLE);
        vPoints3.setVisibility(View.VISIBLE);
        vPoints4.setVisibility(View.VISIBLE);
        vPoints5.setVisibility(View.VISIBLE);
        vPoints6.setVisibility(View.VISIBLE);
        vPoints7.setVisibility(View.VISIBLE);
        vPoints8.setVisibility(View.VISIBLE);
        vPoints9.setVisibility(View.VISIBLE);
        vPoints10.setVisibility(View.VISIBLE);
        vPoints11.setVisibility(View.VISIBLE);
        vPoints12.setVisibility(View.VISIBLE);
        vPoints13.setVisibility(View.VISIBLE);
        vPoints14.setVisibility(View.VISIBLE);
        vPoints15.setVisibility(View.VISIBLE);
        vPoints16.setVisibility(View.VISIBLE);
        vPoints17.setVisibility(View.VISIBLE);
        vPoints18.setVisibility(View.VISIBLE);
    }

    private void hidePoints() {
        vPoints1.setVisibility(View.GONE);
        vPoints2.setVisibility(View.GONE);
        vPoints3.setVisibility(View.GONE);
        vPoints4.setVisibility(View.GONE);
        vPoints5.setVisibility(View.GONE);
        vPoints6.setVisibility(View.GONE);
        vPoints7.setVisibility(View.GONE);
        vPoints8.setVisibility(View.GONE);
        vPoints9.setVisibility(View.GONE);
        vPoints10.setVisibility(View.GONE);
        vPoints11.setVisibility(View.GONE);
        vPoints12.setVisibility(View.GONE);
        vPoints13.setVisibility(View.GONE);
        vPoints14.setVisibility(View.GONE);
        vPoints15.setVisibility(View.GONE);
        vPoints16.setVisibility(View.GONE);
        vPoints17.setVisibility(View.GONE);
        vPoints18.setVisibility(View.GONE);
    }

    int currentRound;
    Spinner spinner;
    View view;
    TournamentDTO tournament;
    LeaderBoardDTO leaderBoard;
    AdministratorDTO administrator;
    GolfGroupDTO golfGroup;
    Context ctx;
    ResponseDTO response;
    Button btnSave;
    RelativeLayout frontNine, backNine;
    static final String LOG = "ScoringByHoleFragment";

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
        Log.i(LOG, "Tournament has been set - to set spinner");
        if (tournament.getGolfRounds() == 1) {
            currentRound = 1;
            spinner.setVisibility(View.GONE);
        } else {
            setRoundSpinner();
        }

        switch (tournament.getTournamentType()) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                showPoints();
                break;
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                hidePoints();
                break;
        }
        if (tournament.getClosedForScoringFlag() > 0) {
            disableButtons();

        }

    }


    public void setTourneyPlayerScore(LeaderBoardDTO leaderBoard) {
        this.leaderBoard = leaderBoard;
        Log.i(LOG, "TourneyPlayerScore has been set - to fill scorecard");

        calculateCurrentRound();
        //TODO - THINK!! - check current round's tee - abort if 0 or go to tee activity
//        try {
//            TourneyScoreByRoundDTO x = leaderBoard.getTourneyScoreByRoundList().get(currentRound - 1);
//            if (x.getTee() == 0) { //we have a problem
//                Intent intent = new Intent(ctx, TeeTimeActivity.class);
//                intent.putExtra("tournament", tournament);
//                intent.putExtra("currentRound", currentRound);
//                intent.putExtra("leaderBoard", leaderBoard);
//                startActivityForResult(intent, REQUEST_TEE_TIME);
//                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.set_tee), 5, Gravity.CENTER);
//                return;
//            }
//        } catch (Exception e) {
//
//        }
        if (currentRound == 0) {
            currentRound = 1;
            tsbr = leaderBoard.getTourneyScoreByRoundList().get(0);
        } else {
            tsbr = leaderBoard.getTourneyScoreByRoundList().get(currentRound - 1);
        }
        fillScoreCard();
        txtPlayer.setText(leaderBoard.getPlayer().getFullName());
        if (tournament.getHolesPerRound() == 9) {
            reminder.setVisibility(View.VISIBLE);
        } else {
            reminder.setVisibility(View.GONE);
        }
        Statics.setRobotoFontBold(ctx,txtPlayer);
        ImageView imageView = (ImageView)view.findViewById(R.id.SBH_image);
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("golfgroup")
                .append(SharedUtil.getGolfGroup(ctx).getGolfGroupID()).append("/")
                .append("player/t")
                .append(leaderBoard.getPlayer().getPlayerID())
                .append(".jpg");
        Picasso.with(ctx).load(sb.toString()).placeholder(ctx.getResources().getDrawable(R.drawable.boy)).into(imageView);

//        if (tournament.getHolesPerRound() == 9) {
//            if (leaderBoard.getTourneyScoreByRoundList()
//                    .get(currentRound - 1).getTee() == 0) {
//                //no tee times set
//                backNine.setVisibility(View.VISIBLE);
//                frontNine.setVisibility(View.VISIBLE);
//            } else {
//                if (leaderBoard.getTourneyScoreByRoundList()
//                        .get(currentRound - 1).getTee() == 1) {
//                    frontNineOn();
//                } else {
//                    backNineOn();
//                }
//            }
//        }
    }

    private void frontNineOn() {
        backNine.setVisibility(View.GONE);
        frontNine.setVisibility(View.VISIBLE);
    }

    private void backNineOn() {
        backNine.setVisibility(View.VISIBLE);
        frontNine.setVisibility(View.GONE);
    }

    private void calculateCurrentRound() {
        if (leaderBoard == null) return;
        CompleteRounds.markScoringCompletion(leaderBoard);

        int index = 0;

        for (TourneyScoreByRoundDTO tbs : leaderBoard.getTourneyScoreByRoundList()) {
            if (tbs.getScoringComplete() == 0) {
                currentRound = index + 1;
                spinner.setSelection(index);
                break;
            }
            index++;
        }
    }

    public void setAdministrator(AdministratorDTO administrator) {
        this.administrator = administrator;
    }

    public void setGolfGroup(GolfGroupDTO golfGroup) {
        this.golfGroup = golfGroup;
    }

    static final int BASE_SCORE = 3;
    int score1 = BASE_SCORE,
            score2 = BASE_SCORE,
            score3 = BASE_SCORE,
            score4 = BASE_SCORE,
            score5 = BASE_SCORE,
            score6 = BASE_SCORE,
            score7 = BASE_SCORE,
            score8 = BASE_SCORE,
            score9 = BASE_SCORE,
            score10 = BASE_SCORE,
            score11 = BASE_SCORE,
            score12 = BASE_SCORE,
            score13 = BASE_SCORE,
            score14 = BASE_SCORE,
            score15 = BASE_SCORE,
            score16 = BASE_SCORE,
            score17 = BASE_SCORE,
            score18 = BASE_SCORE;
    int points1 = 0,
            points2 = 0,
            points3 = 0,
            points4 = 0,
            points5 = 0,
            points6 = 0,
            points7 = 0,
            points8 = 0,
            points9 = 0,
            points10 = 0,
            points11 = 0,
            points12 = 0,
            points13 = 0,
            points14 = 0,
            points15 = 0,
            points16 = 0,
            points17 = 0,
            points18 = 0;
    int roundTotal, roundPointTotal;
    static final int REQUEST_TEE_TIME = 303;

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.i(LOG, "onActivityResult reqCode: " + reqCode + " resCode: " + resCode);
        if (resCode == Activity.RESULT_OK) {
            switch (reqCode) {

            }
        }
    }
}
