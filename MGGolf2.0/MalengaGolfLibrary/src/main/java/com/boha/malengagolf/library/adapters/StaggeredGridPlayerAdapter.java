package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.LeaderBoardDTO;
import com.boha.malengagolf.library.fragments.StaggeredListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by aubreyM on 2014/06/18.
 */



public class StaggeredGridPlayerAdapter extends BaseAdapter {
    private Context mContext;



    private int newPos = 19;
    private List<LeaderBoardDTO> leaderBoardList;
    private StaggeredListener imageAdapterListener;

    public StaggeredGridPlayerAdapter(List<LeaderBoardDTO> leaderBoardList, Context context,
                                StaggeredListener listener) {
        mContext = context;
        this.leaderBoardList = leaderBoardList;
        this.imageAdapterListener = listener;
        mContext = context;
    }


    @Override
    public int getCount() {
        return leaderBoardList == null ? 0 : leaderBoardList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null; Holder h;
        final LeaderBoardDTO lb = leaderBoardList.get(position);

        if (convertView == null) {
             h = new Holder();
            view = View.inflate(mContext, R.layout.tourn_player_grid_item, null);
            h.image1 = (ImageView) view.findViewById(R.id.TPGG_image);
            h.image2 = (ImageView) view.findViewById(R.id.TPGG_image2);
            h.tv_player = (TextView) view.findViewById(R.id.TPGG_txtName);
            h.tvLastHole = (TextView) view.findViewById(R.id.TPGG_lastHole);
            h.tvPar = (TextView) view.findViewById(R.id.TPGG_par);
            h.parLabel = (TextView) view.findViewById(R.id.TPGG_parLabel);
            h.lastHoleLabel = (TextView) view.findViewById(R.id.TPGG_lastHoleLabel);
            h.tourPosition = (TextView) view.findViewById(R.id.TPGG_txtPosition);
            view.setTag(h);
        } else {
            view = convertView;
        }

        h = (Holder) view.getTag();
        h.tv_player.setText(lb.getPlayer().getFullName());
        h.tv_player.setVisibility(View.VISIBLE);

        if (lb.getLastHole() == 0) {
            h.tvLastHole.setVisibility(View.GONE);
            h.lastHoleLabel.setVisibility(View.GONE);
        } else {
            h.tvLastHole.setText("" + lb.getLastHole());
            h.tvLastHole.setVisibility(View.VISIBLE);
            h.lastHoleLabel.setVisibility(View.VISIBLE);
        }

        if (lb.getParStatus() == 0) {
            h.tvPar.setTextColor(mContext.getResources().getColor(R.color.black));
            h.tvPar.setText(mContext.getResources().getString(R.string.even));
        }
        if (lb.getParStatus() > 0) {
            h.tvPar.setTextColor(mContext.getResources().getColor(R.color.opaque_red));
            h.tvPar.setText("" + (lb.getParStatus() * -1));
        }
        if (lb.getParStatus() < 0) {
            h.tvPar.setTextColor(mContext.getResources().getColor(R.color.blue));
            h.tvPar.setText("+" + (lb.getParStatus() * -1));
        }
        if (lb.getParStatus() == LeaderBoardDTO.NO_PAR_STATUS) {
            h.tvPar.setVisibility(View.GONE);
            h.parLabel.setVisibility(View.GONE);
        }

        h.image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapterListener.onPlayerTapped(lb, position);
            }
        });
        h.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapterListener.onPlayerTapped(lb, position);
            }
        });

        if (lb.getPosition() == 0) {
            h.tourPosition.setVisibility(View.GONE);
        } else {
            if (lb.isTied()) {
                h.tourPosition.setText("T" + lb.getPosition());
            } else {
                h.tourPosition.setText("" + lb.getPosition());
            }
        }

        try {
            int rem = position % 2;
            if (rem == 0) {
                h.image1.setVisibility(View.VISIBLE);
                h.image2.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(lb.getImageURL(mContext),h.image1, options);
//                Picasso.with(mContext).load(lb.getImageURL(mContext))
//                        .placeholder(mContext.getResources().getDrawable(R.drawable.boy))
//                        .fit()
//                        .centerCrop()
//                        .into(h.image1);
            } else {
                h.image2.setVisibility(View.VISIBLE);
                h.image1.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(lb.getImageURL(mContext),h.image2, options);
//                Picasso.with(mContext).load(lb.getImageURL(mContext))
//                        .placeholder(mContext.getResources().getDrawable(R.drawable.boy))
//                        .fit()
//                        .centerCrop()
//                        .into(h.image2);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return view;
    }

    Random random = new Random(System.currentTimeMillis());
    class Holder {
        ImageView image1, image2;
        TextView tourPosition, tv_player, tvLastHole, tvPar, parLabel, lastHoleLabel;
    }

    private String getDate(String url) {
        try {
            int index = url.lastIndexOf("/");
            int end = url.lastIndexOf(".");
            String dateString = url.substring(index + 2, end);
            long date = Long.parseLong(dateString);
            return sdf.format(new Date(date));
        } catch (NumberFormatException e) {
        }
        return "";
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", loc);
    static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_action_add_person) // resource or drawable
            .showImageForEmptyUri(R.drawable.boy) // resource or drawable
            .showImageOnFail(R.drawable.boy) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .cacheInMemory(false) // default
            .cacheOnDisk(true) // default
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .build();
}
