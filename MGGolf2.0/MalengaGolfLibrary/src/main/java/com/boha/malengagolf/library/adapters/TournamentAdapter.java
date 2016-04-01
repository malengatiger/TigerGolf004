package com.boha.malengagolf.library.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.fragments.TournamentListListener;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TournamentAdapter extends ArrayAdapter<TournamentDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<TournamentDTO> mList;
    private Context ctx;
    private boolean markSelected;
    private int selectedTourneyIndex;
    private Vibrator vb;
    private TournamentListListener tournamentListListener;


    public TournamentAdapter(Context context, int textViewResourceId,
                             List<TournamentDTO> list, boolean markSelected, int selectedTourneyIndex,
                             TournamentListListener tournamentListListener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.tournamentListListener = tournamentListListener;
        this.selectedTourneyIndex = selectedTourneyIndex;
        ctx = context;
        vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        this.markSelected = markSelected;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TournamentAdapter(Context context, int textViewResourceId,
                             List<TournamentDTO> list, TournamentListListener tournamentListListener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.tournamentListListener = tournamentListListener;
        vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName, txtClubName, txtStatus, txtNumber;
        TextView txtStart, txtEnd, txtRounds, txtPlayers, txtType;
        ImageView imgScoring, imgEdit, imgLeaderBd, imgEmail,
                imgCamera, imgGallery, imgDelete, imgTee, imgInvite;
        ImageView btnExample;
        ImageView image;
        View vOpen, vClosed;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TournamentDTO p = mList.get(position);
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.TRN_txtName);
            v.txtStart = (TextView) convertView
                    .findViewById(R.id.TRN_txtStartDate);
            v.txtEnd = (TextView) convertView
                    .findViewById(R.id.TRN_txtEndDate);
            v.txtRounds = (TextView) convertView
                    .findViewById(R.id.TRN_txtCounter);
            v.txtClubName = (TextView) convertView
                    .findViewById(R.id.TRN_txtClubName);
            v.txtStatus = (TextView) convertView
                    .findViewById(R.id.TRN_txtStatus);
            v.txtPlayers = (TextView) convertView
                    .findViewById(R.id.TRN_txtPlayerCounter);
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.TRN_txtNumber);
            v.txtType = (TextView) convertView
                    .findViewById(R.id.TRN_txtTournamentType);
            v.btnExample = (ImageView) convertView
                    .findViewById(R.id.TRN_exampleDelete);

            v.imgCamera = (ImageView) convertView
                    .findViewById(R.id.TRN_imgCamera);
            v.imgEdit = (ImageView) convertView
                    .findViewById(R.id.TRN_imgEdit);
            v.imgEmail = (ImageView) convertView
                    .findViewById(R.id.TRN_imgEmail);
            v.imgGallery = (ImageView) convertView
                    .findViewById(R.id.TRN_imgGallery);
            v.imgLeaderBd = (ImageView) convertView
                    .findViewById(R.id.TRN_imgLeaderBoard);
            v.imgScoring = (ImageView) convertView
                    .findViewById(R.id.TRN_imgScoring);
            v.imgTee = (ImageView) convertView
                    .findViewById(R.id.TRN_imgTee);
            v.imgDelete = (ImageView) convertView
                    .findViewById(R.id.TRN_imgDelete);
            v.imgInvite = (ImageView) convertView
                    .findViewById(R.id.TRN_imgInvite);

            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


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
                    vb.vibrate(50);
                    tournamentListListener.deleteSampleTournaments();
                }
            });
        } else {
            v.btnExample.setVisibility(View.GONE);
        }

        v.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(50);
                tournamentListListener.takePictures(p);
            }
        });
        v.imgScoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(50);
                if (p.getClosedForScoringFlag() == 0) {
                    tournamentListListener.manageTournamentPlayersScores(p);
                } else {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tourn_closed));
                }
            }
        });


        v.imgLeaderBd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vb.vibrate(50);
                tournamentListListener.viewLeaderBoard(p);
            }
        });
        v.imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tournamentListListener.viewGallery(p);
            }
        });
        try {
            if (v.imgDelete != null) {
                v.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vb.vibrate(50);
                        if (p.getClosedForScoringFlag() == 0) {
                            tournamentListListener.removeTournament(p);
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
                        vb.vibrate(50);
                        if (p.getClosedForScoringFlag() == 0) {
                            tournamentListListener.editTournament(p);
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
                        vb.vibrate(50);
                        if (p.getClosedForScoringFlag() == 0) {
                            tournamentListListener.sendPlayerEmail(p);
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
                        vb.vibrate(50);
                        if (p.getClosedForScoringFlag() == 0) {
                            tournamentListListener.manageTournamentTees(p);
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
                        vb.vibrate(50);
                        tournamentListListener.inviteAppUser();
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
        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.3f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.3f);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleUpX).with(scaleUpY);

        scaleDown.start();

    }

    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
}
