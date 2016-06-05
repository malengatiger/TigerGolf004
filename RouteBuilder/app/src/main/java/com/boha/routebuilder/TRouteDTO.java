/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.routebuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aubreymalabie
 */
public class TRouteDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer routeID;
    private String routeName;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private String startAddress;
    private String endAddress;
    private Double distance;
    private String distanceString;
    private String durationString;
    private String description;
    private Double endLongitude;
    private List<TStepDTO> steps;

    public TRouteDTO() {
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public List<TStepDTO> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    public void setSteps(List<TStepDTO> steps) {
        this.steps = steps;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public List<TStepDTO> getTStepList() {
        return steps;
    }

    public void setTStepList(List<TStepDTO> tStepList) {
        this.steps = tStepList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (routeID != null ? routeID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TRouteDTO)) {
            return false;
        }
        TRouteDTO other = (TRouteDTO) object;
        if ((this.routeID == null && other.routeID != null) || (this.routeID != null && !this.routeID.equals(other.routeID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.routebuilder.data.TRoute[ routeID=" + routeID + " ]";
    }
    
}
