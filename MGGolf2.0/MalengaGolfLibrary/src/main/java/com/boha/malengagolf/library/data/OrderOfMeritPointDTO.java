package com.boha.malengagolf.library.data;

/**
 * Created by aubreyM on 2014/04/28.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

/**
 *
 * @author aubreyM
 */
public class OrderOfMeritPointDTO implements Serializable {
    private int orderOfMeritPointID;
    private int win;
    private int top5, top3;
    private int top10;
    private int top20;
    private int top30;
    private int top40;
    private int top50, top100;
    private int golfGroupID;
    private int tiedFirst;


    public int getOrderOfMeritPointID() {
        return orderOfMeritPointID;
    }

    public void setOrderOfMeritPointID(int orderOfMeritPointID) {
        this.orderOfMeritPointID = orderOfMeritPointID;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getTop5() {
        return top5;
    }

    public void setTop5(int top5) {
        this.top5 = top5;
    }

    public int getTop3() {
        return top3;
    }

    public void setTop3(int top3) {
        this.top3 = top3;
    }

    public int getTop10() {
        return top10;
    }

    public void setTop10(int top10) {
        this.top10 = top10;
    }

    public int getTop20() {
        return top20;
    }

    public void setTop20(int top20) {
        this.top20 = top20;
    }

    public int getTop30() {
        return top30;
    }

    public void setTop30(int top30) {
        this.top30 = top30;
    }

    public int getTop40() {
        return top40;
    }

    public void setTop40(int top40) {
        this.top40 = top40;
    }

    public int getTop50() {
        return top50;
    }

    public void setTop50(int top50) {
        this.top50 = top50;
    }

    public int getTop100() {
        return top100;
    }

    public void setTop100(int top100) {
        this.top100 = top100;
    }

    public int getGolfGroupID() {
        return golfGroupID;
    }

    public void setGolfGroupID(int golfGroupID) {
        this.golfGroupID = golfGroupID;
    }

    public int getTiedFirst() {
        return tiedFirst;
    }

    public void setTiedFirst(int tiedFirst) {
        this.tiedFirst = tiedFirst;
    }


}

