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
import com.aftarobot.aftarobotmigrator.adapters.TripAdapter;
import com.aftarobot.aftarobotmigrator.newdata.LandmarkDTO;
import com.aftarobot.aftarobotmigrator.newdata.TripDTO;
import com.aftarobot.aftarobotmigrator.util.DataUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TripActivity extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    static final String TAG = TripActivity.class.getSimpleName();
    ProgressBar progressBar;
    RecyclerView recycler;
    TextView title;
    Snackbar bar;
    TripAdapter tripAdapter;
    List<TripDTO> trips = new ArrayList<>();
    LandmarkDTO landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        title = (TextView) findViewById(R.id.entityTitle);
        landmark = (LandmarkDTO) getIntent().getSerializableExtra("landmark");
        title.setText("Trips - " + landmark.getLandmarkName());
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lm);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);


        getTrips();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getTrips() {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
        }
        DatabaseReference ref = db.getReference(DataUtil.AFTAROBOT_DB)
                .child(DataUtil.TRIPS)
                .child(landmark.getLandmarkID())
                .child(DataUtil.TRIPS);
        progressBar.setVisibility(View.VISIBLE);
        Log.w(TAG, "getTrips: " + ref.toString() );
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: addListenerForSingleValueEvent: trips: " + dataSnapshot.getChildrenCount());
                trips.clear();
                for (DataSnapshot m: dataSnapshot.getChildren()) {
                    TripDTO t = m.getValue(TripDTO.class);
                    trips.add(t);
                }
                landmark.setTrips(new HashMap<String, TripDTO>());
                for (TripDTO t: trips) {
                    landmark.getTrips().put(t.getTripID(), t);
                }

                progressBar.setVisibility(View.GONE);
                setTripList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setTripList() {

        for (TripDTO o: landmark.getTrips().values()) {
            trips.add(o);
        }
        Collections.sort(trips);
        tripAdapter = new TripAdapter(trips, new TripAdapter.TripListener() {
            @Override
            public void onNameClicked(TripDTO trip) {

            }

            @Override
            public void onNumberClicked(TripDTO trip) {

            }
        });

        recycler.setAdapter(tripAdapter);
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
