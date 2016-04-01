package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.LeaderBoardPager;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.TournamentPlayerListActivity;
import com.boha.malengagolf.library.adapters.TournamentAdapter;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.AppUserDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.MGPageFragment;
import com.boha.malengagolf.library.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class GolfGroupTournamentListFragment extends Fragment implements MGPageFragment {


    public interface TournamentListener {
        public void setBusy();
        public void setNotBusy();
        public void onGalleryRequested(TournamentDTO t);

    }

    TournamentListener tournamentListener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof TournamentListener) {
            tournamentListener = (TournamentListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement TournamentListener");
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
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_scorer_player, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        fragmentManager = getFragmentManager();

        setFields();
        if (saved != null) {
            Log.i(LOG, "onCreateView - getting saved response");
            response = (ResponseDTO) saved.getSerializable("response");
            tournamentList = response.getTournaments();
            setList();

        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                response = (ResponseDTO) bundle.getSerializable("response");
                type = bundle.getInt("type", 0);
                tournamentList = response.getTournaments();
                player = (PlayerDTO)bundle.getSerializable("player");
                scorer = (ScorerDTO)bundle.getSerializable("scorer");
                administrator = (AdministratorDTO)bundle.getSerializable("administrator");
                appUser = (AppUserDTO)bundle.getSerializable("appUser");
                setList();
            }
        }

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.FRAG_list);
    }

    public void setList() {
        if (tournamentList == null) tournamentList = new ArrayList<TournamentDTO>();

        adapter = new TournamentAdapter(ctx, R.layout.tournament_gg_item,
                tournamentList, true, selectedTourneyIndex,
                new TournamentListListener() {
                    @Override
                    public void manageTournamentPlayersScores(TournamentDTO t) {
                        Intent i = new Intent(ctx, TournamentPlayerListActivity.class);
                        i.putExtra("tournament", t);
                        startActivity(i);
                    }

                    @Override
                    public void editTournament(TournamentDTO t) {

                    }

                    @Override
                    public void viewLeaderBoard(TournamentDTO t) {
                        tournament = t;
                        Intent x1 = new Intent(ctx, LeaderBoardPager.class);
                        x1.putExtra("tournament", t);
                        x1.putExtra("player",player);
                        x1.putExtra("scorer",scorer);
                        x1.putExtra("appUser",appUser);
                        startActivity(x1);
                    }

                    @Override
                    public void takePictures(TournamentDTO t) {
                        tournament = t;
                        Intent x1 = new Intent(ctx, PictureActivity.class);
                        x1.putExtra("tournament", t);
                        startActivity(x1);
                    }

                    @Override
                    public void viewGallery(TournamentDTO t) {
                        tournamentListener.onGalleryRequested(t);
                    }

                    @Override
                    public void manageTournamentTees(TournamentDTO t) {

                    }

                    @Override
                    public void inviteAppUser() {
                        Log.w(LOG, "inviteAppUser ----------------------->");
                        Intent i = new Intent(ctx, AppInvitationActivity.class);
                        i.putExtra("type", AppInvitationFragment.APP_USER);
                        startActivity(i);
                    }

                    @Override
                    public void uploadVideo(TournamentDTO t) {

                    }

                    @Override
                    public void sendPlayerTextMessage(TournamentDTO t) {

                    }

                    @Override
                    public void sendPlayerEmail(TournamentDTO t) {

                    }

                    @Override
                    public void removeTournament(TournamentDTO t) {

                    }

                    @Override
                    public void deleteSampleTournaments() {

                    }
                });
        listView.setAdapter(adapter);
        listView.setDividerHeight(10);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTourneyIndex = i;
            }
        });


    }


    public void refreshList(List<TournamentDTO> tList, int type) {
        this.tournamentList = tList;
        this.type = type;
        if (response == null) response = new ResponseDTO();
        response.setTournaments(tList);
        setList();


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.scoring_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        tournament = tournamentList.get(info.position);
        selectedTourneyIndex = info.position;
        menu.setHeaderTitle(tournament.getTourneyName());
        menu.setHeaderIcon(R.drawable.flag32);

        if (tournament.getClosedForScoringFlag() > 0) {
            menu.getItem(0).setVisible(false);
        }
        if (type == APP_USER) {
            menu.getItem(0).setVisible(false);
        }

        super.onCreateContextMenu(menu, v, menuInfo);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        if (item.getItemId() == R.id.menu_gallery) {
            tournamentListener.onGalleryRequested(tournament);
            return true;
        }
        if (item.getItemId() == R.id.menu_picture) {
            return true;
        }
        if (item.getItemId() == R.id.menu_scoring) {
            Intent x = new Intent(ctx, TournamentPlayerListActivity.class);
            x.putExtra("tournament", tournament);
            startActivity(x);
            return true;
         }
        if (item.getItemId() == R.id.menu_help) {
            return true;
        }

        if (item.getItemId() == R.id.menu_leaderboard) {
            Intent x1 = new Intent(ctx, LeaderBoardPager.class);
            x1.putExtra("tournament", tournament);
            x1.putExtra("golfGroup", golfGroup);
            x1.putExtra("player",player);
            x1.putExtra("scorer",scorer);
            x1.putExtra("appUser",appUser);
            startActivity(x1);
            return true;
        }


        return super.onContextItemSelected(item);

    }



    int selectedTourneyIndex;
    FragmentManager fragmentManager;
    ListView listView;
    TextView txtHeader, txtCount;
    GolfGroupDTO golfGroup;
    AdministratorDTO administrator;
    static final String LOG = "TournamentListFragment";
    TournamentListener TournamentListener;
    TournamentAdapter adapter;
    List<TournamentDTO> tournamentList;
    TournamentDTO tournament;
    Context ctx;
    View view;
    ResponseDTO response;
    int type;
    ScorerDTO scorer;
    PlayerDTO player;
    AppUserDTO appUser;

    public void setScorer(ScorerDTO scorer) {
        this.scorer = scorer;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public void setAppUser(AppUserDTO appUser) {
        this.appUser = appUser;
    }

    public static final int APP_USER = 3;
}
