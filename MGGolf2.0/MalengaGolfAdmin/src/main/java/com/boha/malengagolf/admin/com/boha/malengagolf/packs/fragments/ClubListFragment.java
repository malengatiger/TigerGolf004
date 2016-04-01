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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.adapters.ClubAdapter;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ClubListFragment extends Fragment implements PageFragment {

    public interface ClubListener {
        public void onClubPicked(ClubDTO club);
    }

    @Override
    public void onAttach(Activity a) {
        if (a instanceof ClubListener) {
            clubListener = (ClubListener) a;
        } else {
            throw new UnsupportedOperationException("Host "
                    + a.getLocalClassName() + " must implement ClubListener");
        }
        Log.i(LOG,
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
        Log.e(LOG, "onCreateView.........");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_club_list, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        administratorDTO = SharedUtil.getAdministrator(ctx);
        fragmentManager = getFragmentManager();
        setFields();
        if (response != null) {
            Log.e(LOG, "response not null in onCreateView");
            setList();
            return view;
        }
        if (saved != null) {
            Log.i(LOG, "onCreateView - getting saved response");
            response = (ResponseDTO) saved.getSerializable("response");
        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                response = (ResponseDTO)bundle.getSerializable("response");
                if (response.getClubs() == null)
                    response.setClubs(new ArrayList<ClubDTO>());
            }
        }
        Log.e(LOG, "about to setList in onCreateView");
        setList();

        return view;
    }
    public void setClubList(List<ClubDTO> list) {
        Log.i(LOG, "setting clubList ....");
        if (response == null) {
            response = new ResponseDTO();
            response.setClubs(list);
            //setFields();
            //setList();
            Log.e(LOG, "List or response was null, set to new");
            return;
        }
        response.setClubs(list);

    }
    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }
    @Override
    public void setFields() {
        Log.e(LOG, "setFields.........");
        listView = (ListView)view.findViewById(R.id.FRAG_listView);
        btnSearchMap = (Button)view.findViewById(R.id.FRAG_btnMap);
        searchView = (SearchView)view.findViewById(R.id.FRAG_search);
        btnSearchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.toast(ctx, "Search On Map under construction");
            }
        });
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e(LOG,"onQueryTextSubmit: " + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ToastUtil.toast(ctx, "onQueryTextChange: " + s);
                Log.e(LOG,"onQueryTextChange: " + s);
                return false;
            }
        });
    }

    public void setList() {
        Log.e(LOG, "setList.........");
        clubList = response.getClubs();
        clubAdapter = new ClubAdapter(ctx,R.layout.club_item, clubList, null);
        listView.setAdapter(clubAdapter);
    }

    @Override
    public void showPersonDialog(int actionCode) {

    }
    FragmentManager fragmentManager;
    ListView listView;
    Button btnSearchMap;
    SearchView searchView;
    TextView txtHeader, txtCount;
    GolfGroupDTO golfGroup;
    AdministratorDTO administratorDTO;
    static final String LOG = "ClubListFragment";
    ClubListener clubListener;
    Context ctx;
    View view;
    ResponseDTO response;
    List<ClubDTO> clubList;
    ClubDTO club;
    ClubAdapter clubAdapter;

}
