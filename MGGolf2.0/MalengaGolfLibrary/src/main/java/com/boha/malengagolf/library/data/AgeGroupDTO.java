/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.malengagolf.library.data;


import java.io.Serializable;

/**
 *
 * @author Aubrey Malabie
 */
@SuppressWarnings(value = "unused")
public class AgeGroupDTO implements Serializable {

    private int ageGroupID, golfGroupID, ageFrom, ageTo;
    private int gender;
    private String groupName;
    private int numberOfHolesPerRound;

    public int getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(int ageFrom) {
        this.ageFrom = ageFrom;
    }

    public int getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(int ageTo) {
        this.ageTo = ageTo;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public int getAgeGroupID() {
        return ageGroupID;
    }

    public void setAgeGroupID(int ageGroupID) {
        this.ageGroupID = ageGroupID;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumberOfHolesPerRound() {
        return numberOfHolesPerRound;
    }

    public void setNumberOfHolesPerRound(int numberOfHolesPerRound) {
        this.numberOfHolesPerRound = numberOfHolesPerRound;
    }

}
