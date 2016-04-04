package com.boha.malengagolf.library.data;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/04/24.
 */
public class VideoClipContainer  extends SugarRecord implements Serializable {

    List<VideoClipDTO> videoClips = new ArrayList<VideoClipDTO>();

    public List<VideoClipDTO> getVideoClips() {
        return videoClips;
    }

    public void setVideoClips(List<VideoClipDTO> videoClips) {
        this.videoClips = videoClips;
    }

}
