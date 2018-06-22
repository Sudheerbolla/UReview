package com.ureview.activities;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.ureview.R;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class VideoRecorderWorking extends BaseActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 100;
    private VideoView videoView;
    private RangeSeekBar rangeSeekBar;
    private Runnable r;
    private FFmpeg ffmpeg;
    private ProgressDialog progressDialog;
    private Uri selectedVideoUri;
    private static final String TAG = "Sudheer";
    private static final String POSITION = "position";
    private CustomTextView cutVideo;

    private int stopPosition;
    private TextView tvLeft, tvRight;
    private RelativeLayout mainlayout;
    private String filePath;
    private int duration;
    private Context mContext;
    private int option = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);

        this.setFinishOnTouchOutside(false);

        mContext = this;

        cutVideo = findViewById(R.id.cropVideo);
        videoView = findViewById(R.id.VideoView);
        rangeSeekBar = findViewById(R.id.rangeSeekBar);
        mainlayout = findViewById(R.id.mainlayout);
        tvLeft = findViewById(R.id.tvLeft);
        tvRight = findViewById(R.id.tvRight);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);
        rangeSeekBar.setEnabled(false);
        loadFFMpegBinary();
        cutVideo.setOnClickListener(v -> {
            if (selectedVideoUri != null) {
                if (rangeSeekBar.getSelectedMaxValue().intValue() - rangeSeekBar.getSelectedMinValue().intValue() > 59) {
                    StaticUtils.showToast(VideoRecorderWorking.this, "Video cannot be more than 60 seconds in duration");
                } else if (rangeSeekBar.getSelectedMinValue().intValue() == rangeSeekBar.getAbsoluteMinValue().intValue()
                        && rangeSeekBar.getSelectedMaxValue().intValue() == rangeSeekBar.getAbsoluteMaxValue().intValue()) {
                    Intent intent = new Intent();
                    intent.putExtra(StaticUtils.FILEURI, selectedVideoUri);
                    setResult(StaticUtils.VIDEO_TRIMMING_RESULT, intent);
                    finish();
                } else {
                    proceedWithVideoOperation();
                    try {
                        if (new File(selectedVideoUri.getPath()).length() / (1024 * 1024) > 5) {
                            StaticUtils.showToast(VideoRecorderWorking.this, "above 5 mb");
                        } else StaticUtils.showToast(VideoRecorderWorking.this, "below 5 mb");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else
                Snackbar.make(mainlayout, "Please upload a video", 4000).show();
        });

        findViewById(R.id.imgClose).setOnClickListener(view -> finish());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("selectedVideoUri")) {
            selectedVideoUri = bundle.getParcelable("selectedVideoUri");

            videoView.setVideoURI(selectedVideoUri);
            videoView.start();

            videoView.setOnPreparedListener(mp -> {
                duration = mp.getDuration() / 1000;
                tvLeft.setText("00:00:01");

                tvRight.setText(getTime(mp.getDuration() / 1000));
                mp.setLooping(true);
                rangeSeekBar.setRangeValues(0, duration);
                rangeSeekBar.setSelectedMinValue(1);
                rangeSeekBar.setSelectedMaxValue(duration);
                rangeSeekBar.setEnabled(true);
                if (duration > 60) {
                    cutVideo.setText("Trim Video and continue");
                } else {
                    cutVideo.setText("Upload Video");
                }
                rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        videoView.seekTo((int) minValue * 1000);
                        tvLeft.setText(getTime((int) bar.getSelectedMinValue()));
                        tvRight.setText(getTime((int) bar.getSelectedMaxValue()));
                        if (bar.getSelectedMaxValue().intValue() - bar.getSelectedMinValue().intValue() > 60) {
                            cutVideo.setText("Trim Video and continue");
                        } else {
                            cutVideo.setText("Upload Video");
                        }
                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(r = new Runnable() {
                    @Override
                    public void run() {
                        if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                            videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                        handler.postDelayed(r, 1000);
                    }
                }, 1000);

            });
        }
    }

    private void proceedWithVideoOperation() {
//        executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
        executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue(), rangeSeekBar.getSelectedMaxValue().intValue());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
        videoView.start();
    }

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }

    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d(TAG, "ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
            Log.d(TAG, "EXception no controlada : " + e);
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();

    }

    /**
     * Command for cutting video
     */
    private void executeCutVideoCommand(int startMs, int endMs) {
        option = 0;
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String filePrefix = "cut_video";
        String fileExtn = ".mp4";
        String yourRealPath = getPath(this, selectedVideoUri);
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
//        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
        String[] complexCommand = {"-ss", "" + startMs, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs), "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
        execFFmpegBinary(complexCommand);

    }

    private void executeCompressVideoCommand() {
        option = 1;
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String filePrefix = "compress_video";
        String fileExtn = ".mp4";
        String path = "file://" + filePath;
        String yourRealPath = getPath(this, Uri.parse(path));
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
//        ffmpeg -y -i path -s 480x320 -r 20 -c:v libx264 -preset ultrafast -c:a copy -me_method zero -tune fastdecode -tune zerolatency -strict -2 -b:v 1000k -pix_fmt yuv420p /storage/emulated/0/output.mp4
        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "480x320", "-r", "25", "-vcodec", "mpeg4", "-b:v", "500k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.filePath};
        execFFmpegBinary(complexCommand);
    }

    public static String[] combine(String[] arg1, String[] arg2, String[] arg3) {
        String[] result = new String[arg1.length + arg2.length + arg3.length];
        System.arraycopy(arg1, 0, result, 0, arg1.length);
        System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
        System.arraycopy(arg3, 0, result, arg1.length + arg2.length, arg3.length);
        return result;
    }

    /**
     * Executing ffmpeg binary
     */
    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
                    if (option == 0) {
                        executeCompressVideoCommand();
                        progressDialog.show();
                    } else if (option == 1) {
                        Intent intent = new Intent();
                        intent.putExtra(StaticUtils.FILEPATH, filePath);
                        setResult(StaticUtils.VIDEO_TRIMMING_RESULT, intent);
                        finish();
                    }
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    progressDialog.setMessage("progress : " + s);
                    progressDialog.setMessage("Processing...");
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    progressDialog.hide();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    boolean success = deleteDir(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     */
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri.
     */
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}
