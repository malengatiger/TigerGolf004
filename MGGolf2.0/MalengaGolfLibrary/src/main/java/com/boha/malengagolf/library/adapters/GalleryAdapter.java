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

import com.android.volley.toolbox.ImageLoader;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.boha.malengagolf.library.util.GalleryRow;
import com.boha.malengagolf.library.util.Statics;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aubreyM on 2014/04/22.
 */
public class GalleryAdapter extends ArrayAdapter<GalleryRow> {
    List<GalleryRow> galleryRows;
    View view;
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private final Context ctx;
    private final ImageLoader imageLoader;
    public GalleryAdapter(Context context, int resource, List<GalleryRow> objects, ImageLoader imageLoader) {
        super(context, resource, objects);
        mLayoutRes = resource;
        this.imageLoader = imageLoader;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        galleryRows = objects;
        ctx = context;
    }

    static class Holder {
        ImageView image;
        TextView txtDetail;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            holder = new Holder();
            holder.image = (ImageView)convertView.findViewById(R.id.grid_item_image);
            holder.txtDetail = (TextView)convertView.findViewById(R.id.grid_item_label);
            convertView.setTag(holder);

    } else {
        holder = (Holder) convertView.getTag();
    }

        GalleryRow galleryRow = galleryRows.get(position);
        if (galleryRow.getTournament() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append(PhotoUploadDTO.GOLF_GROUP_PREFIX)
                    .append(galleryRow.getGolfGroup().getGolfGroupID()).append("/");
            sb.append(PhotoUploadDTO.TOURNAMENT_PREFIX).append(galleryRow.getTournament().getTournamentID())
                    .append("/");
            sb.append(PhotoUploadDTO.THUMB_PREFIX).append(galleryRow.getTournament()
                    .getTournamentID()).append("/");
            sb.append(galleryRow.getFileName());
            Picasso.with(ctx).load(sb.toString()).into(holder.image);

        } else {

            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append(PhotoUploadDTO.GOLF_GROUP_PREFIX)
                    .append(galleryRow.getGolfGroup().getGolfGroupID()).append("/");
            sb.append(PhotoUploadDTO.THUMB_PREFIX).append(galleryRow.getGolfGroup()
                    .getGolfGroupID()).append("/");
            sb.append(galleryRow.getFileName());
            Picasso.with(ctx).load(sb.toString()).into(holder.image);
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
}
