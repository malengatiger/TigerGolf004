package com.boha.malengagolf.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.adapters.ClubAdapter;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.CountryDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ProvinceDTO;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.CacheUtil;
import com.boha.malengagolf.library.util.ErrorUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.Statics;
import com.boha.malengagolf.library.util.ToastUtil;
import com.boha.malengagolf.library.util.WebSocketUtil;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/30.
 */
public class GolfCourseMapActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private FragmentActivity fragmentActivity;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.golf_map);
        ctx = getApplicationContext();
        fragmentActivity = this;
        initialize();
        setFields();

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
            }
        });
        googleMap = mapFragment.getMap();
        if (googleMap == null) {
            ToastUtil.toast(ctx, "Map not available. Please try again!");
            finish();
            return;
        }

        requestOrigin = getIntent().getIntExtra("requestOrigin", ORIGIN_SEARCH);
        golfGroup = SharedUtil.getGolfGroup(ctx);
        setMap();
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

    }

    private LatLng selectedLatLng;

    private void setMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLatLng = latLng;
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                newClubLatLng = latLng;
                if (addingNewClub) {
                    BitmapDescriptor bmm = BitmapDescriptorFactory.fromResource(R.drawable.flag48);
                    LatLng pnt = new LatLng(latitude, longitude);
                    try {
                        googleMap.addMarker(new MarkerOptions()
                                .title(editClubName.getText().toString())
                                .icon(bmm)
                                .draggable(true)
                                .snippet(province.getProvinceName())
                                .position(pnt));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pnt, 16.0f));
                        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {

                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {

                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                newClubLatLng = marker.getPosition();

                                final AlertDialog.Builder diag = new AlertDialog.Builder(fragmentActivity);
                                diag.setTitle(ctx.getResources().getString(R.string.club_location))
                                        .setMessage(ctx.getResources().getString(R.string.club_location_ask))
                                        .setPositiveButton(ctx.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.i(LOG, "########## about to add the new club");
                                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                                                sendClubData(newClubLatLng.latitude, newClubLatLng.longitude);
                                            }
                                        })
                                        .setNegativeButton(ctx.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(LOG, "Google Map is null or some other problem", e);
                    }
                }

            }
        });

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //ensure that all markers in bounds
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }

                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cu);
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.e(LOG, "marker clicked: " + marker.getTitle());
                club = null;
                for (ClubDTO c : clubList) {
                    if (marker.getTitle().equalsIgnoreCase(c.getClubName())) {
                        club = c;
                    }
                }
                latitude = club.getLatitude();
                longitude = club.getLongitude();
                showClubSelectionDialog();
                return false;
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                startLocationUpdates();
            }
        });
    }

    private void sendClubData(double latitude, double longitude) {
        ClubDTO club = new ClubDTO();
        club.setProvinceID(province.getProvinceID());
        club.setClubName(editClubName.getText().toString());
        club.setLatitude(latitude);
        club.setLongitude(longitude);

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_CLUB);
        w.setClub(club);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }
                        addingNewClub = false;
                        closeForEdit();
                        clubList.add(0, response.getClubs().get(0));
                        adapter.notifyDataSetChanged();
                        putClubMarkersOnMap();
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
                        setRefreshActionButtonState(false);
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, response)) {
//                    return;
//                }
//                addingNewClub = false;
//                closeForEdit();
//                clubList.add(0, response.getClubs().get(0));
//                adapter.notifyDataSetChanged();
//                putClubMarkersOnMap();
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    ClubDTO club;

    private void showClubSelectionDialog() {
        if (club == null) return;
        final AlertDialog.Builder diag = new AlertDialog.Builder(this);
        diag.setTitle(club.getClubName());
        if (requestOrigin == ORIGIN_SEARCH) {
            diag.setMessage(ctx.getResources().getString(R.string.club_directions))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startDirectionsMap(club.getLatitude(), club.getLongitude());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        } else {

            diag.setMessage(ctx.getResources().getString(R.string.setup_question))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


        }
        // create alert dialog
        AlertDialog alertDialog = diag.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("club", club);
        setResult(RESULT_OK, i);
        finish();
    }

    public static final int TOURNAMENT_SETUP = 33, RESERVATION_SETUP = 66;
    Context ctx;
    List<ClubDTO> clubList;
    double latitude, longitude;
    LatLng newClubLatLng;
    List<Marker> markers = new ArrayList<Marker>();

    private void putClubMarkersOnMap() {
        Log.i(LOG, "putClubMarkersOnMap .........." + clubList.size());
        int index = 0;
        googleMap.clear();
        markers.clear();
        if (clubList == null) {
            Log.e(LOG, "ClubList is null");
            return;
        }
        for (ClubDTO crs : clubList) {
            LatLng pnt = new LatLng(crs.getLatitude(), crs.getLongitude());
            Marker m =
                    googleMap.addMarker(new MarkerOptions()
                            .title(crs.getClubName())
                            .icon(bmdList.get(index))
                            .snippet(crs.getProvinceName())
                            .position(pnt));
            markers.add(m);
            index++;
        }


        //ensure that all markers in bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        int padding = 60; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        Log.w(LOG, "############## onStop stopping google service clients");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }

    private void initialize() {
        //TODO - figure out how to use Drive API to store tournament pictures, store links to photos in DB
        // reduce the need to do it on my own server ...THINK!!
        Log.e(LOG, "############ initialize GoogleApiClient... do not need it at this point");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    LocationRequest mLocationRequest;
    Location location;
    static final float ACCURACY_THRESHOLD = 100;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    boolean mRequestingLocationUpdates;

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG, "########### onConnected .... what is in the bundle...?");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.w(LOG, "## requesting location updates ....");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            setRefreshActionButtonState(true);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(LOG, "## onLocationChanged accuracy = " + location.getAccuracy());
        if (location.getAccuracy() <= ACCURACY_THRESHOLD) {
            this.location = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mLatitude = latitude;
            mLongitude = longitude;
            stopLocationUpdates();
            getNearbyClubs();
        }
    }


    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            try {
                ((GolfCourseMapActivity) getActivity()).onDialogDismissed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    static final String LOG = "GolfCourseMapActivity";
    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.golf_map_menu, menu);
        mMenu = menu;
        menu.getItem(0).setVisible(false);
        loadIcons();
        loadDrawables();
        if (provinceList == null) {
            getCachedClubs();
        }
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_list);
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


        if (item.getItemId() == R.id.menu_map) {
            mapLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return true;
        }
        if (item.getItemId() == R.id.menu_list) {
            mapLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            return true;
        }
        if (item.getItemId() == R.id.menu_search_by_state) {
            provinceSpinner.setVisibility(View.VISIBLE);
            setProvinceSpinner();
            return true;
        }
        if (item.getItemId() == R.id.menu_add) {
            addingNewClub = true;
            arrangeForEdit();
            return true;
        }


        return super.onOptionsItemSelected(item);

    }

    private void setList() {
        Log.i(LOG, "setList ..........");
        adapter = new ClubAdapter(ctx, R.layout.club_item, clubList, drawableList);
        if (listView == null) {
            Log.e(LOG, "listView is NULL, why????");
            setFields();
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                club = clubList.get(i);

                showClubSelectionDialog();
                Log.e(LOG, "club has courses: " + club.getClubCourses().size());
            }
        });
    }

    ClubAdapter adapter;
    EditText editClubName;
    boolean addingNewClub;
    View editLayout;
    Button btnSave;

    private void arrangeForEdit() {

        try {
            provinceSpinner.setSelection(0);
        } catch (Exception e) {
        }
        seekBar.setVisibility(View.GONE);
        txtSeekBar.setVisibility(View.GONE);
        txtResults.setVisibility(View.GONE);
        txtPages.setVisibility(View.GONE);
        aSwitch.setVisibility(View.GONE);
        txtDown.setVisibility(View.GONE);
        txtUp.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingNewClub = true;
                if (province == null) {
                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_province));
                    return;
                }
                if (editClubName.getText().toString().isEmpty()) {
                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_clubname));
                    return;
                }
                ToastUtil.toast(ctx, ctx.getResources().getString(R.string.tap_select));
            }
        });
    }

    private void closeForEdit() {
        seekBar.setVisibility(View.VISIBLE);
        txtSeekBar.setVisibility(View.VISIBLE);
        txtResults.setVisibility(View.VISIBLE);
        txtPages.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);
        aSwitch.setVisibility(View.GONE);
    }

    private void setFields() {
        Log.i(LOG, "setFields ..........");
        imgStreetView = (ImageView) findViewById(R.id.MAP_streetView);
        aSwitch = (Switch) findViewById(R.id.MAP_switch);
        mapLayout = findViewById(R.id.mapLayout);
        editLayout = findViewById(R.id.MAP_editLayout);
        editClubName = (EditText) findViewById(R.id.MAP_editClubName);
        btnSave = (Button) findViewById(R.id.MAP_btnSave);
        editLayout.setVisibility(View.GONE);
        provinceSpinner = (Spinner) findViewById(R.id.MAP_spinner);
        provinceSpinner.setVisibility(View.GONE);
        seekBar = (SeekBar) findViewById(R.id.MAP_seekBar);
        txtSeekBar = (TextView) findViewById(R.id.MAP_seekBarValue);
        txtPages = (TextView) findViewById(R.id.MAP_pages);
        txtUp = (TextView) findViewById(R.id.MAP_btnUp);
        txtDown = (TextView) findViewById(R.id.MAP_btnDown);
        txtResults = (TextView) findViewById(R.id.MAP_results);
        txtResults.setText("");
        listView = (ListView) findViewById(R.id.list);
        disableButtons();
        String name = SharedUtil.getGolfGroup(ctx).getCountryName();
        if (name.contains("united-states")
                || name.contains("united-kingdom")
                || name.contains("canada")) {
            txtSeekBar.setText("20 miles");
        } else {
            txtSeekBar.setText("20 km");
        }
        imgStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStreetView();
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                //googleMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
            }
        });
        txtUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage == totalPages) return;
                currentPage++;
                switch (searchType) {
                    case FROM_PROVINCE:
                        getProvinceClubs();
                        break;
                    case FROM_NEARBY:
                        startLocationUpdates();
                        break;
                }
            }
        });
        txtDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage == 1) return;
                currentPage--;
                switch (searchType) {
                    case FROM_PROVINCE:
                        getProvinceClubs();
                        break;
                    case FROM_NEARBY:
                        startLocationUpdates();
                        break;
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                String locale = ctx.getResources().getConfiguration().locale.getCountry();
                if (locale.equalsIgnoreCase("US")) {
                    txtSeekBar.setText("" + i + " miles");
                } else {
                    txtSeekBar.setText("" + i + " km");
                }
                radius = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        txtSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seekBar.getProgress() > 0) {
                    startLocationUpdates();
                }
            }
        });
        txtPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seekBar.getProgress() > 0) {
                    startLocationUpdates();
                }
            }
        });
    }

    private void startStreetView() {
        Intent streetView = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.streetview:cbll="
                        + latitude + ","
                        + longitude + "&cbp=1,99.56,,1,-5.27&mz=21")
        );
        startActivity(streetView);
    }

    private void setProvinceSpinner() {
        Log.i(LOG, "setProvinceSpinner ...");
        List<String> list = new ArrayList<String>();
        list.add(ctx.getResources().getString(R.string.select_province));
        if (provinceList == null) {
            Log.e(LOG, "###### error, provinceList is NULL ------->");
            provinceSpinner.setVisibility(View.GONE);
            return;
        }
        for (ProvinceDTO p : provinceList) {
            list.add(p.getProvinceName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        provinceSpinner.setAdapter(adapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    province = null;
                    return;
                }
                currentPage = 1;
                province = provinceList.get(i - 1);
                if (addingNewClub) return;
                getProvinceClubs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getProvinceList() {
        Log.i(LOG, "getProvinceList ....");

        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_COUNTRY, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    provinceList = response.getProvinces();
                    setProvinceSpinner();
                }
            }

            @Override
            public void onDataCached() {

            }
        });

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_PROVINCES);
        w.setCountryID(SharedUtil.getGolfGroup(ctx).getCountryID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        if (provinceList == null || provinceList.isEmpty()) {
            setRefreshActionButtonState(true);
        }
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        provinceList = r.getProvinces();
                        setProvinceSpinner();
                        CountryDTO c = new CountryDTO();
                        c.setCountryName(SharedUtil.getGolfGroup(getApplicationContext()).getCountryName());
                        c.setCountryID(SharedUtil.getGolfGroup(getApplicationContext()).getCountryID());
                        c.setProvinces(provinceList);
                        r.setCountry(c);

                        CacheUtil.cacheData(getApplicationContext(), r, CacheUtil.CACHE_COUNTRY, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });

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
                        setRefreshActionButtonState(false);
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                setRefreshActionButtonState(false);
                if (r.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, r.getMessage());
                    return;
                }
                provinceList = r.getProvinces();
                setProvinceSpinner();
                CountryDTO c = new CountryDTO();
                c.setCountryName(SharedUtil.getGolfGroup(getApplicationContext()).getCountryName());
                c.setCountryID(SharedUtil.getGolfGroup(getApplicationContext()).getCountryID());
                c.setProvinces(provinceList);
                r.setCountry(c);

                CacheUtil.cacheData(getApplicationContext(), r, CacheUtil.CACHE_COUNTRY, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached() {

                    }
                });

            }

            @Override
            public void onVolleyError(VolleyError error) {
                setRefreshActionButtonState(false);
                ErrorUtil.showServerCommsError(ctx);
            }
        });
    }

    boolean isFirstTime = true;

    private void getCachedClubs() {
        CacheUtil.getCachedData(getApplicationContext(), CacheUtil.CACHE_NEAREST_CLUBS, 0, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    clubList = response.getClubs();
                    if (googleMap != null)
                        putClubMarkersOnMap();
                    setList();
                }
            }

            @Override
            public void onDataCached() {

            }
        });
    }

    private void getNearbyClubs() {
        Log.i(LOG, "getNearbyClubs ..........");
        if (latitude == 0 && longitude == 0) return;
        try {
            provinceSpinner.setSelection(0);
        } catch (Exception e) {
        }


        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_CLUBS_NEARBY);
        w.setLatitude(latitude);
        w.setLongitude(longitude);
        w.setRadius(radius);
        w.setPage(currentPage);

        //TODO - check if Canada and UK use miles or km. Sort out the proper country names
        if (golfGroup.getCountryName().contains("united-states")) {
            w.setRadiusType(RequestDTO.MILES);
        } else {
            w.setRadiusType(RequestDTO.KILOMETRES);
        }
        if (!BaseVolley.checkNetworkOnDevice(getApplicationContext())) {
            return;
        }
        txtResults.setText("");
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        if (isFirstTime) {
                            isFirstTime = false;
                            getProvinceList();
                        }
                        clubList = r.getClubs();
                        putClubMarkersOnMap();
                        setList();

                        CacheUtil.cacheData(getApplicationContext(), r, CacheUtil.CACHE_NEAREST_CLUBS, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });

                        totalPages = r.getTotalPages();
                        txtPages.setText("" + currentPage + "/" + r.getTotalPages());
                        txtResults.setText(ctx.getResources().getString(R.string.clubs_on_map)
                                + " " + clubList.size() + "/" + r.getTotalClubs());
                        if (r.getTotalPages() > 1) {
                            enableButtons();
                        } else {
                            disableButtons();
                        }
                        searchType = FROM_NEARBY;
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
                        setRefreshActionButtonState(false);
                        ErrorUtil.showServerCommsError(ctx);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                if (isFirstTime) {
//                    isFirstTime = false;
//                    getProvinceList();
//                }
//                clubList = r.getClubs();
//                putClubMarkersOnMap();
//                setList();
//
//                CacheUtil.cacheData(getApplicationContext(), r, CacheUtil.CACHE_NEAREST_CLUBS, new CacheUtil.CacheUtilListener() {
//                    @Override
//                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onDataCached() {
//
//                    }
//                });
//
//                totalPages = r.getTotalPages();
//                txtPages.setText("" + currentPage + "/" + r.getTotalPages());
//                txtResults.setText(ctx.getResources().getString(R.string.clubs_on_map)
//                        + " " + clubList.size() + "/" + r.getTotalClubs());
//                if (r.getTotalPages() > 1) {
//                    enableButtons();
//                } else {
//                    disableButtons();
//                }
//                searchType = FROM_NEARBY;
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void enableButtons() {
        txtUp.setVisibility(View.VISIBLE);
        txtDown.setVisibility(View.VISIBLE);
    }

    private void disableButtons() {
        txtUp.setVisibility(View.GONE);
        txtDown.setVisibility(View.GONE);
    }

    private void getProvinceClubs() {

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_CLUBS_IN_PROVINCE);
        w.setProvinceID(province.getProvinceID());
        w.setPage(currentPage);

        //TODO - check if Canada and UK use miles or km. Sort out the proper country names

        if (golfGroup.getCountryName().contains("united-states")) {
            w.setRadiusType(RequestDTO.MILES);
        } else {
            w.setRadiusType(RequestDTO.KILOMETRES);
        }
        if (!BaseVolley.checkNetworkOnDevice(getApplicationContext())) {
            return;
        }
        txtResults.setText("");
        setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        Log.e(LOG, "Have found " + r.getClubs().size() + " clubs in province");
                        clubList = r.getClubs();
                        putClubMarkersOnMap();
                        setList();
                        totalPages = r.getTotalPages();
                        txtPages.setText("" + currentPage + "/" + r.getTotalPages());
                        txtResults.setText(ctx.getResources().getString(R.string.clubs_on_map)
                                + " " + clubList.size() + "/" + r.getTotalClubs());
                        if (r.getTotalPages() > 1) {
                            enableButtons();
                        } else {
                            disableButtons();
                        }
                        searchType = FROM_PROVINCE;
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
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setRefreshActionButtonState(false);
//                if (!ErrorUtil.checkServerError(ctx, r)) {
//                    return;
//                }
//                Log.e(LOG, "Have found " + r.getClubs().size() + " clubs in province");
//                clubList = r.getClubs();
//                putClubMarkersOnMap();
//                setList();
//                totalPages = r.getTotalPages();
//                txtPages.setText("" + currentPage + "/" + r.getTotalPages());
//                txtResults.setText(ctx.getResources().getString(R.string.clubs_on_map)
//                        + " " + clubList.size() + "/" + r.getTotalClubs());
//                if (r.getTotalPages() > 1) {
//                    enableButtons();
//                } else {
//                    disableButtons();
//                }
//                searchType = FROM_PROVINCE;
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ErrorUtil.showServerCommsError(ctx);
//            }
//        });
    }

    private void startDirectionsMap(double lat, double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + mLatitude + "," + mLongitude
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    Spinner provinceSpinner;
    SeekBar seekBar;
    TextView txtSeekBar, txtPages, txtUp, txtDown, txtResults;
    ListView listView;
    ResponseDTO responseDTO;
    List<ProvinceDTO> provinceList;
    ProvinceDTO province;
    GolfGroupDTO golfGroup;
    int currentPage = 1, radius = 20;
    int totalPages, searchType, requestOrigin;
    public static final int FROM_PROVINCE = 1, FROM_NEARBY = 2, ORIGIN_TOURNAMENT = 3,
            ORIGIN_SEARCH = 4;
    View mapLayout;
    Switch aSwitch;
    ImageView imgStreetView;
    double mLatitude, mLongitude;

    List<BitmapDescriptor> bmdList = new ArrayList<BitmapDescriptor>();

    private void loadIcons() {
        try {
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_1));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_2));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_3));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_4));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_5));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_6));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_7));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_8));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_9));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_10));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_11));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_12));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_13));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_14));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_15));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_16));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_17));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_18));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_19));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_20));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_21));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_22));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_23));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_24));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_25));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_26));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_27));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_28));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_29));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_30));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_31));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_32));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_33));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_34));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_35));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_36));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_37));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_38));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_39));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_40));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_41));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_42));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_43));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_44));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_45));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_46));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_47));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_48));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_49));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_50));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_51));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_52));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_53));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_54));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_55));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_56));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_57));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_58));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_59));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_60));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_61));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_62));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_63));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_64));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_65));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_66));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_67));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_68));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_69));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_70));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_71));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_72));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_73));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_74));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_75));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_76));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_77));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_78));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_79));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_80));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_81));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_82));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_83));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_84));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_85));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_86));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_87));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_88));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_89));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_90));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_91));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_92));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_93));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_94));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_95));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_96));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_97));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_98));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_99));
            bmdList.add(BitmapDescriptorFactory.fromResource(R.drawable.number_100));
        } catch (Exception e) {
            Log.e(LOG, "Load icons failed", e);
        }


    }

    List<Drawable> drawableList = new ArrayList<Drawable>();

    private void loadDrawables() {
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_1));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_2));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_3));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_4));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_5));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_6));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_7));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_8));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_9));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_10));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_11));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_12));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_13));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_14));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_15));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_16));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_17));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_18));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_19));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_20));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_21));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_22));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_23));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_24));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_25));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_26));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_27));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_28));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_29));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_30));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_31));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_32));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_33));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_34));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_35));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_36));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_37));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_38));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_39));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_40));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_41));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_42));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_43));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_44));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_45));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_46));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_47));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_48));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_49));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_50));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_51));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_52));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_53));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_54));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_55));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_56));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_57));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_58));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_59));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_60));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_61));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_62));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_63));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_64));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_65));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_66));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_67));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_68));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_69));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_70));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_71));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_72));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_73));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_74));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_75));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_76));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_77));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_78));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_79));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_80));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_81));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_82));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_83));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_84));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_85));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_86));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_87));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_88));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_89));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_90));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_91));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_92));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_93));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_94));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_95));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_96));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_97));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_98));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_99));
        drawableList.add(ctx.getResources().getDrawable(R.drawable.number_100));


    }
}
