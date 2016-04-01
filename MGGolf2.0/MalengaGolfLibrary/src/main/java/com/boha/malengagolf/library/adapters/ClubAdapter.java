package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.ClubDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ClubAdapter extends ArrayAdapter<ClubDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ClubDTO> mList;
    private List<Drawable> bmdList;
    private Context ctx;

    public ClubAdapter(Context context, int textViewResourceId,
                       List<ClubDTO> list, List<Drawable> bmdList) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.bmdList = bmdList;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtProvince, txtClubName;
        TextView txtDistance;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtProvince = (TextView) convertView
                    .findViewById(R.id.CLUB_txtProvince);
            item.txtDistance = (TextView) convertView
                    .findViewById(R.id.CLUB_txtDistance);
            item.txtClubName = (TextView) convertView
                    .findViewById(R.id.CLUB_txtName);
            item.image = (ImageView) convertView
                   .findViewById(R.id.CLUB_image);
            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        ClubDTO p = mList.get(position);
        item.txtProvince.setText(p.getProvinceName());
        item.txtClubName.setText(p.getClubName());
        if (p.getDistance() == 0) {
            item.txtDistance.setTextColor(ctx.getResources().getColor(R.color.grey));
        } else {
            item.txtDistance.setTextColor(ctx.getResources().getColor(R.color.blue));
        }
        item.txtDistance.setText(df.format(p.getDistance()));
        item.image.setImageDrawable(bmdList.get(position));

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

    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat y = new SimpleDateFormat("dd MMMM yyyy", x);
    static final DecimalFormat df = new DecimalFormat("###,###,##0.0");
}
