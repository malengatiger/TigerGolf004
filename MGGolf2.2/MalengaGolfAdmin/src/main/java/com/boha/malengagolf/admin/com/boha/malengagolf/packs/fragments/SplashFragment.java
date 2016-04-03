package com.boha.malengagolf.admin.com.boha.malengagolf.packs.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.activities.AppInvitationActivity;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.LeaderBoardPage;
import com.boha.malengagolf.library.util.SharedUtil;

/**
 * Created by aubreyM on 2014/04/23.
 */
public class SplashFragment extends Fragment implements PageFragment, LeaderBoardPage {


    @Override
    public void onAttach(Activity a) {
        Log.i("SplashFragment",
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
                .inflate(R.layout.splash, container, false);

        setFields();
        return view;
    }

    public void setLoading(boolean isOn) {

    }

    @Override
    public void onResume() {
        setFields();
        super.onResume();
    }

    @Override
    public void setFields() {
        image = (ImageView)view.findViewById(R.id.SPLASH_image);
        try {
            image.setImageDrawable(SharedUtil.getRandomSplash(ctx));
        } catch (OutOfMemoryError e) {
            Log.e("Splash", "----------------------------> OutOfMemoryError occcured");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInviteRequest();
            }
        });
    }

    public void setList() {
        // dummy
    }

    @Override
    public void showPersonDialog(int actionCode) {
        // dummy
    }

    private void confirmInviteRequest() {
        AlertDialog.Builder diag = new AlertDialog.Builder(ctx);
        diag.setTitle(ctx.getResources().getString(R.string.subject))
                .setMessage(ctx.getResources().getString(R.string.invite_confirm))
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent x = new Intent(ctx, AppInvitationActivity.class);
                        x.putExtra("type", AppInvitationFragment.APP_USER);
                        startActivity(x);
                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

    }
    ImageView image;
    Context ctx;
    View view;
}
