package com.boha.malengagolf.library.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.adapters.PlayerMessageAdapter;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class MessageFragment extends Fragment {

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
                .inflate(R.layout.fragment_message, container, false);
        setFields();
        golfGroup = SharedUtil.getGolfGroup(ctx);

        return view;

    }

    private void setFields() {
        Log.w(LOG, "setFields ,,,,");
        radioEmail = (RadioButton) view.findViewById(R.id.MSG_radioEmail);
        radioText = (RadioButton) view.findViewById(R.id.MSG_radioText);
        btnGo = (Button) view.findViewById(R.id.MSG_btnSend);
        txtTitle = (TextView) view.findViewById(R.id.MSG_title);
        txtCount = (TextView) view.findViewById(R.id.MSG_count);
        list = (ListView) view.findViewById(R.id.MSG_list);
        checkBox = (CheckBox) view.findViewById(R.id.MSG_allCheck);
        txtSelected = (TextView) view.findViewById(R.id.MSG_selected);


        radioEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setList(PlayerMessageAdapter.EMAIL_MESSAGE);
                }
            }
        });
        radioText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setList(PlayerMessageAdapter.TEXT_MESSAGE);
                }
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cnt = 0;
                for (PlayerDTO p: playerList) {
                    if (p.isSelected()) {
                        cnt++;
                    }
                }
                if (cnt == 0) {
                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_player));
                    return;
                }
                if (radioEmail.isChecked()) {
                    startEmail();
                    return;
                }
                if (radioText.isChecked()) {
                    startTextMessage();
                    return;
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int cnt = 0;
                if (b) {
                    for (PlayerDTO p : playerList) {
                        p.setSelected(true);
                        cnt++;
                    }
                } else {
                    for (PlayerDTO p : playerList) {
                        p.setSelected(false);
                    }
                }
                adapter.notifyDataSetChanged();
                txtSelected.setText(""+cnt);
            }
        });

    }


    private void setList(int type) {
        Log.w(LOG, "setList ...................");
        adapter = new PlayerMessageAdapter(ctx, R.layout.message_item, playerList, type, new PlayerMessageAdapter.PlayerMessageListener() {
            @Override
            public void onPlayerChecked(PlayerDTO player, int index) {
                int cnt = 0;
                for (PlayerDTO p : playerList) {
                    if (p.isSelected()) {
                        cnt++;
                    }
                }
                txtSelected.setText("" + cnt);
            }
        });
        list.setAdapter(adapter);
        txtCount.setText("" + playerList.size());
    }

    public void setPlayerList(List<PlayerDTO> playerList, int index) {
        Log.w(LOG, "setPlayerList - " + playerList.size());
        this.playerList = playerList;
        if (index > -1) {
            player = playerList.get(index);
            player.setSelected(true);
        }
        if (list != null) {
            setList(PlayerMessageAdapter.EMAIL_MESSAGE);
            if (index > -1)
                list.setSelection(index);
        }
    }

    private void startTextMessage() {
        String separator = "; ";
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
            separator = ", ";
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (PlayerDTO p : playerList) {
                if (p.isSelected()) {
                    if (p.getCellphone() != null) {
                        Pattern pat = Pattern.compile(" ");
                        String[] c = pat.split(p.getCellphone());
                        for (String s: c) {
                            sb.append(s);
                        }
                        sb.append(separator);
                    }
                }
            }
            StringBuilder sba = new StringBuilder();
            sba.append("<h2>").append(golfGroup.getGolfGroupName()).append("</h2>");
            final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sb.toString()));
            //shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.message_from) + " " + golfGroup.getGolfGroupName());
//            shareIntent.putExtra(
//                    Intent.EXTRA_TEXT,
//                    Html.fromHtml(sba.toString())
//            );
            startActivity(Intent.createChooser(shareIntent, "Email:"));
        } catch (Exception e) {

        }
    }
    private void startEmail() {
        String separator = "; ";
        if(android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")){
            separator = ", ";
        }
        StringBuilder sb = new StringBuilder();
        for (PlayerDTO p : playerList) {
            if (p.isSelected()) {
                sb.append(p.getEmail()).append(separator);
            }
        }
        StringBuilder sba = new StringBuilder();
        sba.append("<h2>").append(golfGroup.getGolfGroupName()).append("</h2>");
        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + sb.toString()));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.message_from) + " " + golfGroup.getGolfGroupName());
        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(sba.toString())
        );
        startActivity(Intent.createChooser(shareIntent, "Email:"));
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", loc);

    PlayerDTO player;
    View view;
    Context ctx;
    GolfGroupDTO golfGroup;
    TextView txtTitle, txtCount, txtSelected;
    Button btnGo;
    ListView list;
    List<PlayerDTO> playerList;
    PlayerMessageAdapter adapter;
    ImageView image;
    CheckBox checkBox;
    RadioButton radioEmail, radioText;
    static final String LOG = "messageFragment";
}
