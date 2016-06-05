package com.aftarobot.aftarobotmigrator.activities;

import android.content.Intent;
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
import com.aftarobot.aftarobotmigrator.adapters.LandmarkAdapter;
import com.aftarobot.aftarobotmigrator.newdata.LandmarkDTO;
import com.aftarobot.aftarobotmigrator.newdata.RouteCityDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LandmarkActivity extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    static final String TAG = LandmarkActivity.class.getSimpleName();
    ProgressBar progressBar;
    RecyclerView recycler;
    TextView title;
    Snackbar bar;
    LandmarkAdapter landmarkAdapter;
    List<LandmarkDTO> landmarks = new ArrayList<>();
    RouteCityDTO routeCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        title = (TextView) findViewById(R.id.entityTitle);
        routeCity = (RouteCityDTO) getIntent().getSerializableExtra("routeCity");
        title.setText("Landmarks - " + routeCity.getCityName());
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lm);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        db = FirebaseDatabase.getInstance();

        setLandmarkList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setLandmarkList() {

        for (LandmarkDTO o : routeCity.getLandmarks().values()) {
            landmarks.add(o);
        }
        Collections.sort(landmarks);
        landmarkAdapter = new LandmarkAdapter(landmarks, new LandmarkAdapter.LandmarkListener() {
            @Override
            public void onNameClicked(LandmarkDTO landmark) {
                Log.d(TAG, "onNameClicked: landmark: " + landmark.getLandmarkName());
            }

            @Override
            public void onNumberClicked(LandmarkDTO landmark) {
                Log.d(TAG, "onNumberClicked: landmark: " + landmark.getLandmarkName());
                getTrips(landmark);
            }
        });


        recycler.setAdapter(landmarkAdapter);
    }

    private void getTrips(final LandmarkDTO landmark) {
        Intent m = new Intent(getApplicationContext(),TripActivity.class);
        m.putExtra("landmark", landmark);
        startActivity(m);

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
