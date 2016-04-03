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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlayerHistoryAdapter extends ArrayAdapter<LeaderBoardDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<LeaderBoardDTO> mList;
    private Context ctx;

    public PlayerHistoryAdapter(Context context,
                                int textViewResourceId,
                                List<LeaderBoardDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtPlayer, txtRound1, txtRound2, txtRound3, txtPosition, txtStartDate,
                txtRound4, txtRound5, txtRound6, txtTotal, txtPar, txtTourney, txtClub;
        View v1, v2, v3, v4, v5, v6, vt, vp;

        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            viewHolderItem = new ViewHolderItem();

            viewHolderItem.txtPosition = (TextView) convertView
                    .findViewById(R.id.PH__position);
            viewHolderItem.txtRound1 = (TextView) convertView
                    .findViewById(R.id.LS_round1);
            viewHolderItem.txtRound2 = (TextView) convertView
                    .findViewById(R.id.LS_round2);
            viewHolderItem.txtRound3 = (TextView) convertView
                    .findViewById(R.id.LS_round3);
            viewHolderItem.txtRound4 = (TextView) convertView
                    .findViewById(R.id.LS_round4);
            viewHolderItem.txtRound5 = (TextView) convertView
                    .findViewById(R.id.LS_round5);
            viewHolderItem.txtRound6 = (TextView) convertView
                    .findViewById(R.id.LS_round6);
            viewHolderItem.txtTotal = (TextView) convertView
                    .findViewById(R.id.LS_total);
            viewHolderItem.txtPar = (TextView) convertView
                    .findViewById(R.id.LS_parStatus);

            viewHolderItem.txtTourney = (TextView) convertView
                    .findViewById(R.id.PH__tournName);
            viewHolderItem.txtClub = (TextView) convertView
                    .findViewById(R.id.PH__clubName);
            viewHolderItem.txtStartDate = (TextView) convertView
                    .findViewById(R.id.PH__startDate);

            viewHolderItem.v1 = convertView.findViewById(R.id.LS_round1Layout);
            viewHolderItem.v2 = convertView.findViewById(R.id.LS_round2Layout);
            viewHolderItem.v3 = convertView.findViewById(R.id.LS_round3Layout);
            viewHolderItem.v4 = convertView.findViewById(R.id.LS_round4Layout);
            viewHolderItem.v5 = convertView.findViewById(R.id.LS_round5Layout);
            viewHolderItem.v6 = convertView.findViewById(R.id.LS_round6Layout);
            viewHolderItem.vt = convertView.findViewById(R.id.LS_totLayout);
            viewHolderItem.vp = convertView.findViewById(R.id.LS_parLayout);

            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        LeaderBoardDTO p = mList.get(position);
        if (p.isTied()) {
            viewHolderItem.txtPosition.setText("T" + p.getPosition());
        } else {
            if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
                viewHolderItem.txtPosition.setVisibility(View.GONE);
            } else {
                viewHolderItem.txtPosition.setVisibility(View.VISIBLE);
            }
            if (p.getPosition() < 10) {
                viewHolderItem.txtPosition.setText("0" + p.getPosition());
            } else {
                viewHolderItem.txtPosition.setText("" + p.getPosition());
            }
        }

        viewHolderItem.txtTourney.setText(p.getTournamentName());
        viewHolderItem.txtClub.setText(p.getClubName());
        //TODO - fix this
        formatParStatus(viewHolderItem.txtRound1, p.getScoreRound1(), p.getTourneyScoreByRoundList().get(0).getPar());

        switch (p.getRounds()) {
            case 1:
                viewHolderItem.vt.setVisibility(View.GONE);
                viewHolderItem.v2.setVisibility(View.GONE);
                viewHolderItem.v3.setVisibility(View.GONE);
                viewHolderItem.v4.setVisibility(View.GONE);
                viewHolderItem.v5.setVisibility(View.GONE);
                viewHolderItem.v6.setVisibility(View.GONE);
                break;
            case 2:
                formatParStatus(viewHolderItem.txtRound2, p.getScoreRound2(), p.getTourneyScoreByRoundList().get(1).getPar());
                viewHolderItem.v3.setVisibility(View.GONE);
                viewHolderItem.v4.setVisibility(View.GONE);
                viewHolderItem.v5.setVisibility(View.GONE);
                viewHolderItem.v5.setVisibility(View.GONE);
                viewHolderItem.v6.setVisibility(View.GONE);
                break;
            case 3:
                formatParStatus(viewHolderItem.txtRound2, p.getScoreRound2(), p.getTourneyScoreByRoundList().get(1).getPar());
                formatParStatus(viewHolderItem.txtRound3, p.getScoreRound3(), p.getTourneyScoreByRoundList().get(2).getPar());
                viewHolderItem.v4.setVisibility(View.GONE);
                viewHolderItem.v5.setVisibility(View.GONE);
                viewHolderItem.v6.setVisibility(View.GONE);
                break;
            case 4:
                formatParStatus(viewHolderItem.txtRound2, p.getScoreRound2(), p.getTourneyScoreByRoundList().get(1).getPar());
                formatParStatus(viewHolderItem.txtRound3, p.getScoreRound3(), p.getTourneyScoreByRoundList().get(2).getPar());
                formatParStatus(viewHolderItem.txtRound4, p.getScoreRound4(), p.getTourneyScoreByRoundList().get(3).getPar());
                viewHolderItem.v5.setVisibility(View.GONE);
                viewHolderItem.v6.setVisibility(View.GONE);
                break;
            case 5:
                formatParStatus(viewHolderItem.txtRound2, p.getScoreRound2(), p.getTourneyScoreByRoundList().get(1).getPar());
                formatParStatus(viewHolderItem.txtRound3, p.getScoreRound3(), p.getTourneyScoreByRoundList().get(2).getPar());
                formatParStatus(viewHolderItem.txtRound4, p.getScoreRound4(), p.getTourneyScoreByRoundList().get(3).getPar());
                formatParStatus(viewHolderItem.txtRound5, p.getScoreRound5(), p.getTourneyScoreByRoundList().get(4).getPar());
                viewHolderItem.v6.setVisibility(View.GONE);
                break;
            case 6:
                formatParStatus(viewHolderItem.txtRound2, p.getScoreRound2(), p.getTourneyScoreByRoundList().get(1).getPar());
                formatParStatus(viewHolderItem.txtRound3, p.getScoreRound3(), p.getTourneyScoreByRoundList().get(2).getPar());
                formatParStatus(viewHolderItem.txtRound4, p.getScoreRound4(), p.getTourneyScoreByRoundList().get(3).getPar());
                formatParStatus(viewHolderItem.txtRound5, p.getScoreRound5(), p.getTourneyScoreByRoundList().get(4).getPar());
                formatParStatus(viewHolderItem.txtRound6, p.getScoreRound6(), p.getTourneyScoreByRoundList().get(5).getPar());
                break;
        }

        viewHolderItem.txtTotal.setTextColor(ctx.getResources().getColor(R.color.black));
        viewHolderItem.txtTotal.setText("" + p.getTotalScore());
        viewHolderItem.txtStartDate.setText(y.format(new Date(p.getStartDate())));

        if (p.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
            viewHolderItem.vp.setVisibility(View.GONE);

        } else {
            viewHolderItem.vp.setVisibility(View.VISIBLE);
            formatStrokes(viewHolderItem.txtPar, p.getParStatus());
        }

        int x = position % 2;
        if (x > 0) {
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.beige_pale));
        } else {
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.white));
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

    private void formatParStatus(TextView txt, int score, int par) {
        if (score < par) {
            txt.setTextColor(ctx.getResources().getColor(R.color.opaque_red));
        }
        if (score == par || score == 0) {
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
        }
        if (score > par) {
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
        }
        txt.setText("" + score);
    }

    private void formatStrokes(TextView txt, int parStatus) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
            txt.setText("E");
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
            txt.setText("+" + (parStatus * -1));
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ctx.getResources().getColor(R.color.opaque_red));
            txt.setText("-" + parStatus);
        }


    }

    static final int UNDER = 1, PAR = 2, OVER = 3;
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
}
