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
import com.boha.malengagolf.library.base.CityDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class CityAdapter extends ArrayAdapter<CityDTO> {
    List<CityDTO> cityList;
    View view;
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private final Context ctx;

    public CityAdapter(Context context, int resource, List<CityDTO> objects) {
        super(context, resource, objects);
        mLayoutRes = resource;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cityList = objects;
        ctx = context;
    }

    static class Holder {
        ImageView image;
        TextView txtCityName, txtLat, txtLng, txtNum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.DD_img);
            holder.txtCityName = (TextView) convertView.findViewById(R.id.DD_cityName);
            holder.txtLat = (TextView) convertView.findViewById(R.id.DD_latitude);
            holder.txtLng = (TextView) convertView.findViewById(R.id.DD_longitude);
            holder.txtNum = (TextView) convertView.findViewById(R.id.DD_number);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        CityDTO p = cityList.get(position);
        holder.txtNum.setText("" + (position + 1));
        if (p.getLatitude() == 0) {
            holder.txtCityName.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLat.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLng.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLat.setText("00.000000");
            holder.txtLng.setText("00.000000");

        } else {
            holder.txtCityName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.txtLat.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLng.setTextColor(ctx.getResources().getColor(R.color.grey));
            holder.txtLat.setText("" + p.getLatitude());
            holder.txtLng.setText("" + p.getLongitude());
        }
        holder.txtCityName.setText(p.getCityName());

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
