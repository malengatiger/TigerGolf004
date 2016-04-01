package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.ScoreCardActivity;
import com.boha.malengagolf.library.adapters.PlayerHistoryAdapter;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
/* COPYRIGHT (C) 1997 Aubrey Malabie. All Rights Reserved. */
/**
 * Created by aubreyM on 2014/04/09.
 */
public class PlayerHistoryFragment extends Fragment implements MGPageFragment {

    @Override
    public void onAttach(Activity a) {

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
        Log.i(LOG, "onCreateView");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_player_history, container, false);
        Bundle b = getArguments();
        if (b != null) {
            player = (PlayerDTO)b.getSerializable("player");
            getPlayerHistory();
        }


        setFields();
        return view;
    }
    private void getPlayerHistory() {
        RequestDTO z = new RequestDTO();
        z.setRequestType(RequestDTO.GET_PLAYER_HISTORY);
        z.setPlayerID(player.getPlayerID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, z, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        leaderBoardList = response.getLeaderBoardList();
                        if (leaderBoardList == null) {
                            leaderBoardList = new ArrayList<LeaderBoardDTO>();
                        }
                        setList();
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

                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN,z,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                leaderBoardList = response.getLeaderBoardList();
//                if (leaderBoardList == null) {
//                    leaderBoardList = new ArrayList<LeaderBoardDTO>();
//                }
//                setList();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.PH_FRAG_list);
        txtPlayerName = (TextView) view.findViewById(R.id.PH_FRAG_playerName);
        txtTourneyCount = (TextView) view.findViewById(R.id.PH_FRAG_count);
        imageView = (ImageView)view.findViewById(R.id.PH_FRAG_image);
        Statics.setRobotoFontLight(ctx, txtPlayerName);
    }

    public void setList() {
        Log.i(LOG, "setList");
        setName();
        txtTourneyCount.setText("" + leaderBoardList.size());
        playerHistoryAdapter = new PlayerHistoryAdapter(ctx, R.layout.player_history_item, leaderBoardList);
        listView.setAdapter(playerHistoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
                Intent xx = new Intent(ctx, ScoreCardActivity.class);
                xx.putExtra("leaderBoard", leaderBoard);
               startActivity(xx);
            }
        });
    }
    private void setName() {
        txtPlayerName.setText(player.getFullName());
        Statics.setRobotoFontBold(ctx,txtPlayerName);
        //image
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("golfgroup")
                .append(SharedUtil.getGolfGroup(ctx).getGolfGroupID()).append("/")
                .append("player/t")
                .append(player.getPlayerID())
                .append(".jpg");
        Picasso.with(ctx).load(sb.toString()).placeholder(ContextCompat.getDrawable(ctx, R.drawable.boy)).into(imageView);

    }
    LeaderBoardDTO leaderBoard;
    public void setLeaderBoardList(PlayerDTO player, List<LeaderBoardDTO> leaderBoardList) {

        this.leaderBoardList = leaderBoardList;
        this.player = player;

        setList();

    }

    PlayerHistoryAdapter playerHistoryAdapter;
    ListView listView;
    TextView txtPlayerName, txtTourneyCount;
    static final String LOG = "PlayersHistoryFragment";
    Context ctx;
    View view;
    ImageView imageView;
    PlayerDTO player;
    ResponseDTO response;
    List<LeaderBoardDTO> leaderBoardList;


}
