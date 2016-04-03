package com.boha.malengagolf.admin.com.boha.malengagolf.packs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.data.ParentDTO;
import com.boha.malengagolf.library.util.Statics;

import java.util.List;

public class ParentAdapter extends ArrayAdapter<ParentDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ParentDTO> mList;
    private Context ctx;
    private int golfGroupID;

    private ImageLoader imageLoader;

    public ParentAdapter(Context context, int textViewResourceId,
                         List<ParentDTO> list, ImageLoader imageLoader, int golfGroupID) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.golfGroupID = golfGroupID;
        ctx = context;
        this.imageLoader = imageLoader;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName;
        TextView txtEmail, txtCell;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.txtName = (TextView) convertView
                    .findViewById(R.id.PSN_txtName);
            viewHolderItem.txtEmail = (TextView) convertView
                    .findViewById(R.id.PSN_txtEmail);
            viewHolderItem.txtCell = (TextView) convertView
                    .findViewById(R.id.PSN_txtCell);
            viewHolderItem.image = (ImageView) convertView
                    .findViewById(R.id.PSN_imagex);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }


        ParentDTO p = mList.get(position);
        viewHolderItem.txtName.setText(p.getFirstName() + " "
                + p.getLastName());
        viewHolderItem.txtEmail.setText(p.getEmail());
        viewHolderItem.txtCell.setText(p.getCellphone());
        //image
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("golfgroup")
                .append(golfGroupID).append("/parent/");
        sb.append("t");
        sb.append(p.getParentID()).append(".jpg");

        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

}
