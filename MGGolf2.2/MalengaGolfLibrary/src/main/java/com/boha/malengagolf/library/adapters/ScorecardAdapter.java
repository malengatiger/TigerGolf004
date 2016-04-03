package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.ClubCourseDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/15.
 */
public class ScorecardAdapter extends ArrayAdapter<TourneyScoreByRoundDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<TourneyScoreByRoundDTO> mList;
    private Context ctx;
    private int tournamentType;

    public ScorecardAdapter(Context context,
                            int textViewResourceId,
                            List<TourneyScoreByRoundDTO> list, int tournamentType) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.tournamentType = tournamentType;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView
                txtHole1, txtHole2, txtHole3, txtRound,
                txtHole4, txtHole5, txtHole6, txtHole7, txtHole8, txtHole9,
                txtHole10, txtHole11, txtHole12, txtHole13, txtHole14, txtHole15,
                txtHole16, txtHole17, txtHole18,
                txtTotalFN, txtTotalBN, txtPar, txtTotal, txtParLabel;
        TextView
                txtPoints1, txtPoints2, txtPoints3,
                txtPoints4, txtPoints5, txtPoints6, txtPoints7, txtPoints8, txtPoints9,
                txtPoints10, txtPoints11, txtPoints12, txtPoints13, txtPoints14, txtPoints15,
                txtPoints16, txtPoints17, txtPoints18, txtTotalPoints, txtTotalPointsLabel,
                txtTotalFNPoints, txtTotalBNPoints, txtPointsLabelFN, txtPointsLabelBN;
        TextView txtHPar1, txtHPar2, txtHPar3, txtHPar4, txtHPar5, txtHPar6, txtHPar7,
                txtHPar8, txtHPar9, txtHPar10, txtHPar11, txtHPar12, txtHPar13, txtHPar14,
                txtHPar15, txtHPar16, txtHPar17, txtHPar18;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem vh;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            vh = new ViewHolderItem();
            vh.txtHole1 = (TextView) convertView.findViewById(R.id.CARD_hole1);
            vh.txtHole2 = (TextView) convertView.findViewById(R.id.CARD_hole2);
            vh.txtHole3 = (TextView) convertView.findViewById(R.id.CARD_hole3);
            vh.txtHole4 = (TextView) convertView.findViewById(R.id.CARD_hole4);
            vh.txtHole5 = (TextView) convertView.findViewById(R.id.CARD_hole5);
            vh.txtHole6 = (TextView) convertView.findViewById(R.id.CARD_hole6);
            vh.txtHole7 = (TextView) convertView.findViewById(R.id.CARD_hole7);
            vh.txtHole8 = (TextView) convertView.findViewById(R.id.CARD_hole8);
            vh.txtHole9 = (TextView) convertView.findViewById(R.id.CARD_hole9);
            vh.txtTotalFN = (TextView) convertView.findViewById(R.id.CARD_totalFN);
            //
            vh.txtHole10 = (TextView) convertView.findViewById(R.id.CARD_hole10);
            vh.txtHole11 = (TextView) convertView.findViewById(R.id.CARD_hole11);
            vh.txtHole12 = (TextView) convertView.findViewById(R.id.CARD_hole12);
            vh.txtHole13 = (TextView) convertView.findViewById(R.id.CARD_hole13);
            vh.txtHole14 = (TextView) convertView.findViewById(R.id.CARD_hole14);
            vh.txtHole15 = (TextView) convertView.findViewById(R.id.CARD_hole15);
            vh.txtHole16 = (TextView) convertView.findViewById(R.id.CARD_hole16);
            vh.txtHole17 = (TextView) convertView.findViewById(R.id.CARD_hole17);
            vh.txtHole18 = (TextView) convertView.findViewById(R.id.CARD_hole18);
            vh.txtTotalBN = (TextView) convertView.findViewById(R.id.CARD_totalBN);
            vh.txtRound = (TextView) convertView.findViewById(R.id.CARD_round);

            vh.txtHPar1 = (TextView) convertView.findViewById(R.id.CARD_holepar1);
            vh.txtHPar2 = (TextView) convertView.findViewById(R.id.CARD_holepar2);
            vh.txtHPar3 = (TextView) convertView.findViewById(R.id.CARD_holepar3);
            vh.txtHPar4 = (TextView) convertView.findViewById(R.id.CARD_holepar4);
            vh.txtHPar5 = (TextView) convertView.findViewById(R.id.CARD_holepar5);
            vh.txtHPar6 = (TextView) convertView.findViewById(R.id.CARD_holepar6);
            vh.txtHPar7 = (TextView) convertView.findViewById(R.id.CARD_holepar7);
            vh.txtHPar8 = (TextView) convertView.findViewById(R.id.CARD_holepar8);
            vh.txtHPar9 = (TextView) convertView.findViewById(R.id.CARD_holepar9);
            vh.txtHPar10 = (TextView) convertView.findViewById(R.id.CARD_holepar10);
            vh.txtHPar11 = (TextView) convertView.findViewById(R.id.CARD_holepar11);
            vh.txtHPar12 = (TextView) convertView.findViewById(R.id.CARD_holepar12);
            vh.txtHPar13 = (TextView) convertView.findViewById(R.id.CARD_holepar13);
            vh.txtHPar14 = (TextView) convertView.findViewById(R.id.CARD_holepar14);
            vh.txtHPar15 = (TextView) convertView.findViewById(R.id.CARD_holepar15);
            vh.txtHPar16 = (TextView) convertView.findViewById(R.id.CARD_holepar16);
            vh.txtHPar17 = (TextView) convertView.findViewById(R.id.CARD_holepar17);
            vh.txtHPar18 = (TextView) convertView.findViewById(R.id.CARD_holepar18);

            vh.txtPoints1 = (TextView) convertView.findViewById(R.id.CARD_points1);
            vh.txtPoints2 = (TextView) convertView.findViewById(R.id.CARD_points2);
            vh.txtPoints3 = (TextView) convertView.findViewById(R.id.CARD_points3);
            vh.txtPoints4 = (TextView) convertView.findViewById(R.id.CARD_points4);
            vh.txtPoints5 = (TextView) convertView.findViewById(R.id.CARD_points5);
            vh.txtPoints6 = (TextView) convertView.findViewById(R.id.CARD_points6);
            vh.txtPoints7 = (TextView) convertView.findViewById(R.id.CARD_points7);
            vh.txtPoints8 = (TextView) convertView.findViewById(R.id.CARD_points8);
            vh.txtPoints9 = (TextView) convertView.findViewById(R.id.CARD_points9);
            vh.txtPoints10 = (TextView) convertView.findViewById(R.id.CARD_points10);
            vh.txtPoints11 = (TextView) convertView.findViewById(R.id.CARD_points11);
            vh.txtPoints12 = (TextView) convertView.findViewById(R.id.CARD_points12);
            vh.txtPoints13 = (TextView) convertView.findViewById(R.id.CARD_points13);
            vh.txtPoints14 = (TextView) convertView.findViewById(R.id.CARD_points14);
            vh.txtPoints15 = (TextView) convertView.findViewById(R.id.CARD_points15);
            vh.txtPoints16 = (TextView) convertView.findViewById(R.id.CARD_points16);
            vh.txtPoints17 = (TextView) convertView.findViewById(R.id.CARD_points17);
            vh.txtPoints18 = (TextView) convertView.findViewById(R.id.CARD_points18);

            vh.txtTotalFNPoints = (TextView) convertView.findViewById(R.id.CARD_totalFNPoints);
            vh.txtTotalBNPoints = (TextView) convertView.findViewById(R.id.CARD_totalBNPoints);
            vh.txtPointsLabelFN = (TextView) convertView.findViewById(R.id.CARD_totalFNPointsLabel);
            vh.txtPointsLabelBN = (TextView) convertView.findViewById(R.id.CARD_totalBNPointsLabel);
            vh.txtTotalPoints = (TextView) convertView.findViewById(R.id.CARD_totalPoints);
            vh.txtTotalPointsLabel = (TextView) convertView.findViewById(R.id.CARD_pointsLabel);

            vh.txtPar = (TextView) convertView.findViewById(R.id.CARD_par);
            vh.txtParLabel = (TextView) convertView.findViewById(R.id.CARD_parLabel);
            convertView.setTag(vh);
            vh.txtTotal = (TextView) convertView.findViewById(R.id.CARD_total);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolderItem) convertView.getTag();
        }

        TourneyScoreByRoundDTO p = mList.get(position);
        ClubCourseDTO cc = p.getClubCourse();
        vh.txtRound.setText("" + p.getGolfRound());
        vh.txtHole1.setText("" + p.getScore1());
        vh.txtHole2.setText("" + p.getScore2());
        vh.txtHole3.setText("" + p.getScore3());
        vh.txtHole4.setText("" + p.getScore4());
        vh.txtHole5.setText("" + p.getScore5());
        vh.txtHole6.setText("" + p.getScore6());
        vh.txtHole7.setText("" + p.getScore7());
        vh.txtHole8.setText("" + p.getScore8());
        vh.txtHole9.setText("" + p.getScore9());
        //
        vh.txtHole10.setText("" + p.getScore10());
        vh.txtHole11.setText("" + p.getScore11());
        vh.txtHole12.setText("" + p.getScore12());
        vh.txtHole13.setText("" + p.getScore13());
        vh.txtHole14.setText("" + p.getScore14());
        vh.txtHole15.setText("" + p.getScore15());
        vh.txtHole16.setText("" + p.getScore16());
        vh.txtHole17.setText("" + p.getScore17());
        vh.txtHole18.setText("" + p.getScore18());
        if (tournamentType == 0) {
            tournamentType = RequestDTO.STROKE_PLAY_INDIVIDUAL;
        }
        switch (tournamentType) {
            case RequestDTO.STABLEFORD_INDIVIDUAL:
                Totals totals = setPoints(p, vh);
                vh.txtTotalFNPoints.setText("" + totals.frontNine);
                vh.txtTotalBNPoints.setText("" + totals.backNine);
                vh.txtTotalPoints.setText("" + (totals.frontNine + totals.backNine));
                vh.txtPar.setVisibility(View.GONE);
                vh.txtParLabel.setVisibility(View.GONE);
                break;
            case RequestDTO.STROKE_PLAY_INDIVIDUAL:
                hidePoints(vh);
                vh.txtTotalPoints.setVisibility(View.GONE);
                vh.txtTotalPoints.setVisibility(View.GONE);
                vh.txtTotalPointsLabel.setVisibility(View.GONE);
                break;
        }
        //style par colors
        styleColors(p, vh, cc);

        int totFN = p.getScore1() + p.getScore2() + p.getScore3() + p.getScore4() + p.getScore5() +
                p.getScore6() + p.getScore7() + p.getScore8() + p.getScore9();
        vh.txtTotalFN.setText("" + totFN);
        //
        int totBN = p.getScore10() + p.getScore11() + p.getScore12() + p.getScore13() + p.getScore14() +
                p.getScore15() + p.getScore16() + p.getScore17() + p.getScore18();
        vh.txtTotalBN.setText("" + totBN);

        int total = totFN + totBN;
        vh.txtTotal.setText("" + total);

        setParStatus(p, vh.txtPar, vh.txtTotal);

        int x = position % 2;
        if (x > 0) {
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.beige_pale));
        } else {
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        }
        //set hole par numbers

        vh.txtHPar1.setText("" + cc.getParHole1());
        vh.txtHPar2.setText("" + cc.getParHole2());
        vh.txtHPar3.setText("" + cc.getParHole3());
        vh.txtHPar4.setText("" + cc.getParHole4());
        vh.txtHPar5.setText("" + cc.getParHole5());
        vh.txtHPar6.setText("" + cc.getParHole6());
        vh.txtHPar7.setText("" + cc.getParHole7());
        vh.txtHPar8.setText("" + cc.getParHole8());
        vh.txtHPar9.setText("" + cc.getParHole9());
        vh.txtHPar10.setText("" + cc.getParHole10());
        vh.txtHPar10.setText("" + cc.getParHole11());
        vh.txtHPar12.setText("" + cc.getParHole12());
        vh.txtHPar13.setText("" + cc.getParHole13());
        vh.txtHPar14.setText("" + cc.getParHole14());
        vh.txtHPar15.setText("" + cc.getParHole15());
        vh.txtHPar16.setText("" + cc.getParHole16());
        vh.txtHPar17.setText("" + cc.getParHole17());
        vh.txtHPar18.setText("" + cc.getParHole18());
        //
        animateView(convertView);
        return (convertView);
    }

    private void hidePoints(ViewHolderItem vh) {
        vh.txtPoints1.setVisibility(View.GONE);
        vh.txtPoints2.setVisibility(View.GONE);
        vh.txtPoints3.setVisibility(View.GONE);
        vh.txtPoints4.setVisibility(View.GONE);
        vh.txtPoints5.setVisibility(View.GONE);
        vh.txtPoints6.setVisibility(View.GONE);
        vh.txtPoints7.setVisibility(View.GONE);
        vh.txtPoints8.setVisibility(View.GONE);
        vh.txtPoints9.setVisibility(View.GONE);
        //
        vh.txtPoints10.setVisibility(View.GONE);
        vh.txtPoints11.setVisibility(View.GONE);
        vh.txtPoints12.setVisibility(View.GONE);
        vh.txtPoints13.setVisibility(View.GONE);
        vh.txtPoints14.setVisibility(View.GONE);
        vh.txtPoints15.setVisibility(View.GONE);
        vh.txtPoints16.setVisibility(View.GONE);
        vh.txtPoints17.setVisibility(View.GONE);
        vh.txtPoints18.setVisibility(View.GONE);
        vh.txtTotalBNPoints.setVisibility(View.GONE);
        vh.txtTotalFNPoints.setVisibility(View.GONE);
        vh.txtPointsLabelFN.setVisibility(View.GONE);
        vh.txtPointsLabelBN.setVisibility(View.GONE);
    }

    private Totals setPoints(TourneyScoreByRoundDTO p, ViewHolderItem vh) {
        int fn = 0, bn = 0;
        vh.txtPoints1.setText("" + p.getPoints1());
        vh.txtPoints2.setText("" + p.getPoints2());
        vh.txtPoints3.setText("" + p.getPoints3());
        vh.txtPoints4.setText("" + p.getPoints4());
        vh.txtPoints5.setText("" + p.getPoints5());
        vh.txtPoints6.setText("" + p.getPoints6());
        vh.txtPoints7.setText("" + p.getPoints7());
        vh.txtPoints8.setText("" + p.getPoints8());
        vh.txtPoints9.setText("" + p.getPoints9());
        //
        vh.txtPoints10.setText("" + p.getPoints10());
        vh.txtPoints11.setText("" + p.getPoints11());
        vh.txtPoints12.setText("" + p.getPoints12());
        vh.txtPoints13.setText("" + p.getPoints13());
        vh.txtPoints14.setText("" + p.getPoints14());
        vh.txtPoints15.setText("" + p.getPoints15());
        vh.txtPoints16.setText("" + p.getPoints16());
        vh.txtPoints17.setText("" + p.getPoints17());
        vh.txtPoints18.setText("" + p.getPoints18());

        fn += p.getPoints1();
        fn += p.getPoints2();
        fn += p.getPoints3();
        fn += p.getPoints4();
        fn += p.getPoints5();
        fn += p.getPoints6();
        fn += p.getPoints7();
        fn += p.getPoints8();
        fn += p.getPoints9();

        bn += p.getPoints10();
        bn += p.getPoints11();
        bn += p.getPoints12();
        bn += p.getPoints13();
        bn += p.getPoints14();
        bn += p.getPoints15();
        bn += p.getPoints16();
        bn += p.getPoints17();
        bn += p.getPoints18();
        return new Totals(fn,bn);
    }

    private void setParStatus(TourneyScoreByRoundDTO r, TextView txtPar, TextView txtTotal) {
        ClubCourseDTO clubCourseDTO = r.getClubCourse();

        int cnt = 0;
        ClubCourseDTO cc = r.getClubCourse();
        int parStatus = 0;
        if (r.getScore1() == 0) cnt++;
        else parStatus += cc.getParHole1() - r.getScore1();
        if (r.getScore2() == 0) cnt++;
        else parStatus += cc.getParHole2() - r.getScore2();
        if (r.getScore3() == 0) cnt++;
        else parStatus += cc.getParHole3() - r.getScore3();
        if (r.getScore4() == 0) cnt++;
        else parStatus += cc.getParHole4() - r.getScore4();
        if (r.getScore5() == 0) cnt++;
        else parStatus += cc.getParHole5() - r.getScore5();
        if (r.getScore6() == 0) cnt++;
        else parStatus += cc.getParHole6() - r.getScore6();
        if (r.getScore7() == 0) cnt++;
        else parStatus += cc.getParHole7() - r.getScore7();
        if (r.getScore8() == 0) cnt++;
        else parStatus += cc.getParHole8() - r.getScore8();
        if (r.getScore9() == 0) cnt++;
        else parStatus += cc.getParHole9() - r.getScore9();
        if (r.getScore10() == 0) cnt++;
        else parStatus += cc.getParHole10() - r.getScore10();
        if (r.getScore11() == 0) cnt++;
        else parStatus += cc.getParHole11() - r.getScore11();
        if (r.getScore12() == 0) cnt++;
        else parStatus += cc.getParHole12() - r.getScore12();
        if (r.getScore13() == 0) cnt++;
        else parStatus += cc.getParHole13() - r.getScore13();
        if (r.getScore14() == 0) cnt++;
        else parStatus += cc.getParHole14() - r.getScore14();
        if (r.getScore15() == 0) cnt++;
        else parStatus += cc.getParHole15() - r.getScore15();
        if (r.getScore16() == 0) cnt++;
        else parStatus += cc.getParHole16() - r.getScore16();
        if (r.getScore17() == 0) cnt++;
        else parStatus += cc.getParHole17() - r.getScore17();
        if (r.getScore18() == 0) cnt++;
        else parStatus += cc.getParHole18() - r.getScore18();
        if (cnt < 18) {
            txtTotal.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else {
            txtTotal.setTextColor(ctx.getResources().getColor(R.color.green));
        }
        if (parStatus == 0) {
            txtPar.setText("E");
            txtPar.setTextColor(ctx.getResources().getColor(R.color.black));
        }
        if (parStatus > 0) {
            txtPar.setText("-" + parStatus);
            txtPar.setTextColor(ctx.getResources().getColor(R.color.absa_red));
        }
        if (parStatus < 0) {
            txtPar.setText("+");
            txtPar.setTextColor(ctx.getResources().getColor(R.color.blue));
        }


    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    private void formatTotal(TextView txt, int parStatus) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ctx.getResources().getColor(R.color.absa_red));
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
        }

    }

    private void formatStrokes(TextView txt, int parStatus) {
        if (parStatus == 0) { //Even par
            txt.setTextColor(ctx.getResources().getColor(R.color.black));
            txt.setText("E");
        }
        if (parStatus > 0) { //under par
            txt.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            txt.setText("-" + parStatus);
        }
        if (parStatus < 0) { //over par
            txt.setTextColor(ctx.getResources().getColor(R.color.blue));
            txt.setText("+" + (parStatus * -1));
        }

    }

    private void styleColors(TourneyScoreByRoundDTO p, ViewHolderItem vh, ClubCourseDTO cc) {

        if (p.getScore1() == 0) {
            vh.txtHole1.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole1() == p.getScore1()) {
            vh.txtHole1.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole1() > p.getScore1()) {
                vh.txtHole1.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole1.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        //
        if (p.getScore2() == 0) {
            vh.txtHole2.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole2() == p.getScore2()) {
            vh.txtHole2.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole2() > p.getScore2()) {
                vh.txtHole2.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole2.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore3() == 0) {
            vh.txtHole3.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole3() == p.getScore3()) {
            vh.txtHole3.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole3() > p.getScore3()) {
                vh.txtHole3.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole3.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore4() == 0) {
            vh.txtHole4.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole4() == p.getScore4()) {
            vh.txtHole4.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole4() > p.getScore4()) {
                vh.txtHole4.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole4.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore5() == 0) {
            vh.txtHole5.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole5() == p.getScore5()) {
            vh.txtHole5.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole5() > p.getScore5()) {
                vh.txtHole5.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole5.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore6() == 0) {
            vh.txtHole6.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole6() == p.getScore6()) {
            vh.txtHole6.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole6() > p.getScore6()) {
                vh.txtHole6.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole6.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore7() == 0) {
            vh.txtHole7.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole7() == p.getScore7()) {
            vh.txtHole7.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole7() > p.getScore7()) {
                vh.txtHole7.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole7.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore8() == 0) {
            vh.txtHole8.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole8() == p.getScore8()) {
            vh.txtHole8.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole8() > p.getScore8()) {
                vh.txtHole8.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole8.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore9() == 0) {
            vh.txtHole9.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole9() == p.getScore9()) {
            vh.txtHole9.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole9() > p.getScore9()) {
                vh.txtHole9.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole9.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        //Log.w("adap", "Processinhg hole 10, p.getScore10: " + p.getScore10() + " cc parHole10: " + cc.getParHole10() + " " + cc.getClubName() + " - " + cc.getCourseName());
        if (p.getScore10() == 0) {
            vh.txtHole10.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole10() == p.getScore10()) {
            vh.txtHole1.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole10() > p.getScore10()) {
                vh.txtHole10.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole10.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore11() == 0) {
            vh.txtHole11.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole11() == p.getScore11()) {
            vh.txtHole11.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole11() > p.getScore11()) {
                vh.txtHole11.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole11.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore12() == 0) {
            vh.txtHole12.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole12() == p.getScore12()) {
            vh.txtHole12.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole12() > p.getScore12()) {
                vh.txtHole12.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole12.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore13() == 0) {
            vh.txtHole13.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole13() == p.getScore13()) {
            vh.txtHole13.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole13() > p.getScore13()) {
                vh.txtHole13.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole13.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore14() == 0) {
            vh.txtHole14.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole14() == p.getScore14()) {
            vh.txtHole14.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole14() > p.getScore14()) {
                vh.txtHole14.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole14.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore15() == 0) {
            vh.txtHole15.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole15() == p.getScore15()) {
            vh.txtHole15.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole15() > p.getScore15()) {
                vh.txtHole15.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole15.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore16() == 0) {
            vh.txtHole16.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole16() == p.getScore16()) {
            vh.txtHole16.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole16() > p.getScore16()) {
                vh.txtHole16.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole16.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore17() == 0) {
            vh.txtHole17.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole17() == p.getScore17()) {
            vh.txtHole17.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole17() > p.getScore17()) {
                vh.txtHole17.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole17.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
        if (p.getScore18() == 0) {
            vh.txtHole18.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else if (cc.getParHole18() == p.getScore18()) {
            vh.txtHole18.setTextColor(ctx.getResources().getColor(R.color.black));
        } else {
            if (cc.getParHole18() > p.getScore18()) {
                vh.txtHole18.setTextColor(ctx.getResources().getColor(R.color.absa_red));
            } else {
                vh.txtHole18.setTextColor(ctx.getResources().getColor(R.color.blue));
            }
        }
    }

    static final int UNDER = 1, PAR = 2, OVER = 3;
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);

    class Totals {
        int frontNine, backNine;

        public Totals(int frontNine, int backNine) {
            this.frontNine = frontNine;
            this.backNine = backNine;
        }
    }
}