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
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdministratorAdapter extends ArrayAdapter<AdministratorDTO> {
    public interface AdministrationAdapterListener {
        public void onCameraRequested(AdministratorDTO p, int position);
        public void onEditRequested(AdministratorDTO p);
        public void onMessageRequested(AdministratorDTO p);
        public void onInvitationRequested(AdministratorDTO p);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<AdministratorDTO> mList;
    private Context ctx;
    private AdministrationAdapterListener listener;

    public AdministratorAdapter(Context context, int textViewResourceId,
                                List<AdministratorDTO> list,
                                AdministrationAdapterListener listener) {
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
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.PSN_txtNum);
            v.txtEmail = (TextView) convertView
                    .findViewById(R.id.PSN_txtEmail);
            v.txtCell = (TextView) convertView
                    .findViewById(R.id.PSN_txtCell);

            v.txtCountGreen = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterGreen);
            v.txtCountBlack = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterBlack);
            v.txtCountRed = (TextView) convertView
                    .findViewById(R.id.PSN_txtCounterRed);

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
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


        final AdministratorDTO p = mList.get(position);
        v.txtName.setText(p.getFirstName() + " "
                + p.getLastName());
        v.txtEmail.setText(p.getEmail());
        if (position < 9) {
            v.txtNumber.setText("0" + (position + 1));
        } else {
            v.txtNumber.setText("" + (position + 1));
        }

        v.txtCell.setText(p.getCellphone());
        v.txtCountRed.setVisibility(View.GONE);
        v.txtCountBlack.setVisibility(View.VISIBLE);
        v.txtCountGreen.setVisibility(View.GONE);

        Picasso.with(ctx)
                .load(p.getImageURL())
                .error(R.drawable.boy)
                .into(v.imagex);
        //
        v.imgCamera.setOnClickListener(new View.OnClickListener() {
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

        v.imagex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraRequested(p, position);
            }
        });

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
