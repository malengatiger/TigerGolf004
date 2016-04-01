package com.boha.malengagolf.library.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/20.
 */
public class TimeSheetFragment extends Fragment {
    public interface TimeSheetFragmentListener {

    }

    LayoutInflater inflater;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG, "onCreateView");
        this.inflater = inflater;
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.tee_table, container, false);

        return view;
    }

    int sortCount = 0;

    private void setFields() {
        tableLayout = (TableLayout) view.findViewById(R.id.TAB_table);
        txtTourney = (TextView) view.findViewById(R.id.TAB_tourneyName);
        txtSort = (TextView) view.findViewById(R.id.TAB_txtSort);
        txtClub = (TextView) view.findViewById(R.id.TAB_clubName);
        txtDate = (TextView) view.findViewById(R.id.TAB_date);
        txtTourney.setText(tournament.getTourneyName());
        txtClub.setText(tournament.getClubName());
        if (tournament.getGolfRounds() == 1) {
            txtDate.setText(sdf.format(new Date(tournament.getStartDate())));
        } else {
            try {
                for (LeaderBoardDTO board : leaderBoardList) {
                    for (TourneyScoreByRoundDTO x : board.getTourneyScoreByRoundList()) {
                        if (x.getTeeTime() > 0) {
                            txtDate.setText(sdf.format(new Date(x.getTeeTime())));
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                //ignore
            }
        }
        txtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort();
            }
        });
        setTableLayout();
        sort();
    }

    private void setTableLayout() {
        tableLayout.removeAllViews();
        tableLayout.addView(inflater.inflate(R.layout.tee_row_item_header, null));
        int count = 0;
        for (LeaderBoardDTO board : leaderBoardList) {
            TourneyScoreByRoundDTO x = board.getTourneyScoreByRoundList().get(round - 1);
            View v = inflater.inflate(R.layout.tee_row_item, null);
            TextView txtAge = (TextView) v.findViewById(R.id.TA_txtAge);
            TextView txtHole = (TextView) v.findViewById(R.id.TA_txtHole);
            TextView txtPlayer = (TextView) v.findViewById(R.id.TA_txtPlayer);
            TextView txtTeeTime = (TextView) v.findViewById(R.id.TA_txtTime);

            if (board.getAgeGroup() != null) {
                txtAge.setText(board.getAgeGroup().getGroupName());
            } else {
                txtAge.setText("");
            }

            switch (x.getTee()) {
                case 0:
                    txtHole.setText("");
                    break;
                case 1:
                    txtHole.setText("01");
                    txtHole.setTextColor(ctx.getResources().getColor(R.color.green));
                    break;
                case 10:
                    txtHole.setText("10");
                    break;
            }
            txtPlayer.setText(board.getPlayer().getFullName());

            if (x.getTeeTime() > 0) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTimeInMillis(x.getTeeTime());
                int hr = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                StringBuilder sb = new StringBuilder();
                if (hr < 10) {
                    sb.append("0").append(hr).append(":");
                } else {
                    sb.append(hr).append(":");
                }
                if (min < 10) {
                    sb.append("0").append(min);
                } else {
                    sb.append(min);
                }
                txtTeeTime.setText(sb.toString());
            } else {
                txtTeeTime.setText("");
                txtTeeTime.setBackgroundColor(ctx.getResources().getColor(R.color.yellow_pale));
                txtTeeTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.tee_time_notdone));
                    }
                });
            }
            tableLayout.addView(v);
            count++;
            if (count > 9 ) {
                tableLayout.addView(inflater.inflate(R.layout.tee_row_item_header, null));
                count = 0;
            }
        }
        View v = inflater.inflate(R.layout.tee_row_item, null);
        TextView txtAge = (TextView) v.findViewById(R.id.TA_txtAge);
        TextView txtHole = (TextView) v.findViewById(R.id.TA_txtHole);
        TextView txtPlayer = (TextView) v.findViewById(R.id.TA_txtPlayer);
        TextView txtTeeTime = (TextView) v.findViewById(R.id.TA_txtTime);
        txtAge.setText("");
        txtHole.setText("");
        txtPlayer.setText("");
        txtTeeTime.setText("");
        tableLayout.addView(v);
        View v1 = inflater.inflate(R.layout.tee_row_item_header, null);
        TextView txtAge1 = (TextView) v.findViewById(R.id.TA_txtAge);
        TextView txtHole1 = (TextView) v.findViewById(R.id.TA_txtHole);
        TextView txtPlayer1 = (TextView) v.findViewById(R.id.TA_txtPlayer);
        TextView txtTeeTime1 = (TextView) v.findViewById(R.id.TA_txtTime);
        txtAge1.setText("");
        txtHole1.setText("");
        txtPlayer1.setText("");
        txtTeeTime1.setText("");
        tableLayout.addView(v1);
    }

    int sortBy = LeaderBoardDTO.SORT_AGE_GROUP;

    private void sort() {
        switch (sortBy) {
            case LeaderBoardDTO.SORT_TEE_TIME:
                for (LeaderBoardDTO x : leaderBoardList) {
                    x.setSortType(LeaderBoardDTO.SORT_TEE_TIME);
                    x.setRound(round);
                }
                break;
            case LeaderBoardDTO.SORT_AGE_GROUP:
                for (LeaderBoardDTO x : leaderBoardList) {
                    x.setSortType(LeaderBoardDTO.SORT_AGE_GROUP);
                    x.setRound(round);
                }
                break;
            case LeaderBoardDTO.SORT_TEE_HOLES:
                for (LeaderBoardDTO x : leaderBoardList) {
                    x.setSortType(LeaderBoardDTO.SORT_TEE_HOLES);
                    x.setRound(round);
                }
                break;
            case LeaderBoardDTO.SORT_PLAYER_NAME:
                for (LeaderBoardDTO x : leaderBoardList) {
                    x.setSortType(LeaderBoardDTO.SORT_PLAYER_NAME);
                    x.setRound(round);
                }
                break;
        }
        sortBy++;
        if (sortBy > LeaderBoardDTO.SORT_PLAYER_NAME) {
            sortBy = LeaderBoardDTO.SORT_AGE_GROUP;
        }
        Collections.sort(leaderBoardList);
        setTableLayout();
    }

    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", loc);

    TextView txtTourney, txtClub, txtDate, txtSort;
    TableLayout tableLayout;
    int round;
    TournamentDTO tournament;
    List<LeaderBoardDTO> leaderBoardList;

    public void setData(TournamentDTO tournament,
                        List<LeaderBoardDTO> leaderBoardList, int round) {
        this.leaderBoardList = leaderBoardList;
        this.round = round;
        this.tournament = tournament;
        setFields();
    }

    Context ctx;
    static final String LOG = "TimeSheetFragment";
}
