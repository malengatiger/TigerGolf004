package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.ScorecardAdapter;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.CompleteRounds;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/15.
 */
public class ScoreCardFragment extends Fragment  {
    static final String LOG = "ScoreCardFragment";
    @Override
    public void onAttach(Activity a) {
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
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_scorecard, container, false);
        setFields();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.PSC_list);
        txtTourneyName = (TextView) view.findViewById(R.id.PSC_tourneyName);
        txtPlayerName = (TextView) view.findViewById(R.id.PSC_playerName);
        txtRounds = (TextView) view.findViewById(R.id.PSC_count);
        txtAverage = (TextView) view.findViewById(R.id.PSC_averageStrokes);
        imageView = (ImageView)view.findViewById(R.id.PSC_image);
        Statics.setRobotoFontLight(ctx, txtPlayerName);
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
    ImageView imageView;
    int par;
    LeaderBoardDTO leaderBoard;
    List<CompleteRounds> completeRoundsList;
    public void setLeaderBoard(LeaderBoardDTO dto) {
        leaderBoard = dto;

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
        //image
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("golfgroup")
          .append(SharedUtil.getGolfGroup(ctx).getGolfGroupID()).append("/")
          .append("player/t")
          .append(leaderBoard.getPlayer().getPlayerID())
          .append(".jpg");
        Picasso.with(ctx).load(sb.toString()).placeholder(
                ctx.getResources().getDrawable(R.drawable.boy)).into(imageView);

        setList();
    }
    static final DecimalFormat df = new DecimalFormat("###,###.00");


    public void setData(PlayerDTO player, String tournamentName,
                         List<TourneyScoreByRoundDTO> tourneyScoreByRoundDTOList) {
        this.tourneyScoreByRoundDTOList = tourneyScoreByRoundDTOList;
        this.player = player;

        txtRounds.setText("" + tourneyScoreByRoundDTOList.size());
        txtPlayerName.setText(player.getFullName());
        txtTourneyName.setText(tournamentName);
        setList();
    }

}
