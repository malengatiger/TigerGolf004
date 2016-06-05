package com.aftarobot.aftarobotmigrator.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aftarobot.aftarobotmigrator.R;
import com.aftarobot.aftarobotmigrator.adapters.RouteAdapter;
import com.aftarobot.aftarobotmigrator.newdata.CityDTO;
import com.aftarobot.aftarobotmigrator.newdata.RouteDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    static final String TAG = RouteActivity.class.getSimpleName();
    ProgressBar progressBar;
    RecyclerView recycler;
    TextView title;
    Snackbar bar;
    RouteAdapter routeAdapter;
    ValueEventListener eventListener;
    List<RouteDTO> routeList = new ArrayList<>();
    CityDTO city;
    HashMap<String, CityDTO> cities = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        title = (TextView) findViewById(R.id.entityTitle);
        city = (CityDTO)getIntent().getSerializableExtra("city");
        title.setText("Routes - " + city.getName());
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lm);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);


        setCityList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setCityList() {

        for (RouteDTO o: city.getRoutes().values()) {
            routeList.add(o);
        }
        routeAdapter = new RouteAdapter(routeList, new RouteAdapter.RouteListener() {
            @Override
            public void onNameClicked(RouteDTO city) {
                Log.d(TAG, "onNameClicked: city: " + city.getName());
            }

            @Override
            public void onNumberClicked(RouteDTO city) {
                Log.d(TAG, "onNumberClicked: city: " + city.getName());
            }
        });
        recycler.setAdapter(routeAdapter);
    }
      private void errorBar(String message) {
        bar = Snackbar.make(progressBar, message, Snackbar.LENGTH_INDEFINITE);
        bar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
        bar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.show();
    }

}
