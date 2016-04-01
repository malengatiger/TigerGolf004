package com.boha.malengagolf.library.adapters;

import android.content.Context;
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
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;
import com.boha.malengagolf.library.util.Statics;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TourneyPlayerAdapter extends ArrayAdapter<LeaderBoardDTO> {

    public interface TourneyPlayerListener {
        public void onRemovePlayerRequested(LeaderBoardDTO leaderBoard, int index);
        public void onScoringRequested(LeaderBoardDTO leaderBoard);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<LeaderBoardDTO> mList;
    private Context ctx;
    private int golfGroupID, rounds, par;
    boolean useAgeGroups;
    private boolean hideIcons;
    private TourneyPlayerListener listener;

    public TourneyPlayerAdapter(Context context, int textViewResourceId,
                                List<LeaderBoardDTO> list,
                                int golfGroupID, int rounds, int par, boolean useAgeGroups,
                                boolean hideIcons,
                                TourneyPlayerListener listener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.golfGroupID = golfGroupID;
        ctx = context;
        this.par = par;
        this.hideIcons = hideIcons;
        this.listener = listener;
        this.useAgeGroups = useAgeGroups;
        this.rounds = rounds;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName, txtTotalPar, txtTee1, txtTee2, txtTee3, txtTee4, txtTee5, txtTee6, txtNumber, txtTotalLabel;
        TextView txtRound1p, txtRound2p, txtRound3p,
                txtRound4p, txtRound5p, txtRound6p;

        ImageView image;
        View round2, round3, round4, round5, round6, tot, icons;
        ImageView imgRemove, imgScoring;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem vhi;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            vhi = new ViewHolderItem();
            vhi.txtName = (TextView) convertView
                    .findViewById(R.id.TNPL_txtName);
            vhi.imgRemove = (ImageView) convertView
                    .findViewById(R.id.TNPL_imgRemovePlayer);
            vhi.imgScoring = (ImageView) convertView
                    .findViewById(R.id.TNPL_imgScoring);
            vhi.txtNumber = (TextView) convertView
                    .findViewById(R.id.TNPL_number);

            vhi.txtRound1p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar1);
            vhi.txtRound2p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar2);
            vhi.txtRound3p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar3);
            vhi.txtRound4p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar4);
            vhi.txtRound5p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar5);
            vhi.txtRound6p = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar6);


            vhi.txtTotalPar = (TextView) convertView
                    .findViewById(R.id.TNPL_txtPar7);
            vhi.txtTotalLabel = (TextView) convertView
                    .findViewById(R.id.TNPL_txtLabel7);

            vhi.txtTee1 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee1);
            vhi.txtTee2 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee2);
            vhi.txtTee3 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee3);
            vhi.txtTee4 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee4);
            vhi.txtTee5 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee5);
            vhi.txtTee6 = (TextView) convertView
                    .findViewById(R.id.TNPL_txtTee6);


            vhi.round2 = convertView.findViewById(R.id.TNPL_layoutRound2);
            vhi.round3 = convertView.findViewById(R.id.TNPL_layoutRound3);
            vhi.round4 = convertView.findViewById(R.id.TNPL_layoutRound4);
            vhi.round5 = convertView.findViewById(R.id.TNPL_layoutRound5);
            vhi.round6 = convertView.findViewById(R.id.TNPL_layoutRound6);
            vhi.tot = convertView.findViewById(R.id.TNPL_layoutTotalScore);
            vhi.icons = convertView.findViewById(R.id.TNPL_layoutActions);


            vhi.image = (ImageView) convertView
                    .findViewById(R.id.TNPL_image);
            convertView.setTag(vhi);
        } else {
            vhi = (ViewHolderItem) convertView.getTag();
        }

       final LeaderBoardDTO p = mList.get(position);
        switch (p.getTournamentType()) {
            case TournamentDTO.STABLEFORD_INDIVIDUAL:
                vhi.txtTotalLabel.setText(ctx.getResources().getString(R.string.total_points));
                vhi.txtTotalPar.setText("" + p.getTotalPoints());
                break;
            case TournamentDTO.STROKE_PLAY_INDIVIDUAL:
                vhi.txtTotalLabel.setText(ctx.getResources().getString(R.string.total_score));
                vhi.txtTotalPar.setText("" + p.getTotalScore());
                break;
            case TournamentDTO.BETTER_BALL_STABLEFORD:
                break;
            case TournamentDTO.BETTER_BALL_STROKEPLAY:
                break;

        }
        vhi.txtNumber.setText("" + (position + 1));
        if (hideIcons) {
            vhi.imgRemove.setVisibility(View.GONE);
        } else {
            vhi.imgRemove.setVisibility(View.VISIBLE);
        }
        vhi.imgScoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onScoringRequested(p);
            }
        });
        vhi.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemovePlayerRequested(p, position);
            }
        });
        switch (rounds) {
            case 1:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                } catch (Exception e) {
                }
                vhi.tot.setVisibility(View.GONE);
                vhi.round2.setVisibility(View.GONE);
                vhi.round3.setVisibility(View.GONE);
                vhi.round4.setVisibility(View.GONE);
                vhi.round5.setVisibility(View.GONE);
                vhi.round6.setVisibility(View.GONE);
                break;
            case 2:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                vhi.txtRound2p.setText("" + p.getScoreRound2());

                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    format(p.getTourneyScoreByRoundList().get(1), vhi.txtRound2p);
                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                    formatTee(vhi.txtTee2, p.getTourneyScoreByRoundList().get(1).getTee());
                } catch (Exception e) {
                }

                vhi.tot.setVisibility(View.VISIBLE);
                vhi.round3.setVisibility(View.GONE);
                vhi.round4.setVisibility(View.GONE);
                vhi.round5.setVisibility(View.GONE);
                vhi.round6.setVisibility(View.GONE);
                break;
            case 3:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                vhi.txtRound2p.setText("" + p.getScoreRound2());
                vhi.txtRound3p.setText("" + p.getScoreRound3());

                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    format(p.getTourneyScoreByRoundList().get(1), vhi.txtRound2p);
                    format(p.getTourneyScoreByRoundList().get(2), vhi.txtRound3p);

                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                    formatTee(vhi.txtTee2, p.getTourneyScoreByRoundList().get(1).getTee());
                    formatTee(vhi.txtTee3, p.getTourneyScoreByRoundList().get(2).getTee());
                } catch (Exception e) {
                }

                vhi.tot.setVisibility(View.VISIBLE);
                vhi.round4.setVisibility(View.GONE);
                vhi.round5.setVisibility(View.GONE);
                vhi.round6.setVisibility(View.GONE);
                break;
            case 4:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                vhi.txtRound2p.setText("" + p.getScoreRound2());
                vhi.txtRound3p.setText("" + p.getScoreRound3());
                vhi.txtRound4p.setText("" + p.getScoreRound4());

                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    format(p.getTourneyScoreByRoundList().get(1), vhi.txtRound2p);
                    format(p.getTourneyScoreByRoundList().get(2), vhi.txtRound3p);
                    format(p.getTourneyScoreByRoundList().get(3), vhi.txtRound4p);

                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                    formatTee(vhi.txtTee2, p.getTourneyScoreByRoundList().get(1).getTee());
                    formatTee(vhi.txtTee3, p.getTourneyScoreByRoundList().get(2).getTee());
                    formatTee(vhi.txtTee4, p.getTourneyScoreByRoundList().get(3).getTee());
                } catch (Exception e) {
                }

                vhi.tot.setVisibility(View.VISIBLE);
                vhi.round5.setVisibility(View.GONE);
                vhi.round6.setVisibility(View.GONE);
                break;
            case 5:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                vhi.txtRound2p.setText("" + p.getScoreRound2());
                vhi.txtRound3p.setText("" + p.getScoreRound3());
                vhi.txtRound4p.setText("" + p.getScoreRound4());
                vhi.txtRound5p.setText("" + p.getScoreRound5());

                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    format(p.getTourneyScoreByRoundList().get(1), vhi.txtRound2p);
                    format(p.getTourneyScoreByRoundList().get(2), vhi.txtRound3p);
                    format(p.getTourneyScoreByRoundList().get(3), vhi.txtRound4p);
                    format(p.getTourneyScoreByRoundList().get(4), vhi.txtRound5p);

                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                    formatTee(vhi.txtTee2, p.getTourneyScoreByRoundList().get(1).getTee());
                    formatTee(vhi.txtTee3, p.getTourneyScoreByRoundList().get(2).getTee());
                    formatTee(vhi.txtTee4, p.getTourneyScoreByRoundList().get(3).getTee());
                    formatTee(vhi.txtTee5, p.getTourneyScoreByRoundList().get(4).getTee());
                } catch (Exception e) {
                }

                vhi.tot.setVisibility(View.VISIBLE);
                vhi.round6.setVisibility(View.GONE);
                break;
            case 6:
                vhi.txtRound1p.setText("" + p.getScoreRound1());
                vhi.txtRound2p.setText("" + p.getScoreRound2());
                vhi.txtRound3p.setText("" + p.getScoreRound3());
                vhi.txtRound4p.setText("" + p.getScoreRound4());
                vhi.txtRound5p.setText("" + p.getScoreRound5());
                vhi.txtRound6p.setText("" + p.getScoreRound6());

                try {
                    format(p.getTourneyScoreByRoundList().get(0), vhi.txtRound1p);
                    format(p.getTourneyScoreByRoundList().get(1), vhi.txtRound2p);
                    format(p.getTourneyScoreByRoundList().get(2), vhi.txtRound3p);
                    format(p.getTourneyScoreByRoundList().get(3), vhi.txtRound4p);
                    format(p.getTourneyScoreByRoundList().get(4), vhi.txtRound5p);
                    format(p.getTourneyScoreByRoundList().get(5), vhi.txtRound6p);

                    formatTee(vhi.txtTee1, p.getTourneyScoreByRoundList().get(0).getTee());
                    formatTee(vhi.txtTee2, p.getTourneyScoreByRoundList().get(1).getTee());
                    formatTee(vhi.txtTee3, p.getTourneyScoreByRoundList().get(2).getTee());
                    formatTee(vhi.txtTee4, p.getTourneyScoreByRoundList().get(3).getTee());
                    formatTee(vhi.txtTee5, p.getTourneyScoreByRoundList().get(4).getTee());
                    formatTee(vhi.txtTee6, p.getTourneyScoreByRoundList().get(5).getTee());
                } catch (Exception e) {
                }
                vhi.tot.setVisibility(View.VISIBLE);
                break;

        }


        if (p.getTotalScore() == 0) {
            vhi.tot.setVisibility(View.GONE);
        }

        if (useAgeGroups) {
            vhi.txtName.setText(p.getPlayer().getLastName() + ", "
                    + p.getPlayer().getFirstName() + " - (" + p.getPlayer().getAge() + ")");
        } else {
            vhi.txtName.setText(p.getPlayer().getLastName() + ", "
                    + p.getPlayer().getFirstName());
        }

        //image
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("golfgroup")
                .append(golfGroupID).append("/player/");
        sb.append("t");
        sb.append(p.getPlayer().getPlayerID()).append(".jpg");
        Picasso.with(ctx).load(sb.toString()).into(vhi.image);

        Statics.setRobotoFontLight(ctx,vhi.txtName);
        Statics.setRobotoFontLight(ctx,vhi.txtRound1p);
        Statics.setRobotoFontLight(ctx,vhi.txtRound2p);
        Statics.setRobotoFontLight(ctx,vhi.txtRound3p);
        Statics.setRobotoFontLight(ctx,vhi.txtRound4p);
        Statics.setRobotoFontLight(ctx,vhi.txtRound5p);
        animateView(convertView);
        return (convertView);
    }

    private void formatTee(TextView txt, int tee) {
        switch (tee) {
            case 0:
                txt.setText("00");
                txt.setTextColor(ctx.getResources().getColor(R.color.grey_light));
                return;
            case 1:
                txt.setText("01");
                txt.setTextColor(ctx.getResources().getColor(R.color.black));
                return;
            case 2:
                txt.setText("10");
                txt.setTextColor(ctx.getResources().getColor(R.color.blue));
                return;
        }
    }

    private void format(TourneyScoreByRoundDTO tsbr, TextView txt) {
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
                txt.setTextColor(ctx.getResources().getColor(com.boha.malengagolf.library.R.color.opaque_red));
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


    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

}
