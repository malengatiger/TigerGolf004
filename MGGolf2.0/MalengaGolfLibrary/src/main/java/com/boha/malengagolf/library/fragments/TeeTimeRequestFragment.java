package com.boha.malengagolf.library.fragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.TimeSheetActivity;
import com.boha.malengagolf.library.adapters.TeeTimeAdapter;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.LeaderBoardPage;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class TeeTimeRequestFragment extends Fragment implements
        LeaderBoardPage,
        TeeTimeAdapter.TeeTimeRequestListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public interface TeeTimeFragmentListener {
        public void setBusy();
        public void setNotBusy();
        public void onTeeTimesCompleted(List<LeaderBoardDTO> leaderBoardList);
    }

    TeeTimeFragmentListener teeTimeFragmentListener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof TeeTimeFragmentListener) {
            teeTimeFragmentListener = (TeeTimeFragmentListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement TeeTimeFragmentListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG, "onCreateView");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_tee, container, false);
        setFields();
        Bundle bundle = getArguments();
        if (bundle != null) {
            tournament = (TournamentDTO) bundle.getSerializable("tournament");
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.TEE_list);
        txTourneyName = (TextView) view.findViewById(R.id.TEE_tournName);
        txtClubName = (TextView) view.findViewById(R.id.TEE_clubName);
        txtPlayerCount = (TextView) view.findViewById(R.id.TEE_count);
        spinner = (Spinner) view.findViewById(R.id.TEE_roundSpinner);
        txtDate = (TextView) view.findViewById(R.id.TEE_roundDate);
        txtTeeTimesSet = (TextView) view.findViewById(R.id.TEE_teeTimecount);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournament.getGolfRounds() == 1) return;
                showDateDialog();
            }
        });
        txtTeeTimesSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeSheetActivity();
            }
        });
        txtPlayerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeSheetActivity();
            }
        });
    }

    private void showDateDialog() {

        if (mYear == 0) {
            Calendar cal = GregorianCalendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dp = DatePickerDialog.newInstance(this, mYear, mMonth, mDay, true);
        dp.setYearRange(2013, 2037);
        dp.show(getFragmentManager(), "ttDate");

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        date = cal.getTimeInMillis();
        txtDate.setText(sdf.format(cal.getTime()));

    }

    public void setList() {
        Log.i(LOG, "setList");
        if (round == 0) round = 1;
        setTeeTimesDone();
        teeTimeAdapter = new TeeTimeAdapter(ctx, R.layout.tee_time_item,
                leaderBoardList,
                round, this);
        listView.setAdapter(teeTimeAdapter);

        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
            }
        });
    }

    private void startTimeSheetActivity() {
        Intent u = new Intent(ctx, TimeSheetActivity.class);
        ResponseDTO dto = new ResponseDTO();
        dto.setLeaderBoardList(leaderBoardList);
        u.putExtra("response", dto);
        u.putExtra("round", round);
        u.putExtra("tournament", tournament);
        startActivity(u);
    }
    TextView txtTeeTimesSet;
    private void setTeeTimesDone() {
            int cnt = 0;
        for (LeaderBoardDTO x: leaderBoardList) {
            TourneyScoreByRoundDTO v = x.getTourneyScoreByRoundList().get(round - 1);
            if (v.getTeeTime() > 0) cnt++;
        }
        txtTeeTimesSet.setText("" + cnt);
    }
    private void setRoundSpinner() {
        if (tournament.getGolfRounds() == 1) {
            round = 1;
            txtDate.setText(sdf.format(new Date(tournament.getStartDate())));
            spinner.setVisibility(View.GONE);
            return;
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < tournament.getGolfRounds(); i++) {
            list.add(ctx.getResources().getString(R.string.round) + " " + (i + 1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                R.layout.xxsimple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                round = (i + 1);
                TourneyScoreByRoundDTO x = leaderBoard.getTourneyScoreByRoundList().get(i);
                if (x.getTeeTime() > 0)
                    txtDate.setText(sdf.format(new Date(x.getTeeTime())));
                else
                    txtDate.setText(ctx.getResources().getString(R.string.date));
                setList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int round, mYear, mMonth, mDay, mHour, mMinute;
    private long date;
    private TeeTimeAdapter teeTimeAdapter;
    private Spinner spinner;
    private ListView listView;
    private TextView txTourneyName, txtClubName,
            txtPlayerCount, txtDate;
    static final String LOG = "TeeTimeRequestFragment";
    private Context ctx;
    private View view;
    private TournamentDTO tournament;
    private ResponseDTO response;
    private List<LeaderBoardDTO> leaderBoardList;
    private LeaderBoardDTO leaderBoard;
    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", loc);

    private TourneyScoreByRoundDTO tourneyScoreByRound;
    private int selectedIndex, tee;
    private int currentRound;
    private LeaderBoardDTO leaderBoardFromScoringFragment;

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setLeaderBoardFromScoringFragment(LeaderBoardDTO leaderBoardFromScoringFragment) {
        this.leaderBoardFromScoringFragment = leaderBoardFromScoringFragment;
    }
    FragmentActivity act;
    @Override
    public void onTeeTimeRequested(TourneyScoreByRoundDTO score, int index, int tee) {
        tourneyScoreByRound = score;
        selectedIndex = index;
        this.tee = tee;

        if (score.getTeeTime() > 0) {
            Date dt = new Date(score.getTeeTime());
            Calendar c = GregorianCalendar.getInstance();
            c.setTime(dt);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            date = c.getTimeInMillis();
        } else {
            mHour = 7;
            mMinute = 0;
        }

        TimePickerDialog x = TimePickerDialog.newInstance(this, mHour, mMinute, true, true);
        x.show(getFragmentManager(), "ttTimeDiag");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, mYear);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.DAY_OF_MONTH, mDay);
        cal.set(Calendar.HOUR_OF_DAY, mHour);
        cal.set(Calendar.MINUTE, mMinute);
        tourneyScoreByRound.setTeeTime(cal.getTimeInMillis());
        tourneyScoreByRound.setTee(tee);
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_TEE_TIMES);
        w.setTourneyScoreByRound(tourneyScoreByRound);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        teeTimeFragmentListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        teeTimeFragmentListener.setNotBusy();
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        leaderBoardList = response.getLeaderBoardList();
                        setList();
                        listView.setSelection(selectedIndex);
                        teeTimeFragmentListener.onTeeTimesCompleted(leaderBoardList);
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        teeTimeFragmentListener.setNotBusy();
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                teeTimeFragmentListener.setNotBusy();
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                leaderBoardList = response.getLeaderBoardList();
//                setList();
//                listView.setSelection(selectedIndex);
//                teeTimeFragmentListener.onTeeTimesCompleted(leaderBoardList);
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                teeTimeFragmentListener.setNotBusy();
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });

    }

    public void setLeaderBoardList(TournamentDTO tournament, List<LeaderBoardDTO> leaderBoardList) {
        this.leaderBoardList = leaderBoardList;
        this.tournament = tournament;
        try {
            leaderBoard = leaderBoardList.get(0);
        } catch (Exception e) {
            Log.e(LOG, "Leaderboard could not be gotten at index 0");
            return;
        }
        setRoundSpinner();
        if (currentRound > 0) {
            spinner.setSelection(currentRound - 1);
        }
        setList();
        int index = 0;
        if (leaderBoardFromScoringFragment != null) {
            for (LeaderBoardDTO d: leaderBoardList) {
                if (d.getLeaderBoardID() == leaderBoardFromScoringFragment.getLeaderBoardID()) {
                    break;
                }
                index++;
            }
            listView.setSelection(index);
        }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(tournament.getStartDate());
        date = tournament.getStartDate();
        txtDate.setText(sdf.format(cal.getTime()));
        mHour = 7;
        mMinute = 0;
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH) + 1;
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        fillFields();

    }
    private void fillFields() {
        txtClubName.setText(tournament.getClubName());
        txTourneyName.setText(tournament.getTourneyName());
        txtPlayerCount.setText("" + leaderBoardList.size());
    }
    private void showConfirmCloseDialog() {
        final AlertDialog.Builder diag = new AlertDialog.Builder(getActivity());
        diag.setTitle(ctx.getResources().getString(R.string.close_tourn));


        diag
                .setMessage(ctx.getResources().getString(R.string.close_question) + "\n" + tournament.getTourneyName())
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = diag.create();

        // show it
        alertDialog.show();
    }
}
