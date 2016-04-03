package com.boha.malengagolf.library.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.PlayerDTO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;



public class PlayerMessageAdapter extends ArrayAdapter<PlayerDTO> {

    public interface PlayerMessageListener {
        public void onPlayerChecked(PlayerDTO player, int index);
    }
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<PlayerDTO> mList;
    private Context ctx;
    private PlayerMessageListener listener;
    public static final int TEXT_MESSAGE = 1, EMAIL_MESSAGE = 2;
    int type;

    public PlayerMessageAdapter(Context context, int textViewResourceId,
                         List<PlayerDTO> list, int type, PlayerMessageListener listener) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        this.listener = listener;
        mList = list;
        this.type = type;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName;
        CheckBox chkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtName = (TextView) convertView
                    .findViewById(R.id.MSGI_txtName);
            item.chkBox = (CheckBox) convertView
                    .findViewById(R.id.MSGI_checkBox);

            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        final PlayerDTO p = mList.get(position);
        item.txtName.setText(p.getFullName());
        item.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                p.setSelected(b);
                Log.w("PlayerMessageAdapter", "onCheckedChanged: " + b);
                listener.onPlayerChecked(p,position);
            }
        });
        if (p.isSelected()) {
            item.chkBox.setChecked(true);
            Log.w("PlayerMessageAdapter", "Player selected: " + p.getFullName());
        } else {
            item.chkBox.setChecked(false);
            Log.e("PlayerMessageAdapter", "Player de-selected: " + p.getFullName());
        }


        checkType(p);
        animateView(convertView);
        return (convertView);
    }

    private void checkType (PlayerDTO p) {
        switch (type) {
            case EMAIL_MESSAGE:
                if (p.getEmail() == null) {
                    p.setSelected(false);
                }
                break;
            case TEXT_MESSAGE:
                if (p.getCellphone() == null) {
                    p.setSelected(false);
                }
                break;
        }
    }
    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    static final Locale cc = Locale.getDefault();
    static  final SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd MMMM yyyy", cc);
}

