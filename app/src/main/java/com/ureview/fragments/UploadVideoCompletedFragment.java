package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;

public class UploadVideoCompletedFragment extends BaseFragment {
    private View rootView;
    private MainActivity mainActivity;

    public static UploadVideoCompletedFragment newInstance() {
        return new UploadVideoCompletedFragment();
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
        rootView.findViewById(R.id.txtDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clearBackStackCompletely();
                mainActivity.setHomeFragment();
            }
        });
        return rootView;
    }

}
