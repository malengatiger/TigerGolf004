package com.boha.golfpractice.player.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

import com.boha.golfpractice.player.R;
import com.boha.golfpractice.player.dto.ResponseDTO;
import com.boha.golfpractice.player.dto.VideoUploadDTO;
import com.boha.golfpractice.player.util.SharedUtil;
import com.boha.golfpractice.player.util.SnappyVideo;
import com.boha.golfpractice.player.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalVideoPlayerActivity extends Activity {

    VideoView mVideoView;
    Chronometer chronometer;
    VideoUploadDTO videoUpload;
    ImageView replay;
    View btnLayout;
    Context ctx;
    List<File> fileList;
    List<String> nameList;
    Spinner spinner;
    ImageView playIcon, thumb;
    View spinnerLayout;
    static final String LOG = LocalVideoPlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.activity_local_video_player);
        videoUpload = (VideoUploadDTO) getIntent().getSerializableExtra("video");

        setFields();
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFile != null) {
                    playVideo(selectedFile);
                }
            }
        });

        if (videoUpload != null) {
            spinnerLayout.setVisibility(View.GONE);
            File file = new File(videoUpload.getFilePath());
            playVideo(file);
            return;
        }
        spinnerLayout.setVisibility(View.VISIBLE);
        getFiles();

    }

    private void setFields() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        spinner = (Spinner) findViewById(R.id.spinner);
        playIcon = (ImageView) findViewById(R.id.play);
        thumb = (ImageView) findViewById(R.id.thumb);
        btnLayout = findViewById(R.id.btnLayout);
        replay = (ImageView) findViewById(R.id.replay);
        spinnerLayout = findViewById(R.id.spinnerLayout);

    }

    private void playVideo(File file) {
        Uri uri = Uri.fromFile(file);

        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                chronometer.stop();
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                mVideoView.start();

            }
        });

        mVideoView.start();

    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
    private void getFiles() {
        SnappyVideo.getVideo((MonApp) getApplication(), new SnappyVideo.ReadListener() {
            @Override
            public void onDataRead(ResponseDTO response) {
                fileList = new ArrayList<>();
                nameList = new ArrayList<>();
                for (VideoUploadDTO v: response.getVideoUploadList()) {
                    fileList.add(new File(v.getFilePath()));
                    Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(
                            v.getFilePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    thumb.setImageBitmap(bmThumbnail);
                    if (v.getPlayerName() == null) {
                        v.setPlayerName(SharedUtil.getPlayer(getApplicationContext()).getFullName());
                    }
                    nameList.add(v.getPlayerName() + " " + sdf.format(new Date(v.getDateTaken())));
                }
                if (fileList.isEmpty()) {
                    Util.showToast(getApplicationContext(),"No video files aboard");
                    finish();
                    return;
                }

                loadSpinner();
            }

            @Override
            public void onError() {

            }
        });



    }

    List<Bitmap> thumbNails = new ArrayList<>();
    File selectedFile;

    private void loadSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(), android.R.layout.simple_spinner_item, nameList);
        nameList.add(0, "Please select Video");
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedFile = null;
                    return;
                }
                selectedFile = fileList.get(position - 1);
                playVideo(selectedFile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
