package com.boha.malengagolf.library.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boha.malengagolf.library.R;

/**
 * Created by aubreymalabie on 4/4/16.
 */
public class ScoringByHoleFragmentNew extends android.support.v4.app.Fragment {
    Context ctx;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e("scoring", "---------------------------> onCreateView ...");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_scoring_by_hole_new, container, false);

        return view;
    }
}
