package com.boha.routebuilder;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreymalabie on 4/18/16.
 */
public class MyLocation {
    LatLng startLocation, endLocation;
    String startAddress, endAddress;
    String stringDuration, stringDistance;
    double distance;
    List<Step> steps;

    public List<Step> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getStringDuration() {
        return stringDuration;
    }

    public void setStringDuration(String stringDuration) {
        this.stringDuration = stringDuration;
    }

    public String getStringDistance() {
        return stringDistance;
    }

    public void setStringDistance(String stringDistance) {
        this.stringDistance = stringDistance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
