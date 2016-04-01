package com.boha.malengagolf.admin.com.boha.malengagolf.packs.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boha.malengagolf.admin.R;
import com.boha.malengagolf.library.AppInvitationActivity;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.ParentDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.fragments.AppInvitationFragment;
import com.boha.malengagolf.library.util.PersonInterface;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.Util;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class PersonEditDialog extends DialogFragment {

    public interface DialogListener {
        public void onRecordAdded(PersonInterface person);
        public void onRecordUpdated();
        public void onRecordDeleted();
    }
    public PersonEditDialog() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.person_edit, container);
        btnBirthDate = (Button) view.findViewById(R.id.ED_PSN_btnBirthDate);
        chkInvite = (CheckBox) view.findViewById(R.id.ED_PSN_chkInvite);
        eFirstName = (EditText) view.findViewById(R.id.ED_PSN_firstName);
        eLastName = (EditText) view.findViewById(R.id.ED_PSN_lastName);
        eMail = (EditText) view.findViewById(R.id.ED_PSN_email);
        eCellphone = (EditText) view.findViewById(R.id.ED_PSN_cellphone);
        txtGroupName = (TextView)view.findViewById(R.id.ED_PSN_groupName);
        txtPersonType = (TextView)view.findViewById(R.id.ED_PSN_personType);
        progressBar = (ProgressBar)view.findViewById(R.id.ED_PSN_progress);
        progressBar.setVisibility(View.GONE);

        radioFemale = (RadioButton)view.findViewById(R.id.ED_PSN_radioFemale);
        radioMale = (RadioButton)view.findViewById(R.id.ED_PSN_radioMale);
        final RadioGroup rg = (RadioGroup)view.findViewById(R.id.ED_PSN_radioGroup);
        rg.setVisibility(View.GONE);

        btnCancel = (Button)view.findViewById(R.id.ED_PSN_btnCancel);
        btnSave = (Button)view.findViewById(R.id.ED_PSN_btnSave);
        getDialog().setTitle(ctx.getResources().getString(R.string.golf_people));

        txtGroupName.setText(SharedUtil.getGolfGroup(ctx).getGolfGroupName());
        Log.e(LOG, "onCreateView --- personType: " + personType);
        switch (personType) {
            case PLAYER:
                txtPersonType.setText(ctx.getResources().getString(R.string.player));
                rg.setVisibility(View.VISIBLE);
                break;
            case PARENT:
                txtPersonType.setText(ctx.getResources().getString(R.string.parent));
                break;
            case SCORER:
                txtPersonType.setText(ctx.getResources().getString(R.string.scorer));
                break;
            case ADMIN:
                txtPersonType.setText(ctx.getResources().getString(R.string.admin));
                break;
        }
        if (action == ACTION_UPDATE) {
            fillForm();
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switch (action) {
                    case ACTION_ADD:
                        registerActor();
                        break;
                    case ACTION_UPDATE:
                        updateActor();
                        break;
                }
            }
        });
        if (personType != PLAYER) {
            btnBirthDate.setVisibility(View.GONE);
        }
        btnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int xYear, xMth, xDay;
                if (mYear == 0) {
                    xYear = calendar.get(Calendar.YEAR);
                    xMth = calendar.get(Calendar.MONTH);
                    xDay = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    xYear = mYear;
                    xMth = mMonth;
                    xDay = mDay;
                }
                dpBirthDate = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog,
                                                  int year, int month, int day) {

                                mYear = year;
                                mMonth = month;
                                mDay = day;
                                btnBirthDate.setText(Util.getLongerDate(mDay, mMonth,
                                        mYear));
                                calendar.set(Calendar.YEAR, mYear);
                                calendar.set(Calendar.MONTH, mMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, mDay);
                                calendar.set(Calendar.HOUR, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                birthDate = calendar.getTimeInMillis();
                            }


                        }, xYear, xMth, xDay, true
                );
                dpBirthDate.setVibrate(true);
                dpBirthDate.setYearRange(1940, 2036);
                Bundle args = new Bundle();
                args.putInt("year", mYear);
                args.putInt("month", mMonth);
                args.putInt("day", mDay);

                dpBirthDate.setArguments(args);
                dpBirthDate.show(fragmentManager, "diagx");

            }
        });
        animate(view);
        return view;
    }

    android.support.v4.app.FragmentManager fragmentManager;
    public  void setFragmentManager(FragmentManager x) {
        fragmentManager = x;
    }
    int mYear, mMonth, mDay;
    long birthDate;
    DatePickerDialog dpBirthDate;
    CheckBox chkInvite;
    Activity act;
    private void updateActor() {
        Log.e(LOG, "updateActor --- personType: " + personType);
        if (eFirstName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_firstname));
            return;
        }
        if (eLastName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_lastname));
            return;
        }
        if (eCellphone.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_cell));
            return;
        }
        if (eMail.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_email));
            return;
        }
        RequestDTO req = new RequestDTO();
        switch (personType) {
            case PLAYER:
                player.setFirstName(eFirstName.getText().toString());
                player.setLastName(eLastName.getText().toString());
                player.setCellphone(eCellphone.getText().toString());
                player.setEmail(eMail.getText().toString());
                if (birthDate > 0) {
                    player.setDateOfBirth(birthDate);
                }
                req.setRequestType(RequestDTO.UPDATE_PLAYER);
                req.setPlayer(player);
                break;
            case PARENT:
                parent.setFirstName(eFirstName.getText().toString());
                parent.setLastName(eLastName.getText().toString());
                parent.setCellphone(eCellphone.getText().toString());
                parent.setEmail(eMail.getText().toString());
                req.setRequestType(RequestDTO.UPDATE_PARENT);
                req.setParent(parent);
                break;
            case SCORER:
                scorer.setFirstName(eFirstName.getText().toString());
                scorer.setLastName(eLastName.getText().toString());
                scorer.setCellphone(eCellphone.getText().toString());
                scorer.setEmail(eMail.getText().toString());
                req.setRequestType(RequestDTO.UPDATE_SCORER);
                req.setScorer(scorer);
                break;
            case ADMIN:
                administrator.setFirstName(eFirstName.getText().toString());
                administrator.setLastName(eLastName.getText().toString());
                administrator.setCellphone(eCellphone.getText().toString());
                administrator.setEmail(eMail.getText().toString());
                req.setRequestType(RequestDTO.UPDATE_ADMINISTRATOR);
                req.setAdministrator(administrator);
                break;
        }
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (personType) {
                            case PLAYER:
                                diagListener.onRecordUpdated();
                                break;
                            case PARENT:
                                diagListener.onRecordUpdated();
                                break;
                            case SCORER:
                                diagListener.onRecordUpdated();
                                break;
                            case ADMIN:
                                diagListener.onRecordUpdated();
                                break;
                        }
                        if (chkInvite.isChecked()) {
                            startInvitation();
                        }
                        dismiss();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.error_server_comms));
                    }
                });

            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                switch (personType) {
//                    case PLAYER:
//                        diagListener.onRecordUpdated();
//                        break;
//                    case PARENT:
//                        diagListener.onRecordUpdated();
//                        break;
//                    case SCORER:
//                        diagListener.onRecordUpdated();
//                        break;
//                    case ADMIN:
//                        diagListener.onRecordUpdated();
//                        break;
//                }
//                if (chkInvite.isChecked()) {
//                    startInvitation();
//                }
//                dismiss();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//                ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });

    }
    private void startInvitation() {
        Intent i = new Intent(ctx, AppInvitationActivity.class);
        switch (personType) {
            case PLAYER:
                i.putExtra("email", player.getEmail());
                i.putExtra("pin", player.getPin());
                i.putExtra("member", player.getFullName());
                i.putExtra("type", AppInvitationFragment.PLAYER);
                break;
            case PARENT:
                i.putExtra("email", parent.getEmail());
                i.putExtra("pin", parent.getPin());
                i.putExtra("member", parent.getFirstName() + " " + parent.getLastName());
                i.putExtra("type", AppInvitationFragment.PLAYER);
                break;
            case SCORER:
                i.putExtra("email", scorer.getEmail());
                i.putExtra("pin", scorer.getPin());
                i.putExtra("member", scorer.getFullName());
                i.putExtra("type", AppInvitationFragment.SCORER);
                break;
            case ADMIN:
                i.putExtra("email", administrator.getEmail());
                i.putExtra("pin", administrator.getPin());
                i.putExtra("member", administrator.getFirstName() + " " + administrator.getLastName());
                i.putExtra("type", AppInvitationFragment.ADMIN);
                break;
        }
        startActivity(i);
    }
    private void registerActor() {
        Log.e(LOG, "registerActor --- personType: " + personType);
        if (eFirstName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_firstname));
            return;
        }
        if (eLastName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_lastname));
            return;
        }
        if (eCellphone.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_cell));
            return;
        }
        if (eMail.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_email));
            return;
        }
        RequestDTO req = new RequestDTO();
        req.setGolfGroupID(SharedUtil.getGolfGroup(ctx).getGolfGroupID());
        switch (personType) {
            case PLAYER:
                req.setRequestType(RequestDTO.ADD_PLAYER);
                player = new PlayerDTO();
                player.setFirstName(eFirstName.getText().toString());
                player.setLastName(eLastName.getText().toString());
                player.setEmail(eMail.getText().toString());
                player.setCellphone(eCellphone.getText().toString());
                if (birthDate > 0) {
                    player.setDateOfBirth(birthDate);
                }
                req.setPlayer(player);
                break;
            case PARENT:
                req.setRequestType(RequestDTO.ADD_PARENT);
                parent = new ParentDTO();
                parent.setFirstName(eFirstName.getText().toString());
                parent.setLastName(eLastName.getText().toString());
                parent.setEmail(eMail.getText().toString());
                parent.setCellphone(eCellphone.getText().toString());
                req.setParent(parent);
                break;
            case SCORER:
                req.setRequestType(RequestDTO.ADD_SCORER);
                scorer = new ScorerDTO();
                scorer.setFirstName(eFirstName.getText().toString());
                scorer.setLastName(eLastName.getText().toString());
                scorer.setEmail(eMail.getText().toString());
                scorer.setCellphone(eCellphone.getText().toString());
                req.setScorer(scorer);
                break;
            case ADMIN:
                req.setRequestType(RequestDTO.ADD_ADMINISTRATOR);
                administrator = new AdministratorDTO();
                administrator.setFirstName(eFirstName.getText().toString());
                administrator.setLastName(eLastName.getText().toString());
                administrator.setEmail(eMail.getText().toString());
                administrator.setCellphone(eCellphone.getText().toString());
                administrator.setGolfGroupID(SharedUtil.getGolfGroup(ctx).getGolfGroupID());
                req.setAdministrator(administrator);
                break;
        }

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (personType) {
                            case PLAYER:
                                diagListener.onRecordAdded(response.getPlayers().get(0));
                                break;
                            case PARENT:
                                diagListener.onRecordAdded(response.getParents().get(0));
                                break;
                            case SCORER:
                                diagListener.onRecordAdded(response.getScorers().get(0));
                                break;
                            case ADMIN:
                                diagListener.onRecordAdded(response.getAdministrator());
                                break;
                        }
                        if (chkInvite.isChecked()) {
                            startInvitation();
                        }
                        dismiss();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.error_server_comms));
                    }
                });

            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                switch (personType) {
//                    case PLAYER:
//                        diagListener.onRecordAdded(response.getPlayers().get(0));
//                        break;
//                    case PARENT:
//                        diagListener.onRecordAdded(response.getParents().get(0));
//                        break;
//                    case SCORER:
//                        diagListener.onRecordAdded(response.getScorers().get(0));
//                        break;
//                    case ADMIN:
//                        diagListener.onRecordAdded(response.getAdministrator());
//                        break;
//                }
//                if (chkInvite.isChecked()) {
//                    startInvitation();
//                }
//                dismiss();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//                ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });
    }

    private void animate(View v) {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.grow_fade_in_center);
        a.setDuration(1500);
        v.startAnimation(a);
    }
    EditText eFirstName, eLastName, eCellphone, eMail;
    TextView txtGroupName, txtPersonType;
    Button btnCancel,btnSave;
    DialogListener diagListener;
    RadioButton radioMale, radioFemale;
    View view;
    Context ctx;
    int personType, action;
    PersonInterface person;
    ProgressBar progressBar;
    PlayerDTO player;
    ParentDTO parent;
    ScorerDTO scorer;
    Button btnBirthDate;
    AdministratorDTO administrator;

    public void setPerson(PersonInterface person) {
        this.person = person;


    }
    static final String LOG = "PersonEditDialog";
    private void fillForm() {
        Log.e(LOG, "fillForm --- personType: " + personType);
        switch (personType) {
            case SCORER:
                scorer = (ScorerDTO)person;
                eFirstName.setText(scorer.getFirstName());
                eLastName.setText(scorer.getLastName());
                eCellphone.setText(scorer.getCellphone());
                eMail.setText(scorer.getEmail());

                break;
            case PLAYER:
                player = (PlayerDTO)person;
                eFirstName.setText(player.getFirstName());
                eLastName.setText(player.getLastName());
                eCellphone.setText(player.getCellphone());
                eMail.setText(player.getEmail());
                if (player.getGender() == 1) {
                    radioMale.setChecked(true);
                }
                if (player.getGender() == 2) {
                    radioFemale.setChecked(true);
                }
                if (player.getDateOfBirth() > 0) {
                    birthDate = player.getDateOfBirth();
                    btnBirthDate.setText(Util.getLongerDate(new Date(player.getDateOfBirth())));
                }
                break;
            case PARENT:
                parent = (ParentDTO)person;
                eFirstName.setText(parent.getFirstName());
                eLastName.setText(parent.getLastName());
                eCellphone.setText(parent.getCellphone());
                eMail.setText(parent.getEmail());

                break;

            case ADMIN:
                administrator = (AdministratorDTO)person;
                eFirstName.setText(administrator.getFirstName());
                eLastName.setText(administrator.getLastName());
                eCellphone.setText(administrator.getCellphone());
                eMail.setText(administrator.getEmail());

                break;
        }
    }

    public static final int ADMIN = 1, PLAYER = 2, PARENT = 3, SCORER = 4, ACTION_ADD = 33, ACTION_UPDATE = 34;

    public void setPersonType(int personType) {
        this.personType = personType;
        Log.e(LOG, "setPersonType --- personType: " + personType);
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }



    public void setActivity(Activity act) {
        this.act = act;
    }

    public DialogListener getDiagListener() {
        return diagListener;
    }

    public void setDiagListener(DialogListener diagListener) {
        this.diagListener = diagListener;
    }
}
