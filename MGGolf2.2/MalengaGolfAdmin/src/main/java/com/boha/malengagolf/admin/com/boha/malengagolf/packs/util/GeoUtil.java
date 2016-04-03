package com.boha.malengagolf.admin.com.boha.malengagolf.packs.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class GeoUtil {

    public interface GeocoderListener {
        public void onGeocoded(Address address);
        public void onError();
    }
    static GeocoderListener geocoderListener;
    static Context ctx;
    static LatLng p;
    static Address addressFound;

    public static void geoCode(LatLng latLng, Context context, GeocoderListener listener) {
        ctx = context;
        geocoderListener = listener;
        p = latLng;
        new GeoTask().execute();
    }
    static class GeoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {
            Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(p.latitude, p.longitude, 1);
                String strCompleteAddress= "";
                if (addresses.size() > 0){
                    addressFound = addresses.get(0);
                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                        strCompleteAddress+= addresses.get(0).getAddressLine(i) + "\n";
                }
                Log.i("GeoUtil address found => ", strCompleteAddress);
                Toast.makeText(ctx, strCompleteAddress, Toast.LENGTH_LONG).show();
            }
            catch (IOException e) {
                e.printStackTrace();
                return 9;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer v) {
            if (v.intValue() > 0) {
                geocoderListener.onError();
                return;
            }
            geocoderListener.onGeocoded(addressFound);
        }

        @Override
        protected void onPreExecute() {

        }

    }
}
