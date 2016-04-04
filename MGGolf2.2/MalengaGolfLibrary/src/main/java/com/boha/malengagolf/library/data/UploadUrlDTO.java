package com.boha.malengagolf.library.data;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/07/23.
 */
public class UploadUrlDTO extends SugarRecord implements Serializable {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
