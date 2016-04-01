package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class AppInvitationFragment extends Fragment {


    public interface AppInvitationListener {
        public void setBusy();
        public void setNotBusy();
    }
    AppInvitationListener appInvitationListener;
    @Override
    public void onAttach(Activity a) {

        if (a instanceof AppInvitationListener) {
            appInvitationListener = (AppInvitationListener)a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
            " must implement AppInvitationListener");
        }
        Log.i(LOG,
                "onAttach ---- fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG, "onCreateView");
        ctx = getActivity();
        act = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_invite, container, false);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        setFields();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        editEmail = (EditText) view.findViewById(R.id.INV_editEmail);
        txtGolfGroupName = (TextView) view.findViewById(R.id.INV_groupName);
        txtEmail = (TextView) view.findViewById(R.id.INV_txtEmail);
        txtGolfGroupName.setText(golfGroup.getGolfGroupName());
        txtInstruction = (TextView) view.findViewById(R.id.INV_text);
        txtMember = (TextView) view.findViewById(R.id.INV_txtMemberName);
        chkAdmin = (CheckBox) view.findViewById(R.id.INV_chkAdmin);
        chkPlayer = (CheckBox) view.findViewById(R.id.INV_chkPlayer);
        chkLeaderboard = (CheckBox) view.findViewById(R.id.INV_chkLeaderboard);
        chkScorer = (CheckBox) view.findViewById(R.id.INV_chkScorer);
        btnSend = (Button) view.findViewById(R.id.INV_btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    StringBuilder sba;

    private void validate() {
        if (type == APP_USER) {
            if (editEmail.getText().toString().isEmpty()) {
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.enter_email));
                return;
            } else {
                email = editEmail.getText().toString();
            }
        }
        sba = new StringBuilder();
        sba.append(getHeader());
        if (chkLeaderboard.isChecked()) {
            sba.append(getLeaderBoardLink());
            sba.append("<p>").append(ctx.getResources().getString(R.string.golfgroup_number)).append("</p>");
            sba.append("<h3>").append(golfGroup.getGolfGroupID()).append("</h3>");
        }
        if (chkScorer.isChecked()) {
            sba.append(getScorerLink());
        }
        if (chkPlayer.isChecked()) {
            sba.append(getPlayerLink());
        }

        if (chkAdmin.isChecked()) {
            sba.append(getAdminLink());
            sba.append(getFooter());
            showConfirmDialog();
            return;
        }
        sba.append(getFooter());
        sendInvitation();
    }

    FragmentActivity act;
    public static final int ADMIN = 1, SCORER = 2, PLAYER = 3, APP_USER = 4;

    private void sendInvitation() {

        if (type == APP_USER) {
            RequestDTO w = new RequestDTO();
            w.setRequestType(RequestDTO.REGISTER_APP_USER);
            w.setEmail(email);
            w.setGolfGroupID(golfGroup.getGolfGroupID());

            appInvitationListener.setBusy();
            WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO response) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appInvitationListener.setNotBusy();
                            if (!ErrorUtil.checkServerError(ctx, response)) {
                                return;
                            }
                            final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.subject));
                            shareIntent.putExtra(
                                    Intent.EXTRA_TEXT,
                                    Html.fromHtml(sba.toString())
                            );
                            startActivity(Intent.createChooser(shareIntent, "Email:"));
                        }
                    });
                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(final String message) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appInvitationListener.setNotBusy();
                            ToastUtil.errorToast(ctx, message);
                        }
                    });
                }
            });
//            BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//                @Override
//                public void onResponseReceived(ResponseDTO response) {
//                    appInvitationListener.setNotBusy();
//                    if (!ErrorUtil.checkServerError(ctx, response)) {
//                        return;
//                    }
//                    final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.subject));
//                    shareIntent.putExtra(
//                            Intent.EXTRA_TEXT,
//                            Html.fromHtml(sba.toString())
//                    );
//                    startActivity(Intent.createChooser(shareIntent, "Email:"));
//                }
//
//                @Override
//                public void onVolleyError(VolleyError error) {
//                    appInvitationListener.setNotBusy();
//                }
//            });
        } else {
            final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.subject));
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    Html.fromHtml(sba.toString())
            );
            startActivity(Intent.createChooser(shareIntent, "Email:"));
        }

//        String body="<!DOCTYPE html><html><body><a href=\"http://www.w3schools.com\" target=\"_blank\">Visit W3Schools.com!</a>" +
//                "<p>If you set the target attribute to \"_blank\", the link will open in a new browser window/tab.</p></body></html>";
//        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        emailIntent.setType("text/html");
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.subject));
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));


    }

    private void showConfirmDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(ctx.getResources().getString(R.string.admin_app))
                .setMessage(ctx.getResources().getString(R.string.invite_dialog))
                .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendInvitation();
                    }
                })
                .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>").append(golfGroup.getGolfGroupName()).append("</h2>");
        sb.append("<p>").append(ctx.getResources().getString(R.string.invited)).append("</p>");
        switch (type) {
            case ADMIN:
                sb.append("<h3>").append(ctx.getResources().getString(R.string.admin_app)).append("</h3>");
                break;
            case SCORER:
                sb.append("<h3>").append(ctx.getResources().getString(R.string.scorer_app)).append("</h3>");
                break;
            case PLAYER:
                sb.append("<h3>").append(ctx.getResources().getString(R.string.player_app)).append("</h3>");
                break;
            case APP_USER:
                sb.append("<h3>").append(ctx.getResources().getString(R.string.leaderbd_app)).append("</h3>");
                break;

        }
        return sb.toString();
    }

    private String getFooter() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>").append(ctx.getResources().getString(R.string.enjoy)).append("</h2>");
        return sb.toString();
    }

    private String getLeaderBoardLink() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(ctx.getResources().getString(R.string.click_link)).append("</p>");
        sb.append("<p>").append(Statics.INVITE_LEADERBORAD).append("</p>");
        return sb.toString();
    }

    private String getScorerLink() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(ctx.getResources().getString(R.string.click_link)).append("</p>");
        sb.append("<p>").append(Statics.INVITE_SCORER).append("</p>");
        sb.append(getPinNote());
        return sb.toString();
    }

    private String getPinNote() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(ctx.getResources().getString(R.string.pin_note)).append("</p>");
        sb.append("<h4>").append(pin).append("</h4>");
        return sb.toString();
    }

    private String getPlayerLink() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(ctx.getResources().getString(R.string.click_link)).append("</p>");
        sb.append("<p>").append(Statics.INVITE_PLAYER).append("</p>");
        sb.append(getPinNote());
        return sb.toString();
    }

    private String getAdminLink() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(ctx.getResources().getString(R.string.click_link)).append("</p>");
        sb.append("<p>").append(Statics.INVITE_ADMIN).append("</p>");
        sb.append(getPinNote());
        return sb.toString();
    }

    private List<String> links = new ArrayList<String>();
    private TextView txtGolfGroupName, txtInstruction,
            txtMember, txtEmail;
    private Button btnSend;
    private CheckBox chkAdmin, chkPlayer, chkScorer, chkLeaderboard;
    private EditText editEmail;
    static final String LOG = "AppInvitationFragment";
    private Context ctx;
    private View view;

    private int type;
    private GolfGroupDTO golfGroup;
    private String email, pin, member;
    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", loc);


    public void setData(String email, String pin, String member, int type) {
        this.email = email;
        this.pin = pin;
        this.member = member;
        this.type = type;
        if (txtMember != null) {
            editEmail.setVisibility(View.GONE);
            txtMember.setText(member);
            txtEmail.setText(email);
            switch (type) {
                case SCORER:
                    chkScorer.setChecked(true);
                    chkLeaderboard.setChecked(true);
                    break;
                case ADMIN:
                    chkAdmin.setChecked(true);
                    chkLeaderboard.setChecked(true);
                    break;
                case PLAYER:
                    chkPlayer.setChecked(true);
                    chkLeaderboard.setChecked(true);
                    break;
                case APP_USER:
                    chkLeaderboard.setChecked(true);
                    editEmail.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.GONE);
                    txtMember.setText(ctx.getResources().getString(R.string.lb_viewer));
                    break;
            }
        }
    }

}
