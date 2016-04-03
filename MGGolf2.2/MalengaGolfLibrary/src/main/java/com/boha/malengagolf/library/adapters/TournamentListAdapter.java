package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreymalabie on 4/2/16.
 */
public class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.TournamentViewHolder>{

    public interface TournamentsListener {

        public void manageTournamentPlayersScores(TournamentDTO t);
        public void editTournament(TournamentDTO t);
        public void viewLeaderBoard(TournamentDTO t);

        public void takePictures(TournamentDTO t);
        public void viewGallery(TournamentDTO t);
        public void manageTournamentTees(TournamentDTO t);

        public void inviteAppUser();
        public void uploadVideo(TournamentDTO t);

        public void sendPlayerTextMessage(TournamentDTO t);
        public void sendPlayerEmail(TournamentDTO t);

        public void removeTournament(TournamentDTO t);
        public void deleteSampleTournaments();
    }
    private TournamentsListener listener;
    private List<TournamentDTO> tournaments;
    private Context ctx;

    public TournamentListAdapter(List<TournamentDTO> tournaments,
                                 Context context, TournamentsListener listener) {
        this.tournaments = tournaments;
        this.ctx = context;
        this.listener = listener;
    }
    @Override
    public TournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                return new TournamentViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tournament_item, parent, false));

    }

    @Override
    public void onBindViewHolder(TournamentViewHolder v, final int position) {

        final TournamentDTO p = tournaments.get(position);
        v.txtNumber.setText("" + (position + 1));
        v.txtName.setText(p.getTourneyName());
        v.txtClubName.setText(p.getClubName());
        v.txtStart.setText(y.format(new Date(p.getStartDate())));
        v.txtEnd.setText(y.format(new Date(p.getEndDate())));
        v.txtRounds.setText("0" + p.getGolfRounds());
        switch (p.getTournamentType()) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                v.txtType.setText(ctx.getResources().getString(R.string.stableford_indiv));
                break;
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                v.txtType.setText(ctx.getResources().getString(R.string.strokeplay_indiv));
                break;
        }
        if (p.getNumberOfRegisteredPlayers() < 10) {
            v.txtPlayers.setText("0" + p.getNumberOfRegisteredPlayers());
        } else {
            v.txtPlayers.setText("" + p.getNumberOfRegisteredPlayers());
        }
        if (p.getClosedForScoringFlag() == 1) {
            v.txtStatus.setText(ctx.getResources().getString(R.string.tourn_closed));
            v.txtStatus.setVisibility(View.VISIBLE);
        } else {
            v.txtStatus.setVisibility(View.GONE);
        }
        if (p.getExampleFlag() > 0) {
            v.btnExample.setVisibility(View.VISIBLE);
            v.btnExample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.deleteSampleTournaments();
                }
            });
        } else {
            v.btnExample.setVisibility(View.GONE);
        }

        v.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.takePictures(p);
            }
        });
        v.imgScoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (p.getClosedForScoringFlag() == 0) {
                    listener.manageTournamentPlayersScores(p);
                } else {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                }
            }
        });


        v.imgLeaderBd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.viewLeaderBoard(p);
            }
        });
        v.imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.viewGallery(p);
            }
        });
        try {
            if (v.imgDelete != null) {
                v.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (p.getClosedForScoringFlag() == 0) {
                            listener.removeTournament(p);
                        } else {
                            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                        }
                    }
                });
            }
            if (v.imgEdit != null) {
                v.imgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (p.getClosedForScoringFlag() == 0) {
                            listener.editTournament(p);
                        } else {
                            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                        }
                    }
                });
            }
            if (v.imgEmail != null) {
                v.imgEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (p.getClosedForScoringFlag() == 0) {
                            listener.sendPlayerEmail(p);
                        } else {
                            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                        }
                    }
                });
            }
            if (v.imgTee != null) {
                v.imgTee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (p.getClosedForScoringFlag() == 0) {
                            listener.manageTournamentTees(p);
                        } else {
                            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                        }
                    }
                });
            }
            if (v.imgInvite != null) {
                v.imgInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.inviteAppUser();
                    }
                });
            }
        } catch (Exception e) {

        }
        if (p.getClosedForScoringFlag() > 0) {
            if (v.imgEdit != null)
                v.imgEdit.setAlpha(0.3f);
            if (v.imgEmail != null)
                v.imgEmail.setAlpha(0.3f);
            if (v.imgTee != null)
                v.imgTee.setAlpha(0.3f);
            if (v.imgCamera != null)
                v.imgCamera.setAlpha(0.3f);
            if (v.imgDelete != null)
                v.imgDelete.setAlpha(0.3f);
            if (v.imgScoring != null)
                v.imgScoring.setAlpha(0.3f);
        } else {
            if (v.imgEdit != null)
                v.imgEdit.setAlpha(1.0f);
            if (v.imgEmail != null)
                v.imgEmail.setAlpha(1.0f);
            if (v.imgTee != null)
                v.imgTee.setAlpha(1.0f);
            if (v.imgCamera != null)
                v.imgCamera.setAlpha(1.0f);
            if (v.imgDelete != null)
                v.imgDelete.setAlpha(1.0f);
            if (v.imgScoring != null)
                v.imgScoring.setAlpha(1.0f);
        }
        Statics.setRobotoFontBold(ctx, v.txtClubName);
        Statics.setRobotoFontBold(ctx, v.txtName);
        Statics.setRobotoFontBold(ctx, v.txtType);
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    public class TournamentViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtClubName, txtStatus, txtNumber;
        TextView txtStart, txtEnd, txtRounds, txtPlayers, txtType;
        ImageView imgScoring, imgEdit, imgLeaderBd, imgEmail,
                imgCamera, imgGallery, imgDelete, imgTee, imgInvite;
        ImageView btnExample;


        public TournamentViewHolder(View item) {
            super(item);
            txtName = (TextView) item
                    .findViewById(R.id.TRN_txtName);
            txtStart = (TextView) item
                    .findViewById(R.id.TRN_txtStartDate);
            txtEnd = (TextView) item
                    .findViewById(R.id.TRN_txtEndDate);
            txtRounds = (TextView) item
                    .findViewById(R.id.TRN_txtCounter);
            txtClubName = (TextView) item
                    .findViewById(R.id.TRN_txtClubName);
            txtStatus = (TextView) item
                    .findViewById(R.id.TRN_txtStatus);
            txtPlayers = (TextView) item
                    .findViewById(R.id.TRN_txtPlayerCounter);
            txtNumber = (TextView) item
                    .findViewById(R.id.TRN_txtNumber);
            txtType = (TextView) item
                    .findViewById(R.id.TRN_txtTournamentType);
            btnExample = (ImageView) item
                    .findViewById(R.id.TRN_exampleDelete);

            imgCamera = (ImageView) item
                    .findViewById(R.id.TRN_imgCamera);
            imgEdit = (ImageView) item
                    .findViewById(R.id.TRN_imgEdit);
            imgEmail = (ImageView) item
                    .findViewById(R.id.TRN_imgEmail);
            imgGallery = (ImageView) item
                    .findViewById(R.id.TRN_imgGallery);
            imgLeaderBd = (ImageView) item
                    .findViewById(R.id.TRN_imgLeaderBoard);
            imgScoring = (ImageView) item
                    .findViewById(R.id.TRN_imgScoring);
            imgTee = (ImageView) item
                    .findViewById(R.id.TRN_imgTee);
            imgDelete = (ImageView) item
                    .findViewById(R.id.TRN_imgDelete);
            imgInvite = (ImageView) item
                    .findViewById(R.id.TRN_imgInvite);

        }

    }

    static final String LOG = TournamentListAdapter.class.getSimpleName();
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);


}
