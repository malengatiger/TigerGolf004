package com.boha.malengagolf.player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;

/**
 * Created by aubreyM on 2014/05/22.
 */
public class GroupListActivty extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playergroups);
        groupListFragment = (GroupListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
    }

    private void getGroups() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_PLAYER_GROUPS);
        w.setPlayerID(SharedUtil.getPlayer(getApplicationContext()).getPlayerID());

        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(getApplicationContext(),Statics.ADMIN_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, getApplicationContext(),
                new BaseVolley.BohaVolleyListener() {
                    @Override
                    public void onResponseReceived(ResponseDTO response) {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
                            return;
                        }
                        groupListFragment.setGolfGroups(response.getGolfGroups());
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                        setRefreshActionButtonState(false);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups, menu);
        mMenu = menu;
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