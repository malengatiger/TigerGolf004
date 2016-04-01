package com.boha.malengagolf.admin.com.boha.malengagolf.packs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.MGApp;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.ScoringByHoleActivity;
import com.boha.malengagolf.library.TeeTimeActivity;
import com.boha.malengagolf.library.adapters.TourneyPlayerAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.CompleteRounds;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/11.
 */
public class TourneyPlayerActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tplayers);
        ctx = getApplicationContext();
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        response = (ResponseDTO) getIntent().getSerializableExtra("response");
        golfGroup = SharedUtil.getGolfGroup(ctx);
        setFields();

        playerList = response.getPlayers();
        setPlayerStringList();
        setTitle(ctx.getResources().getString(R.string.players));
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addPlayers() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_TOURNAMENT_PLAYERS);
        List<Integer> list = new ArrayList<Integer>();
        for (PlayerDTO p: playerList) {
            Integer d = Integer.valueOf(p.getPlayerID());
            list.add(d);
        }
        //TODO - deal with very large membership - if > 144 - use new dialog and checkboxes
        w.setIdList(list);
        w.setTournamentID(tournament.getTournamentID());
        sendData(w);

    }
    private void addPlayer() {
        if (checkAll.isChecked()) {
            addPlayers();
            return;
        }
        if (player == null) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.select_player));
            return;
        }
        for (LeaderBoardDTO tps : leaderBoardList) {
            if (tps.getPlayer().getPlayerID() == player.getPlayerID()) {
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.player_already_in_tourn));
                return;
            }
        }
        Log.e(LOG, "Tournament : " + tournament.getTournamentID() + " " + tournament.getTourneyName());
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_TOURNAMENT_PLAYER);
        LeaderBoardDTO tps = new LeaderBoardDTO();
        PlayerDTO p = new PlayerDTO();
        p.setPlayerID(player.getPlayerID());
        tps.setPlayer(p);
        tps.setTournamentID(tournament.getTournamentID());

        w.setLeaderBoard(tps);
        notifyAdapter(tps);
        sendData(w);

    }

    private void notifyAdapter(LeaderBoardDTO lb) {
        leaderBoardList.add(0, lb);
        adapter.notifyDataSetChanged();
        txtCount.setText("" + leaderBoardList.size());
    }

    private void getCachedPlayers() {
        CacheUtil.getCachedData(ctx,CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    leaderBoardList = response.getLeaderBoardList();
                    setList();
                }
                getTournamentPlayers();
            }

            @Override
            public void onDataCached() {

            }
        });

    }
    private void sendData(RequestDTO w) {
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }

                        leaderBoardList = response.getLeaderBoardList();
                        if (leaderBoardList != null) {
                            txtCount.setText("" + leaderBoardList.size());
                            setList();
                            listView.setSelection(leaderBoardList.size() - 1);
                            CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {

                                }

                                @Override
                                public void onDataCached() {

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.socket_closed));
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
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
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//
//                leaderBoardList = response.getLeaderBoardList();
//                txtCount.setText("" + leaderBoardList.size());
//                setList();
//                listView.setSelection(leaderBoardList.size() - 1);
//                CacheUtil.cacheData(ctx,response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
//                    @Override
//                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onDataCached() {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }
    private void getTournamentPlayers() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TOURNAMENT_PLAYERS);
        w.setTournamentID(tournament.getTournamentID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO r) {

                response = r;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        leaderBoardList = response.getLeaderBoardList();
                        setList();
                        checkScoringCompletion();
                    }
                });

                CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached() {

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
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                response = r;
//                leaderBoardList = r.getLeaderBoardList();
//                setList();
//                checkScoringCompletion();
//                CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
//                    @Override
//                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onDataCached() {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void setPlayerSpinner() {
        if (tournament.getClosedForScoringFlag() > 0) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, playerStringList);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        playerSpinner.setAdapter(adapter);

        playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    player = null;
                    btnAddPlayer.setText(ctx.getResources().getString(R.string.select_player));
                    return;
                }
                player = playerList.get(i - 1);
                btnAddPlayer.setText(ctx.getResources().getString(R.string.register) + " " + player.getFirstName() + " " + player.getLastName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setPlayerStringList() {
        playerStringList = new ArrayList<String>();
        Log.i(LOG, "Sorting ... sortByAge: " + sortByAge);
        if (tournament.getUseAgeGroups() == 0) {
            sortByAge = false;
        }
        if (sortByAge) {
            playerStringList.add("(00) - " + ctx.getResources().getString(R.string.select_player));
            for (PlayerDTO p : playerList) {
                p.setSortType(PlayerDTO.SORT_BY_AGE);
            }
        } else {
            playerStringList.add("  " + ctx.getResources().getString(R.string.select_player));
            for (PlayerDTO p : playerList) {
                p.setSortType(PlayerDTO.SORT_BY_NAME);
            }
        }
        Collections.sort(playerList);
        Log.i(LOG, "setPlayerStringList coming thru ..playerList: " + playerList.size());


        for (PlayerDTO tps : playerList) {
            StringBuilder sb = new StringBuilder();
            if (tournament.getUseAgeGroups() == 1) {
                if (sortByAge) {
                    sb.append("(").append(appendAge(tps.getAge())).append(") - ");
                    sb.append(tps.getLastName()).append(", ").append(tps.getFirstName());

                } else {
                    sb.append(tps.getLastName()).append(", ").append(tps.getFirstName());
                    sb.append(" - (").append(appendAge(tps.getAge())).append(")");

                }

            } else {
                sb.append(tps.getLastName()).append(", ").append(tps.getFirstName());
            }
            playerStringList.add(sb.toString());
        }
        if (leaderBoardList != null) {
            if (sortByAge) {
                for (LeaderBoardDTO b : leaderBoardList) {
                    b.setSortType(LeaderBoardDTO.SORT_PLAYER_AGE);
                }
            } else {
                for (LeaderBoardDTO b : leaderBoardList) {
                    b.setSortType(LeaderBoardDTO.SORT_PLAYER_NAME);
                }
            }
            Collections.sort(leaderBoardList);
            setList();
        }
        setPlayerSpinner();


        sortByAge = !sortByAge;
    }

    private String appendAge(int age) {
        if (age < 10) {
            return "0" + age;
        } else {
            return "" + age;
        }
    }

    boolean sortByAge = true;
    int index;
    List<String> playerStringList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tourn_player_menu, menu);
        mMenu = menu;
        Log.e(LOG, "onCreateOptionsMenu");

        getCachedPlayers();

        return true;
    }

    static final int REQUEST_PICTURE = 1123;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_camera:
                Intent z = new Intent(ctx, PictureActivity.class);
                z.putExtra("tournament", tournament);
                startActivityForResult(z, REQUEST_PICTURE);
                return true;

            case R.id.menu_refresh:
                getTournamentPlayers();
                return true;

            case R.id.menu_help:
                ToastUtil.toast(ctx, "Under Construction");
                return true;
            case R.id.menu_back:
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_refresh);
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
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        hasPaused = true;
        super.onPause();
    }

    boolean hasPaused;

    @Override
    public void onResume() {
        Log.i(LOG, "########### onResume ...may want to refresh list");
        if (hasPaused) {
            hasPaused = false;
            getTournamentPlayers();
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...");
        super.onSaveInstanceState(b);
    }

    TextView txtPCount;
    private void setFields() {
        golfGroup = SharedUtil.getGolfGroup(ctx);
        topLayout = findViewById(R.id.TP_topLayout);
        if (tournament.getClosedForScoringFlag() > 0 || tournament.getClosedForRegistrationFlag() > 0) {
            topLayout.setVisibility(View.GONE);
        }
        TextView txt = (TextView) findViewById(R.id.TP_txtTournament);
        txt.setText(tournament.getTourneyName());
        imgSort = (TextView) findViewById(R.id.TP_imageSort);
        listView = (ListView) findViewById(R.id.TP_listView);
        btnAddPlayer = (Button) findViewById(R.id.TP_btnAddPlayer);
        playerSpinner = (Spinner) findViewById(R.id.TP_spinnerPlayers);
        txtCount = (TextView) findViewById(R.id.TP_count);
        txtPCount = (TextView) findViewById(R.id.TP_txtCount);
        checkAll = (CheckBox) findViewById(R.id.TP_checkAll);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    playerSpinner.setVisibility(View.GONE);
                    btnAddPlayer.setText(ctx.getResources().getString(R.string.register_all_players));
                } else {
                    playerSpinner.setVisibility(View.VISIBLE);
                    btnAddPlayer.setText(ctx.getResources().getString(R.string.register_this_player));
                }
            }
        });

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(50L);
                setPlayerStringList();

            }
        });
        if (tournament.getUseAgeGroups() == 0) {
            imgSort.setVisibility(View.GONE);
        }
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayer();
            }
        });



    }

    private void setList() {

        if (leaderBoardList == null) {
            Log.e(LOG,"%%%%%% LeaderBoardList is null, quitting!");
            leaderBoardList = new ArrayList<LeaderBoardDTO>();
        }
        MGApp mgApp = (MGApp) getApplication();
        txtCount.setText("" + leaderBoardList.size());
        txtPCount.setText("" + leaderBoardList.size());
        boolean useAgeGroups = false;
        if (tournament.getUseAgeGroups() == 1) useAgeGroups = true;
        adapter = new TourneyPlayerAdapter(ctx,
                R.layout.tourn_player_item,
                leaderBoardList,
                golfGroup.getGolfGroupID(),
                tournament.getGolfRounds(),
                tournament.getPar(), useAgeGroups, false, new TourneyPlayerAdapter.TourneyPlayerListener() {
            @Override
            public void onRemovePlayerRequested(LeaderBoardDTO l, int index) {
                leaderBoard = l;
                confirmRemoval(index);
            }

            @Override
            public void onScoringRequested(LeaderBoardDTO l) {
                leaderBoard = l;
                if (leaderBoard.isScoringComplete()) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.scoring_closed));
                    return;
                }
                int index = 0;
                for (LeaderBoardDTO dto: leaderBoardList) {
                    if (l.getLeaderBoardID() == dto.getLeaderBoardID()) {
                        selectedIndex = index;
                        break;
                    }
                    index++;
                }
                startScoringActivity();
            }
        });
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setSelection(selectedIndex);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderBoard = leaderBoardList.get(i);
                if (leaderBoard.isScoringComplete()) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.scoring_closed));
                    return;
                }
                selectedIndex = i;
                startScoringActivity();
            }
        });



    }


    int selectedIndex;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (tournament.getClosedForScoringFlag() > 0) {
            return;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tourn_player_context_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        leaderBoard = leaderBoardList.get(info.position);
        selectedIndex = info.position;
        menu.setHeaderTitle(leaderBoard.getPlayer().getFirstName()
                + " " + leaderBoard.getPlayer().getLastName());
        menu.setHeaderIcon(R.drawable.golfer2_48);

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_remove:
                confirmRemoval(selectedIndex);
                return true;
            case R.id.menu_score_by_hole:
                startScoringActivity();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private boolean startTeeTimeActivityIfNeeded() {
        if (currentRound == 0) currentRound = 1;
        calculateCurrentRound();
        TourneyScoreByRoundDTO x = leaderBoard.getTourneyScoreByRoundList()
                .get(currentRound - 1);
        if (x.getTee() == 0) {
            Intent intentx = new Intent(ctx, TeeTimeActivity.class);
            intentx.putExtra("tournament", tournament);
            intentx.putExtra("currentRound", currentRound);
            intentx.putExtra("leaderBoard", leaderBoard);
            startActivityForResult(intentx, GO_GET_TEETIME);
            return true;
        }
        return false;
    }

    int currentRound;

    private void calculateCurrentRound() {
        CompleteRounds.markScoringCompletion(leaderBoard);
        int index = 0;
        for (TourneyScoreByRoundDTO tbs : leaderBoard.getTourneyScoreByRoundList()) {
            if (tbs.getScoringComplete() == 0) {
                currentRound = index + 1;
                break;
            }
            index++;
        }
    }

    private void checkScoringCompletion() {
        if (tournament.getClosedForScoringFlag() > 0) {
            return;
        }
        for (LeaderBoardDTO x : leaderBoardList) {
            CompleteRounds.markScoringCompletion(x);
        }
        int complete = 0;
        for (LeaderBoardDTO x : leaderBoardList) {
            if (x.isScoringComplete()) {
                complete++;
            }
        }


        if (complete == leaderBoardList.size() && leaderBoardList.size() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            if (tournament.getClosedForScoringFlag() == 0) {
                dialog
                        .setTitle(ctx.getResources().getString(R.string.player_scoring))
                        .setMessage(ctx.getResources().getString(R.string.close_scoring))
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                closeTournamentForScoring();
                            }
                        }).create().show();
            }


        }
    }

    private void startScoringActivity() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (tournament.getClosedForRegistrationFlag() == 0) {
            dialog
                    .setTitle(ctx.getResources().getString(R.string.player_reg))
                    .setMessage(ctx.getResources().getString(R.string.close_registration))
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            closeTournamentForRegistration();
                            startActually();
                        }
                    }).create().show();
        } else {
            startActually();
        }
    }

    private void startActually() {
        Intent intentx = new Intent(ctx, ScoringByHoleActivity.class);
        intentx.putExtra("tournament", tournament);
        intentx.putExtra("leaderBoard", leaderBoard);

        Log.e(LOG,"****************** starting for activityForResult: ScoringByHoleActivity, req: " + GO_SCORING);
        startActivityForResult(intentx, GO_SCORING);
    }

    private void closeTournamentForRegistration() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_TOURNAMENT);
        w.setTournament(tournament);
        tournament.setClosedForRegistrationFlag(1);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        closedForRegistration = true;
                        Log.i(LOG, "closedForRegistration flag updated");
                        btnAddPlayer.setVisibility(View.GONE);
                        playerSpinner.setVisibility(View.GONE);
                        checkAll.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.socket_closed));
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
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
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                closedForRegistration = true;
//                Log.i(LOG, "closedForRegistration flag updated");
//                btnAddPlayer.setVisibility(View.GONE);
//                playerSpinner.setVisibility(View.GONE);
//                checkAll.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    boolean closedForRegistration;

    private void closeTournamentForScoring() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_TOURNAMENT);
        tournament.setClosedForScoringFlag(1);
        w.setTournament(tournament);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        Log.i(LOG, "closedForScoring flag updated");
                        btnAddPlayer.setVisibility(View.GONE);
                        playerSpinner.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.socket_closed));
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
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
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                Log.i(LOG, "closedForScoring flag updated");
//                btnAddPlayer.setVisibility(View.GONE);
//                playerSpinner.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    static final int GO_SCORING = 3369, GO_GET_TEETIME = 3135;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(LOG, "--------- onActivityResult request: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == GO_SCORING) {
            if (resultCode == Activity.RESULT_OK) {
                Log.w(LOG, "#############...getting response object ... to reset list, index: " + selectedIndex);
                ResponseDTO d = (ResponseDTO) data.getSerializableExtra("response");
                leaderBoardList = d.getLeaderBoardList();
                Log.i(LOG, "######### onActivityResult, leaderBoardList = " + leaderBoardList.size());
                setList();
                listView.setSelection(selectedIndex);
            }

        }
        if (requestCode == GO_GET_TEETIME) {
            if (resultCode == Activity.RESULT_OK) {
                Log.w(LOG, "...back from teeTime ...");
                ResponseDTO d = (ResponseDTO) data.getSerializableExtra("leaderBoardList");
                leaderBoardList = d.getLeaderBoardList();
                setList();
                listView.setSelection(selectedIndex);
            }
        }
        if (requestCode == REQUEST_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.w(LOG, "...back from picture ...");
                setList();
                listView.setSelection(selectedIndex);
            }
        }
    }

    private void removePlayer(int index) {

        leaderBoardList.remove(index);
        adapter.notifyDataSetChanged();
        txtCount.setText("" + leaderBoardList.size());
        txtPCount.setText("" + leaderBoardList.size());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.REMOVE_TOURNAMENT_PLAYER);
        w.setTournamentID(tournament.getTournamentID());
        w.setPlayerID(leaderBoard.getPlayer().getPlayerID());
        setRefreshActionButtonState(true);

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.errorToast(ctx, response.getMessage());

                        }
                    });
                    return;
                }
                Log.e(LOG, "#### Tournament player removed");
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, "Communications link closed. Please try again");
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
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
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                //ToastUtil.toast(ctx, ctx.getResources().getString(R.string.player_removed));
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        Intent d = new Intent();
        d.putExtra("closedForRegistration", closedForRegistration);
        d.putExtra("numberRegistered", leaderBoardList.size());
        setResult(RESULT_OK, d);
        finish();
    }

    private void confirmRemoval(final int index) {
        AlertDialog.Builder diag = new AlertDialog.Builder(this);
        diag.setTitle(ctx.getResources().getString(com.boha.malengagolf.library.R.string.remove_player))
                .setMessage(ctx.getResources().getString(com.boha.malengagolf.library.R.string.remove_player_msg))
                .setPositiveButton(ctx.getResources().getString(com.boha.malengagolf.library.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removePlayer(index);
                    }
                })
                .setNegativeButton(ctx.getResources().getString(com.boha.malengagolf.library.R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
    View topLayout;
    Menu mMenu;
    Context ctx;
    List<LeaderBoardDTO> leaderBoardList, newList;
    LeaderBoardDTO leaderBoard;
    List<PlayerDTO> playerList;
    PlayerDTO player;
    ResponseDTO response;
    TournamentDTO tournament;
    Spinner playerSpinner;
    Button btnAddPlayer;
    ListView listView;
    GolfGroupDTO golfGroup;
    TourneyPlayerAdapter adapter;
    ImageLoader imageLoader;
    TextView txtCount;
    TextView imgSort;
    CheckBox checkAll;
    Vibrator vb;
    static final String LOG = "TourneyPlayerActivity";

}
