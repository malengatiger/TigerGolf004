package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.PhotoAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.SharedUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class PhotoGridFragment extends Fragment {

    List<PhotoUploadDTO> photoUploads;
    RecyclerView recycler;

    public static PhotoGridFragment newInstance(List<PhotoUploadDTO> list) {
        PhotoGridFragment f = new PhotoGridFragment();
        ResponseDTO w = new ResponseDTO();
        w.setPhotoUploads(list);
        Bundle b = new Bundle();
        b.putSerializable("list",w);
        f.setArguments(b);

        return f;
    }
    @Override
    public void onCreate(Bundle b) {
        if (getArguments() != null) {
            ResponseDTO w = (ResponseDTO)getArguments().getSerializable("list");
            photoUploads = w.getPhotoUploads();
        }
    }
    @Override
    public void onAttach(Activity a) {
        Log.e(LOG, "##### Fragment hosted by " + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_photo_grid, container, false);
        setFields();
        golfGroup = SharedUtil.getGolfGroup(ctx);

        return view;

    }

    @Override
    public void onResume() {
        setList();
    }
    private void setFields() {
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(lm);
    }


    private void setList() {
        Log.w(LOG, "setList ...................");

        adapter = new PhotoAdapter(photoUploads, PhotoAdapter.THUMB, getContext(), new PhotoAdapter.PictureListener() {
            @Override
            public void onPictureClicked(PhotoUploadDTO photoUpload, int position) {
                //todo start photoScroller
            }
        });

        recycler.setAdapter(adapter);

    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", loc);

    View view;
    Context ctx;
    GolfGroupDTO golfGroup;
    PhotoAdapter adapter;

    static final String LOG = PhotoGridFragment.class.getSimpleName();
}
