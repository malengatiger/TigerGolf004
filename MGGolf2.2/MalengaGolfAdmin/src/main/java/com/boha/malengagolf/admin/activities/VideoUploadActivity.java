package com.boha.malengagolf.admin.activities;

import android.view.Menu;
import android.widget.TextView;
import com.boha.malengagolf.library.adapters.VideoClipAdapter;
import com.boha.malengagolf.library.data.VideoClipContainer;
import com.boha.malengagolf.library.data.VideoClipDTO;

import java.util.List;

//import_pic com.google.android.youtube.player.YouTubeBaseActivity;
//import_pic com.google.android.youtube.player.YouTubeInitializationResult;
//import_pic com.google.android.youtube.player.YouTubePlayer;
//import_pic com.google.android.youtube.player.YouTubePlayerView;
//import_pic com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import_pic com.google.api.services.youtube.YouTube;

//import_pic com.google.api.client.http.HttpTransport;


/**
 * Created by aubreyM on 2014/04/24.
 */
public class VideoUploadActivity  {

//    public static final String API_KEY = "AIzaSyBWfqWoQbWs-XCypa9rNVSHI0ZwBcUL1pA";
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(com.boha.malengagolf.library.R.layout.activity_video_upload);
//        ctx = getApplicationContext();
//        listView = (ListView) findViewById(com.boha.malengagolf.library.R.id.VU_list);
//        txtCount = (TextView) findViewById(R.id.VU_count);
//
//
//
//
//    }
//
//    private void setList() throws IOException {
//        VideoClipContainer vcc = FileUtil.getVideoClipContainer(ctx);
//        videoClips = vcc.getVideoClips();
//
//        txtCount.setText("" + videoClips.size());
//        adapter = new VideoClipAdapter(ctx, com.boha.malengagolf.library.R.layout.video_clip, videoClips);
//        listView.setAdapter(adapter);
//        registerForContextMenu(listView);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                videoClip = videoClips.get(i);
//            }
//        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                videoClip = videoClips.get(i);
//            }
//        });
//
//    }

    List<VideoClipDTO> videoClips;
    VideoClipDTO videoClip;
    VideoClipAdapter adapter;
    TextView txtCount;
    private void upLoadYouTubeVideo()  {
        String account = "malengatiger@gmail.com";
//        AsyncVideoUpload.uploadVideo(ctx,videoClip,account, new AsyncVideoUpload.VideoUploadListener() {
//            @Override
//            public void onUserAuthRequired(UserRecoverableAuthIOException auth) {
//                getUserAuth(auth);
//            }
//
//            @Override
//            public void onProgress(double progress) {
//
//            }
//
//            @Override
//            public void onUploadComplete(String videoID) {
//
//            }
//
//            @Override
//            public void onInitiationStarting() {
//
//            }
//
//            @Override
//            public void onInitiationComplete() {
//
//            }
//
//            @Override
//            public void onException(int e) {
//
//            }
//        });

    }

    //google video

//    private void initialize() {
//        playerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                                YouTubePlayer youTubePlayer, boolean wasRestored) {
//                Log.i(LOG, "onInitializationSuccess wasRestored: " + wasRestored);
//                player = youTubePlayer;
//                //setControlsEnabled();
//                // Specify that we want to handle fullscreen behavior ourselves.
//                player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
//                player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
//                    @Override
//                    public void onFullscreen(boolean b) {
//
//                    }
//                });
//                if (!wasRestored) {
//                    player.cueVideo("avP5d16wEp0");
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                YouTubeInitializationResult youTubeInitializationResult) {
//                Log.e(LOG, "onInitializationFailure ");
//            }
//        });
//    }
//
//
//    private void getUserAuth(UserRecoverableAuthIOException mException) {
//
//        startActivityForResult(mException.getIntent(), REQUEST_AUTH);
//    }
//    @Override
//    public void onActivityResult(int reqCode, int resCode, Intent data) {
//        switch (reqCode) {
//            case REQUEST_AUTH:
//                if (resCode == RESULT_OK) {
//                    upLoadYouTubeVideo();
//                }
//                break;
//        }
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(com.boha.malengagolf.library.R.menu.video_upload, menu);
//        mMenu = menu;
//        try {
//            setList();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getTitle().toString().
//                equalsIgnoreCase(ctx.getResources().
//                        getString(com.boha.malengagolf.library.R.string.back))) {
//            finish();
//            return true;
//        }
//
//
//        return false;
//    }
//
//    @Override
//    public void onPause() {
//        overridePendingTransition(com.boha.malengagolf.library.R.anim.slide_in_left, com.boha.malengagolf.library.R.anim.slide_out_right);
//        super.onPause();
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.video_player_context, menu);
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        videoClip = videoClips.get(info.position);
//        menu.setHeaderTitle(ctx.getResources().getString(R.string.video_actions));
//        menu.setHeaderIcon(R.drawable.tools32);
//
//
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        Log.w(LOG,
//                "onContextItemSelected - select option ..." + item.getTitle());
//        switch (item.getItemId()) {
//
//            case R.id.menu_view:
//
//                return true;
//            case R.id.menu_upload:
//                upLoadYouTubeVideo();
//                return true;
//            case R.id.menu_delete:
//
//                return true;
//
//
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
    Menu mMenu;
    static final int REQUEST_AUTH = 33;
    VideoClipContainer vcc;
    //final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    //final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    //GoogleAccountCredential credential;
//    private YouTubePlayer player;
//    private YouTube youTube;
//    private YouTubePlayerView playerView;
//    private ListView listView;
//    private Context ctx;
//    private static String VIDEO_FILE_FORMAT = "video/*";
//    static final String LOG = "VideoUploadActivity";
//    UserRecoverableAuthIOException mException;
}