package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.PhotoUploadDTO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by aubreymalabie on 4/2/16.
 */
public class PhotoAdapter  extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

    public interface PictureListener {
        public void onPictureClicked(PhotoUploadDTO photoUpload,int position);
    }
    private PictureListener listener;
    private List<PhotoUploadDTO> photoList;
    private Context ctx;
    private int imageType;
    public static final int THUMB = 1, FULL_IMAGE = 2;

    public PhotoAdapter(List<PhotoUploadDTO> photos,
                        int imageType,
                        Context context, PictureListener listener) {
        this.photoList = photos;
        this.ctx = context;
        this.imageType = imageType;
        this.listener = listener;
    }
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (imageType) {
            case FULL_IMAGE:
                return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false));
            case THUMB:
                return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_thumb, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {

        final PhotoUploadDTO p = photoList.get(position);
        if (imageType == THUMB) {
            holder.date.setText(sdf1.format(new Date(p.getDateTaken())));
        } else {
            holder.date.setText(sdf2.format(new Date(p.getDateTaken())));
        }
        holder.number.setText("" + (position + 1));
        if (p.getPlayerID() != null) {
            holder.caption.setText(p.getPlayerName());
        } else {
            if (p.getAdministratorID() != null) {
                holder.caption.setText(p.getAdminName());
            } else {
                if (p.getScorerID() != null) {
                    holder.caption.setText(p.getScorerName());
                } else {
                    if (p.getGolfGroupID()  != null) {
                        holder.caption.setText(p.getGolfGroupName());
                    }
                }
            }
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPictureClicked(p,position);
            }
        });
        Picasso.with(ctx)
                .load(p.getUrl())
                .placeholder(ContextCompat.getDrawable(ctx,R.drawable.boy))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView caption, date,number;
        protected int position;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.PHOTO_image);
            caption = (TextView) itemView.findViewById(R.id.PHOTO_caption);
            number = (TextView) itemView.findViewById(R.id.PHOTO_numberAmber);

            date = (TextView) itemView.findViewById(R.id.PHOTO_date);
        }

    }

    static final String LOG = PhotoAdapter.class.getSimpleName();
    static final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH;mm");
    static final SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy HH;mm");
}
