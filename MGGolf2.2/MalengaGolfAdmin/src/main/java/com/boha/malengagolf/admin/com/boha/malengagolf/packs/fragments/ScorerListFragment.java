package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.util.PersonEditDialog;
import com.boha.malengagolf.library.activities.AppInvitationActivity;
import com.boha.malengagolf.library.adapters.ScorerListAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ScorerListFragment extends Fragment implements PageFragment {


    public interface ScorerListener {
        void onScorerPictureRequested(ScorerDTO scorer, int position);

        void onRefreshRequested();
    }

    ScorerListener listener;
    LayoutInflater inflater;
    FloatingActionButton fab;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof ScorerListener) {
            listener = (ScorerListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement ScorerListener");
        }
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        this.inflater = inflater;
        view = inflater
                .inflate(R.layout.fragment_list, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
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
        scorerList = response.getScorers();
        setList(false, 0);
        return view;
    }

    @Override
    public void setFields() {
        recyclerView = (RecyclerView) view.findViewById(R.id.FRAG_listView);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshRequested();
            }
        });
    }

    int selectedIndex;

    public void setList(boolean forceImageRefresh, int index) {

        adapter = new ScorerListAdapter(scorerList, ctx,
                new ScorerListAdapter.ScorerAdapterListener() {
                    @Override
                    public void onCameraRequested(ScorerDTO p, int index) {
                        scorer = p;
                        selectedIndex = index;
                        listener.onScorerPictureRequested(p, index);
                    }

                    @Override
                    public void onEditRequested(ScorerDTO p) {
                        scorer = p;
                        showPersonDialog(PersonEditDialog.ACTION_UPDATE);
                    }

                    @Override
                    public void onMessageRequested(ScorerDTO p) {
                        under();
                    }

                    @Override
                    public void onInvitationRequested(ScorerDTO p) {
                        scorer = p;
                        Intent x = new Intent(ctx, AppInvitationActivity.class);
                        x.putExtra("email", p.getEmail());
                        x.putExtra("pin", p.getPin());
                        x.putExtra("member", p.getFullName());
                        x.putExtra("type", AppInvitationFragment.SCORER);
                        startActivity(x);
                    }
                });
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(selectedIndex);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    ScorerDTO scorer;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.scorer_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        scorer = response.getScorers().get(info.position);
        selectedIndex = info.position;
        menu.setHeaderTitle(scorer.getFullName());
        menu.setHeaderIcon(R.drawable.golfer2_48);

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_invite_scorer:
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("email", scorer.getEmail());
                x.putExtra("pin", scorer.getPin());
                x.putExtra("member", scorer.getFullName());
                x.putExtra("type", AppInvitationFragment.SCORER);
                startActivity(x);
                return true;
            case R.id.menu_tournament_history_scorer:
                under();
                return true;
            case R.id.menu_take_picture_scorer:
                listener.onScorerPictureRequested(scorer, selectedIndex);
                return true;
            case R.id.menu_send_mail_scorer:
                under();
                return true;
            case R.id.menu_send_text_scorer:
                under();
                return true;
            case R.id.menu_change_scorer:
                showPersonDialog(PersonEditDialog.ACTION_UPDATE);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    void under() {
        ToastUtil.toast(ctx, "Under construction. Check later!");
    }

    @Override
    public void showPersonDialog(int actionCode) {
        final PersonEditDialog personEditDialog = new PersonEditDialog();
        personEditDialog.setCtx(ctx);
        personEditDialog.setAction(actionCode);
        personEditDialog.setPersonType(PersonEditDialog.SCORER);

        if (actionCode == PersonEditDialog.ACTION_UPDATE) {
            personEditDialog.setPerson(scorer);
        }
        if (actionCode == PersonEditDialog.ACTION_ADD) {
            personEditDialog.setPerson(new ScorerDTO());
        }
        personEditDialog.setActivity(getActivity());
        personEditDialog.setDiagListener(new PersonEditDialog.DialogListener() {
            @Override
            public void onRecordAdded(PersonInterface person) {
                Log.i(LOG, "scorer added OK");
                if (scorerList == null) {
                    scorerList = new ArrayList<ScorerDTO>();
                }
                scorerList.add(0, (ScorerDTO) person);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRecordUpdated() {
                Log.i(LOG, "scorer updated OK");
            }

            @Override
            public void onRecordDeleted() {
                Log.i(LOG, "scorer deleted OK");
            }
        });
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        personEditDialog.show(getFragmentManager(), "scorerDialog");
                    }
                });
            }
        }, 1000);
    }

    FragmentManager fragmentManager;
    RecyclerView recyclerView;
    GolfGroupDTO golfGroup;
    static final String LOG = "ScorerListFragment";
    ScorerListener ScorerListener;
    Context ctx;
    ScorerListAdapter adapter;
    List<ScorerDTO> scorerList;
    View view;
    ResponseDTO response;


}
