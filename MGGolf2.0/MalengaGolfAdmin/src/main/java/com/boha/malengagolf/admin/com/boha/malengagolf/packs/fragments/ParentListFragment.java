package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.adapters.ParentAdapter;
import com.boha.malengagolf.admin.com.boha.malengagolf.packs.util.PersonEditDialog;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ParentDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ParentListFragment extends Fragment implements PageFragment {


    public interface ParentListener {
        public void onParentPicked(ParentDTO parent);
    }

    @Override
    public void onAttach(Activity a) {
        if (a instanceof ParentListener) {
            ParentListener = (ParentListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement ParentListener");
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
        parentList = response.getParents();
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
        adapter = new ParentAdapter(ctx, R.layout.person_item,
                parentList, imageLoader, golfGroup.getGolfGroupID());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void showPersonDialog(int actionCode) {
        PersonEditDialog personEditDialog = new PersonEditDialog();
        personEditDialog.setCtx(ctx);
        personEditDialog.setAction(actionCode);
        personEditDialog.setPersonType(PersonEditDialog.PARENT);

        if (actionCode == PersonEditDialog.ACTION_UPDATE) {
            personEditDialog.setPerson(parent);
        }
        personEditDialog.setActivity(getActivity());
        personEditDialog.setDiagListener(new PersonEditDialog.DialogListener() {
            @Override
            public void onRecordAdded(PersonInterface person) {
                Log.i(LOG, "parent added OK");
                if (parentList == null) {
                    parentList = new ArrayList<ParentDTO>();
                }
                parentList.add(0, (ParentDTO) person);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRecordUpdated() {
                Log.i(LOG, "parent updated OK");
            }

            @Override
            public void onRecordDeleted() {
                Log.i(LOG, "parent deleted OK");
            }
        });
        personEditDialog.show(getFragmentManager(), "parentDialog");
    }

    FragmentManager fragmentManager;
    ListView listView;
    GolfGroupDTO golfGroup;
    AdministratorDTO administratorDTO;
    static final String LOG = "ParentListFragment";
    ParentListener ParentListener;
    Context ctx;
    View view;
    ResponseDTO response;
    List<ParentDTO> parentList;
    ParentDTO parent;
    ParentAdapter adapter;
    ImageLoader imageLoader;

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }
}
