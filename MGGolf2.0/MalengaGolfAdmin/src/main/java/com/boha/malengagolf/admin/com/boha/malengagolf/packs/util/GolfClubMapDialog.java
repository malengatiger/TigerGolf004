package com.boha.malengagolf.admin.com.boha.malengagolf.packs.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.boha.malengagolf.admin.R;
import com.google.android.gms.maps.model.LatLng;

public class GolfClubMapDialog extends DialogFragment {

    public interface DialogListener {
        public void onDialogDone();
    }
    public GolfClubMapDialog() {
    }

    DialogListener diagListener;
    View view;
    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.registration_edit, container);
        hideFields();
        eName = (EditText) view.findViewById(R.id.EP_groupName);
        eCellphone = (EditText) view.findViewById(R.id.EP_cellphone);
        TextView txt = (TextView)view.findViewById(R.id.EP_header);
        txt.setText(ctx.getResources().getString(R.string.cellphone));

        btnCancel = (Button)view.findViewById(R.id.EP_btnCancel);
        btnSave = (Button)view.findViewById(R.id.EP_btnSave);
        getDialog().setTitle("Map Dialog to record Clubs");


        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                diagListener.onDialogDone();
                dismiss();
            }
        });
        animate(view);
        return view;
    }

    private void hideFields() {
        vFirstName = view.findViewById(R.id.EP_firstName);
        vLastName = view.findViewById(R.id.EP_lastName);
        vSpinner1 = view.findViewById(R.id.EP_editEmail);
        vSpinner2 = view.findViewById(R.id.EP_countrySpinner);
        vFirstName.setVisibility(View.GONE);
        vLastName.setVisibility(View.GONE);
        vSpinner2.setVisibility(View.GONE);
        vSpinner1.setVisibility(View.GONE);

    }
    private void animate(View v) {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.grow_fade_in_center);
        a.setDuration(1000);
        v.startAnimation(a);
    }
    EditText eName, eCellphone, eAddress;
    View vSpinner1, vSpinner2, vFirstName, vLastName;
    LatLng latLng;
    TextView txtHeaderLabel, txtHeader, txtAddLabel;
    Button btnCancel,btnSave;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public DialogListener getDiagListener() {
        return diagListener;
    }

    public void setDiagListener(DialogListener diagListener) {
        this.diagListener = diagListener;
    }
}
