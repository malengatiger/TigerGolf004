package com.boha.malengagolf.library.adapters;

import android.content.Context;
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
import com.boha.malengagolf.library.data.ImportPlayerDTO;

import java.util.List;

public class ImportPlayerAdapter extends ArrayAdapter<ImportPlayerDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ImportPlayerDTO> mList;
    private Context ctx;


    public ImportPlayerAdapter(Context context, int textViewResourceId,
                               List<ImportPlayerDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;


    static class ViewHolderItem {
        TextView txtName, txtEmail, txtNumber;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem vhi;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            vhi = new ViewHolderItem();
            vhi.txtName = (TextView) convertView
                    .findViewById(R.id.IMPI_txtName);

            vhi.txtEmail = (TextView) convertView
                    .findViewById(R.id.IMPI_txtEmail);
            vhi.txtNumber = (TextView) convertView
                    .findViewById(R.id.IMPI_txtNumber);
            vhi.checkBox = (CheckBox) convertView
                    .findViewById(R.id.IMPI_checkBox);

            convertView.setTag(vhi);
        } else {
            vhi = (ViewHolderItem) convertView.getTag();
        }

        final ImportPlayerDTO p = mList.get(position);
        vhi.txtName.setText(p.getFirstName() + " " + p.getLastName());
        vhi.txtEmail.setText(p.getEmail());
        if (position < 9) {
            vhi.txtNumber.setText("0" + (position + 1));
        } else {
            vhi.txtNumber.setText("" + (position + 1));
        }
        vhi.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    p.setSelected(true);
                } else {
                    p.setSelected(false);
                }
            }
        });
        if (p.isSelected()) {
            vhi.checkBox.setChecked(true);
        } else {
            vhi.checkBox.setChecked(false);
        }

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
