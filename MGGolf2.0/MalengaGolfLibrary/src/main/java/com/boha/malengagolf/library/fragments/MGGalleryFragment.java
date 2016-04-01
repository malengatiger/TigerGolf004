package com.boha.malengagolf.library.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.GalleryAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.GalleryRow;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class MGGalleryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.gallery, container, false);
        setFields();
        Bundle bundle = getArguments();
        if (bundle != null) {
            tournament = (TournamentDTO)bundle.getSerializable("tournament");
        }

        return view;

    }

    private void setFields() {
        gridView = (GridView)view.findViewById(R.id.GAL_grid);
        txtName = (TextView) view.findViewById(R.id.GAL_txtTourneyName);
        txtClub = (TextView) view.findViewById(R.id.GAL_txtClubName);
        imgSplash = (ImageView) view.findViewById(R.id.GAL_image);
    }

    public void setData(GolfGroupDTO golfGroup, TournamentDTO tournament,
                              List<GalleryRow> galleryRows) {
        this.tournament = tournament;
        this.golfGroup = golfGroup;
        this.galleryRows = galleryRows;
        Collections.sort(galleryRows);
        galleryAdapter = new GalleryAdapter(ctx,R.layout.gallery_item, galleryRows, imageLoader);
        gridView.setAdapter(galleryAdapter);
        txtName.setText(tournament.getTourneyName());
        txtClub.setText(tournament.getClubName());
        imgSplash.setImageDrawable(SharedUtil.getSplash(ctx, SharedUtil.getSplashIndex(ctx)));
        if (galleryRows.isEmpty()) {
            gridView.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            imgSplash.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(ctx,R.anim.slide_in_left);
            animation.setDuration(1000);
            imgSplash.startAnimation(animation);
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.no_pics),5, Gravity.CENTER);
        } else {
            gridView.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);
            imgSplash.setVisibility(View.GONE);

        }
    }

    GalleryAdapter galleryAdapter;
    List<GalleryRow> galleryRows;
    View view;
    Context ctx;
    GolfGroupDTO golfGroup;
    TournamentDTO tournament;
    GridView gridView;
    ImageLoader imageLoader;
    TextView txtName, txtClub;
    ImageView imgSplash;
}
