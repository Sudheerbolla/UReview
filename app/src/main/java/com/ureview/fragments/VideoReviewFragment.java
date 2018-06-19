package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.adapters.VideosAdapter;
import com.ureview.listeners.IVideosClickListener;
import com.ureview.models.VideoModel;

import java.util.ArrayList;

public class VideoReviewFragment extends BaseFragment implements IVideosClickListener {
    private View rootView;
    private RecyclerView rvTopVideos;
    private VideosAdapter videosAdapter;

    public static VideoReviewFragment newInstance() {
        return new VideoReviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video_review, container, false);
        rvTopVideos = rootView.findViewById(R.id.rvTopVideos);
        rvTopVideos.setNestedScrollingEnabled(false);
        videosAdapter = new VideosAdapter(getActivity(), this, false);
        rvTopVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvTopVideos.setAdapter(videosAdapter);
        return rootView;
    }

    @Override
    public void onClick(View view, ArrayList<VideoModel> videoModels, VideoModel videoModel, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
