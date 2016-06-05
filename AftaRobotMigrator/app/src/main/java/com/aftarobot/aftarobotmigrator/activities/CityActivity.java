package com.aftarobot.aftarobotmigrator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aftarobot.aftarobotmigrator.R;
import com.aftarobot.aftarobotmigrator.adapters.CityAdapter;
import com.aftarobot.aftarobotmigrator.dto.LandmarkDTO;
import com.aftarobot.aftarobotmigrator.dto.ResponseDTO;
import com.aftarobot.aftarobotmigrator.dto.TripDTO;
import com.aftarobot.aftarobotmigrator.newdata.CityDTO;
import com.aftarobot.aftarobotmigrator.util.DataUtil;
import com.aftarobot.aftarobotmigrator.util.OldUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CityActivity extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    static final String TAG = CityActivity.class.getSimpleName();
    ProgressBar progressBar;
    RecyclerView recycler;
    TextView title;
    Snackbar bar;
    CityAdapter cityAdapter;
    ValueEventListener eventListener;
    List<CityDTO> cityList = new ArrayList<>();
    HashMap<String, CityDTO> cities = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        title = (TextView) findViewById(R.id.entityTitle);
        title.setText("AftaRobot Cities");
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lm);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        db = FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        getOldLandmarks();

        check();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getCities() {
        final DatabaseReference citiesRef = db.getReference(DataUtil.AFTAROBOT_DB)
                .child(DataUtil.CITIES);

        Query query = citiesRef.orderByChild("countryID");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: addListenerForSingleValueEvent:\n " + dataSnapshot.getChildrenCount());
                for (DataSnapshot m: dataSnapshot.getChildren()) {
                    CityDTO c = m.getValue(CityDTO.class);
                    cities.put(c.getCityID(),c);
                    cityList.add(c);
                    Log.i(TAG, "onDataChange: City received: " + c.getName());

                }
                setCityList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setCityList() {
        Collections.sort(cityList);
        cityAdapter = new CityAdapter(cityList, new CityAdapter.CityListener() {
            @Override
            public void onNameClicked(CityDTO city) {
                Log.d(TAG, "........onNameClicked: city: " + city.getName());
                showDialog(city);
            }

            @Override
            public void onNumberClicked(CityDTO city) {
                Log.w(TAG, "onNumberClicked: city: " + city.getName() );
                if (city.getRoutes() != null && !city.getRoutes().isEmpty()) {
                    Intent m = new Intent(getApplicationContext(), RouteActivity.class);
                    m.putExtra("city", city);
                    startActivity(m);
                }
            }
        });
        recycler.setAdapter(cityAdapter);
    }
    private void showDialog(final CityDTO city) {
        AlertDialog.Builder dg = new AlertDialog.Builder(this);
        dg.setTitle("Trip Migrator")
                .setMessage("Do you want to add trips?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addTrips(city);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    private void check() {
        if (mAuth.getCurrentUser() != null) {
            getCities();
            return;
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG, "++++++++++++++ onAuthStateChanged:signed_in:" + user.getUid()
                            + " " + user.getEmail());
                    getCities();
                } else {
                    // User needs to sign in
                    Log.e(TAG, "-----------onAuthStateChanged:signed_out - start sign in");
                    //signIn();
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
        signIn();
    }
    private void signIn() {
//        if (TextUtils.isEmpty(email.getText())) {
//            errorBar("Please enter email address");
//            return;
//        }
//        if (TextUtils.isEmpty(password.getText())) {
//            errorBar("Please enter password");
//            return;
//        }
        Log.w(TAG, "signIn: ================ Firebase signin");
        final String e = "aubrey@mlab.co.za";
        final String p = "kktiger3";

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(e, p)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "####### signIn: onComplete: " + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Log.i(TAG, "####### onComplete: we cool, name: "
                                    + user.getDisplayName() + " email: " + user.getEmail()
                                    + " uid: " + user.getUid() + " \ntoken: " + user.getToken(true));


                        } else {
                            Log.e(TAG, "------------ sign in FAILED");
                            errorBar("Sorry! MPS Sign in has failed. Please try again a bit later");
                        }
                    }
                });
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
    private void addTrips(com.aftarobot.aftarobotmigrator.newdata.CityDTO city) {


        if (city.getRoutes() != null) {
            for (com.aftarobot.aftarobotmigrator.newdata.RouteDTO r: city.getRoutes().values()) {
                if (r.getRouteCities() != null) {
                    for (com.aftarobot.aftarobotmigrator.newdata.RouteCityDTO rc: r.getRouteCities().values()) {
                        if (rc.getLandmarks() != null) {
                            for (com.aftarobot.aftarobotmigrator.newdata.LandmarkDTO l: rc.getLandmarks().values()) {

                                LandmarkDTO x = map.get(l.getLandmarkName());
                                if (x != null) {
                                    if (!x.getTripList().isEmpty()) {
                                        for (TripDTO t: x.getTripList()) {
                                            com.aftarobot.aftarobotmigrator.newdata.TripDTO tx = new com.aftarobot.aftarobotmigrator.newdata.TripDTO();
                                            tx.setLandmarkID(l.getLandmarkID());
                                            tx.setLandmarkName(t.getLandmarkName());
                                            tx.setVehicleReg(t.getVehicleReg());
                                            tx.setRouteName(l.getRouteName());
                                            tx.setRouteCityName(l.getRouteCityName());
                                            tx.setCityID(l.getCityID());
                                            tx.setCityName(l.getCityName());
                                            tx.setDateArrived(t.getDateArrived());
                                            tx.setDateDispatched(t.getDateDipatched());
                                            tx.setMarshalName(t.getMarshalName());
                                            tx.setNumberOfPassengers(t.getNumberOfPassengers());
                                            tx.setRouteCityID(l.getRouteCityID());
                                            tx.setAssociatioName(t.getAssociatioName());

                                            DataUtil.addTrip(tx,null);

                                        }
                                    } else {
                                        Log.d(TAG, "addTrips: NO TRIPS");
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "addTrips: No landmarks");
                        }
                    }
                } else {
                    Log.d(TAG, "addTrips: No landmark cities");
                }
            }
        } else {
            Log.d(TAG, "addTrips: no ROUTES!!");
        }
    }
    HashMap<String, LandmarkDTO> map = new HashMap<>();
    List<LandmarkDTO> landmarks;
    private void getOldLandmarks() {
        OldUtil.getOldLandmarks(new OldUtil.OldListener() {
            @Override
            public void onResponse(ResponseDTO response) {
                if (response.getStatusCode() == 0) {
                    landmarks = response.getLandmarks();
                    for (LandmarkDTO m: landmarks) {
                        map.put(m.getLandmarkName(),m);
                    }
                    Log.d(TAG, "--------------- onResponse: landmarks: " + landmarks.size());
//                    DatabaseReference ref = db.getReference(DataUtil.AFTAROBOT_DB)
//                            .child(DataUtil.TRIPS);
//                    ref.removeValue();
                } else {
                    errorBar(response.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                errorBar(message);
            }
        });
    }
}
