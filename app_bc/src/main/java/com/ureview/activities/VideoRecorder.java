package com.ureview.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.ureview.R;
import com.ureview.utils.RuntimePermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoRecorder extends AppCompatActivity implements View.OnClickListener {
    VideoView videoView;
    Uri videoFileUri;
    public static int VIDEO_CAPTURED = 1;
    private Button captureVideoButton, playVideoButton, captureWithoutDataVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);

        captureVideoButton = findViewById(R.id.CaptureVideoButton);
        playVideoButton = findViewById(R.id.PlayVideoButton);
        captureWithoutDataVideoButton = findViewById(R.id.CaptureVideoWithoutDataButton);
        videoView = findViewById(R.id.VideoView);
        captureVideoButton.setOnClickListener(this);
        playVideoButton.setOnClickListener(this);
        captureWithoutDataVideoButton.setOnClickListener(this);
        playVideoButton.setEnabled(false);

        checkPermissions();

    }

    private boolean checkPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (RuntimePermissionUtils.checkPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.CAMERA);
        }
        if (RuntimePermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        final String[] permissions = neededPermissions.toArray(new String[neededPermissions.size()]);

        if (neededPermissions.size() > 0) {
            RuntimePermissionUtils.requestForPermission(this, permissions, 111);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        if (view == captureVideoButton) {
            Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            captureVideoIntent.putExtra("android.intent.extra.durationLimit", 30000);
            captureVideoIntent.putExtra("EXTRA_VIDEO_QUALITY", 0);
            startActivityForResult(captureVideoIntent, VIDEO_CAPTURED);
        } else if (view == captureWithoutDataVideoButton) {
            playVideoButton.setEnabled(false);
            Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            startActivity(captureVideoIntent);
        } else if (view == playVideoButton) {
            videoView.setVideoURI(videoFileUri);
            videoView.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            videoFileUri = data.getData();
            playVideoButton.setEnabled(true);
        }
    }
}
