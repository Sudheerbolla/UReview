package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.utils.views.CustomTextView;

public class UploadVideoFragment extends BaseFragment {
    private View rootView;
    private CustomTextView txtCompleteVideo, txtLocation;
    private MainActivity mainActivity;

    public static UploadVideoFragment newInstance() {
        return new UploadVideoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload_video, container, false);
        txtCompleteVideo = rootView.findViewById(R.id.txtCompleteVideo);
        txtLocation = rootView.findViewById(R.id.txtLocation);
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.replaceFragment(LocationRadiusFragment.newInstance(), true, R.id.mainContainer);
            }
        });
        txtCompleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.setUploadVideoCompletedFragment();
            }
        });
        return rootView;
    }

}
