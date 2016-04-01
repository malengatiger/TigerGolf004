package com.boha.ScraperApp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.util.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.List;

/**
 * Created by aubreyM on 2014/04/30.
 */
public class GolfCourseMapActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.golf_map);
        ctx = getApplicationContext();
        state = (State) getIntent().getSerializableExtra("state");
        initialize();
//        mLocationClient = new LocationClient(getApplicationContext(), this,
//                this);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();

        setMap();
        putMarkersOnMap();

        //lat:-26.151901,lng:28.346994 ebotse

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);


    }

    private void putMarkersOnMap() {
        BitmapDescriptor bmm = BitmapDescriptorFactory
                .fromAsset("images/flag32.png");

        for (CitySCR c : state.getCityList()) {
            for (Course crs : c.getCourseList()) {
                LatLng pnt = new LatLng(crs.getLatitude(), crs.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .title(crs.getCourseName())
                                //.icon(bmm)
                        .snippet(crs.getCityName() + "\n" + c.getStateName())
                        .position(pnt));
            }
        }
        CitySCR city = state.getCityList().get(0);
        LatLng ltlng = new LatLng(city.getLatitude(), city.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 1.0f));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
    }

    private LatLng selectedLatLng;

    private void setMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLatLng = latLng;
                latitude = latLng.latitude;
                longitude = latLng.longitude;
//                if (googleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
//                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                } else {
//                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                }
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;

            }
        });
    }
    Context ctx;
    List<ClubDTO> clubList;
    double latitude, longitude;
    private void getClubsWithin() {
        LoaderRequestDTO w = new LoaderRequestDTO();
        w.setRequestType(LoaderRequestDTO.FIND_CLUBS_WITHIN_RADIUS);
        w.setRadius(100);
        w.setLatitude(latitude);
        w.setLongitude(longitude);

        BaseVolley.loadData(Statics.SERVLET_LOADER, w, ctx, new BaseVolley.BohaLoaderListener() {
            @Override
            public void onResponseReceived(LoaderResponseDTO response) {

                if (response.getStatusCode() > 0) {
                    return;
                }
                clubList = response.getClubList();
                putClubMarkersOnMap();

            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });

    }
    private void putClubMarkersOnMap() {
        BitmapDescriptor bmm = BitmapDescriptorFactory
                .fromAsset("images/flag32.png");
            for (ClubDTO crs : clubList) {
                LatLng pnt = new LatLng(crs.getLatitude(), crs.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .title(crs.getClubName())
                                //.icon(bmm)
                        .snippet(crs.getProvinceName())
                        .position(pnt));
            }

        ClubDTO c = clubList.get(0);
        LatLng ltlng = new LatLng(c.getLatitude(), c.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltlng, 1.0f));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7.0f));
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
//            mLocationClient.connect();
        }

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
//        mLocationClient.disconnect();
        super.onStop();
    }

    private void initialize() {
        Log.e(LOG, "initialize GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG, "onConnected");
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

    // The rest of this code is all about building the error dialog

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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

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
            ((GolfCourseMapActivity) getActivity()).onDialogDismissed();
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
//    protected LocationClient mLocationClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    static final String LOG = "GolfCourseMapActivity";
    Menu mMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        mMenu = menu;
        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_add_country_states);
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

        switch (item.getItemId()) {

            case R.id.menu_search:
                getClubsWithin();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
