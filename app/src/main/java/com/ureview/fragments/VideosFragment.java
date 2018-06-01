package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.adapters.ProfileVideosAdapter;

public class VideosFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rvVideos;
    private ProfileVideosAdapter profileVideosAdapter;

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_videos, container, false);
        rvVideos = rootView.findViewById(R.id.rvVideos);
        profileVideosAdapter = new ProfileVideosAdapter(getActivity());
        rvVideos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvVideos.setNestedScrollingEnabled(false);
        rvVideos.setAdapter(profileVideosAdapter);
        return rootView;
    }

}
