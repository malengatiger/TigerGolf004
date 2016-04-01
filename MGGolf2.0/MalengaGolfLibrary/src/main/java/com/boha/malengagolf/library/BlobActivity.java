package com.boha.malengagolf.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.boha.malengagolf.library.data.AdministratorDTO;
import com.boha.malengagolf.library.data.GolfGroupDTO;
import com.boha.malengagolf.library.data.ParentDTO;
import com.boha.malengagolf.library.data.PlayerDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.data.ScorerDTO;
import com.boha.malengagolf.library.data.TournamentDTO;
import com.boha.malengagolf.library.data.UploadBlobDTO;
import com.boha.malengagolf.library.data.VideoClipContainer;
import com.boha.malengagolf.library.data.VideoClipDTO;
import com.boha.malengagolf.library.util.BlobUpload;
import com.boha.malengagolf.library.util.CacheVideoUtil;
import com.boha.malengagolf.library.util.GLToolbox;
import com.boha.malengagolf.library.util.ImageUtil;
import com.boha.malengagolf.library.util.SharedUtil;
import com.boha.malengagolf.library.util.TextureRenderer;
import com.boha.malengagolf.library.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by aubreyM on 2014/04/21.
 */
public class BlobActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.camera);
        setFields();
        golfGroup = SharedUtil.getGolfGroup(ctx);
        tournament = (TournamentDTO) getIntent().getSerializableExtra("tournament");
        player = (PlayerDTO) getIntent().getSerializableExtra("player");
        parent = (ParentDTO) getIntent().getSerializableExtra("parent");
        scorer = (ScorerDTO) getIntent().getSerializableExtra("scorer");
        admin = (AdministratorDTO) getIntent().getSerializableExtra("administrator");
        if (golfGroup != null) {
            // type = GOLF_GROUP_PICTURE;
        }
        if (tournament != null) {
            type = TOURNAMENT_PICTURE;
        }
        if (player != null) {
            type = PLAYER_PICTURE;
        }
        if (parent != null) {
            type = PARENT_PICTURE;
        }
        if (scorer != null) {
            type = SCORER_PICTURE;
        }
        if (admin != null) {
            type = ADMIN_PICTURE;
        }

        Log.e(LOG, "###### type: " + type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dispatchTakePictureIntent();
    }

    @Override
    public void onResume() {
        Log.d(LOG, "***************** onResume ************");
        super.onResume();
    }


    private void setFields() {
        image = (ImageView) findViewById(R.id.CAM_image);
        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (resultCode == Activity.RESULT_OK) {
                        new PhotoTask().execute();
                    }
                    pictureChanged = true;
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                Uri videoUri = data.getData();
                new FileTask().execute(videoUri);
                //mVideoView.setVideoURI(videoUri);
                break;
        }
    }

    private void sendThumbnail() {
        Log.e(LOG, "..........sendThumbnail ........: " + currentThumbFile.getAbsolutePath());

        BaseVolley.getUploadUrl(ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    ToastUtil.errorToast(ctx, response.getMessage());
                    return;
                }
                String url = response.getUploadUrl().getUrl();
                BlobUpload.upload(url,currentThumbFile, ctx, new BlobUpload.BlobUploadListener() {
                    @Override
                    public void onImageUploaded(UploadBlobDTO response) {
                        Log.e(LOG, "###### Blob has been uploaded OK: servingUrl: "
                        + response.getServingUrl() +
                        " blobKey: " + response.getBlobKey());

                    }

                    @Override
                    public void onUploadError() {
                        Log.e(LOG, "###### Erroruploading blob");
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }
        });
    }


    class FileTask extends AsyncTask<Uri, Void, Integer> {

        @Override
        protected Integer doInBackground(Uri... uris) {
            Uri uri = uris[0];
            VideoClipDTO clip = new VideoClipDTO();
            clip.setUriString(uri.toString());
            File f;
            try {
                f = ImageUtil.getFileFromUri(ctx, uri);
                clip.setFilePath(f.getAbsolutePath());
                clip.setLength(f.length());
                if (tournament != null) {
                    clip.setTournamentID(tournament.getTournamentID());
                    clip.setTournamentName(tournament.getTourneyName());
                }
                if (golfGroup != null) {
                    clip.setGolfGroupID(golfGroup.getGolfGroupID());
                    clip.setGolfGroupName(golfGroup.getGolfGroupName());
                }
                if (vcc == null) {
                    vcc = new VideoClipContainer();
                }
                vcc.getVideoClips().add(0, clip);
                CacheVideoUtil.cacheVideo(ctx, vcc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    }

    String mCurrentPhotoPath;
    File photoFile;
    VideoClipContainer vcc;
    boolean isUploaded;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void dispatchTakeVideoIntent() {
        final Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        CacheVideoUtil.getCachedVideo(ctx, new CacheVideoUtil.CacheVideoListener() {
            @Override
            public void onDataDeserialized(VideoClipContainer x) {
                vcc = x;
                if (vcc != null) {
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }
            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("pic", "Fuck!", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_video) {
            dispatchTakeVideoIntent();
            return true;
        }
        if (item.getItemId() == R.id.menu_upload) {
            sendThumbnail();
            return true;
        }
        if (item.getItemId() == R.id.menu_camera) {
            dispatchTakePictureIntent();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isUploaded) {
            Log.d(LOG, "onBackPressed ... picture uploaded");
           setResult(RESULT_OK);
        } else {
            Log.d(LOG, "onBackPressed ... cancelled");
            setResult(RESULT_CANCELED);
        }
        finish();
    }
    Menu mMenu;
    int type;

    GolfGroupDTO golfGroup;
    TournamentDTO tournament;
    PlayerDTO player;
    ParentDTO parent;
    ScorerDTO scorer;
    AdministratorDTO admin;
    boolean pictureChanged;
    Context ctx;
    Uri fileUri;
    public static final int CAPTURE_IMAGE = 3, GOLF_GROUP_PICTURE = 1, TOURNAMENT_PICTURE = 2, PLAYER_PICTURE = 5,
            PARENT_PICTURE = 6, SCORER_PICTURE = 7, ADMIN_PICTURE = 8;

    Bitmap bitmapForScreen;

    class PhotoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            //Log.e("pic", " file length: " + photoFile.length());
            pictureChanged = false;
            ExifInterface exif = null;
            fileUri = Uri.fromFile(photoFile);
            if (fileUri != null) {
                try {
                    exif = new ExifInterface(photoFile.getAbsolutePath());
                    String orient = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    Log.e("pic", "Orientation says: " + orient);
                    float rotate = 0;
                    if (orient.equalsIgnoreCase("6")) {
                        rotate = 90f;
                    }
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                        getLog(bm, "Raw Camera");
                        //scale and rotate for the screen
                        Matrix matrix = new Matrix();
                        matrix.postScale(1.0f, 1.0f);
                        matrix.postRotate(rotate);
                        bitmapForScreen = Bitmap.createBitmap
                                (bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                        getLog(bitmapForScreen, "Screen");
                        //get thumbnail for upload
                        Matrix matrixThumbnail = new Matrix();
                        matrixThumbnail.postScale(0.4f, 0.4f);
                        //matrixThumbnail.postRotate(rotate);
                        Bitmap thumb = Bitmap.createBitmap
                                (bitmapForScreen, 0, 0, bitmapForScreen.getWidth(),
                                        bitmapForScreen.getHeight(), matrixThumbnail, true);
                        getLog(thumb, "Thumb");
                        //get resized "full" size for upload
                        Matrix matrixF = new Matrix();
                        matrixF.postScale(0.6f, 0.6f);
                        //matrixF.postRotate(rotate);
                        Bitmap fullBm = Bitmap.createBitmap
                                (bitmapForScreen, 0, 0, bitmapForScreen.getWidth(),
                                        bitmapForScreen.getHeight(), matrixF, true);
                        getLog(fullBm, "Full");
                        currentFullFile = ImageUtil.getFileFromBitmap(fullBm, "m" + System.currentTimeMillis() + ".jpg");
                        currentThumbFile = ImageUtil.getFileFromBitmap(thumb, "t" + System.currentTimeMillis() + ".jpg");
                        thumbUri = Uri.fromFile(currentThumbFile);
                        fullUri = Uri.fromFile(currentFullFile);
                        getFileLengths();
                    } catch (Exception e) {
                        Log.e("pic", "Fuck it!", e);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;
                }

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.camera_error));
                return;
            }
            if (thumbUri != null) {
                pictureChanged = true;
                image.setImageBitmap(bitmapForScreen);
                sendThumbnail();
            }
        }
    }

    private void getLog(Bitmap bm, String which) {
        Log.e(LOG, which + " - bitmap: width: "
                + bm.getWidth() + " height: "
                + bm.getHeight() + " rowBytes: "
                + bm.getRowBytes());
    }

    private void getFileLengths() {
        Log.i(LOG, "Thumbnail file length: " + currentThumbFile.length());
        Log.i(LOG, "Full file length: " + currentFullFile.length());

    }

    ImageView image;
    File currentThumbFile, currentFullFile;
    Uri thumbUri, fullUri;
    static final String LOG = "BlobActivity";
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != R.id.none) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        renderResult();
    }
    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        /**
         * Initialize the correct effect based on the selected menu/action item
         */
        if (mCurrentEffect == R.id.none) {
        } else if (mCurrentEffect == R.id.autofix) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_AUTOFIX);
            mEffect.setParameter("scale", 0.5f);

        } else if (mCurrentEffect == R.id.bw) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BLACKWHITE);
            mEffect.setParameter("black", .1f);
            mEffect.setParameter("white", .7f);

        } else if (mCurrentEffect == R.id.brightness) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 2.0f);

        } else if (mCurrentEffect == R.id.contrast) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_CONTRAST);
            mEffect.setParameter("contrast", 1.4f);

        } else if (mCurrentEffect == R.id.crossprocess) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_CROSSPROCESS);

        } else if (mCurrentEffect == R.id.documentary) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_DOCUMENTARY);

        } else if (mCurrentEffect == R.id.duotone) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_DUOTONE);
            mEffect.setParameter("first_color", Color.YELLOW);
            mEffect.setParameter("second_color", Color.DKGRAY);

        } else if (mCurrentEffect == R.id.filllight) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FILLLIGHT);
            mEffect.setParameter("strength", .8f);

        } else if (mCurrentEffect == R.id.fisheye) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FISHEYE);
            mEffect.setParameter("scale", .5f);

        } else if (mCurrentEffect == R.id.flipvert) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FLIP);
            mEffect.setParameter("vertical", true);

        } else if (mCurrentEffect == R.id.fliphor) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FLIP);
            mEffect.setParameter("horizontal", true);

        } else if (mCurrentEffect == R.id.grain) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_GRAIN);
            mEffect.setParameter("strength", 1.0f);

        } else if (mCurrentEffect == R.id.grayscale) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_GRAYSCALE);

        } else if (mCurrentEffect == R.id.lomoish) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_LOMOISH);

        } else if (mCurrentEffect == R.id.negative) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_NEGATIVE);

        } else if (mCurrentEffect == R.id.posterize) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_POSTERIZE);

        } else if (mCurrentEffect == R.id.rotate) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_ROTATE);
            mEffect.setParameter("angle", 180);

        } else if (mCurrentEffect == R.id.saturate) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SATURATE);
            mEffect.setParameter("scale", .5f);

        } else if (mCurrentEffect == R.id.sepia) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SEPIA);

        } else if (mCurrentEffect == R.id.sharpen) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SHARPEN);

        } else if (mCurrentEffect == R.id.temperature) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TEMPERATURE);
            mEffect.setParameter("scale", .9f);

        } else if (mCurrentEffect == R.id.tint) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TINT);
            mEffect.setParameter("tint", Color.MAGENTA);

        } else if (mCurrentEffect == R.id.vignette) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_VIGNETTE);
            mEffect.setParameter("scale", .5f);

        } else {
        }
    }

    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != R.id.none) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        }
        else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }
    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);
        mImageWidth = bitmapForScreen.getWidth();
        mImageHeight = bitmapForScreen.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapForScreen, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    int mCurrentEffect;
}