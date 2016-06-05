/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.routebuilder;

import java.io.Serializable;

/**
 *
 * @author aubreymalabie
 */
public class TStepDTO implements Serializable {
    private Integer stepID;
    private String stepName;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double distance;
    private String instruction;
    private String distanceString;
    private String durationString;
    private Double endLongitude;
    private Integer routeID;

    private static final long serialVersionUID = 1L;
    
    private TRouteDTO route;

    public TStepDTO() {
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public Integer getStepID() {
        return stepID;
    }

    public void setStepID(Integer stepID) {
        this.stepID = stepID;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
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

    public TRouteDTO getRoute() {
        return route;
    }

    public void setRoute(TRouteDTO route) {
        this.route = route;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepID != null ? stepID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TStepDTO)) {
            return false;
        }
        TStepDTO other = (TStepDTO) object;
        if ((this.stepID == null && other.stepID != null) || (this.stepID != null && !this.stepID.equals(other.stepID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.routebuilder.data.TStep[ stepID=" + stepID + " ]";
    }
}
