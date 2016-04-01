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
import com.boha.malengagolf.library.data.VideoClipDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class VideoClipAdapter extends ArrayAdapter<VideoClipDTO> {
    List<VideoClipDTO> videoClips;
    View view;
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private final Context ctx;
    public VideoClipAdapter(Context context, int resource, List<VideoClipDTO> objects) {
        super(context, resource, objects);
        mLayoutRes = resource;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        videoClips = objects;
        ctx = context;
    }

    static class Holder {
        ImageView image, tick;
        TextView txtTournament, txtVideoDate, txtSize;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new Holder();
            holder.image = (ImageView)convertView.findViewById(R.id.VI_image);
            holder.tick = (ImageView)convertView.findViewById(R.id.VI_tick);
            holder.txtTournament = (TextView)convertView.findViewById(R.id.VI_tournament);
            holder.txtVideoDate = (TextView)convertView.findViewById(R.id.VI_date);
            holder.txtSize = (TextView)convertView.findViewById(R.id.VI_size);
            convertView.setTag(holder);

    } else {
        holder = (Holder) convertView.getTag();
    }

        VideoClipDTO p = videoClips.get(position);
        if (p.getTournamentID() == 0) {
            holder.txtTournament.setText(p.getGolfGroupName());
        } else {
            holder.txtTournament.setText(p.getTournamentName());
        }
        holder.txtVideoDate.setText(sdf.format(new Date(p.getVideoDate())));
        holder.txtSize.setText(formatSize(p.getLength()) + " MB");
        if (p.getDateUploaded() > 0) {
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            holder.tick.setVisibility(View.GONE);
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
    private String formatSize(long size) {
        Double d = Double.valueOf(size) / Double.valueOf(1024) / Double.valueOf(1024);
        return df.format(d.doubleValue());
    }
    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###,###.00");
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMMM yyyy  HH:mm");
}
