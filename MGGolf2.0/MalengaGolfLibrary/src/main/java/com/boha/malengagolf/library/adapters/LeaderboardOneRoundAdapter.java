package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.Statics;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LeaderboardOneRoundAdapter extends ArrayAdapter<LeaderBoardDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<LeaderBoardDTO> mList;
    private int golfGroupID;
    private Context ctx;
    private boolean hidePictures;

    public LeaderboardOneRoundAdapter(Context context,
                                      int textViewResourceId,
                                      List<LeaderBoardDTO> list, int golfGroupID, boolean hidePictures) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.hidePictures = hidePictures;
        this.golfGroupID = golfGroupID;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;

    static class ViewHolderItem {
        TextView txtPlayer, txtLastHole, txtTotal, txtPar, txtPosition, txtParLabel;
        ImageView image;
        ImageView winner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtPlayer = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_playerName);
            v.txtPosition = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_position);
            v.txtTotal = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_totalScore);
            v.txtPar = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_par);
            v.txtPar = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_par);
            v.txtParLabel = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_parLabel);
            v.txtLastHole = (TextView) convertView
                    .findViewById(R.id.ONE_ROUND_lastHole);
            v.image = (ImageView) convertView
                    .findViewById(R.id.ONE_ROUND_image);
            v.winner = (ImageView) convertView
                    .findViewById(R.id.ONE_ROUND_winnerImage);

            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }
        if (hidePictures) {
            v.image.setVisibility(View.GONE);
        }
        LeaderBoardDTO p = mList.get(position);
        if (p.getWinnerFlag() > 0) {
            v.winner.setVisibility(View.VISIBLE);
        } else {
            v.winner.setVisibility(View.GONE);
        }


        if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
            v.txtPosition.setVisibility(View.GONE);
        } else {
            v.txtPosition.setVisibility(View.VISIBLE);
        }


        v.txtPosition.setText("" + p.getPosition());


        if (p.isTied()) {
            v.txtPosition.setText("T" + p.getPosition());
        }
        v.txtPlayer.setText(p.getPlayer().getFirstName() + " "
                + p.getPlayer().getLastName());



        //Log.e("adap", "totalPoints: " + p.getTotalPoints() + " tourType: "+ p.getTournamentType());
        if (p.getTournamentType() == 0) {
            p.setTournamentType(RequestDTO.STROKE_PLAY_INDIVIDUAL);
        }
        switch (p.getTournamentType()) {
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                //Log.e("ADapter", "STROKE_PLAY_INDIVIDUAL");
                v.txtParLabel.setText(ctx.getResources().getString(R.string.par));
                if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
                    v.txtPar.setText("N/S");
                    v.txtPar.setTextColor(ContextCompat.getColor(ctx, R.color.grey));

                } else {
                    v.txtPar.setVisibility(View.VISIBLE);
                    formatStrokes(v.txtPar, p.getParStatus());
                }
                formatStrokes(v.txtTotal, p.getParStatus(), p.getTotalScore(), v.txtPosition);
                break;
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                //Log.w("Adapter", "STABLEFORD_INDIVIDUAL");
                v.txtParLabel.setText(ctx.getResources().getString(R.string.points));
                v.txtPar.setText("" + p.getTotalPoints());
                if (p.getTotalPoints() == 0) {
                    v.txtPar.setTextColor(ContextCompat.getColor(ctx, R.color.grey));

                } else {
                    v.txtPar.setVisibility(View.VISIBLE);
                    v.txtPar.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                }
                formatStablefordStrokes(v.txtTotal, p.getTotalPoints(), v.txtPosition, p);
                break;
        }

        v.txtLastHole.setText("" + p.getLastHole());
        int x = position % 2;
        if (x > 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.beige_pale));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
        }
        //image
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("golfgroup")
                    .append(golfGroupID).append("/player/");
            sb.append("t");
            sb.append(p.getPlayer().getPlayerID()).append(".jpg");
            Picasso.with(ctx).load(sb.toString()).into(v.image);
//
//            v.image.setDefaultImageResId(R.drawable.boy);
//            v.image.setImageUrl(sb.toString(), imageLoader);
        } catch (Exception e) {
            Log.w("OneRound", "network image view problem", e);
        }
        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }


    private void formatStrokes(TextView txt, int parStatus) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            txt.setText("E");
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.absa_red));
            txt.setText("-" + parStatus);
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.blue));
            txt.setText("+" + (parStatus * -1));
        }


    }

    private void formatStrokes(TextView txt, int parStatus, int score, TextView txtPos) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblack_oval));
            }
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.absa_red));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xred_oval));
            }
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.blue));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblue_oval));
            }
        }
        txt.setText("" + score);

    }

    private void formatStablefordStrokes(TextView txt, int points, TextView txtPos, LeaderBoardDTO board) {

        int holeCount = 0;
        for (TourneyScoreByRoundDTO tsb: board.getTourneyScoreByRoundList()) {
            if (tsb.getScoringComplete() > 0) {
                holeCount += board.getHolesPerRound();
                continue;
            }
            if (tsb.getScore1() > 0) holeCount++;
            if (tsb.getScore2() > 0) holeCount++;
            if (tsb.getScore3() > 0) holeCount++;
            if (tsb.getScore4() > 0) holeCount++;
            if (tsb.getScore5() > 0) holeCount++;
            if (tsb.getScore6() > 0) holeCount++;
            if (tsb.getScore7() > 0) holeCount++;
            if (tsb.getScore8() > 0) holeCount++;
            if (tsb.getScore9() > 0) holeCount++;
            if (tsb.getScore10() > 0) holeCount++;
            if (tsb.getScore11() > 0) holeCount++;
            if (tsb.getScore12() > 0) holeCount++;
            if (tsb.getScore13() > 0) holeCount++;
            if (tsb.getScore14() > 0) holeCount++;
            if (tsb.getScore15() > 0) holeCount++;
            if (tsb.getScore16() > 0) holeCount++;
            if (tsb.getScore17() > 0) holeCount++;
            if (tsb.getScore18() > 0) holeCount++;
        }


        if (points == (holeCount * 2)) { //Even par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblack_oval));
            }
        }
        if (points > (holeCount * 2)) { //under par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.absa_red));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xred_oval));
            }
        }
        if (points < (holeCount * 2)) { //over par
            txt.setTextColor(ContextCompat.getColor(ctx, R.color.blue));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblue_oval));
            }
        }
        txt.setText("" + points);

    }

    static final int UNDER = 1, PAR = 2, OVER = 3;
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
}
