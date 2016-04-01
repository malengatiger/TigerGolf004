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

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ScorerAdapter extends ArrayAdapter<ScorerDTO> {
    public interface ScorerAdapterListener {
        public void onCameraRequested(ScorerDTO p, int index);
        public void onEditRequested(ScorerDTO p);
        public void onMessageRequested(ScorerDTO p);
        public void onInvitationRequested(ScorerDTO p);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ScorerDTO> mList;
    private Context ctx;
    private ScorerAdapterListener listener;

    public ScorerAdapter(Context context, int textViewResourceId,
                         List<ScorerDTO> list,
                         ScorerAdapterListener listener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.listener = listener;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;
    static class ViewHolderItem {
        TextView txtName, txtNumber;
        TextView txtEmail, txtCell, txtCountRed, txtCountBlack, txtCountGreen;
        ImageView imagex;
        ImageView imgHistory, imgCamera, imgInvite, imgMessage, imgEdit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.PSN_txtName);
            v.txtEmail = (TextView) convertView
                    .findViewById(R.id.PSN_txtEmail);
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.PSN_txtNum);
            v.txtCell = (TextView) convertView
                    .findViewById(R.id.PSN_txtCell);

            v.imagex = (ImageView) convertView
                    .findViewById(R.id.PSN_imagex);
            v.imgHistory = (ImageView) convertView
                    .findViewById(R.id.PA_imgPlayerHistory);
            v.imgCamera = (ImageView) convertView
                    .findViewById(R.id.PA_imgCamera);
            v.imgEdit = (ImageView) convertView
                    .findViewById(R.id.PA_imgEdit);
            v.imgInvite = (ImageView) convertView
                    .findViewById(R.id.PA_imgInvite);
            v.imgMessage = (ImageView) convertView
                    .findViewById(R.id.PA_imgMessage);
            v.imgHistory.setVisibility(View.GONE);
            v.txtCountGreen = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterGreen);
            v.txtCountBlack = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterBlack);
            v.txtCountRed = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterRed);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


        final ScorerDTO p = mList.get(position);
        v.txtName.setText(p.getFirstName() + " "
                + p.getLastName());
        v.txtEmail.setText(p.getEmail());
        v.txtCell.setText(p.getCellphone());
        v.txtCountRed.setVisibility(View.VISIBLE);
        v.txtCountBlack.setVisibility(View.GONE);
        v.txtCountGreen.setVisibility(View.GONE);


       // v.image.setDefaultImageResId(R.drawable.boy);
       // v.image.setImageUrl(sb.toString(), imageLoader);
        //
        v.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraRequested(p, position);
            }
        });
        v.imagex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraRequested(p, position);
            }
        });
        v.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditRequested(p);
            }
        });

        v.imgInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onInvitationRequested(p);
            }
        });
        v.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRequested(p);
            }
        });
        if (position < 9) {
            v.txtNumber.setText("0" + (position + 1));
        } else {
            v.txtNumber.setText("" + (position + 1));
        }

        Picasso.with(ctx)
                .load(p.getImageURL())
                .error(R.drawable.boy)
                .into(v.imagex);

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
    static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_action_add_person) // resource or drawable
            .showImageForEmptyUri(R.drawable.boy) // resource or drawable
            .showImageOnFail(R.drawable.boy) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .cacheInMemory(true) // default
            .cacheOnDisk(true) // default
            .build();
}
