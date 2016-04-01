package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.boha.malengagolf.admin.MainPagerActivity;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.LeaderBoardPager;
import com.boha.malengagolf.library.MGGalleryActivity;
import com.boha.malengagolf.library.MessageActivity;
import com.boha.malengagolf.library.PictureActivity;
import com.boha.malengagolf.library.TeeTimeActivity;
import com.boha.malengagolf.library.adapters.TournamentAdapter;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.fragments.TournamentListListener;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class TournamentListFragment extends Fragment implements PageFragment {


    public interface TournamentListener {
        public void onManageTournamentRequest(TournamentDTO t);

        public void onManagePlayersRequest(TournamentDTO t, List<PlayerDTO> list);

        public void setBusy();

        public void setNotBusy();

    }

    TournamentListener tournamentListener;
    FragmentActivity act;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof TournamentListener) {
            tournamentListener = (TournamentListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement TournamentListener");
        }
        Log.d(LOG,
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
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_list, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administratorDTO = SharedUtil.getAdministrator(ctx);
        fragmentManager = getFragmentManager();

        setFields();
        if (saved != null) {
            Log.i(LOG, "onCreateView - getting saved response");
            response = (ResponseDTO) saved.getSerializable("response");

        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                response = (ResponseDTO) bundle.getSerializable("response");
            }
        }
        tournamentList = response.getTournaments();
        setList();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    @Override
    public void setFields() {
        listView = (ListView) view.findViewById(R.id.FRAG_listView);
    }

    public void setList() {
        if (tournamentList == null) tournamentList = new ArrayList<TournamentDTO>();
        adapter = new TournamentAdapter(ctx, R.layout.tournament_item,
                tournamentList, new TournamentListListener() {
            @Override
            public void manageTournamentPlayersScores(TournamentDTO t) {
                tournament = t;
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.scoring_closed));
                    return;
                }
                tournamentListener.onManagePlayersRequest(t, response.getPlayers());
            }

            @Override
            public void editTournament(TournamentDTO t) {
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                tournament = t;
                tournamentListener.onManageTournamentRequest(t);
            }

            @Override
            public void viewLeaderBoard(TournamentDTO t) {
                Log.d(LOG, "################# viewLeaderBoard tournamentType: " + t.getTournamentType());
                tournament = t;
                SharedUtil.setScrollIndex(ctx, 0);
                Intent x1 = new Intent(ctx, LeaderBoardPager.class);
                x1.putExtra("tournament", t);
                x1.putExtra("golfGroup", golfGroup);
                x1.putExtra("administrator", SharedUtil.getAdministrator(ctx));
                startActivity(x1);
            }

            @Override
            public void takePictures(TournamentDTO t) {
                tournament = t;
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                Intent intentt = new Intent(ctx, PictureActivity.class);
                intentt.putExtra("tournament", t);
                startActivity(intentt);
            }

            @Override
            public void viewGallery(TournamentDTO t) {
                tournament = t;
                Intent intentx = new Intent(ctx, MGGalleryActivity.class);
                intentx.putExtra("tournament", t);
                startActivity(intentx);
//5001758655
            }

            @Override
            public void manageTournamentTees(TournamentDTO t) {
                tournament = t;
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                Intent intentt = new Intent(ctx, TeeTimeActivity.class);
                intentt.putExtra("tournament", t);
                startActivity(intentt);
            }

            @Override
            public void inviteAppUser() {
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("type", AppInvitationFragment.APP_USER);
                startActivity(x);
            }

            @Override
            public void uploadVideo(TournamentDTO t) {
                tournament = t;
                underConstruction();
            }

            @Override
            public void sendPlayerTextMessage(TournamentDTO t) {
                tournament = t;
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                underConstruction();

            }

            @Override
            public void sendPlayerEmail(TournamentDTO t) {
                tournament = t;
                if (tournament.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                CacheUtil.getCachedData(ctx, CacheUtil.CACHE_LEADER_BOARD, tournament.getTournamentID(), new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {
                        if (response.getLeaderBoardList() == null) return;
                        List<PlayerDTO> pList = new ArrayList<PlayerDTO>();
                        for (LeaderBoardDTO b : response.getLeaderBoardList()) {
                            pList.add(b.getPlayer());
                        }
                        response.setPlayers(pList);
                        response.setLeaderBoardList(null);
                        Intent dd = new Intent(ctx, MessageActivity.class);
                        dd.putExtra("response", response);
                        startActivity(dd);
                    }

                    @Override
                    public void onDataCached() {

                    }
                });
            }

            //0824431972 kalidas Dr.
            @Override
            public void removeTournament(TournamentDTO t) {
                if (t.getClosedForScoringFlag() > 0) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                    return;
                }
                if (t.getExampleFlag() > 0) {
                    showDeleteSampleTournament();
                } else {
                    showDeleteTournament(t);
                }
            }

            @Override
            public void deleteSampleTournaments() {
                showDeleteSampleTournament();
            }
        });
        listView.setAdapter(adapter);
        //registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


    }

    public void updateList(TournamentDTO t) {
        for (TournamentDTO dto : response.getTournaments()) {
            if (dto.getTournamentID() == t.getTournamentID()) {
                dto = t;
                break;
            }
        }
        adapter.notifyDataSetChanged();

    }

    public void refreshList(List<TournamentDTO> tList) {
        this.tournamentList = tList;
        response.getTournaments().clear();
        response.setTournaments(tList);
        setList();
//        //TODO - set listview to the added tournament??


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.tourn_context_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        tournament = tournamentList.get(info.position);
        selectedTourneyIndex = info.position;
        menu.setHeaderTitle(tournament.getTourneyName());
        menu.setHeaderIcon(R.drawable.golfer2_48);
        if (tournament.getClosedForScoringFlag() > 0) {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).setTitle(ctx.getResources().getString(R.string.view_players));
            menu.getItem(4).setVisible(false);
            menu.getItem(6).setVisible(false);
            menu.getItem(7).setVisible(false);
            menu.getItem(8).setVisible(false);
        }

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_invite_lb_viewer:
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("type", AppInvitationFragment.APP_USER);
                startActivity(x);
                return true;
            case R.id.menu_pictures:
                Intent intentx = new Intent(ctx, MGGalleryActivity.class);
                intentx.putExtra("tournament", tournament);
                startActivity(intentx);
                return true;
            case R.id.menu_teetime:
                Intent intentt = new Intent(ctx, TeeTimeActivity.class);
                intentt.putExtra("tournament", tournament);
                startActivity(intentt);
                return true;
            case R.id.menu_manage_players:
                tournamentListener.onManagePlayersRequest(tournament, response.getPlayers());
                return true;
            case R.id.menu_manage_tourn:
                tournamentListener.onManageTournamentRequest(tournament);
                return true;
            case R.id.menu_leaderboard:
                Intent x1 = new Intent(ctx, LeaderBoardPager.class);
                x1.putExtra("tournament", tournament);
                x1.putExtra("golfGroup", golfGroup);
                x1.putExtra("administrator", SharedUtil.getAdministrator(ctx));
                startActivity(x1);
                return true;
            case R.id.menu_delete:
                showDeleteTournament(tournament);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    public void onTournamentChange(int reqCode, int resCode, Intent data) {
        Log.i(LOG, "onActivityResult reqCode: " + reqCode + " resCode: " + resCode);
        if (reqCode == MainPagerActivity.MANAGE_TOURNAMENT_PLAYERS) {
            if (resCode == Activity.RESULT_OK) {
                boolean closedForRegistration = data.getBooleanExtra("closedForRegistration", false);
                int count = data.getIntExtra("numberRegistered", 0);
                if (closedForRegistration) {
                    tournament.setClosedForRegistrationFlag(1);
                    Log.i(LOG, "Tournament marked as closedForRegistration");
                }
                tournament.setNumberOfRegisteredPlayers(count);
                adapter.notifyDataSetChanged();
            }
        }
        if (reqCode == MainPagerActivity.MANAGE_TOURNAMENT) {
            if (resCode == Activity.RESULT_OK) {
                boolean isClosed = data.getBooleanExtra("isClosed", false);
                boolean tourneyAdded = data.getBooleanExtra("tourneyAdded", false);
                boolean tourneyUpdated = data.getBooleanExtra("tourneyUpdated", false);
                if (isClosed) {
                    Log.i(LOG, "Tournament closed - setting flag in list");
                    TournamentDTO t = (TournamentDTO) data.getSerializableExtra("tournament");
                    for (TournamentDTO x : tournamentList) {
                        if (t.getTournamentID() == x.getTournamentID()) {
                            x.setClosedForScoringFlag(1);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                if (tourneyUpdated) {
                    Log.i(LOG, "Tournament updated - setting new details");
                    TournamentDTO t = (TournamentDTO) data.getSerializableExtra("tournament");
                    for (TournamentDTO x : tournamentList) {
                        if (t.getTournamentID() == x.getTournamentID()) {
                            x.setTourneyName(t.getTourneyName());
                            x.setGolfRounds(t.getGolfRounds());
                            x.setClubID(t.getClubID());
                            x.setStartDate(t.getStartDate());
                            x.setClubName(t.getClubName());
                            x.setEndDate(t.getEndDate());
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                if (tourneyAdded) {
                    Log.i(LOG, "Tournament added - setting new list");
                    ResponseDTO z = (ResponseDTO) data.getSerializableExtra("response");
                    tournamentList = z.getTournaments();
                    setList();
                    listView.setSelection(selectedTourneyIndex);
                }

            }
        }
    }

    private void underConstruction() {
        ToastUtil.toast(ctx, "Feature under construction. Watch the space!");
    }

    @Override
    public void showPersonDialog(int actionCode) {

    }

    private void showDeleteTournament(final TournamentDTO t) {
        AlertDialog.Builder diag = new AlertDialog.Builder(ctx);
        diag.setTitle(ctx.getResources().getString(R.string.delete_tournament))
                .setMessage(ctx.getResources().getString(R.string.delete_message) + "\n\n" + t.getTourneyName())
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int index = 0;
                        for (TournamentDTO dto : response.getTournaments()) {
                            if (dto.getTournamentID() == t.getTournamentID()) {
                                break;
                            }
                            index++;
                        }
                        response.getTournaments().remove(index);
                        adapter.notifyDataSetChanged();
                        RequestDTO w = new RequestDTO();
                        w.setRequestType(RequestDTO.DELETE_TOURNAMENT);
                        w.setTournamentID(t.getTournamentID());
                        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
                            return;
                        }
                        tournamentListener.setBusy();
                        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
                            @Override
                            public void onMessage(final ResponseDTO response) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tournamentListener.setNotBusy();
                                        if (!ErrorUtil.checkServerError(ctx, response)) {
                                            return;
                                        }
                                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tournament_removed));
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
//                        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//                            @Override
//                            public void onResponseReceived(ResponseDTO r) {
//                                tournamentListener.setNotBusy();
//                                if (!ErrorUtil.checkServerError(ctx, r)) {
//                                    return;
//                                }
//                                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tournament_removed));
//                            }
//
//                            @Override
//                            public void onVolleyError(VolleyError error) {
//                                tournamentListener.setNotBusy();
//                                ErrorUtil.showServerCommsError(ctx);
//                            }
//                        });

                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void showDeleteSampleTournament() {
        AlertDialog.Builder diag = new AlertDialog.Builder(ctx);
        diag.setTitle(ctx.getResources().getString(R.string.delete_sample_tournament))
                .setMessage(ctx.getResources().getString(R.string.delete_sample_message) + "\n\n" + getSampleTournaments())
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        response.getTournaments().remove(selectedTourneyIndex);
                        adapter.notifyDataSetChanged();
                        RequestDTO w = new RequestDTO();
                        w.setRequestType(RequestDTO.DELETE_SAMPLE_TOURNAMENTS);
                        w.setGolfGroupID(golfGroup.getGolfGroupID());
                        w.setCountryID(golfGroup.getCountryID());
                        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
                            return;
                        }
                        tournamentListener.setBusy();
                        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
                            @Override
                            public void onMessage(final ResponseDTO response) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tournamentListener.setNotBusy();
                                        if (!ErrorUtil.checkServerError(ctx, response)) {
                                            return;
                                        }
                                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tournament_removed));
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
//                        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//                            @Override
//                            public void onResponseReceived(ResponseDTO r) {
//                                tournamentListener.setNotBusy();
//                                if (!ErrorUtil.checkServerError(ctx, r)) {
//                                    return;
//                                }
//                                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tournament_removed));
//                            }
//
//                            @Override
//                            public void onVolleyError(VolleyError error) {
//                                tournamentListener.setNotBusy();
//                                ErrorUtil.showServerCommsError(ctx);
//                            }
//                        });

                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private String getSampleTournaments() {
        StringBuilder sb = new StringBuilder();
        for (TournamentDTO t : tournamentList) {
            if (t.getExampleFlag() > 0) {
                sb.append(t.getTourneyName()).append("\n");
            }
        }
        return sb.toString();
    }

    int selectedTourneyIndex;
    FragmentManager fragmentManager;
    ListView listView;
    TextView txtHeader, txtCount;
    GolfGroupDTO golfGroup;
    AdministratorDTO administratorDTO;
    static final String LOG = "TournamentListFragment";
    TournamentListener TournamentListener;
    TournamentAdapter adapter;
    List<TournamentDTO> tournamentList;
    TournamentDTO tournament;
    Context ctx;
    View view;
    ResponseDTO response;

}
