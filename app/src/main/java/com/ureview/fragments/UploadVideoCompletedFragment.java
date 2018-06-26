package com.ureview.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.utils.StaticUtils;

import java.io.File;

public class UploadVideoCompletedFragment extends BaseFragment {
    private View rootView;
    private MainActivity mainActivity;

    public static UploadVideoCompletedFragment newInstance() {
        return new UploadVideoCompletedFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("Uploading video", "", "", false, false, false, false, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload_video_completed, container, false);
        rootView.findViewById(R.id.txtDone).setOnClickListener(view -> {
            mainActivity.clearBackStackCompletely();
            mainActivity.setHomeFragment();
        });
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        StaticUtils.deleteDir(moviesDir);
        return rootView;
    }

}
