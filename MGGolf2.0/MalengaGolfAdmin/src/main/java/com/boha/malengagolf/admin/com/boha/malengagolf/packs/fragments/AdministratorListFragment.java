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
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.adapters.AdministratorAdapter;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.util.PersonEditDialog;
import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class AdministratorListFragment extends Fragment implements PageFragment {

    public interface AdministratorListener {
        public void onAdministratorPictureRequested(AdministratorDTO administrator, int index);

    }
    AdministratorListener listener;
    LayoutInflater inflater;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof AdministratorListener) {
            listener = (AdministratorListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement AdministratorListener");
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
                response = (ResponseDTO)bundle.getSerializable("response");
            }
        }
        administratorList = response.getAdministrators();
        setList(false,0);
        return view;
    }
    @Override
    public void setFields() {
        listView = (ListView)view.findViewById(R.id.FRAG_listView);
    }
    int selectedIndex;

    public void setList(boolean forceImageRefresh, int index) {

        if (forceImageRefresh) {
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            Log.e(LOG,"image cache has been cleared ..........");
        }
        adapter = new AdministratorAdapter(ctx,R.layout.person_item,
                administratorList,
                new AdministratorAdapter.AdministrationAdapterListener() {
                    @Override
                    public void onCameraRequested(AdministratorDTO p, int index) {
                        administrator = p;
                        selectedIndex = index;
                        listener.onAdministratorPictureRequested(p, index);
                    }

                    @Override
                    public void onEditRequested(AdministratorDTO p) {
                        administrator = p;
                        showPersonDialog(PersonEditDialog.ACTION_UPDATE);
                    }

                    @Override
                    public void onMessageRequested(AdministratorDTO p) {
                        administrator = p;
                        under();
                    }

                    @Override
                    public void onInvitationRequested(AdministratorDTO p) {
                        administrator = p;
                        Intent x = new Intent(ctx, AppInvitationActivity.class);
                        x.putExtra("email", p.getEmail());
                        x.putExtra("pin", p.getPin());
                        x.putExtra("member", p.getFullName());
                        x.putExtra("type", AppInvitationFragment.ADMIN);
                        startActivity(x);
                    }
                });
        View view = inflater.inflate(R.layout.header, null);
        TextView txt = (TextView)view.findViewById(R.id.HEADER_text);
        ImageView img = (ImageView)view.findViewById(R.id.HEADER_image);
        txt.setText("Administrators");
        listView.addHeaderView(view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setSelection(selectedIndex);
    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }
    AdministratorDTO administrator;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.admin_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        administrator = response.getAdministrators().get(info.position);
        selectedIndex = info.position;
        menu.setHeaderTitle(administrator.getFullName());
        menu.setHeaderIcon(R.drawable.golfer2_48);

        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.w(LOG,
                "onContextItemSelected - select option ..." + item.getTitle());
        switch (item.getItemId()) {
            case R.id.menu_invite_admin:
                Intent x = new Intent(ctx, AppInvitationActivity.class);
                x.putExtra("email", administrator.getEmail());
                x.putExtra("pin", administrator.getPin());
                x.putExtra("member", administrator.getFullName());
                x.putExtra("type", AppInvitationFragment.ADMIN);
                startActivity(x);
                return true;

            case R.id.menu_take_picture_admin:
                listener.onAdministratorPictureRequested(administrator, selectedIndex);
                return true;
            case R.id.menu_send_mail_admin:
                under();
                return true;
            case R.id.menu_send_text_admin:
                under();
                return true;
            case R.id.menu_change_admin:
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
        personEditDialog.setPersonType(PersonEditDialog.ADMIN);

        if (actionCode == PersonEditDialog.ACTION_UPDATE) {
            personEditDialog.setPerson(administrator);
        }
        if (actionCode == PersonEditDialog.ACTION_ADD) {
            personEditDialog.setPerson(new AdministratorDTO());
        }
        personEditDialog.setActivity(getActivity());
        personEditDialog.setDiagListener(new PersonEditDialog.DialogListener() {
            @Override
            public void onRecordAdded(PersonInterface person) {
                Log.i(LOG, "administrator added OK");
                if (administratorList == null) {
                    administratorList = new ArrayList<AdministratorDTO>();
                }
                administratorList.add(0, (AdministratorDTO) person);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRecordUpdated() {
                Log.i(LOG, "administrator updated OK");
            }

            @Override
            public void onRecordDeleted() {
                Log.i(LOG, "administrator deleted OK");
            }
        });
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        personEditDialog.show(getFragmentManager(), "adminDialog");
                    }
                });
            }
        }, 1000);
    }
    FragmentManager fragmentManager;
    ListView listView;
    TextView txtHeader, txtCount;
    GolfGroupDTO golfGroup;
    static final String LOG = "AdminListFragment";
    Context ctx;
    AdministratorAdapter adapter;
    List<AdministratorDTO> administratorList;
    View view;
    ResponseDTO response;


}
