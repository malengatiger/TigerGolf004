package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.data.ClubCourseDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/05/10.
 */
public class ClubScorecardFragment extends Fragment {
    static final String LOG = "ClubScorecardFragment";
    Context ctx;
    View view;
    ProgressBar bar;
    Button btnSave;
    RadioButton radio9, radio18;

    public interface ClubScorecardListener {
        public void onScorecardUpdated(ClubCourseDTO cc);
    }

    ClubScorecardListener clubScorecardListener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof ClubScorecardListener) {
            clubScorecardListener = (ClubScorecardListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement ClubScorecardListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    FragmentActivity act;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "onCreateView.........");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.dialog_club_scorecard_edit, container, false);
        setFields();
        return view;
    }

    private void submit() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_CLUB_COURSE);
        w.setClubCourse(clubCourse);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        bar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.GONE);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        clubScorecardListener.onScorecardUpdated(clubCourse);
                    }
                });
            }

            @Override
            public void onClose() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.socket_closed));
                    }
                });
            }

            @Override
            public void onError(final String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }


        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                bar.setVisibility(View.GONE);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                clubScorecardListener.onScorecardUpdated(clubCourse);
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                bar.setVisibility(View.GONE);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });

    }

    public void setClub(ClubDTO club) {
        Log.e(LOG, "******** setClub " + club);
        this.club = club;

        txtClub.setText(club.getClubName());
        setCourseSpinner();
    }

    private void setCourseSpinner() {
        if (club.getClubCourses() == null || club.getClubCourses().isEmpty()) {
            //TODO - create new clubcourse and add it to club on server
        }
        if (club.getClubCourses().size() == 1) {
            clubCourse = club.getClubCourses().get(0);
            courseSpinner.setVisibility(View.GONE);
            if (clubCourse.getHoles() == 9) {
                radio9.setChecked(true);
            } else {
                radio18.setChecked(true);
            }
            fillScoreCard();
            calculateTotal();
            return;
        }
        List<String> list = new ArrayList<String>();
        list.add(ctx.getResources().getString(R.string.select_course));
        for (ClubCourseDTO cc : club.getClubCourses()) {
            list.add(cc.getCourseName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    clubCourse = null;
                    return;
                }
                clubCourse = club.getClubCourses().get(i - 1);
                if (clubCourse.getHoles() == 9) {
                    radio9.setChecked(true);
                } else {
                    radio18.setChecked(true);
                }
                fillScoreCard();
                calculateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void fillScoreCard() {

        format(txtScore1, clubCourse.getParHole1());
        format(txtScore2, clubCourse.getParHole2());
        format(txtScore3, clubCourse.getParHole3());
        format(txtScore4, clubCourse.getParHole4());
        format(txtScore5, clubCourse.getParHole5());
        format(txtScore6, clubCourse.getParHole6());
        format(txtScore7, clubCourse.getParHole7());
        format(txtScore8, clubCourse.getParHole8());
        format(txtScore9, clubCourse.getParHole9());
        format(txtScore10, clubCourse.getParHole10());
        format(txtScore11, clubCourse.getParHole11());
        format(txtScore12, clubCourse.getParHole12());
        format(txtScore13, clubCourse.getParHole13());
        format(txtScore14, clubCourse.getParHole14());
        format(txtScore15, clubCourse.getParHole15());
        format(txtScore16, clubCourse.getParHole16());
        format(txtScore17, clubCourse.getParHole17());
        format(txtScore18, clubCourse.getParHole18());
    }

    private void setUpButtons() {
        btnUp1 = (TextView) view.findViewById(R.id.KSE_btnUp1);
        btnUp2 = (TextView) view.findViewById(R.id.KSE_btnUp2);
        btnUp3 = (TextView) view.findViewById(R.id.KSE_btnUp3);
        btnUp4 = (TextView) view.findViewById(R.id.KSE_btnUp4);
        btnUp5 = (TextView) view.findViewById(R.id.KSE_btnUp5);
        btnUp6 = (TextView) view.findViewById(R.id.KSE_btnUp6);
        btnUp7 = (TextView) view.findViewById(R.id.KSE_btnUp7);
        btnUp8 = (TextView) view.findViewById(R.id.KSE_btnUp8);
        btnUp9 = (TextView) view.findViewById(R.id.KSE_btnUp9);

        btnUp10 = (TextView) view.findViewById(R.id.KSE_btnUp10);
        btnUp11 = (TextView) view.findViewById(R.id.KSE_btnUp11);
        btnUp12 = (TextView) view.findViewById(R.id.KSE_btnUp12);
        btnUp13 = (TextView) view.findViewById(R.id.KSE_btnUp13);
        btnUp14 = (TextView) view.findViewById(R.id.KSE_btnUp14);
        btnUp15 = (TextView) view.findViewById(R.id.KSE_btnUp15);
        btnUp16 = (TextView) view.findViewById(R.id.KSE_btnUp16);
        btnUp17 = (TextView) view.findViewById(R.id.KSE_btnUp17);
        btnUp18 = (TextView) view.findViewById(R.id.KSE_btnUp18);


        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == UPPER_LIMIT) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1++;
                format(txtScore1, score1);
                clubCourse.setParHole1(score1);
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
                clubCourse.setParHole2(score2);
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
                clubCourse.setParHole3(score3);
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
                clubCourse.setParHole4(score4);
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
                clubCourse.setParHole5(score5);
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
                clubCourse.setParHole6(score6);
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
                clubCourse.setParHole7(score7);
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
                clubCourse.setParHole8(score8);
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
                clubCourse.setParHole9(score9);
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
                clubCourse.setParHole10(score10);
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
                clubCourse.setParHole11(score11);
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
                clubCourse.setParHole12(score12);
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
                clubCourse.setParHole13(score13);
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
                clubCourse.setParHole14(score14);
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
                clubCourse.setParHole15(score15);
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
                clubCourse.setParHole16(score16);
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
                clubCourse.setParHole17(score17);
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
                clubCourse.setParHole18(score18);
                calculateTotal();
            }
        });
    }

    private void setDownButtons() {
        btnDown1 = (TextView) view.findViewById(R.id.KSE_btnDown1);
        btnDown2 = (TextView) view.findViewById(R.id.KSE_btnDown2);
        btnDown3 = (TextView) view.findViewById(R.id.KSE_btnDown3);
        btnDown4 = (TextView) view.findViewById(R.id.KSE_btnDown4);
        btnDown5 = (TextView) view.findViewById(R.id.KSE_btnDown5);
        btnDown6 = (TextView) view.findViewById(R.id.KSE_btnDown6);
        btnDown7 = (TextView) view.findViewById(R.id.KSE_btnDown7);
        btnDown8 = (TextView) view.findViewById(R.id.KSE_btnDown8);
        btnDown9 = (TextView) view.findViewById(R.id.KSE_btnDown9);

        btnDown10 = (TextView) view.findViewById(R.id.KSE_btnDown10);
        btnDown11 = (TextView) view.findViewById(R.id.KSE_btnDown11);
        btnDown12 = (TextView) view.findViewById(R.id.KSE_btnDown12);
        btnDown13 = (TextView) view.findViewById(R.id.KSE_btnDown13);
        btnDown14 = (TextView) view.findViewById(R.id.KSE_btnDown14);
        btnDown15 = (TextView) view.findViewById(R.id.KSE_btnDown15);
        btnDown16 = (TextView) view.findViewById(R.id.KSE_btnDown16);
        btnDown17 = (TextView) view.findViewById(R.id.KSE_btnDown17);
        btnDown18 = (TextView) view.findViewById(R.id.KSE_btnDown18);
        btnDown1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score1 == 1) {
                    vb.vibrate(VIBRATION_TIME);
                    return;
                }
                score1--;
                format(txtScore1, score1);
                clubCourse.setParHole1(score1);
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
                clubCourse.setParHole2(score2);
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
                clubCourse.setParHole3(score3);
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
                clubCourse.setParHole4(score4);
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
                clubCourse.setParHole5(score5);
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
                clubCourse.setParHole6(score6);
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
                clubCourse.setParHole7(score7);
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
                clubCourse.setParHole8(score8);
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
                clubCourse.setParHole9(score9);
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
                clubCourse.setParHole10(score10);
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
                clubCourse.setParHole11(score11);
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
                clubCourse.setParHole12(score12);
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
                clubCourse.setParHole13(score13);
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
                clubCourse.setParHole14(score14);
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
                clubCourse.setParHole15(score16);
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
                clubCourse.setParHole16(score16);
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
                clubCourse.setParHole17(score17);
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
                clubCourse.setParHole18(score18);
                calculateTotal();
            }
        });

    }

    private void format(TextView txt, int score) {
        if (score < 10) {
            txt.setText("0" + score);
        } else {
            txt.setText("" + score);
        }
    }

    private int calculateTotal() {
        clubPar = 0;
        clubPar += clubCourse.getParHole1();
        clubPar += clubCourse.getParHole2();
        clubPar += clubCourse.getParHole3();
        clubPar += clubCourse.getParHole4();
        clubPar += clubCourse.getParHole5();
        clubPar += clubCourse.getParHole6();
        clubPar += clubCourse.getParHole7();
        clubPar += clubCourse.getParHole8();
        clubPar += clubCourse.getParHole9();
        clubPar += clubCourse.getParHole10();
        clubPar += clubCourse.getParHole11();
        clubPar += clubCourse.getParHole12();
        clubPar += clubCourse.getParHole13();
        clubPar += clubCourse.getParHole14();
        clubPar += clubCourse.getParHole15();
        clubPar += clubCourse.getParHole16();
        clubPar += clubCourse.getParHole17();
        clubPar += clubCourse.getParHole18();

        txtTotal.setText("" + clubPar);
        return clubPar;

    }

    private void validate() {

        if (!radio9.isChecked() && !radio18.isChecked()) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_holes));
            return;
        }
        if (radio9.isChecked()) clubCourse.setHoles(9);
        if (radio18.isChecked()) clubCourse.setHoles(18);
        clubCourse.setPar(calculateTotal());

        int cntPar5 = 0, cntPar4 = 0, cntPar3 = 0;
        if (clubCourse.getParHole1() == 5) cntPar5++;
        if (clubCourse.getParHole1() == 4) cntPar4++;
        if (clubCourse.getParHole1() == 3) cntPar3++;

        if (clubCourse.getParHole2() == 5) cntPar5++;
        if (clubCourse.getParHole2() == 4) cntPar4++;
        if (clubCourse.getParHole2() == 3) cntPar3++;

        if (clubCourse.getParHole3() == 5) cntPar5++;
        if (clubCourse.getParHole3() == 4) cntPar4++;
        if (clubCourse.getParHole3() == 3) cntPar3++;

        if (clubCourse.getParHole4() == 5) cntPar5++;
        if (clubCourse.getParHole4() == 4) cntPar4++;
        if (clubCourse.getParHole4() == 3) cntPar3++;

        if (clubCourse.getParHole5() == 5) cntPar5++;
        if (clubCourse.getParHole5() == 4) cntPar4++;
        if (clubCourse.getParHole5() == 3) cntPar3++;

        if (clubCourse.getParHole6() == 5) cntPar5++;
        if (clubCourse.getParHole6() == 4) cntPar4++;
        if (clubCourse.getParHole6() == 3) cntPar3++;

        if (clubCourse.getParHole7() == 5) cntPar5++;
        if (clubCourse.getParHole7() == 4) cntPar4++;
        if (clubCourse.getParHole7() == 3) cntPar3++;

        if (clubCourse.getParHole8() == 5) cntPar5++;
        if (clubCourse.getParHole8() == 4) cntPar4++;
        if (clubCourse.getParHole8() == 3) cntPar3++;

        if (clubCourse.getParHole10() == 5) cntPar5++;
        if (clubCourse.getParHole10() == 4) cntPar4++;
        if (clubCourse.getParHole10() == 3) cntPar3++;

        if (clubCourse.getParHole11() == 5) cntPar5++;
        if (clubCourse.getParHole11() == 4) cntPar4++;
        if (clubCourse.getParHole11() == 3) cntPar3++;

        if (clubCourse.getParHole12() == 5) cntPar5++;
        if (clubCourse.getParHole12() == 4) cntPar4++;
        if (clubCourse.getParHole12() == 3) cntPar3++;

        if (clubCourse.getParHole13() == 5) cntPar5++;
        if (clubCourse.getParHole13() == 4) cntPar4++;
        if (clubCourse.getParHole13() == 3) cntPar3++;

        if (clubCourse.getParHole14() == 5) cntPar5++;
        if (clubCourse.getParHole14() == 4) cntPar4++;
        if (clubCourse.getParHole14() == 3) cntPar3++;

        if (clubCourse.getParHole15() == 5) cntPar5++;
        if (clubCourse.getParHole15() == 4) cntPar4++;
        if (clubCourse.getParHole15() == 3) cntPar3++;

        if (clubCourse.getParHole16() == 5) cntPar5++;
        if (clubCourse.getParHole16() == 4) cntPar4++;
        if (clubCourse.getParHole16() == 3) cntPar3++;

        if (clubCourse.getParHole17() == 5) cntPar5++;
        if (clubCourse.getParHole17() == 4) cntPar4++;
        if (clubCourse.getParHole17() == 3) cntPar3++;

        if (clubCourse.getParHole18() == 5) cntPar5++;
        if (clubCourse.getParHole18() == 4) cntPar4++;
        if (clubCourse.getParHole18() == 3) cntPar3++;

        StringBuilder sb = new StringBuilder();
        sb.append(ctx.getResources().getString(R.string.club_scorecard)).append("\n");
        sb.append("Par 4: ").append(cntPar4).append("\n");
        sb.append("Par 5: ").append(cntPar5).append("\n");
        sb.append("Par 3: ").append(cntPar3).append("\n");

        calculateTotal();
        AlertDialog.Builder diag = new AlertDialog.Builder(getActivity());
        diag.setTitle(ctx.getResources().getString(R.string.scorecard))
                .setMessage(ctx.getResources().getString(R.string.club_scorecard_ask))
                .setPositiveButton(ctx.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                submit();
                            }
                        }
                )
                .setNegativeButton(ctx.getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }
                )
                .show();
    }


    ClubDTO club;
    ClubCourseDTO clubCourse;
    Vibrator vb;
    TextView txtClub, txtScore1, txtScore2, txtScore3, txtScore4, txtScore5, txtScore6,
            txtScore7, txtScore8, txtScore9, txtScore10, txtScore11, txtScore12,
            txtScore13, txtScore14, txtScore15, txtScore16, txtScore17, txtScore18;

    TextView btnUp1, btnUp2, btnUp3, btnUp4, btnUp5, btnUp6, btnUp7, btnUp8, btnUp9, btnUp10,
            btnUp11, btnUp12, btnUp13, btnUp14, btnUp15, btnUp16, btnUp17, btnUp18;

    TextView btnDown1, btnDown2, btnDown3, btnDown4, btnDown5, btnDown6, btnDown7, btnDown8, btnDown9,
            btnDown10, btnDown11, btnDown12, btnDown13, btnDown14, btnDown15, btnDown16, btnDown17,
            btnDown18, txtTotal;
    Spinner courseSpinner;

    static final int BASE_SCORE = 3, UPPER_LIMIT = 5;
    static final long VIBRATION_TIME = 50L;
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
    int clubPar;
    private void setFields() {
        frontNine = (RelativeLayout) view.findViewById(R.id.KSE_frontNine);
        backNine = (RelativeLayout) view.findViewById(R.id.KSE_backNine);
        vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        btnSave = (Button) view.findViewById(R.id.KSE_btnSave);
        txtScore1 = (TextView) view.findViewById(R.id.KSE_txtScore1);
        txtScore2 = (TextView) view.findViewById(R.id.KSE_txtScore2);
        txtScore3 = (TextView) view.findViewById(R.id.KSE_txtScore3);
        txtScore4 = (TextView) view.findViewById(R.id.KSE_txtScore4);
        txtScore5 = (TextView) view.findViewById(R.id.KSE_txtScore5);
        txtScore6 = (TextView) view.findViewById(R.id.KSE_txtScore6);

        txtScore7 = (TextView) view.findViewById(R.id.KSE_txtScore7);
        txtScore8 = (TextView) view.findViewById(R.id.KSE_txtScore8);
        txtScore9 = (TextView) view.findViewById(R.id.KSE_txtScore9);
        txtScore10 = (TextView) view.findViewById(R.id.KSE_txtScore10);
        txtScore11 = (TextView) view.findViewById(R.id.KSE_txtScore11);
        txtScore12 = (TextView) view.findViewById(R.id.KSE_txtScore12);

        txtScore13 = (TextView) view.findViewById(R.id.KSE_txtScore13);
        txtScore14 = (TextView) view.findViewById(R.id.KSE_txtScore14);
        txtScore15 = (TextView) view.findViewById(R.id.KSE_txtScore15);
        txtScore16 = (TextView) view.findViewById(R.id.KSE_txtScore16);
        txtScore17 = (TextView) view.findViewById(R.id.KSE_txtScore17);
        txtScore18 = (TextView) view.findViewById(R.id.KSE_txtScore18);
        //
        txtTotal = (TextView) view.findViewById(R.id.KSE_txtPar);
        txtClub = (TextView) view.findViewById(R.id.KSE_txtClubName);
        courseSpinner = (Spinner) view.findViewById(R.id.KSE_spinnerCourse);
        btnSave = (Button) view.findViewById(R.id.KSE_btnSave);
        radio18 = (RadioButton) view.findViewById(R.id.KSE_radio18);
        radio9 = (RadioButton) view.findViewById(R.id.KSE_radio9);
        bar = (ProgressBar) view.findViewById(R.id.KSE_progress);
        bar.setVisibility(View.GONE);

        setUpButtons();
        setDownButtons();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });



    }
    RelativeLayout frontNine, backNine;

}
/*
INSERT INTO `kidsgolf`.`clubCourse` (`clubCourseID`, `clubID`, `courseName`, `holes`, `par`, `parHole1`, `parHole2`, `parHole3`, `parHole4`, `parHole5`,
`parHole6`, `parHole7`, `parHole8`, `parHole9`, `parHole10`, `parHole11`, `parHole12`, `parHole13`, `parHole14`, `parHole15`, `parHole16`, `parHole17`,
`parHole18`) VALUES (NULL, '117', 'Rocklands Course', '18', '72', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4');
 */
