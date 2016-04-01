package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.data.TourneyScoreByRoundDTO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TeeTimeAdapter extends ArrayAdapter<LeaderBoardDTO> {

    public interface TeeTimeRequestListener {
        public void onTeeTimeRequested(TourneyScoreByRoundDTO score, int index, int tee);
    }

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<LeaderBoardDTO> mList;
    private Context ctx;
    int round;
    boolean isOne = true, isTen;
    TeeTimeRequestListener teeTimeRequestListener;

    public TeeTimeAdapter(Context context,
                          int textViewResourceId,
                          List<LeaderBoardDTO> list,
                          int round,
                          TeeTimeRequestListener teeTimeRequestListener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.round = round;
        this.teeTimeRequestListener = teeTimeRequestListener;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtPlayer, txtTeeTime;
        RadioButton radioOne, radioTen;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            viewHolderItem = new ViewHolderItem();

            viewHolderItem.txtPlayer = (TextView) convertView
                    .findViewById(R.id.TT_playerName);
            viewHolderItem.txtTeeTime = (TextView) convertView
                    .findViewById(R.id.TT_teeTime);
            viewHolderItem.radioOne = (RadioButton) convertView
                    .findViewById(R.id.TT_radioOne);
            viewHolderItem.radioTen = (RadioButton) convertView
                    .findViewById(R.id.TT_radioTen);

            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        LeaderBoardDTO p = mList.get(position);
        viewHolderItem.txtPlayer.setText(p.getPlayer().getFullName());

        final TourneyScoreByRoundDTO t = p.getTourneyScoreByRoundList().get(round - 1);
        viewHolderItem.txtTeeTime.setText(formatTeeTime(t.getTeeTime()));
        if (t.getTee() == 1) {
            viewHolderItem.radioOne.setChecked(true);
        }
        if (t.getTee() == 10) {
            viewHolderItem.radioTen.setChecked(true);
        }
        viewHolderItem.radioOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOne = b;
            }
        });
        viewHolderItem.radioTen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isTen = b;
            }
        });

        viewHolderItem.txtTeeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tee = 1;
                if (isOne) {
                    teeTimeRequestListener.onTeeTimeRequested(
                            t, position, 1);
                }
                if (isTen) {
                    teeTimeRequestListener.onTeeTimeRequested(
                            t, position, 10);
                }
            }
        });

        int x = position % 2;
        if (x > 0) {
            //convertView.setBackgroundColor(ctx.getResources().getColor(R.color.beige_pale));
        } else {
            //convertView.setBackgroundColor(ctx.getResources().getColor(R.color.white));
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

    private String formatTeeTime(long time) {
        if (time == 0) {
            return "00:00";
        }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(time);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        StringBuilder sb = new StringBuilder();
        if (hr < 10) {
            sb.append("0").append(hr).append(":");
        } else {
            sb.append("").append(hr).append(":");
        }
        if (min < 10) {
            sb.append("0").append(min);
        } else {
            sb.append("").append(min);
        }
        return sb.toString();
    }

    static final int UNDER = 1, PAR = 2, OVER = 3;
    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
}
