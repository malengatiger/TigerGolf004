package com.boha.malengagolf.admin.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.malengagolf.library.data.AppUserDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;

/**
 * Created by aubreyM on 2014/05/22.
 */
public class GroupListActivty extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appuser_groups);
        ctx = getApplicationContext();
        appUser = SharedUtil.getAppUser(ctx);
        groupListFragment = (GroupListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        groupListFragment.setGolfGroups(appUser.getGolfGroupList());
    }

    AppUserDTO appUser;
    Context ctx;
    private void getGroups() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_APPUSER_GROUPS);
        w.setAppUserID(appUser.getAppUserID());

        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
                            return;
                        }
                        appUser.setGolfGroupList(response.getGolfGroups());
                        SharedUtil.saveAppUser(ctx,appUser);
                        groupListFragment.setGolfGroups(response.getGolfGroups());
                    }
                });
            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.socket_closed));
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }


        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, getApplicationContext(),
//                new BaseVolley.BohaVolleyListener() {
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        setRefreshActionButtonState(false);
//                        if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
//                            return;
//                        }
//                        appUser.setGolfGroupList(response.getGolfGroups());
//                        SharedUtil.saveAppUser(ctx,appUser);
//                        groupListFragment.setGolfGroups(response.getGolfGroups());
//                    }
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        setRefreshActionButtonState(false);
//                        ErrorUtil.showServerCommsError(ctx);
//                    }
//                }
//        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups, menu);
        mMenu = menu;
        groupListFragment.setGolfGroups(appUser.getGolfGroupList());
        getGroups();
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_help);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.menu_help) {
            ToastUtil.toast(getApplicationContext(), "Feature under construction");
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    Menu mMenu;
    GroupListFragment groupListFragment;
}