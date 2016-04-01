package com.boha.malengagolf.library;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends ArrayAdapter<LeaderBoardDTO> {

    public interface LeaderBoardListener {
        public void onScrollToItem(int index);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<LeaderBoardDTO> mList;
    private int rounds, par;
    private Context ctx;
    private GolfGroupDTO golfGroup;
    private boolean hidePictures;

    private LeaderBoardListener listener;
    public LeaderboardAdapter(Context context,
                              int textViewResourceId,
                              List<LeaderBoardDTO> list, int rounds,
                              int par,  boolean hidePictures,
                              LeaderBoardListener listener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.rounds = rounds;
        this.listener = listener;
        this.par = par;
        this.hidePictures = hidePictures;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        golfGroup = SharedUtil.getGolfGroup(ctx);
    }

    View view;


    static class ViewHolderItem {
        TextView txtPlayer, txtRound1, txtRound2, txtRound3, txtPosition, txtStatus,
                txtRound4, txtRound5, txtRound6, txtTotal, txtPar, txtLastHole;
        View v1, v2, v3, v4, v5, v6, vt, vp, vlh, vPoints, pp1, pp2, pp3, pp4, pp5, pp6;
        ImageView image;
        ImageView winner;
        TextView txtPointsRound1, txtPointsRound2, txtPointsRound3,
                txtPointsRound4, txtPointsRound5, txtPointsRound6, txtPointsTotal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtPlayer = (TextView) convertView
                    .findViewById(R.id.LBITEM_playerName);
            v.txtPosition = (TextView) convertView
                    .findViewById(R.id.LBITEM_position);
            v.txtRound1 = (TextView) convertView
                    .findViewById(R.id.LS_round1);
            v.txtRound2 = (TextView) convertView
                    .findViewById(R.id.LS_round2);
            v.txtRound3 = (TextView) convertView
                    .findViewById(R.id.LS_round3);
            v.txtRound4 = (TextView) convertView
                    .findViewById(R.id.LS_round4);
            v.txtRound5 = (TextView) convertView
                    .findViewById(R.id.LS_round5);
            v.txtRound6 = (TextView) convertView
                    .findViewById(R.id.LS_round6);
            v.txtTotal = (TextView) convertView
                    .findViewById(R.id.LS_total);
            v.image = (ImageView) convertView
                    .findViewById(R.id.LBITEM_image);

            v.txtPar = (TextView) convertView
                    .findViewById(R.id.LS_parStatus);
            v.txtLastHole = (TextView) convertView
                    .findViewById(R.id.LBITEM_lastHole);
            v.txtStatus = (TextView) convertView
                    .findViewById(R.id.LBITEM_lastHoleStatus);
            v.winner = (ImageView) convertView
                    .findViewById(R.id.LBITEM_winnerImage);

            v.txtPointsRound1 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround1);
            v.txtPointsRound2 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround2);
            v.txtPointsRound3 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround3);
            v.txtPointsRound4 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround4);
            v.txtPointsRound5 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround5);
            v.txtPointsRound6 = (TextView) convertView
                    .findViewById(R.id.LS_POINTSround6);
            v.txtPointsTotal = (TextView) convertView
                    .findViewById(R.id.LS_POINTStotal);


            v.v1 = convertView.findViewById(R.id.LS_round1Layout);
            v.v2 = convertView.findViewById(R.id.LS_round2Layout);
            v.v3 = convertView.findViewById(R.id.LS_round3Layout);
            v.v4 = convertView.findViewById(R.id.LS_round4Layout);
            v.v5 = convertView.findViewById(R.id.LS_round5Layout);
            v.v6 = convertView.findViewById(R.id.LS_round6Layout);
            //
            v.pp1 = convertView.findViewById(R.id.LS_POINTSround1Layout);
            v.pp2 = convertView.findViewById(R.id.LS_POINTSround2Layout);
            v.pp3 = convertView.findViewById(R.id.LS_POINTSround3Layout);
            v.pp4 = convertView.findViewById(R.id.LS_POINTSround4Layout);
            v.pp5 = convertView.findViewById(R.id.LS_POINTSround5Layout);
            v.pp6 = convertView.findViewById(R.id.LS_POINTSround6Layout);
            //
            v.vt = convertView.findViewById(R.id.LS_totLayout);
            v.vp = convertView.findViewById(R.id.LS_parLayout);
            v.vlh = convertView.findViewById(R.id.LBITEM_lastHoleLayout);
            v.vPoints = convertView.findViewById(R.id.LS_POINTS_layout);

            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }

        if (hidePictures) {
            v.image.setVisibility(View.GONE);
        }
        LeaderBoardDTO p = mList.get(position);
        listener.onScrollToItem(position);
        if (p.getWinnerFlag() > 0) {
            v.winner.setVisibility(View.VISIBLE);
        } else {
            v.winner.setVisibility(View.GONE);
        }
        if (p.getTournamentType() == TournamentDTO.STABLEFORD_INDIVIDUAL ||
                p.getTournamentType() == TournamentDTO.BETTER_BALL_STABLEFORD) {
            v.vPoints.setVisibility(View.VISIBLE);
            v.txtPar.setVisibility(View.GONE);
            formatStablefordStrokes(v.txtPointsTotal,p.getTotalPoints(), v.txtPosition, p);

        } else {
            if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
                v.vp.setVisibility(View.GONE);
            } else {
                v.vp.setVisibility(View.VISIBLE);
                formatStrokes(v.txtPar, p.getParStatus(), v.txtPosition);
            }
            v.vPoints.setVisibility(View.GONE);
        }

        v.vlh.setVisibility(View.VISIBLE);
        v.txtLastHole.setText("" + p.getLastHole());

        if (p.getCurrentRoundStatus() == 0) {
            v.txtStatus.setText("E");
        }
        if (p.getCurrentRoundStatus() > 0) {
            v.txtStatus.setText("-" + p.getCurrentRoundStatus());
        }
        if (p.getCurrentRoundStatus() < 0) {
            v.txtStatus.setText("+" + (p.getCurrentRoundStatus() * -1));
        }
        if (p.getLastHole() == p.getHolesPerRound()) {
            v.txtLastHole.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            v.txtLastHole.setTextColor(ctx.getResources().getColor(R.color.green));
        }


        if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
            v.txtPosition.setVisibility(View.GONE);
        } else {
            v.txtPosition.setVisibility(View.VISIBLE);
        }
        if (p.getPosition() < 10) {
            v.txtPosition.setText("0" + p.getPosition());
        } else {
            v.txtPosition.setText("" + p.getPosition());
        }
        if (p.isTied()) {
            v.txtPosition.setText("T" + p.getPosition());
        }

        v.txtPlayer.setText(p.getPlayer().getFirstName() + " "
                + p.getPlayer().getLastName());
        format(v.txtRound1, p.getTourneyScoreByRoundList().get(0));
        v.txtPointsRound1.setText(""+p.getTourneyScoreByRoundList().get(0).getTotalPoints());
        switch (rounds) {
            case 1:
                v.vt.setVisibility(View.GONE);
                v.v2.setVisibility(View.GONE);
                v.v3.setVisibility(View.GONE);
                v.v4.setVisibility(View.GONE);
                v.v5.setVisibility(View.GONE);
                v.v6.setVisibility(View.GONE);
                v.pp2.setVisibility(View.GONE);
                v.pp3.setVisibility(View.GONE);
                v.pp4.setVisibility(View.GONE);
                v.pp5.setVisibility(View.GONE);
                v.pp6.setVisibility(View.GONE);
                break;
            case 2:
                format(v.txtRound2, p.getTourneyScoreByRoundList().get(1));
                v.txtPointsRound2.setText(""+p.getTourneyScoreByRoundList().get(1).getTotalPoints());
                v.v3.setVisibility(View.GONE);
                v.v4.setVisibility(View.GONE);
                v.v5.setVisibility(View.GONE);
                v.v6.setVisibility(View.GONE);

                v.pp3.setVisibility(View.GONE);
                v.pp4.setVisibility(View.GONE);
                v.pp5.setVisibility(View.GONE);
                v.pp6.setVisibility(View.GONE);
                break;
            case 3:
                format(v.txtRound2, p.getTourneyScoreByRoundList().get(1));
                format(v.txtRound3, p.getTourneyScoreByRoundList().get(2));
                v.txtPointsRound2.setText(""+p.getTourneyScoreByRoundList().get(1).getTotalPoints());
                v.txtPointsRound3.setText(""+p.getTourneyScoreByRoundList().get(2).getTotalPoints());
                v.v4.setVisibility(View.GONE);
                v.v5.setVisibility(View.GONE);
                v.v6.setVisibility(View.GONE);

                v.pp4.setVisibility(View.GONE);
                v.pp5.setVisibility(View.GONE);
                v.pp6.setVisibility(View.GONE);
                break;
            case 4:
                format(v.txtRound2, p.getTourneyScoreByRoundList().get(1));
                format(v.txtRound3, p.getTourneyScoreByRoundList().get(2));
                format(v.txtRound4, p.getTourneyScoreByRoundList().get(3));
                v.txtPointsRound2.setText(""+p.getTourneyScoreByRoundList().get(1).getTotalPoints());
                v.txtPointsRound3.setText(""+p.getTourneyScoreByRoundList().get(2).getTotalPoints());
                v.txtPointsRound4.setText(""+p.getTourneyScoreByRoundList().get(3).getTotalPoints());
                v.v5.setVisibility(View.GONE);
                v.v6.setVisibility(View.GONE);

                v.pp5.setVisibility(View.GONE);
                v.pp6.setVisibility(View.GONE);
                break;
            case 5:
                format(v.txtRound2, p.getTourneyScoreByRoundList().get(1));
                format(v.txtRound3, p.getTourneyScoreByRoundList().get(2));
                format(v.txtRound4, p.getTourneyScoreByRoundList().get(3));
                format(v.txtRound5, p.getTourneyScoreByRoundList().get(4));
                v.txtPointsRound2.setText(""+p.getTourneyScoreByRoundList().get(1).getTotalPoints());
                v.txtPointsRound3.setText(""+p.getTourneyScoreByRoundList().get(2).getTotalPoints());
                v.txtPointsRound4.setText(""+p.getTourneyScoreByRoundList().get(3).getTotalPoints());
                v.txtPointsRound5.setText(""+p.getTourneyScoreByRoundList().get(4).getTotalPoints());

                v.v6.setVisibility(View.GONE);
                v.pp6.setVisibility(View.GONE);
                break;
            case 6:
                format(v.txtRound2, p.getTourneyScoreByRoundList().get(1));
                format(v.txtRound3, p.getTourneyScoreByRoundList().get(2));
                format(v.txtRound4, p.getTourneyScoreByRoundList().get(3));
                format(v.txtRound5, p.getTourneyScoreByRoundList().get(4));
                format(v.txtRound6, p.getTourneyScoreByRoundList().get(5));
                v.txtPointsRound2.setText(""+p.getTourneyScoreByRoundList().get(1).getTotalPoints());
                v.txtPointsRound3.setText(""+p.getTourneyScoreByRoundList().get(2).getTotalPoints());
                v.txtPointsRound4.setText(""+p.getTourneyScoreByRoundList().get(3).getTotalPoints());
                v.txtPointsRound5.setText(""+p.getTourneyScoreByRoundList().get(4).getTotalPoints());
                v.txtPointsRound6.setText(""+p.getTourneyScoreByRoundList().get(5).getTotalPoints());
                break;
        }

        v.txtTotal.setTextColor(ctx.getResources().getColor(R.color.black));
        v.txtTotal.setText("" + p.getTotalScore());
        v.txtPointsTotal.setText("" + p.getTotalPoints());




        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("golfgroup")
                    .append(golfGroup.getGolfGroupID()).append("/player/");
            sb.append("t");
            sb.append(p.getPlayer().getPlayerID()).append(".jpg");
            Picasso.with(ctx).load(sb.toString()).into(v.image);
//
//            v.image.setDefaultImageResId(R.drawable.boy);
//            v.image.setImageUrl(sb.toString(), imageLoader);
        } catch (Exception e) {
            Log.w("LeaderBoardAdapter", "network image view problem", e);
        }
        Statics.setRobotoFontLight(ctx, v.txtPlayer);
        animateView(convertView);
        return (convertView);
    }


    private void format(TextView txt, TourneyScoreByRoundDTO tsbr) {
        //Log.e("adapter","format, par: " + tsbr.getPar() + " totalScore: " + tsbr.getTotalScore());
        int cnt = 0;
        if (tsbr.getScore1() > 0) cnt++;
        if (tsbr.getScore2() > 0) cnt++;
        if (tsbr.getScore3() > 0) cnt++;
        if (tsbr.getScore4() > 0) cnt++;
        if (tsbr.getScore5() > 0) cnt++;
        if (tsbr.getScore6() > 0) cnt++;
        if (tsbr.getScore7() > 0) cnt++;
        if (tsbr.getScore8() > 0) cnt++;
        if (tsbr.getScore9() > 0) cnt++;
        if (tsbr.getScore10() > 0) cnt++;
        if (tsbr.getScore11() > 0) cnt++;
        if (tsbr.getScore12() > 0) cnt++;
        if (tsbr.getScore13() > 0) cnt++;
        if (tsbr.getScore14() > 0) cnt++;
        if (tsbr.getScore15() > 0) cnt++;
        if (tsbr.getScore16() > 0) cnt++;
        if (tsbr.getScore17() > 0) cnt++;
        if (tsbr.getScore18() > 0) cnt++;

        if (cnt == tsbr.getHolesPerRound()) {
            if (tsbr.getTotalScore() < tsbr.getPar()) {
                txt.setTextColor(ctx.getResources().getColor(com.boha.malengagolf.library.R.color.absa_red));
            }
            if (tsbr.getTotalScore() == tsbr.getPar()) {
                txt.setTextColor(ctx.getResources().getColor(com.boha.malengagolf.library.R.color.black));
            }
            if (tsbr.getTotalScore() > tsbr.getPar()) {
                txt.setTextColor(ctx.getResources().getColor(com.boha.malengagolf.library.R.color.blue));
            }
        } else {
            txt.setTextColor(ctx.getResources().getColor(com.boha.malengagolf.library.R.color.grey));
        }
        txt.setText("" + tsbr.getTotalScore());

    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
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
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblack_oval));
            }
        }
        if (points > (holeCount * 2)) { //under par
            txt.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xred_oval));
            }
        }
        if (points < (holeCount * 2)) { //over par
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
            if (txtPos != null) {
                txtPos.setBackground(ctx.getResources().getDrawable(R.drawable.xblue_oval));
            }
        }
        txt.setText("" + points);

    }

    private void formatStrokes(TextView txt, int parStatus, TextView pos) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
            pos.setBackground(ctx.getResources().getDrawable(R.drawable.xblack_oval));
            txt.setText(ctx.getResources().getString(R.string.even));
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            pos.setBackground(ctx.getResources().getDrawable(R.drawable.xred_oval));
            txt.setText("-" + parStatus);
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
            pos.setBackground(ctx.getResources().getDrawable(R.drawable.xblue_oval));
            txt.setText("+" + (parStatus * -1));
        }


    }

    static final int UNDER = 1, PAR = 2, OVER = 3;
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
}
