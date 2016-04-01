package com.boha.malengagolf.admin.com.boha.malengagolf.packs.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlayerAdapter extends ArrayAdapter<PlayerDTO> {

    public interface PlayerAdapterListener {
        public void onCameraRequested(PlayerDTO p, int position);
        public void onEditRequested(PlayerDTO p);
        public void onHistoryRequested(PlayerDTO p);
        public void onMessageRequested(PlayerDTO p, int position);
        public void onInvitationRequested(PlayerDTO p);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<PlayerDTO> mList;
    private Context ctx;
    private int golfGroupID;
    private PlayerAdapterListener listener;

    public PlayerAdapter(Context context, int textViewResourceId,
                         List<PlayerDTO> list, int golfGroupID,
                         PlayerAdapterListener listener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.listener = listener;
        this.golfGroupID = golfGroupID;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName, txtNum;
        TextView txtEmail, txtCell, txtBirthdate, txtCountLabel, txtCountRed, txtCountBlack, txtCountGreen;
        ImageView imagex;
        ImageView imgHistory, imgCamera, imgInvite, imgMessage, imgEdit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtName = (TextView) convertView
                    .findViewById(R.id.PSN_txtName);
            item.txtEmail = (TextView) convertView
                    .findViewById(R.id.PSN_txtEmail);
            item.txtCell = (TextView) convertView
                    .findViewById(R.id.PSN_txtCell);
            item.txtCountLabel = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterLabel);
            item.txtCountGreen = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterGreen);
            item.txtCountBlack = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterBlack);
            item.txtCountRed = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterRed);
            item.txtBirthdate = (TextView) convertView
                    .findViewById(R.id.PSN_txtBirthdate);
            item.imgCamera = (ImageView) convertView
                    .findViewById(R.id.PA_imgCamera);
            item.imgEdit = (ImageView) convertView
                    .findViewById(R.id.PA_imgEdit);
            item.imgHistory = (ImageView) convertView
                    .findViewById(R.id.PA_imgPlayerHistory);
            item.imgInvite = (ImageView) convertView
                    .findViewById(R.id.PA_imgInvite);
            item.imgMessage = (ImageView) convertView
                    .findViewById(R.id.PA_imgMessage);

            item.txtNum = (TextView) convertView
                    .findViewById(R.id.PSN_txtNum);

            item.imagex = (ImageView) convertView
                    .findViewById(R.id.PSN_imagex);
            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }



        final PlayerDTO p = mList.get(position);
        if (position < 9) {
            item.txtNum.setText("0" + (position + 1));
        } else {
            item.txtNum.setText("" + (position + 1));
        }
        item.txtName.setText(p.getFullName());
        item.txtEmail.setText(p.getEmail());
        item.txtCell.setText(p.getCellphone());
        item.txtCountRed.setVisibility(View.GONE);
        item.txtCountBlack.setVisibility(View.GONE);

        item.txtCountGreen.setText("" + p.getNumberOfTournaments());
        item.txtCountLabel.setText(ctx.getResources().getString(R.string.number_tourn));
        if (p.getDateOfBirth() > 0) {
            item.txtBirthdate.setText(sdf.format(new Date(p.getDateOfBirth())));
            item.txtBirthdate.setVisibility(View.VISIBLE);
        } else {
            item.txtBirthdate.setVisibility(View.GONE);
        }
        Picasso.with(ctx)
                .load(p.getImageURL(ctx))
                .placeholder(ContextCompat.getDrawable(ctx,R.drawable.boy))
                .into(item.imagex);

        item.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraRequested(p, position);
            }
        });
        item.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditRequested(p);
            }
        });
        item.imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onHistoryRequested(p);
            }
        });
        item.imgInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onInvitationRequested(p);
            }
        });
        item.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRequested(p, position);
            }
        });
        item.imagex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraRequested(p, position);
            }
        });
        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }
    static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_action_add_person) // resource or drawable
            .showImageForEmptyUri(R.drawable.boy) // resource or drawable
            .showImageOnFail(R.drawable.boy) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .cacheInMemory(true) // default
            .cacheOnDisk(true) // default
            .build();
    static final Locale cc = Locale.getDefault();
    static  final SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd MMMM yyyy", cc);
}
