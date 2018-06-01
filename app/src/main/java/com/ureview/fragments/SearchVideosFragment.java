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
import com.ureview.adapters.SearchVideosAdapter;

public class SearchVideosFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rvSearchVideo;
    private SearchVideosAdapter searchVideosAdapter;

    public static SearchVideosFragment newInstance() {
        return new SearchVideosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_video, container, false);

        rvSearchVideo = rootView.findViewById(R.id.rvSearchVideo);
        searchVideosAdapter = new SearchVideosAdapter(getActivity());
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchVideo.setAdapter(searchVideosAdapter);
        return rootView;
    }

}
