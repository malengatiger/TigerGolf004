package com.boha.malengagolf.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boha.malengagolf.library.data.ClubDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.util.ToastUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

//import com.google.android.gms.location.LocationClient;


public class GolfMapActivity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener,
        LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {


	GoogleMap googleMap;
	GoogleApiClient mGoogleApiClient;
	LocationRequest locationRequest;
	Location location;
    static final String LOG = "GolfMapActivity";
	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {
		super.onStart();


	}
	@Override
	public void onStop() {

		super.onStop();
	}

	private void stopPeriodicUpdates() {
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG, "### onCreate - activity getting created");
		setContentView(R.layout.fragment_map);
		ctx = getApplicationContext();
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		// Create a new global location parameters object

		 mLocationRequest = LocationRequest.create();
		 mLocationRequest.setInterval(5000);
		 mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		 mLocationRequest.setFastestInterval(1000);


		setFields();
		setMap();
	}



	private void moveCamera(double lat, double lng) {
		LatLng a = new LatLng(lat, lng);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(a, 9));
		map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 2000, null);
	}

	private void setFields() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.golf_map_menu, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(LOG, "### onConnectionFailed");

	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(LOG,
				"### ---> PlayServices onConnected() - gotta start something! >>");

	}

	@Override
	public void onConnectionSuspended(int i) {

	}



	@Override
	public void onLocationChanged(Location loc) {
		Log.e(LOG, "### Location changed, lat: " + loc.getLatitude()
				+ " lng: " + loc.getLongitude());
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		mCurrentLocation = loc;
        moveCamera(latitude,longitude);


	}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng arg0) {
		selectedPoint = arg0;
	}
	
	private void putClubsOnMap() {
		if (response.getClubs().size() == 0) {
			ToastUtil.toast(ctx, "No disaster responses found around you");
			return;
		}
		// remove markers in map ...

		for (ClubDTO lm : response.getClubs()) {
			Marker m = markerMap.get(Integer.valueOf(lm.getClubID()));
			if (m != null) {
				m.remove();
			}
		}
		BitmapDescriptor bm = BitmapDescriptorFactory
				.fromAsset("images/moz_round48.png");

		for (ClubDTO p : response.getClubs()) {
			LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
			MarkerOptions mo = new MarkerOptions()
					.position(latLng)
					.title(p.getClubName())
					.icon(bm)
					.snippet(p.getEmail());
			map.addMarker(mo);
		}

	}

    private LatLng selectedPoint;
	private void setMap() {

		try {
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
					.findFragmentById(R.id.map);
			map = mapFragment.getMap();
			if (map != null) {
				map.setMyLocationEnabled(true);
				map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					@Override
					public void onMapClick(LatLng a) {
                        selectedPoint = a;
						Log.e("TMA", "*** map clicked on: " + a.toString());
					}
				});
				map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
					@Override
					public View getInfoWindow(Marker m) {
						return null;
					}

					@Override
					public View getInfoContents(Marker m) {
						inflater = (LayoutInflater) ctx
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View v = inflater.inflate(
								R.layout.info_window, null);
						// ImageView img = (ImageView) v
						// .findViewById(R.id.INFO_WINDOW_image);
						TextView txtLandmark = (TextView) v
								.findViewById(R.id.INFO_WINDOW_landmarkName);
						TextView txtRoute = (TextView) v
								.findViewById(R.id.INFO_WINDOW_routeName);
						TextView txtCount = (TextView) v
								.findViewById(R.id.INFO_WINDOW_waiting);
						
						hasJustTappedLandmark = true;
						return v;
					}
				});
			} else {
				// toast
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private int type;
	private static final int CHECK_IN_TO_SERVER = 1, REPORT_OCCURENCE = 2;
	private static final int ZOOM = 11;
	private List<LatLng> allLatLngs;
	private SharedPreferences sp;
	private LinearLayout smartLayout;
	Button btnTrip;
	private Location mCurrentLocation;
	Context ctx;
	LayoutInflater inflater;
	private LocationRequest mLocationRequest;
	GoogleMap map;
	double latitude, longitude;
	SeekBar seekBar;
	Button btnSearch, btnClose;
	ResponseDTO response;
	Vibrator vb;
	TextView txtRadius, txtCount, txtTitle, txtEstimated;
	HashMap<Integer, Marker> markerMap;
	boolean hasJustTappedLandmark;

}
