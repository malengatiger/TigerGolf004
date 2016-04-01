package com.boha.ScraperApp.scraper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boha.ScraperApp.R;
import com.boha.malengagolf.library.data.ClubDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class ClubAdapter extends ArrayAdapter<ClubDTO> {
    List<ClubDTO> clubList;
    View view;
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private final Context ctx;

    public ClubAdapter(Context context, int resource, List<ClubDTO> objects) {
        super(context, resource, objects);
        mLayoutRes = resource;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clubList = objects;
        ctx = context;
    }

    static class Holder {
        ImageView image;
        TextView txtClubName, txtCount, txtNumber, txtLat, txtLng;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.CC_img);
            holder.txtClubName = (TextView) convertView.findViewById(R.id.CC_clubName);
            holder.txtLat = (TextView) convertView.findViewById(R.id.CC_latitude);
            holder.txtLng = (TextView) convertView.findViewById(R.id.CC_longitude);
            holder.txtNumber = (TextView) convertView.findViewById(R.id.CC_number);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        ClubDTO p = clubList.get(position);
        holder.txtNumber.setText("" + (position + 1));

        holder.txtClubName.setText(p.getClubName());
        if (p.getLatitude() == 0) {
            holder.txtLat.setText("00.0000000");
            holder.txtLng.setText("00.0000000");
            holder.txtLat.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLng.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else {
            holder.txtLat.setText("" + p.getLatitude());
            holder.txtLng.setText("" + p.getLongitude());
            holder.txtLat.setTextColor(ctx.getResources().getColor(R.color.green));
            holder.txtLng.setTextColor(ctx.getResources().getColor(R.color.blue));
        }
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.makeInAnimation(ctx,true);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    private String formatSize(long size) {
        Double d = Double.valueOf(size) / Double.valueOf(1024) / Double.valueOf(1024);
        return df.format(d.doubleValue());
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###,###.00");
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMMM yyyy  HH:mm");
}
