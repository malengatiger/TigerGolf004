package com.boha.golfpractice.player.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.golfpractice.player.R;
import com.boha.golfpractice.player.dto.PlayerDTO;
import com.boha.golfpractice.player.dto.VideoUploadDTO;
import com.boha.golfpractice.player.services.CDNVideoService;
import com.boha.golfpractice.player.util.MonLog;
import com.boha.golfpractice.player.util.SharedUtil;
import com.boha.golfpractice.player.util.SnappyVideo;
import com.boha.golfpractice.player.util.ThemeChooser;
import com.boha.golfpractice.player.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoCaptureActivity extends AppCompatActivity {

    private PlayerDTO player;
    private FloatingActionButton fab;
    private static final int REQUEST_VIDEO_CAPTURE = 1098;
    private ImageView image;
    private String authToken;
    private boolean videoTaken;
    private TextView txtTitle;
    private Button btnUpload, btnPlay;

    private Context ctx;
    private MonApp monApp;
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
    File videoFile;


    static final String LOG = VideoCaptureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChooser.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        ctx = getApplicationContext();
        monApp = (MonApp) getApplication();

        player = (PlayerDTO) getIntent().getSerializableExtra("player");
        if (player == null) {
            player = SharedUtil.getPlayer(ctx);
        }

        authToken = SharedUtil.getAuthToken(getApplicationContext());
        try {
            createVideoFile();
            setFields();
        } catch (IOException e) {
            Log.e(LOG, "Unable to get video file", e);
            Util.showErrorToast(ctx, "Unable to get video file");
            finish();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        MonLog.w(getApplicationContext(),LOG,"---- onSaveInstanceState");
        if (videoUpload != null) {
            b.putSerializable("video",videoUpload);
        }
        if (player != null) {
            b.putSerializable("player",player);
        }
        super.onSaveInstanceState(b);
    }
    private void setFields() {
        image = (ImageView) findViewById(R.id.image);
        txtTitle = (TextView)findViewById(R.id.title);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        btnPlay = (Button)findViewById(R.id.btnPlay);

        btnUpload.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(LOG, ".......btnUpload OnClickListener ... start YouTubeService ....");
                Intent m = new Intent(getApplicationContext(),CDNVideoService.class);
                startService(m);


            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(),LocalVideoPlayerActivity.class);
                m.putExtra("video",videoUpload);
                startActivity(m);
            }
        });
    }

    private void createVideoFile() throws IOException {
        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG, "WRITE_EXTERNAL_STORAGE permission not granted yet");
            return;
        }

        String imageFileName = ""+player.getPlayerID() + "-"+ sdf.format(new Date()) + ".mp4";

        File root;
        if (Util.hasStorage(true)) {
            Log.i(LOG, "###### get file from getExternalStoragePublicDirectory");
            root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
        } else {
            Log.i(LOG, "###### get file from getDataDirectory");
            root = Environment.getDataDirectory();
        }
        File dir = new File(root, "tgolf");
        if (!dir.exists()) {
            dir.mkdir();
        }

        videoFile = new File(dir, imageFileName);
        Log.w(LOG, "Video file has been created: " + videoFile.getAbsolutePath());

    }

    private void dispatchTakeVideoIntent() {
        final Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 6000000);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Util.showErrorToast(getApplicationContext(), "No video camera app available");
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        cacheVideo(uri);
                    } else {
                        Util.showErrorToast(ctx, "Unable to get video file");
                    }
                } else {
                    Log.e(LOG, "---------- Unable to getvideo file from camera");
                    Util.showErrorToast(ctx, "Unable to get video file");

                }
                break;

        }


    }

    @Override
    public void onBackPressed() {
        MonLog.d(getApplicationContext(), LOG, "onBackPressed");

        if (videoTaken) {
            Intent m = new Intent();
            m.putExtra("videoTaken", videoTaken);
            setResult(RESULT_OK, m);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    VideoUploadDTO videoUpload;

    /**
     * Cache video clip prior to upload
     */
    private void cacheVideo(Uri uri) {

        Log.e(LOG,"******** the computed path: " + uri.toString());
        String path = uri.toString().substring(5);
        MonLog.e(ctx,LOG,"Prior to caching, videoFile lenghth: " + videoFile.length());
        videoFile = new File(path);
        Log.w(LOG,"videoFile length: " + getLength(videoFile.length()));

        videoUpload = new VideoUploadDTO();
        videoUpload.setPlayerID(player.getPlayerID());
        videoUpload.setFilePath(videoFile.getAbsolutePath());

        videoUpload.setDateTaken(new Date().getTime());
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(
                videoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        File thumb = Util.getFile(getApplicationContext(),bmThumbnail);
        if (thumb != null) {
            videoUpload.setThumbnailPath(thumb.getAbsolutePath());
            MonLog.e(getApplicationContext(),LOG,"Thumbnail path saved: " + thumb.getAbsolutePath());
        }
        videoUpload.setPlayerName(player.getFullName());
        videoTaken = true;
        btnUpload.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.VISIBLE);

        SnappyVideo.addVideo((MonApp) getApplication(), videoUpload,
                new SnappyVideo.WriteListener() {
            @Override
            public void onDataWritten() {

            }

            @Override
            public void onError() {

            }
        });


    }




    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###0.00");

    private String getLength(long length) {
        Double d = Double.parseDouble("" + length) / (1024 * 1024);
        return df.format(d) + " MB";

    }
}
