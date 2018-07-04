package com.ureview.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.ureview.R;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.pickimage.ImageFilePath;

import java.io.File;

public class VideoRecorder extends BaseActivity {

    private VideoView videoView;
    private FFmpeg ffmpeg;
    private Uri selectedVideoUri;
    private static final String TAG = "Sudheer";
    private int stopPosition;
    private RelativeLayout mainlayout;
    private String filePath;
    private Context mContext;
    private int option = 0;
    private String cutPath;
    private RelativeLayout relProgress;
    private int videoWidth, videoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        this.setFinishOnTouchOutside(false);
        mContext = this;
        videoView = findViewById(R.id.VideoView);
        mainlayout = findViewById(R.id.mainlayout);
        relProgress = findViewById(R.id.relProgress);
        loadFFMpegBinary();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("selectedVideoUri")) {
            selectedVideoUri = bundle.getParcelable("selectedVideoUri");
            videoView.setVideoURI(selectedVideoUri);
            videoView.start();
            videoView.setOnPreparedListener(mp -> {
                mp.setVolume(0, 0);
                int duration = mp.getDuration() / 1000;
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                mp.setLooping(false);
                if (duration > 60) {
                    proceedWithVideoOperation(58);
                } else {
                    proceedWithVideoOperation(mp.getDuration());
                }
            });
        }
    }

    private void proceedWithVideoOperation(int end) {
//        executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
//        executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue(), rangeSeekBar.getSelectedMaxValue().intValue());
        executeCutVideoCommand(0, end);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
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
                .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
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
        String yourRealPath = ImageFilePath.getPath(this, selectedVideoUri);
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
        String[] cutCommand = {"-ss", "" + startMs, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs), "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
//        String[] compressCommand = {"-y", "-i", yourRealPath, "-s", "480x320", "-r", "25", "-vcodec", "mpeg4", "-b:v", "500k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.filePath};
//        String[] complexCommand = combine(cutCommand, compressCommand);
        execFFmpegBinary(cutCommand);

    }

    private void executeCompressVideoCommand() {
        cutPath = "file://" + filePath;
        option = 1;
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String filePrefix = "compress_video";
        String fileExtn = ".mp4";
        String yourRealPath = ImageFilePath.getPath(this, Uri.parse(cutPath));
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();

        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", String.valueOf(videoWidth).concat("x").concat(String.valueOf(videoHeight)), "-r", "20", "-vcodec", "mpeg4", "-b:v", "500k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.filePath};
//        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "480x720", "-r", "20", "-vcodec", "libx264", "-b:v", "750k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.filePath};
//        String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "720x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "750k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.filePath};
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
                        relProgress.setVisibility(View.VISIBLE);
                    } else if (option == 1) {
                        relProgress.setVisibility(View.GONE);
                        Intent intent = new Intent();
                        intent.putExtra(StaticUtils.FILEPATH, filePath);
                        setResult(StaticUtils.VIDEO_TRIMMING_RESULT, intent);
                        finish();
                    }
                }

                @Override
                public void onProgress(String s) {

                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    relProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

}
