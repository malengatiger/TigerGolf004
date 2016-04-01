package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.admin.ClubScorecardActivity;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.GolfCourseMapActivity;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.ClubCourseDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentCourseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.Util;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by aubreyM on 2014/05/07.
 */
public class TournamentFragment extends Fragment {
    static final String LOG = "TournamentFragment";

    public interface TournamentListener {
        public void onTournamentAdded(List<TournamentDTO> tournament);

        public void onTournamentUpdated(TournamentDTO tournament);

        public void setBusy();

        public void setNotBusy();
    }

    private TournamentListener tournamentListener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof TournamentListener) {
            tournamentListener = (TournamentListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement TournamentFragment.TournamentListener");
        }
        Log.d(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "onCreateView.........");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.tourn_edit, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administrator = SharedUtil.getAdministrator(ctx);
        setFields();

        return view;
    }

    boolean isClosed;

    private void checkStatus() {
        Log.e(LOG, "######## checkStatus: " + action);
        if (action == ADD_NEW) {
            checkBoxClose.setVisibility(View.GONE);
            txtTourneyStatus.setVisibility(View.GONE);
            return;
        }
        if (action == UPDATE) {
            if (tournament.getClosedForScoringFlag() > 0) {
                checkBoxClose.setChecked(true);
                checkBoxClose.setEnabled(false);
                txtTourneyStatus.setVisibility(View.VISIBLE);
                btnDown.setEnabled(false);
                btnEnd.setEnabled(false);
                btnStart.setEnabled(false);
                btnSave.setEnabled(false);
                txtRounds.setText("" + tournament.getGolfRounds());
            }
            if (tournament.getScoringCommencedFlag() > 0) {
                radio18.setEnabled(false);
                radio9.setEnabled(false);
                radioStablefordIndiv.setEnabled(false);
                radioStrokePlayIndiv.setEnabled(false);
                radioNo.setEnabled(false);
                radioYes.setEnabled(false);
                btnFindClubs.setEnabled(false);
                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
                btnStart.setEnabled(false);
                btnSave.setEnabled(false);
                txtRounds.setText("" + tournament.getGolfRounds());
            }
            fillForm();
        }
    }

    DatePickerDialog dpStart, dpEnd;

    private void resetText() {
        txtAddedCourse.setText("");
        StringBuilder sb = new StringBuilder();
        int round = 1;
        for (TournamentCourseDTO x : tournamentCourseList) {
            sb.append(round)
                    .append(" - ")
                    .append(x.getClubCourse().getCourseName())
                    .append("\n");
            round++;
        }
        txtAddedCourse.setText(sb.toString());
    }

    private void setCourseChoiceFieldsVisible() {
        courseSpinner.setVisibility(View.VISIBLE);
        btnAddCourse.setVisibility(View.VISIBLE);
        btnRemove.setVisibility(View.VISIBLE);
        txtAddedCourse.setVisibility(View.VISIBLE);
    }

    private void setCourseChoiceFieldsGone() {
        courseSpinner.setVisibility(View.GONE);
        btnAddCourse.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);
        txtAddedCourse.setVisibility(View.GONE);
    }

    int par = 72, numberOfHoles;

    private void setFields() {
        isFirstTime = true;
        numberRounds = 1;
        parLayout = view.findViewById(R.id.ET_layoutSetPar);
        parLayout.setVisibility(View.GONE);
        btnDown = (TextView) view.findViewById(R.id.ET_btnRoundsDown);
        btnUp = (TextView) view.findViewById(R.id.ET_btnRoundsUp);
        btnStart = (Button) view.findViewById(R.id.ET_btnStartDate);
        btnEnd = (Button) view.findViewById(R.id.ET_btnEndDate);
        btnSave = (Button) view.findViewById(R.id.ET_btnSave);

        radio18 = (RadioButton) view.findViewById(R.id.ET_radio18);
        radio9 = (RadioButton) view.findViewById(R.id.ET_radio9);
        radioYes = (RadioButton) view.findViewById(R.id.ET_radioYes);
        radioNo = (RadioButton) view.findViewById(R.id.ET_radioNo);

        radioStrokePlayIndiv = (RadioButton) view.findViewById(R.id.ET_radioStrokePlayIndiv);
        radioStablefordIndiv = (RadioButton) view.findViewById(R.id.ET_radioStablefordIndiv);

        txtGroupName = (TextView) view.findViewById(R.id.ET_groupName);
        txtRounds = (TextView) view.findViewById(R.id.ET_txtNumberRounds);
        txtRounds.setText("" + numberRounds);
        txtTourneyStatus = (TextView) view.findViewById(R.id.ET_txtTournStatus);
        txtTourneyStatus.setVisibility(View.GONE);
        checkBoxClose = (CheckBox) view.findViewById(R.id.ET_checkBoxCloseTourn);
        btnFindClubs = (Button) view.findViewById(R.id.ET_btnFindClubs);
        btnCheckScorecard = (Button) view.findViewById(R.id.ET_btnCheckScorecard);
        clubSpinner = (Spinner) view.findViewById(R.id.ET_spinnerClub);
        courseSpinner = (Spinner) view.findViewById(R.id.ET_spinnerCourse);

        btnParDown = (TextView) view.findViewById(R.id.ET_btnParDown);
        btnParUp = (TextView) view.findViewById(R.id.ET_btnParUp);
        txtPar = (TextView) view.findViewById(R.id.ET_txtPar);
        txtPar.setText("72");

        btnAddCourse = (TextView) view.findViewById(R.id.ET_btnAdd);
        btnRemove = (TextView) view.findViewById(R.id.ET_btnRemove);
        txtAddedCourse = (TextView) view.findViewById(R.id.ET_txtAddedCourse);
        setCourseChoiceFieldsGone();

        editTournName = (EditText) view.findViewById(R.id.ET_eTournName);

        txtGroupName.setText(SharedUtil.getGolfGroup(ctx).getGolfGroupName());
        btnFindClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(ctx, GolfCourseMapActivity.class);
                t.putExtra("requestOrigin", GolfCourseMapActivity.ORIGIN_TOURNAMENT);
                startActivityForResult(t, REQUEST_CLUB_SEARCH);
            }
        });
        btnCheckScorecard.setVisibility(View.GONE);
        btnCheckScorecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkScoreCard(true);
            }
        });

        switch (action) {
            case ADD_NEW:
                numberRounds = 1;
                txtRounds.setText("" + numberRounds);
                btnEnd.setVisibility(View.GONE);
                break;
            case UPDATE:
                fillForm();
                break;
            case DELETE:
                fillForm();
                break;
        }
        radio18.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    par = 72;
                    txtPar.setText("72");
                    parLayout.setVisibility(View.GONE);
                }
            }
        });
        radio9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    par = 36;
                    txtPar.setText("36");
                    parLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        btnParUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio18.isChecked()) {
                    if (par > 79) {
                        return;
                    }
                    par++;
                    txtPar.setText("" + par);
                }
                if (radio9.isChecked()) {
                    if (par > 40) {
                        return;
                    }

                    par++;
                    txtPar.setText("" + par);
                }
            }
        });
        btnParDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio18.isChecked()) {
                    if (par < 66) {
                        return;
                    }
                    par--;
                    txtPar.setText("" + par);
                }
                if (radio9.isChecked()) {
                    if (par < 27) {
                        return;
                    }
                    par--;
                    txtPar.setText("" + par);
                }
            }
        });
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentCourseList == null || tournamentCourseList.isEmpty()) {
                    tournamentCourseList = new ArrayList<TournamentCourseDTO>();

                }
                if (clubCourse == null) {
                    courseSpinner.setSelection(1);
                    clubCourse = club.getClubCourses().get(0);
                }
                Log.i(LOG, "btnAddCourse adding course: " + clubCourse.getCourseName());
                TournamentCourseDTO tc = new TournamentCourseDTO();
                ClubCourseDTO cc = new ClubCourseDTO();
                cc.setClubCourseID(clubCourse.getClubCourseID());
                cc.setCourseName(clubCourse.getCourseName());
                tc.setClubCourse(cc);
                tournamentCourseList.add(tc);
                if (tournamentCourseList.size() > numberRounds) {
                    tournamentCourseList.remove(tournamentCourseList.size() - 1);
                }
                resetText();
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentCourseList == null || tournamentCourseList.isEmpty()) {
                    return;
                }
                Log.i(LOG, "btnRemove removing course: " + tournamentCourseList.get(0).getClubCourse().getCourseName());
                tournamentCourseList.remove(0);
                resetText();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int xYear, xMth, xDay;
                if (mYear == 0) {
                    xYear = calendar.get(Calendar.YEAR);
                    xMth = calendar.get(Calendar.MONTH);
                    xDay = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    xYear = mYear;
                    xMth = mMonth;
                    xDay = mDay;
                }
                dpStart = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog,
                                                  int year, int month, int day) {

                                mYear = year;
                                mMonth = month;
                                mDay = day;

                                calendar.set(Calendar.YEAR, mYear);
                                calendar.set(Calendar.MONTH, mMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, mDay);
                                calendar.set(Calendar.HOUR, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                startDate = calendar.getTimeInMillis();
                                btnStart.setText(sdf.format(calendar.getTime()));
                                if (endDate == 0) {
                                    endDate = startDate;
                                    btnEnd.setText(Util.getLongDate(mDay, mMonth,
                                            mYear));
                                }
                            }


                        }, xYear, xMth, xDay, true
                );
                dpStart.setVibrate(true);
                dpStart.setYearRange(1980, 2036);
                Bundle args = new Bundle();
                args.putInt("year", mYear);
                args.putInt("month", mMonth);
                args.putInt("day", mDay);

                dpStart.setArguments(args);
                dpStart.show(getFragmentManager(), "diagx");

            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int xYear, xMth, xDay;
                if (mYear == 0) {
                    xYear = calendar.get(Calendar.YEAR);
                    xMth = calendar.get(Calendar.MONTH);
                    xDay = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    xYear = mYear;
                    xMth = mMonth;
                    xDay = mDay;
                }
                dpEnd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog,
                                                  int year, int month, int day) {
                                tYear = year;
                                tMonth = month;
                                tDay = day;

                                calendar.set(Calendar.YEAR, tYear);
                                calendar.set(Calendar.MONTH, tMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, tDay);
                                calendar.set(Calendar.HOUR, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                endDate = calendar.getTimeInMillis();
                                btnEnd.setText(sdf.format(calendar.getTime()));
                                //check dates
                                if (mYear > 0 && tYear > 0) {
                                    Calendar calFrom = GregorianCalendar.getInstance();
                                    calFrom.set(mYear, mMonth, mDay);
                                    Date f = calFrom.getTime();
                                    Calendar calTo = GregorianCalendar.getInstance();
                                    calTo.set(tYear, tMonth, tDay);
                                    Date t = calTo.getTime();
                                    if (t.before(f)) {
                                        ToastUtil.errorToast(ctx, "Wrong dates");
                                        animateButtonOut();
                                    } else {
                                        animateButtonIn();
                                    }
                                }
                            }


                        }, xYear, xMth, xDay, true
                );
                dpEnd.setVibrate(true);
                dpEnd.setYearRange(1980, 2036);
                Bundle args = new Bundle();
                args.putInt("year", tYear);
                args.putInt("month", tMonth);
                args.putInt("day", tDay);

                dpEnd.setArguments(args);
                dpEnd.show(getFragmentManager(), "diagxt");

            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberRounds == 1) {
                    btnEnd.setVisibility(View.GONE);
                    endDate = startDate;
                    return;
                } else {
                    btnEnd.setVisibility(View.VISIBLE);
                }
                numberRounds--;
                txtRounds.setText("" + numberRounds);
                if (numberRounds == 1) {
                    btnEnd.setVisibility(View.GONE);
                    endDate = startDate;
                }

            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberRounds == 6) return;
                numberRounds++;
                txtRounds.setText("" + numberRounds);
                btnEnd.setVisibility(View.VISIBLE);

            }
        });


    }

    boolean isStart, isConfirmed;
    int mYear, mMonth, mDay, tYear, tMonth, tDay;

    private void fillForm() {
        Log.e(LOG, "updating tournament ...fillForm ...");
        editTournName.setText(tournament.getTourneyName());
        btnStart.setText(sdf.format(new Date(tournament.getStartDate())));
        btnEnd.setText(sdf.format(new Date(tournament.getEndDate())));
        txtRounds.setText("" + tournament.getGolfRounds());
        //
        if (tournament.getClubName() != null) {
            btnFindClubs.setText(tournament.getClubName());
            club = new ClubDTO();
            club.setClubName(tournament.getClubName());
            club.setClubID(tournament.getClubID());
            club.setClubCourses(new ArrayList<ClubCourseDTO>());
            for (TournamentCourseDTO tc : tournament.getTournamentCourses()) {
                club.getClubCourses().add(tc.getClubCourse());
            }
        }
        startDate = tournament.getStartDate();
        endDate = tournament.getEndDate();
        if (tournament.getUseAgeGroups() == 1) {
            radioYes.setChecked(true);
        } else {
            radioNo.setChecked(true);
        }
        switch (tournament.getTournamentType()) {
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                radioStrokePlayIndiv.setChecked(true);
                break;
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                radioStablefordIndiv.setChecked(true);
                break;
        }

        tournamentCourseList = tournament.getTournamentCourses();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (TournamentCourseDTO x : tournamentCourseList) {
            if (!map.containsKey(x.getClubCourse().getClubCourseID())) {
                map.put(x.getClubCourse().getClubCourseID(), 0);
            }
        }

        int cnt = 0;
        Set<Integer> set = map.keySet();
        Iterator<Integer> iter = set.iterator();

        while (iter.hasNext()) {
            Integer mEntry = (Integer) iter.next();
            cnt++;
        }
        if (cnt > 1) {
            txtAddedCourse.setVisibility(View.VISIBLE);
            resetText();
            courseSpinner.setVisibility(View.VISIBLE);
            btnAddCourse.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
        }
    }

    private void submit() {
        isConfirmed = false;
        if (editTournName.getText().toString().isEmpty()) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.enter_tourn));
            return;
        }
        if (startDate == 0) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.enter_startdate));
            return;
        }
        if (endDate == 0) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.enter_enddate));
            return;
        }
        if (club == null) {
            if (tournament.getClubID() > 0) {

            } else {
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_club));
                return;
            }
        }
        if (clubCourse == null) {
            if (!tournament.getTournamentCourses().isEmpty()) {

            } else {
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_course));
                return;
            }
        }
        if (!radioYes.isChecked() && !radioNo.isChecked()) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.use_age_groups));
            return;
        }
        if (!radioStrokePlayIndiv.isChecked() && !radioStablefordIndiv.isChecked()) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_tourn_type));
            return;
        }
        if (action == UPDATE) {
            if (checkBoxClose.isChecked()) {
                showConfirmCloseDialog();
                return;
            }
        }
        RequestDTO req = new RequestDTO();

        switch (action) {
            case ADD_NEW:
                tournament = new TournamentDTO();
                req.setRequestType(RequestDTO.ADD_TOURNAMENT);

                break;
            case UPDATE:
                req.setRequestType(RequestDTO.UPDATE_TOURNAMENT);
                if (isConfirmed) {
                    tournament.setClosedForScoringFlag(1);
                }
                break;
            case DELETE:
                break;
        }
        //
        tournament.setGolfGroupID(SharedUtil.getGolfGroup(ctx).getGolfGroupID());
        tournament.setTourneyName(editTournName.getText().toString());
        tournament.setGolfRounds(numberRounds);
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);
        tournament.setClubID(club.getClubID());
        tournament.setClubName(club.getClubName());
        tournament.setPar(par);

        if (radioYes.isChecked()) {
            tournament.setUseAgeGroups(1);
        }
        if (radio18.isChecked())
            tournament.setHolesPerRound(18);
        if (radio9.isChecked()) {
            tournament.setHolesPerRound(9);
        }
        if (radioStrokePlayIndiv.isChecked()) {
            tournament.setTournamentType(RequestDTO.STROKE_PLAY_INDIVIDUAL);
        }
        if (radioStablefordIndiv.isChecked()) {
            tournament.setTournamentType(RequestDTO.STABLEFORD_INDIVIDUAL);
        }

        if (club.getClubCourses().size() == 1) {
            tournamentCourseList = new ArrayList<TournamentCourseDTO>();
            for (int i = 0; i < numberRounds; i++) {
                TournamentCourseDTO x = new TournamentCourseDTO();
                x.setRound(i + 1);
                x.setClubCourse(club.getClubCourses().get(0));
                x.setTournamentID(tournament.getTournamentID());
                tournamentCourseList.add(x);
            }
        } else {
            if (numberRounds != tournamentCourseList.size()) {
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.rounds_and_courses));
                return;
            } else {
                int i = 0;
                for (TournamentCourseDTO x : tournamentCourseList) {
                    x.setRound(i + 1);
                    i++;
                }
            }
        }


        tournament.setTournamentCourses(tournamentCourseList);
        req.setTournament(tournament);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        tournamentListener.setBusy();

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tournamentListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, r)) return;
                        Log.i(LOG, "Request responded OK");

                        response = r;
                        switch (action) {
                            case ADD_NEW:
                                Log.i(LOG, "Tournament added, about to tell host TourneyActivity");
                                tourneyAdded = true;
                                tourneyUpdated = false;
                                tournamentListener.onTournamentAdded(r.getTournaments());
                                break;
                            case UPDATE:
                                tourneyAdded = false;
                                tourneyUpdated = true;
                                tournamentListener.onTournamentUpdated(r.getTournaments().get(0));
                                break;
                        }
                    }
                });
            }

            @Override
            public void onClose() {
                act.runOnUiThread(new Runnable() {
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

//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                tournamentListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, r)) return;
//                Log.i(LOG, "Request responded OK");
//
//                response = r;
//                switch (action) {
//                    case ADD_NEW:
//                        Log.i(LOG, "Tournament added, about to tell host TourneyActivity");
//                        tourneyAdded = true;
//                        tourneyUpdated = false;
//                        tournamentListener.onTournamentAdded(r.getTournaments());
//                        break;
//                    case UPDATE:
//                        tourneyAdded = false;
//                        tourneyUpdated = true;
//                        tournamentListener.onTournamentUpdated(r.getTournaments().get(0));
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                tournamentListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//
//            }
//        });
    }

    boolean tourneyAdded, tourneyUpdated, isFirstTime;

    private void setCourseSpinner() {
        List<String> list = new ArrayList<String>();
        list.add(ctx.getResources().getString(R.string.select_course));
        for (ClubCourseDTO p : club.getClubCourses()) {
            list.add(p.getCourseName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item_blue, list);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    clubCourse = null;
                    return;
                }
                clubCourse = club.getClubCourses().get(i - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showConfirmCloseDialog() {
        final AlertDialog.Builder diag = new AlertDialog.Builder(getActivity());
        diag.setTitle(ctx.getResources().getString(R.string.close_tourn));


        diag
                .setMessage(ctx.getResources().getString(R.string.close_question) + "\n" + tournament.getTourneyName())
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isConfirmed = true;
                        checkBoxClose.setChecked(false);
                        closeTournament();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkBoxClose.setChecked(false);
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = diag.create();

        // show it
        alertDialog.show();
    }
    FragmentActivity act;
    private void closeTournament() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.CLOSE_TOURNAMENT);
        w.setTournamentID(tournament.getTournamentID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        tournamentListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tournamentListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                        isClosed = true;
                    }
                });
            }

            @Override
            public void onClose() {
                act.runOnUiThread(new Runnable() {
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
//            public void onResponseReceived(ResponseDTO r) {
//                tournamentListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
//                isClosed = true;
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                tournamentListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void animateButtonIn() {
        Animation a = AnimationUtils.loadAnimation(ctx,
                R.anim.grow_fade_in_center);
        a.setDuration(1000);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.startAnimation(a);
    }

    private void animateButtonOut() {
        Animation a = AnimationUtils.loadAnimation(ctx,
                R.anim.shrink_fade_out_center);
        a.setDuration(1000);
        a.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnSave.setVisibility(View.GONE);
            }
        });
        btnSave.startAnimation(a);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CLUB_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                club = (ClubDTO) data.getSerializableExtra("club");
                if (club != null) {
                    btnFindClubs.setText(club.getClubName());
                    if (!club.getClubCourses().isEmpty()) {
                        if (club.getClubCourses().size() == 1) {
                            clubCourse = club.getClubCourses().get(0);
                            btnCheckScorecard.setVisibility(View.VISIBLE);
                            checkScoreCard(false);
                        } else {
                            setCourseSpinner();
                            setCourseChoiceFieldsVisible();
                        }
                    }
                }
            }
        }
    }

    private void checkScoreCard(boolean forceDisplay) {
        Log.d(LOG, "############## checkScoreCard .........");
        if (clubCourse == null) {
            Log.e(LOG, "############# clubCourse is null, bypassing check");
            return;
        }
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

        if (clubCourse.getParHole9() == 5) cntPar5++;
        if (clubCourse.getParHole9() == 4) cntPar4++;
        if (clubCourse.getParHole9() == 3) cntPar3++;

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

        Log.e(LOG, "par3: " + cntPar3 + " par4: " + cntPar4 + " par5: " + cntPar5);
        //check if scorecard edited, if not, show scorecard
        if (cntPar4 == 18 || forceDisplay) {
            Intent s = new Intent(ctx, ClubScorecardActivity.class);
            s.putExtra("club", club);
            startActivityForResult(s, REQUEST_CLUB_SCORECARD);
        }

    }

    static final int REQUEST_CLUB_SEARCH = 3033, REQUEST_CLUB_SCORECARD = 3034;
    boolean toggle;
    List<TournamentCourseDTO> tournamentCourseList = new ArrayList<TournamentCourseDTO>();
    ClubCourseDTO clubCourse;
    Context ctx;
    View view;
    GolfGroupDTO golfGroup;
    AdministratorDTO administrator;
    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
    private Button btnStart, btnEnd, btnSave;
    private TextView txtGroupName, txtRounds, txtTourneyStatus, btnUp, btnDown;
    private CheckBox checkBoxClose;
    private Spinner clubSpinner, courseSpinner;
    private Button btnFindClubs, btnCheckScorecard;
    private RadioButton radio18, radio9, radioYes, radioNo, radioStrokePlayIndiv, radioStablefordIndiv;
    private EditText editTournName;
    private View parLayout;
    TextView btnAddCourse, btnRemove, btnParUp, btnParDown, txtPar;
    TextView txtAddedCourse;
    private int action, numberRounds;
    private TournamentDTO tournament;
    private long startDate, endDate;
    public static final int ADD_NEW = 1, UPDATE = 2, DELETE = 3;
    ResponseDTO response;
    ProvinceDTO province;
    ClubDTO club;
    List<ClubDTO> clubList;
    private List<TournamentCourseDTO> tournamentCourses;

    public void setAction(int action) {
        Log.e(LOG, "######## setAction: " + action);
        this.action = action;
    }

    public void setTournament(TournamentDTO tournament) {
        Log.e(LOG, "######## setTournament: " + tournament);
        this.tournament = tournament;
        if (tournament != null) {
            checkStatus();
        }
    }

}
