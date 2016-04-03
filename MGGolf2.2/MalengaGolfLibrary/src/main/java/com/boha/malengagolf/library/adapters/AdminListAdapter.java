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
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aubreymalabie on 4/2/16.
 */
public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.AdminViewHolder>{

    public interface AdministrationAdapterListener {
        public void onCameraRequested(AdministratorDTO p, int position);
        public void onEditRequested(AdministratorDTO p);
        public void onMessageRequested(AdministratorDTO p);
        public void onInvitationRequested(AdministratorDTO p);
    }
   
    private List<AdministratorDTO> mList;
    private AdministrationAdapterListener listener;
    private Context ctx;

    public AdminListAdapter(List<AdministratorDTO> list,
                            Context context, AdministrationAdapterListener listener) {
        this.mList = list;
        this.ctx = context;
        this.listener = listener;
    }
    @Override
    public AdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new AdminViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false));

    }

    @Override
    public void onBindViewHolder(AdminViewHolder v, final int position) {

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

        if (p.getImageURL() != null && !p.getImageURL().isEmpty()) {
            Picasso.with(ctx)
                    .load(p.getImageURL())
                    .placeholder(ContextCompat.getDrawable(ctx, R.drawable.boy))
                    .into(v.imagex);
        } else {
            Picasso.with(ctx)
                    .load(R.drawable.boy)
                    .into(v.imagex);
        }
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

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        TextView txtEmail, txtCell, txtCountRed, txtCountBlack, txtCountGreen;
        CircleImageView imagex;
        ImageView imgHistory, imgCamera, imgInvite, imgMessage, imgEdit;


        public AdminViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView
                    .findViewById(R.id.PSN_txtName);
            txtNumber = (TextView) itemView
                    .findViewById(R.id.PSN_txtNum);
            txtEmail = (TextView) itemView
                    .findViewById(R.id.PSN_txtEmail);
            txtCell = (TextView) itemView
                    .findViewById(R.id.PSN_txtCell);

            txtCountGreen = (TextView) itemView
                    .findViewById(R.id.PSN_txtCounterGreen);
            txtCountBlack = (TextView) itemView
                    .findViewById(R.id.PSN_txtCounterBlack);
            txtCountRed = (TextView) itemView
                    .findViewById(R.id.PSN_txtCounterRed);

            imagex = (CircleImageView) itemView
                    .findViewById(R.id.PSN_imagex);
            imgHistory = (ImageView) itemView
                    .findViewById(R.id.PA_imgPlayerHistory);
            imgCamera = (ImageView) itemView
                    .findViewById(R.id.PA_imgCamera);
            imgEdit = (ImageView) itemView
                    .findViewById(R.id.PA_imgEdit);
            imgInvite = (ImageView) itemView
                    .findViewById(R.id.PA_imgInvite);
            imgMessage = (ImageView) itemView
                    .findViewById(R.id.PA_imgMessage);
            imgHistory.setVisibility(View.GONE);
        }

    }

    static final String LOG = AdminListAdapter.class.getSimpleName();
    static final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH;mm");
    static final SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy HH;mm");
}
