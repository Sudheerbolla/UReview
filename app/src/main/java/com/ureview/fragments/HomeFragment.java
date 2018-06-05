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
import com.ureview.activities.MainActivity;
import com.ureview.adapters.HomeCategoryAdapter;
import com.ureview.adapters.NewsFeedAdapter;
import com.ureview.adapters.ProfileVideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.utils.views.CustomTextView;

public class HomeFragment extends BaseFragment implements IClickListener, View.OnClickListener {
    private View rootView;
    private RecyclerView rvCategories, rvNewsFeed, rvNearByVideos, rvTopRated;
    private NewsFeedAdapter newsFeedAdapter;
    private HomeCategoryAdapter homeCategoryAdapter;
    private MainActivity mainActivity;
    private CustomTextView txtSeeAllVideos, txtSeeAllTopRated;
    private ProfileVideosAdapter profileVideosAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rvNewsFeed = rootView.findViewById(R.id.rvNewsFeed);
        rvCategories = rootView.findViewById(R.id.rvCategories);
        rvNewsFeed.setNestedScrollingEnabled(false);
        rvCategories.setNestedScrollingEnabled(false);
        newsFeedAdapter = new NewsFeedAdapter(getActivity(), this);
        homeCategoryAdapter = new HomeCategoryAdapter(getActivity());
        rvNewsFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNewsFeed.setAdapter(newsFeedAdapter);
        rvCategories.setAdapter(homeCategoryAdapter);


        rvNearByVideos = rootView.findViewById(R.id.rvNearByVideos);
        rvTopRated = rootView.findViewById(R.id.rvTopRated);

        txtSeeAllVideos = rootView.findViewById(R.id.txtSeeAllVideos);
        txtSeeAllTopRated = rootView.findViewById(R.id.txtSeeAllTopRated);

        rvNearByVideos.setNestedScrollingEnabled(false);
        rvTopRated.setNestedScrollingEnabled(false);

        profileVideosAdapter = new ProfileVideosAdapter(getActivity());
        rvNearByVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvTopRated.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvNearByVideos.setAdapter(profileVideosAdapter);
        rvTopRated.setAdapter(profileVideosAdapter);

        txtSeeAllTopRated.setOnClickListener(this);
        txtSeeAllVideos.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view, int position) {
        mainActivity.setVideoReviewFragment();
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onClick(View view) {

    }
}
