package com.boha.malengagolf.library.data;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/07/23.
 */
public class UploadBlobDTO implements Serializable {
    private String servingUrl, blobKey;


    public String getServingUrl() {
        return servingUrl;
    }

    public void setServingUrl(String servingUrl) {
        this.servingUrl = servingUrl;
    }

    public String getBlobKey() {
        return blobKey;
    }

    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }


}
