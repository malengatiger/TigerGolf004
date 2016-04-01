package com.boha.malengagolf.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.GolfGroupAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.SharedUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/15.
 */
public class GroupListFragment extends Fragment {
    static final String LOG = "GroupListFragment";

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
                .inflate(R.layout.fragment_group_list, container, false);
        setFields();

        golfGroups = new ArrayList<GolfGroupDTO>();
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_GOLFGROUPS, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response.getGolfGroups() != null) {
                    golfGroups = response.getGolfGroups();
                }
            }

            @Override
            public void onDataCached() {

            }
        });

        setList();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        listView = (ListView) view.findViewById(R.id.GROUP_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent x = new Intent(ctx, MainPagerActivity.class);
                x.putExtra("group", golfGroups.get(i));
                SharedUtil.saveGolfGroup(ctx, golfGroups.get(i));
                startActivity(x);
            }
        });
    }

    public void setList() {
        Log.i(LOG, "setList.............");
        adapter = new GolfGroupAdapter(ctx,
                R.layout.group_item,
                golfGroups);
        listView.setAdapter(adapter);
    }

    Context ctx;
    View view;
    ListView listView;
    TextView txtGroupName, txtNumber;
    GolfGroupAdapter adapter;
    List<GolfGroupDTO> golfGroups;

    static final DecimalFormat df = new DecimalFormat("###,###.00");

    public void setGolfGroups(List<GolfGroupDTO> golfGroups) {
        this.golfGroups = golfGroups;
        setList();
    }
}
