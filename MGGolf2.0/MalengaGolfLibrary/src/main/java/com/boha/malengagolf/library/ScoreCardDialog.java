package com.boha.malengagolf.library;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.boha.malengagolf.library.adapters.ScorecardAdapter;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.CompleteRounds;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/26.
 */
public class ScoreCardDialog extends DialogFragment {

    public ScoreCardDialog() {
    }
    public ScoreCardDialog(LeaderBoardDTO b) {
        this.leaderBoard = b;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_scorecard,
                container, false);
        ctx = getActivity();
        if (savedInstanceState != null) {
            Log.i(LOG,"savedInstanceState is not null, restoring");
            leaderBoard = (LeaderBoardDTO)savedInstanceState.getSerializable("leaderBoard");
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.mgStyle);
        }

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        setFields();
        setLeaderBoardFields();
        setList();
        //setRetainInstance(true);
        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.e(LOG, "onSaveInstanceState");
        b.putSerializable("leaderBoard", leaderBoard);
        super.onSaveInstanceState(b);
    }

    @Override
    public void onDestroy() {
        Log.e(LOG, "onDestroy");
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.mgStyle);

     }
    public void setFields() {
        Log.i(LOG, "setFields ...");
        listView = (ListView) view.findViewById(R.id.PSC_list);
        txtTourneyName = (TextView) view.findViewById(R.id.PSC_tourneyName);
        txtPlayerName = (TextView) view.findViewById(R.id.PSC_playerName);
        txtRounds = (TextView) view.findViewById(R.id.PSC_count);
        txtAverage = (TextView) view.findViewById(R.id.PSC_averageStrokes);
        getDialog().setTitle(ctx.getResources().getString(R.string.scorecard));

        //TODO set size according to number of scorecards needed
        getDialog().getWindow().setLayout(500,750);

        txtPlayerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(LOG,"onConfigurationChanged, orientation: " + newConfig.orientation);
        dismiss();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    public void setList() {
        Log.i(LOG, "setList");
        adapter = new ScorecardAdapter(ctx,
                R.layout.scorecard_layout,
                tourneyScoreByRoundDTOList, leaderBoard.getTournamentType());
        listView.setAdapter(adapter);

    }
    Context ctx;
    View view;
    ListView listView;
    TextView txtPlayerName, txtTourneyName, txtRounds, txtAverage;
    ScorecardAdapter adapter;
    List<TourneyScoreByRoundDTO> tourneyScoreByRoundDTOList;
    PlayerDTO player;
    int par;
    LeaderBoardDTO leaderBoard;
    List<CompleteRounds> completeRoundsList;
    private void setLeaderBoardFields() {
        if (leaderBoard == null) {
            dismiss();
            return;
        }
        Log.e(LOG, "setLeaderBoardFields " + leaderBoard.getPlayer().getFullName());
        List<LeaderBoardDTO> list = new ArrayList<LeaderBoardDTO>();
        list.add(leaderBoard);
        completeRoundsList = CompleteRounds.getCompletedRounds(list);
        double t = 0;
        for (CompleteRounds pav: completeRoundsList) {
            t += pav.getAverage();
        }
        if (completeRoundsList.isEmpty()) {
            txtAverage.setText("N/A");
        } else {
            t = t / completeRoundsList.size();
            txtAverage.setText(df.format(t));
        }
        this.tourneyScoreByRoundDTOList = leaderBoard.getTourneyScoreByRoundList();
        this.player = leaderBoard.getPlayer();

        txtRounds.setText("" + tourneyScoreByRoundDTOList.size());
        txtPlayerName.setText(player.getFullName());
        txtTourneyName.setText(leaderBoard.getTournamentName());

        //setList();
    }
    static final DecimalFormat df = new DecimalFormat("###,###.00");
    static final String LOG = "ScoreCardDialog";

}
