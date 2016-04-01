package com.boha.malengagolf.library.util;

import com.boha.malengagolf.library.data.VideoClipDTO;

/**
 * Created by aubreyM on 2014/04/24.
 */
 public class AsyncVideoUpload {
    static String account;
    static VideoClipDTO videoClip;
    static long start, end;
//    public interface VideoUploadListener {
//        public void onUserAuthRequired(UserRecoverableAuthIOException auth);
//        public void onProgress(double progress);
//        public void onUploadComplete(String videoID);
//        public void onInitiationStarting();
//        public void onInitiationComplete();
//        public void onException(int e);
//    }
//    static VideoUploadListener videoUploadListener;
//    public static void uploadVideo(Context context, VideoClipDTO clip, String acct, VideoUploadListener listener) {
//        ctx = context;
//        videoUploadListener = listener;
//        videoClip = clip;
//        account = acct;
//        start = System.currentTimeMillis();
//        new UploadTask().execute();
//        Log.e(LOG, "YouTube video upload starting ....");
//    }
//    static class UploadTask extends AsyncTask<Void, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//
//            List<String> scopes = new ArrayList<String>();
//            scopes.add(YouTubeScopes.YOUTUBE);
//            scopes.add(YouTubeScopes.YOUTUBE_UPLOAD);
//            credential =
//                    GoogleAccountCredential.usingOAuth2(ctx, scopes);
//            credential.setSelectedAccountName(account);
//            youTube = new YouTube.Builder(httpTransport, jsonFactory, credential)
//                            .setApplicationName("MalengaGolf/1.0").build();
//
//            File videoFile = new File(videoClip.getFilePath());
//            if (!videoFile.exists()) {
//                return 1;
//            }
//            Log.d(LOG, "VideoFile length: " + videoFile.length() + " - " + videoFile.getAbsolutePath());
//            Video video = new Video();
//            VideoStatus status = new VideoStatus();
//            status.setPrivacyStatus("public");
//            video.setStatus(status);
//
//            VideoSnippet snippet = new VideoSnippet();
//            snippet.setTitle("MalengaGolf Upload via Java on " + new Date().toString());
//            snippet.setDescription(
//                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + new Date().toString());
//
//            // Set your keywords.
//            List<String> tags = new ArrayList<String>();
//            tags.add("unpack");
//            tags.add("tournament");
//            tags.add("golf");
//            tags.add("YouTube Data API V3");
//            tags.add("erase me");
//            snippet.setTags(tags);
//            video.setSnippet(snippet);
//
//            try {
//                InputStreamContent mediaContent = new InputStreamContent(
//                        VIDEO_FILE_FORMAT, new BufferedInputStream(new FileInputStream(videoFile)));
//                mediaContent.setLength(videoFile.length());
//                YouTube.Videos.Insert videoInsert = youTube.videos()
//                        .insert("snippet,statistics,status", video, mediaContent);
//                MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
//                uploader.setDirectUploadEnabled(false); //false = resumable
//
//                MediaHttpUploaderProgressListener progressListener =
//                        new MediaHttpUploaderProgressListener() {
//                            public void progressChanged(MediaHttpUploader uploader) throws IOException {
//
//                                switch (uploader.getUploadState()) {
//                                    case INITIATION_STARTED:
//                                        videoUploadListener.onInitiationStarting();
//                                        Log.i(LOG, "******* YouTube Initiation Started");
//                                        break;
//                                    case INITIATION_COMPLETE:
//                                        videoUploadListener.onInitiationComplete();
//                                        Log.i(LOG, "******* YouTube Initiation Completed");
//                                        break;
//                                    case MEDIA_IN_PROGRESS:
//                                        videoUploadListener.onProgress(uploader.getProgress());
//                                        Log.e(LOG, "******* YouTube Upload in progress .....");
//                                        Log.i(LOG, "******* YouTube Upload percentage: " + uploader.getProgress());
//                                        break;
//                                    case MEDIA_COMPLETE:
//                                        Log.i(LOG, "******* YouTube Upload Completed!");
//                                        break;
//                                    case NOT_STARTED:
//                                        Log.e(LOG, "******* YouTube Upload Not Started!");
//                                        break;
//                                }
//                            }
//                        };
//                uploader.setProgressListener(progressListener);
//                // Execute upload.
//                Video returnedVideo = videoInsert.execute();
//                videoClip.setYouTubeID(returnedVideo.getId());
//                videoClip.setDateUploaded(new Date().getTime());
//                videoUploadListener.onUploadComplete(returnedVideo.getId());
//                // Print out returned results.
//                Log.i(LOG, "\n================== Returned Video ==================\n");
//                Log.i(LOG, "  - Id: " + returnedVideo.getId());
//                Log.i(LOG, "  - Title: " + returnedVideo.getSnippet().getTitle());
//                Log.i(LOG, "  - Tags: " + returnedVideo.getSnippet().getTags());
//                Log.i(LOG, "  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
//                Log.i(LOG, "  - Video Count: " + returnedVideo.getStatistics().getViewCount());
////Br09WLvtoDU
//            } catch (UserRecoverableAuthIOException e) {
//                videoUploadListener.onUserAuthRequired(e);
//                Log.e(LOG, "YouTube Auth", e);
//                return ERROR_AUTH;
//            } catch (GoogleJsonResponseException e) {
//                Log.e(LOG, "YouTube ", e);
//                return ERROR_JSON;
//            } catch (IOException e) {
//                Log.e(LOG, "YouTube ", e);
//                return ERROR_IO_EXCEPTION;
//            } catch (Throwable t) {
//                Log.e(LOG, "YouTube ", t);
//                return ERROR_GENERIC;
//            }
//            return 0;
//        }
//        @Override
//        protected void onPostExecute(Integer res) {
//            if (res.intValue() > 0) {
//                videoUploadListener.onException(res.intValue());
//                return;
//            }
//
//        }
//    }
////    private void getUserAuth() {
// //
// //        startActivityForResult(mException.getIntent(), REQUEST_AUTH);
// //    }
//     static final int REQUEST_AUTH = 33;
//    static final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//    static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//    static GoogleAccountCredential credential;
//    static  YouTube youTube;
//
//    static  Context ctx;
//    static String VIDEO_FILE_FORMAT = "video/*";
//    static final String LOG = "AsyncVideoUpload";
//    UserRecoverableAuthIOException mException;
    public static final int ERROR_JSON = 4, ERROR_IO_EXCEPTION = 5, ERROR_GENERIC = 6, ERROR_AUTH = 3;
}
