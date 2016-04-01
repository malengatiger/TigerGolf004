package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.adapters.PlayerAdapter;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.util.PersonEditDialog;
import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.MessageActivity;
import com.boha.malengagolf.library.PlayerHistoryActivity;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class PlayerListFragment extends Fragment implements PageFragment {


    public interface PlayerListener {
        public void onPlayerPictureRequested(PlayerDTO player, int index);

    }
    PlayerListener listener;
    LayoutInflater inflater;
    @Override
    public void onAttach(Activity a) {
        if (a instanceof PlayerListener) {
            listener = (PlayerListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement PlayerListener");
        }
        Log.d(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName());
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
        this.inflater = inflater;
        view = inflater
                .inflate(R.layout.fragment_list, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administratorDTO = SharedUtil.getAdministrator(ctx);
        fragmentManager = getFragmentManager();
        setFields();
        if (saved != null) {
            Log.e(LOG, "7&&&&&&&& ------  --- onCreateView - getting saved response");
            response = (ResponseDTO) saved.getSerializable("response");
        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                response = (ResponseDTO)bundle.getSerializable("response");
            }
        }
        playerList = response.getPlayers();
        setList(false,0);

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.e(LOG, "7&&&&&&&& ------  --- onSaveInstanceState");
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }
    @Override
    public void setFields() {
        listView = (ListView)view.findViewById(R.id.FRAG_listView);

    }

    PlayerAdapter personAdapter;

    List<PlayerDTO> playerList;
    int selectedIndex;


    public void setList(boolean forceImageRefresh, int index) {
        if (forceImageRefresh) {
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            Log.w(LOG, "image cache has been cleared ...........");
        }
        personAdapter = new PlayerAdapter(ctx, R.layout.person_item,
                playerList, golfGroup.getGolfGroupID(),
                new PlayerAdapter.PlayerAdapterListener() {
                    @Override
                    public void onCameraRequested(PlayerDTO p, int index) {
                        player = p;
                        selectedIndex = index;
                        listener.onPlayerPictureRequested(p, index);
                    }

                    @Override
                    public void onEditRequested(PlayerDTO p) {
                        player = p;
                        showPersonDialog(PersonEditDialog.ACTION_UPDATE);
                    }

                    @Override
                    public void onHistoryRequested(PlayerDTO p) {
                        player = p;
                        Intent intent = new Intent(ctx, PlayerHistoryActivity.class);
                        intent.putExtra("player", player);
                        startActivity(intent);
                    }

                    @Override
                    public void onMessageRequested(PlayerDTO p, int index) {
                        player = p;
                        Intent dd = new Intent(ctx, MessageActivity.class);
                        ResponseDTO w = new ResponseDTO();
                        w.setPlayers(playerList);
                        dd.putExtra("response", w);
                        dd.putExtra("index", index);
                        startActivity(dd);
                    }

                    @Override
                    public void onInvitationRequested(PlayerDTO p) {
                        player = p;
                        Intent x = new Intent(ctx, AppInvitationActivity.class);
                        x.putExtra("email", player.getEmail());
                        x.putExtra("pin", player.getPin());
                        x.putExtra("member", player.getFullName());
                        x.putExtra("type", AppInvitationFragment.PLAYER);
                        startActivity(x);
                    }
                });
        if (playerList == null) return;
        View view = inflater.inflate(R.layout.header, null);
        TextView txt = (TextView)view.findViewById(R.id.HEADER_text);
        ImageView img = (ImageView)view.findViewById(R.id.HEADER_image);
        txt.setText("Players");
        listView.addHeaderView(view);
        listView.setAdapter(personAdapter);
        registerForContextMenu(listView);
        listView.setSelection(selectedIndex);
    }

    PlayerDTO player;
    @Override
    public void showPersonDialog(int actionCode) {

        final PersonEditDialog personEditDialog = new PersonEditDialog();
        personEditDialog.setCtx(ctx);
        personEditDialog.setAction(actionCode);
        personEditDialog.setPersonType(PersonEditDialog.PLAYER);
        personEditDialog.setFragmentManager(getFragmentManager());

        if (actionCode == PersonEditDialog.ACTION_UPDATE) {
            personEditDialog.setPerson(player);
        }
        personEditDialog.setActivity(getActivity());
        personEditDialog.setDiagListener(new PersonEditDialog.DialogListener() {
            @Override
            public void onRecordAdded(PersonInterface person) {
                Log.i(LOG, "Player added OK");
            }

            @Override
            public void onRecordUpdated() {
                Log.i(LOG, "Player updated OK");
            }

            @Override
            public void onRecordDeleted() {
                Log.i(LOG, "Player deleted OK");
            }
        });

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        personEditDialog.show(getFragmentManager(), "playerDialog");
                    }
                });
            }
        }, 1000);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.player_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        player = playerList.get(info.position);
        selectedIndex = info.position;
        menu.setHeaderTitle(player.getFullName());
        menu.setHeaderIcon(R.drawable.golfer2_48);

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_invite_player:
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("email", player.getEmail());
                x.putExtra("pin", player.getPin());
                x.putExtra("member", player.getFullName());
                x.putExtra("type", AppInvitationFragment.PLAYER);
                startActivity(x);
                return true;
            case R.id.menu_tournament_history:
                Intent intent = new Intent(ctx, PlayerHistoryActivity.class);
                intent.putExtra("player", player);
                startActivity(intent);
                return true;
            case R.id.menu_take_picture:
                listener.onPlayerPictureRequested(player,selectedIndex);
                return true;
            case R.id.menu_send_mail:
                under();
                return true;
            case R.id.menu_send_text:
                under();
                return true;
            case R.id.menu_change:
                showPersonDialog(PersonEditDialog.ACTION_UPDATE);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    void under() {
        ToastUtil.toast(ctx, "Under construction. Please Check later!");
    }

    FragmentManager fragmentManager;
    ListView listView;
    TextView txtHeader, txtCount;
    GolfGroupDTO golfGroup;
    AdministratorDTO administratorDTO;
    static final String LOG = "PlayerListFragment";
    PlayerListener PlayerListener;
    Context ctx;
    View view;
    ResponseDTO response;
    static final int REQUEST_PLAYER_PICTURE = 3137;

//    public void setPlayerList(List<PlayerDTO> playerList) {
//        this.playerList = playerList;
//        response.setPlayers(playerList);
//
//        if (listView != null) {
//            setList(false,0);
//        }
//
//        ResponseDTO w = new ResponseDTO();
//        w.setPlayers(playerList);
//        CacheUtil.cacheData(ctx, w, CacheUtil.CACHE_PLAYERS, new CacheUtil.CacheUtilListener() {
//            @Override
//            public void onFileDataDeserialized(ResponseDTO response) {
//
//            }
//
//            @Override
//            public void onDataCached() {
//
//            }
//        });
//    }
}
