package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreymalabie on 4/2/16.
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder> {

    public interface PlayerAdapterListener {
        public void onCameraRequested(PlayerDTO p, int position);

        public void onEditRequested(PlayerDTO p);

        public void onHistoryRequested(PlayerDTO p);

        public void onMessageRequested(PlayerDTO p, int position);

        public void onInvitationRequested(PlayerDTO p);
    }

    private PlayerAdapterListener listener;
    private List<PlayerDTO> players;
    private Context ctx;

    public PlayerListAdapter(List<PlayerDTO> players,
                             Context context, PlayerAdapterListener listener) {
        this.players = players;
        this.ctx = context;
        this.listener = listener;
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PlayerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false));

    }

    @Override
    public void onBindViewHolder(PlayerViewHolder item, final int position) {

        final PlayerDTO p = players.get(position);

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
        if (p.getImageURL() != null && !p.getImageURL().isEmpty()) {
            Picasso.with(ctx)
                    .load(p.getImageURL())
                    .placeholder(ContextCompat.getDrawable(ctx, R.drawable.boy))
                    .into(item.imagex);
        } else {
            Picasso.with(ctx)
                    .load(R.drawable.boy)
                    .into(item.imagex);
        }
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

    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtName, txtNum;
        protected TextView txtEmail, txtCell, txtBirthdate, txtCountLabel, txtCountRed, txtCountBlack, txtCountGreen;
        protected ImageView imagex;
        protected ImageView imgHistory, imgCamera, imgInvite, imgMessage, imgEdit;


        public PlayerViewHolder(View item) {
            super(item);
            txtName = (TextView) item
                    .findViewById(R.id.PSN_txtName);
            txtEmail = (TextView) item
                    .findViewById(R.id.PSN_txtEmail);
            txtCell = (TextView) item
                    .findViewById(R.id.PSN_txtCell);
            txtCountLabel = (TextView) item
                    .findViewById(R.id.PSN_txtCounterLabel);
            txtCountGreen = (TextView) item
                    .findViewById(R.id.PSN_txtCounterGreen);
            txtCountBlack = (TextView) item
                    .findViewById(R.id.PSN_txtCounterBlack);
            txtCountRed = (TextView) item
                    .findViewById(R.id.PSN_txtCounterRed);
            txtBirthdate = (TextView) item
                    .findViewById(R.id.PSN_txtBirthdate);
            imgCamera = (ImageView) item
                    .findViewById(R.id.PA_imgCamera);
            imgEdit = (ImageView) item
                    .findViewById(R.id.PA_imgEdit);
            imgHistory = (ImageView) item
                    .findViewById(R.id.PA_imgPlayerHistory);
            imgInvite = (ImageView) item
                    .findViewById(R.id.PA_imgInvite);
            imgMessage = (ImageView) item
                    .findViewById(R.id.PA_imgMessage);

            txtNum = (TextView) item
                    .findViewById(R.id.PSN_txtNum);

            imagex = (ImageView) item
                    .findViewById(R.id.PSN_imagex);
        }

    }

    static final String LOG = PlayerListAdapter.class.getSimpleName();
    static final Locale cc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", cc);

}
