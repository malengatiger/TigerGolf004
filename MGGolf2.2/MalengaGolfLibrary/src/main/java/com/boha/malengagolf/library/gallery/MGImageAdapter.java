package com.boha.malengagolf.library.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boha.malengagolf.library.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MGImageAdapter extends BaseAdapter {
    private Context mContext;

    public interface ImageAdapterListener {
        public void onImageClick(String url);
    }

    private int newPos = 19;
    private List<String> imageURLs;
    private ImageAdapterListener imageAdapterListener;
    boolean showDate;
    private Pattern pattern = Pattern.compile("-?\\d+");

    public MGImageAdapter(List<String> imageURLs, boolean showDate, Context context,
                          ImageAdapterListener imageAdapterListener) {
        mContext = context;
        this.imageURLs = imageURLs;
        this.imageAdapterListener = imageAdapterListener;
        this.showDate = showDate;
    }


    @Override
    public int getCount() {
        return imageURLs == null ? 0 : imageURLs.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Holder h;
        final String url = imageURLs.get(position);


        if (convertView == null) {
            h = new Holder();
            view = View.inflate(mContext, R.layout.image_item, null);
            h.image = (ImageView) view.findViewById(R.id.SQ_image);
            h.txt = (TextView) view.findViewById(R.id.SQ_text);
            h.txtLayout = view.findViewById(R.id.SQ_layout);
            view.setTag(h);
        } else {
            view = convertView;
        }

        h = (Holder) view.getTag();
        if (showDate) {
            String sTime = "";
            Matcher m = pattern.matcher(url);
            while (m.find()) {
                sTime = m.group();
            }
            long time = Long.parseLong(sTime);
            h.txt.setText(sdf.format(new Date(time)));
        } else {
            h.txtLayout.setVisibility(View.GONE);
        }
        h.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapterListener.onImageClick(url);
            }
        });

        try {
            ImageLoader.getInstance().displayImage(url,h.image, options);
//            Picasso.with(mAppContext).load(url)
//                    .placeholder(mAppContext.getResources().getDrawable(R.drawable.ic_launcher))
//                    .into(h.image);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return view;
    }

    Random rand = new Random(System.currentTimeMillis());

    class Holder {
        ImageView image;
        TextView txt;
        View txtLayout;
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
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", loc);
}
