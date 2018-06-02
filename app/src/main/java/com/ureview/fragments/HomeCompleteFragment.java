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
import com.ureview.adapters.ProfileVideosAdapter;
import com.ureview.utils.views.CustomTextView;

public class HomeCompleteFragment extends BaseFragmentNew implements View.OnClickListener {
    private View rootView;
    private RecyclerView rvCategories, rvNewsFeed, rvTopRated;
    private HomeCategoryAdapter homeCategoryAdapter;
    private ProfileVideosAdapter profileVideosAdapter;
    private CustomTextView txtSeeAllVideos, txtSeeAllTopRated;
    private MainActivity mainActivity;

    public static HomeCompleteFragment newInstance() {
        return new HomeCompleteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_complete, container, false);

        rvNewsFeed = rootView.findViewById(R.id.rvNewsFeed);
        rvTopRated = rootView.findViewById(R.id.rvTopRated);
        rvCategories = rootView.findViewById(R.id.rvCategories);

        txtSeeAllVideos = rootView.findViewById(R.id.txtSeeAllVideos);
        txtSeeAllTopRated = rootView.findViewById(R.id.txtSeeAllTopRated);

        rvNewsFeed.setNestedScrollingEnabled(false);
        rvCategories.setNestedScrollingEnabled(false);
        rvTopRated.setNestedScrollingEnabled(false);

        profileVideosAdapter = new ProfileVideosAdapter(getActivity());
        homeCategoryAdapter = new HomeCategoryAdapter(getActivity());
        rvNewsFeed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvTopRated.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvNewsFeed.setAdapter(profileVideosAdapter);
        rvTopRated.setAdapter(profileVideosAdapter);
        rvCategories.setAdapter(homeCategoryAdapter);

        txtSeeAllTopRated.setOnClickListener(this);
        txtSeeAllVideos.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        mainActivity.setHomeFragment();
    }
}
