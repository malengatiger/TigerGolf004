package com.boha.malengagolf.library.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boha.malengagolf.library.R;

/**
 * Created by aubreymalabie on 4/4/16.
 */
public class ScoringByHoleFragmentNew extends android.support.v4.app.Fragment {
    Context ctx;
    View view;
    LinearLayout frontNine, backNine;
    LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e("scoring", "---------------------------> onCreateView ...");
        ctx = getActivity();
        this.inflater = inflater;
        view = inflater
                .inflate(R.layout.fragment_scoring_by_hole_new, container, false);

        setUpNines();
        return view;
    }

    private void setUpNines() {
        frontNine = (LinearLayout)view.findViewById(R.id.frontNine);
        backNine = (LinearLayout)view.findViewById(R.id.frontNine);

        for (int i = 0; i < 9; i++) {
            View v = inflater.inflate(R.layout.scoring_template,null);
        }


    }
    TextView txtPlayer, txtScore1, txtScore2, txtScore3, txtScore4, txtScore5, txtScore6,
            txtScore7, txtScore8, txtScore9, txtScore10, txtScore11, txtScore12,
            txtScore13, txtScore14, txtScore15, txtScore16, txtScore17, txtScore18;

    TextView txtPoints1, txtPoints2, txtPoints3, txtPoints4, txtPoints5, txtPoints6,
            txtPoints7, txtPoints8, txtPoints9, txtPoints10, txtPoints11, txtPoints12,
            txtPoints13, txtPoints14, txtPoints15, txtPoints16, txtPoints17, txtPoints18;

    ImageButton btnUp1, btnUp2, btnUp3, btnUp4, btnUp5, btnUp6, btnUp7, btnUp8, btnUp9, btnUp10,
            btnUp11, btnUp12, btnUp13, btnUp14, btnUp15, btnUp16, btnUp17, btnUp18;

    ImageButton btnUpX1, btnUpX2, btnUpX3, btnUpX4, btnUpX5, btnUpX6, btnUpX7, btnUpX8, btnUpX9, btnUpX10,
            btnUpX11, btnUpX12, btnUpX13, btnUpX14, btnUpX15, btnUpX16, btnUpX17, btnUpX18;


    ImageButton btnDown1, btnDown2, btnDown3, btnDown4, btnDown5, btnDown6, btnDown7, btnDown8, btnDown9,
            btnDown10, btnDown11, btnDown12, btnDown13, btnDown14, btnDown15, btnDown16, btnDown17,
            btnDown18, txtTotal;

    ImageButton btnDownX1, btnDownX2, btnDownX3, btnDownX4, btnDownX5, btnDownX6, btnDownX7, btnDownX8, btnDownX9,
            btnDownX10, btnDownX11, btnDownX12, btnDownX13, btnDownX14, btnDownX15, btnDownX16, btnDownX17,
            btnDownX18;

    View vPoints1, vPoints2, vPoints3, vPoints4, vPoints5, vPoints6, vPoints7, vPoints8, vPoints9,
            vPoints10, vPoints11, vPoints12, vPoints13, vPoints14, vPoints15, vPoints16, vPoints17, vPoints18;


}
