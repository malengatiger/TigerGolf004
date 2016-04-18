package com.boha.routebuilder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aubreymalabie on 4/17/16.
 */

public class DirectionsJSONParser {

    private static List<MyLocation> getStartEndLocations(JSONArray legsArr) throws JSONException {
        List<MyLocation> myLocs = new ArrayList<>();
        for (int m = 0; m < legsArr.length(); m++) {
            JSONObject b = legsArr.getJSONObject(m);
            JSONObject dist = b.getJSONObject("distance");
            JSONObject duration = b.getJSONObject("duration");
            String endAddress = b.getString("end_address");
            JSONObject endLocation = b.getJSONObject("end_location");
            String startAddress = b.getString("start_address");
            JSONObject startLocation = b.getJSONObject("start_location");
            StringBuilder sb = new StringBuilder();
            sb.append("startAddress: ").append(startAddress).append("\n");
            sb.append("endAddress: ").append(endAddress).append("\n");
            sb.append("duration: ").append(duration).append("\n");
            sb.append("dist: ").append(dist).append("\n\n");
            Log.e("DirectionsParser",sb.toString());
            MyLocation myloc = new MyLocation();
            myloc.setEndAddress(endAddress);
            myloc.setStartAddress(startAddress);
            LatLng start = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));
            LatLng end = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

            myloc.setStartLocation(start);
            myloc.setEndLocation(end);

            myLocs.add(myloc);

            JSONArray steps = b.getJSONArray("steps");
            int count = steps.length();
            for (int n = 0; n < steps.length(); n++) {
                JSONObject x = steps.getJSONObject(n);
                JSONObject distx = x.getJSONObject("distance");
                JSONObject durationx = x.getJSONObject("duration");
                JSONObject endLocX = x.getJSONObject("end_location");
                JSONObject startLocX = x.getJSONObject("start_location");
                String ins = x.getString("html_instructions");
                StringBuilder s = new StringBuilder();
                s.append("instruction: ").append(ins).append("\n");
                s.append("startLocx: " + startLocX).append(" endLocx: ").append(endLocX).append("\n");
                s.append("duration: ").append(durationx).append("\n");
                s.append("dist: ").append(distx).append("\n\n");
                Log.d("DirectionsParser",s.toString());

                JSONObject polyLine = x.getJSONObject("polyline");
                Log.i("Dir","polyLine>>>: " + polyLine );
            }
        }
        return myLocs;
    }
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public static Bag parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        List<MyLocation> myLocs = new ArrayList<>();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                myLocs = getStartEndLocations(jLegs);
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        Bag bag = new Bag();
        bag.setList(routes);
        bag.setMyLocations(myLocs);

        return bag;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}