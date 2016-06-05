package com.boha.routebuilder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.routebuilder.util.Snappy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;

    private GoogleMap mGoogleMap;
    private List<LatLng> points;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    LatLng originLatLng, destLatlng;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 121:

                mapFragment.getMapAsync(this);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        if (location != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),11f);
            mGoogleMap.animateCamera(cu);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] strings = new String[2];
            strings[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            strings[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, strings, 121);
            return;
        }
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);


        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(LOG, "------------- onMapLongClick. latLng: " + latLng);
                Toast.makeText(getApplicationContext(), "Location selected", Toast.LENGTH_SHORT).show();
                if (originLatLng == null) {
                    originLatLng = latLng;
                    Log.w(LOG, "Origin point saved: " + originLatLng);
                } else {
                    destLatlng = latLng;
                    Log.w(LOG, "Destination point saved: " + destLatlng);
                    getDirections();
                    originLatLng = null;
                }

            }
        });

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (lineOptions != null) {
                    mGoogleMap.addPolyline(lineOptions);
                }
            }
        });


    }

       private void getDirections() {

        String str_origin = "origin=" + originLatLng.latitude + "," + originLatLng.longitude;
        String str_dest = "destination=" + destLatlng.latitude + "," + destLatlng.longitude;
        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append(str_origin).append("&").append(str_dest);
        sb.append("&alternatives=true");

        String url = sb.toString();
        Log.w(LOG, "Sending directions request:\n " + url);

        new DownloadTask().execute(url);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        if (location != null) {
            if (location.getAccuracy() > ACCURACY_LIMIT) {
                startLocationUpdates();
            }
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);

        }
    }

    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    Location location;
    static final float ACCURACY_LIMIT = 100;
    @Override
    public void onLocationChanged(Location location) {

        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            if (mGoogleMap != null) {
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),location.getLongitude()),11f);
                mGoogleMap.animateCamera(cu);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = sendDirectionsRequest(url[0]);
                Log.d(LOG, data);
            } catch (Exception e) {
                Log.d(LOG, "Failed download of directions", e);
            }
            return data;
        }

        private void configureTimeouts(OkHttpClient client) {
            client.setConnectTimeout(40, TimeUnit.SECONDS);
            client.setReadTimeout(60, TimeUnit.SECONDS);
            client.setWriteTimeout(40, TimeUnit.SECONDS);

        }

        protected String sendDirectionsRequest(final String url) throws Exception {
            OkHttpClient client = new OkHttpClient();
            configureTimeouts(client);

            Log.w(LOG, "### sending request to Google Directions server"
                    + "\n" + url);

            Request okHttpRequest = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(okHttpRequest).execute();
            String path = response.body().string();
            Log.i(LOG, path);
            return path;

        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.e("", e.getMessage());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    List<TRouteDTO> routes = new ArrayList<>();
    int colorIndex;

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Bag bag = parser.parse(jObject);
                routes = bag.getList();
                MapsActivity.this.routes = bag.getRoutes();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;

            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            Log.w(LOG, "Number of Routes: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                Log.w(LOG, "Number of Points in Route: " + path.size());
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                Log.d(LOG, "adding points to polyline: " + points.size());
                lineOptions.addAll(points);
                lineOptions.width(32);

                random = new Random(System.currentTimeMillis());
                colorIndex = random.nextInt(colors.length - 1);

                lineOptions.color(ContextCompat.getColor(getApplicationContext(), colors[1]));
            }

            // Drawing polyline in the Google Map for the i-th route
            if (points != null)
                Log.e(LOG, "number of points: " + points.size());

            mGoogleMap.addPolyline(lineOptions);
            Snappy.addRoutes((RBApp) getApplication(), routes, new Snappy.SnappyListener() {
                @Override
                public void onRoutesAdded() {

                }

                @Override
                public void onRoutesRetrieved(List<TRouteDTO> routes) {

                }

                @Override
                public void onError(String message) {

                }

                @Override
                public void onRouteDeleted() {

                }
            });
            putRoutesOnMap();
            CameraUpdate cu = CameraUpdateFactory.zoomTo(12f);
            mGoogleMap.animateCamera(cu);
            originLatLng = null;
            destLatlng = null;
        }
    }

    PolylineOptions lineOptions = null;
    private void putRoutesOnMap() {
        IconGenerator gen = new IconGenerator(getApplicationContext());
        int index = 1;
        for (TRouteDTO m : routes) {
            View v = getLayoutInflater().inflate(R.layout.map_site_icon, null);
            TextView txt = (TextView) v.findViewById(R.id.badge);
            TextView here = (TextView) v.findViewById(R.id.here);
            txt.setVisibility(View.VISIBLE);
            here.setTextColor(ContextCompat.getColor(getApplicationContext(), colors[colorIndex]));
            String[] ss = m.getStartAddress().split(",");
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (String s : ss) {
                sb.append(s).append("\n");
                count++;
                if (count > 1) {
                    break;
                }
            }
            here.setText(sb.toString());
            txt.setText("" + index);
            gen.setContentView(v);
            gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            Bitmap bm = gen.makeIcon();

            BitmapDescriptor desc = BitmapDescriptorFactory
                    .fromBitmap(bm);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(m.getStartLatitude(),m.getStartLongitude()))
                    .icon(desc);

            final Marker m1 = mGoogleMap.addMarker(markerOptions);

            View v2 = getLayoutInflater().inflate(R.layout.map_site_icon, null);
            TextView txt2 = (TextView) v2.findViewById(R.id.badge);
            txt2.setVisibility(View.VISIBLE);
            TextView here2 = (TextView) v2.findViewById(R.id.here);
            here2.setTextColor(ContextCompat.getColor(getApplicationContext(), colors[colorIndex]));
            String[] ss2 = m.getEndAddress().split(",");
            StringBuilder sb2 = new StringBuilder();
            count = 0;
            for (String s : ss2) {
                sb2.append(s).append("\n");
                count++;
                if (count > 1) {
                    break;
                }
            }
            here2.setText(sb2.toString());
            txt2.setText("" + index);
            gen.setContentView(v2);
            gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            Bitmap bm2 = gen.makeIcon();
            BitmapDescriptor desc2 = BitmapDescriptorFactory
                    .fromBitmap(bm2);
            MarkerOptions markerOptions2 = new MarkerOptions()
                    .position(new LatLng(m.getEndLatitude(),m.getEndLongitude()))
                    .icon(desc2);

            final Marker m2 = mGoogleMap.addMarker(markerOptions2);
            putStepsOnMap(m);

        }
    }

    private void putStepsOnMap(TRouteDTO loc) {
        IconGenerator gen = new IconGenerator(getApplicationContext());

        int index = 1;
        for (TStepDTO m : loc.getSteps()) {
            View v2 = getLayoutInflater().inflate(R.layout.stop_number, null);
            TextView number = (TextView) v2.findViewById(R.id.number);
            number.setText("" + index);
            View numLayout = v2.findViewById(R.id.numberLayout);

            numLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colors[colorIndex]));
            gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            gen.setContentView(v2);
            Bitmap bm = gen.makeIcon();
            BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bm);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(m.getStartLatitude(),m.getStartLongitude()))
                    .icon(desc);

            final Marker m1 = mGoogleMap.addMarker(markerOptions);
            index++;

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LOG, "################ onStart .... connecting mGoogleApiClient ");
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        Log.e(LOG, "########## onStop");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }


    Random random = new Random(System.currentTimeMillis());
    int[] colors = {R.color.blue_400, R.color.red_400, R.color.green_400, R.color.purple_700, R.color.light_blue_400,
            R.color.orange_400, R.color.black, R.color.blue_500, R.color.purple_400,
            R.color.pink_400, R.color.amber_400, R.color.grey_400, R.color.black, R.color.medium_sea_green,
            R.color.teal_400, R.color.brown_200};
    static final String LOG = MapsActivity.class.getSimpleName();
}
