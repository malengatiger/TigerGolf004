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
import com.boha.malengagolf.library.data.GolfGroupDTO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GolfGroupAdapter extends ArrayAdapter<GolfGroupDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<GolfGroupDTO> mList;
    private Context ctx;

    public GolfGroupAdapter(Context context, int textViewResourceId,
                            List<GolfGroupDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtGroupName, txtGroupNumber;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtGroupName = (TextView) convertView
                    .findViewById(R.id.GRP_txtName);
            item.txtGroupNumber = (TextView) convertView
                    .findViewById(R.id.GRP_txtNumber);

            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        GolfGroupDTO p = mList.get(position);
        item.txtGroupName.setText(p.getGolfGroupName());
        item.txtGroupNumber.setText("" + p.getGolfGroupID());

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
