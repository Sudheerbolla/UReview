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
import com.ureview.adapters.HomeCategoryAdapter;
import com.ureview.adapters.NewsFeedAdapter;

public class HomeFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rvCategories, rvNewsFeed;
    private NewsFeedAdapter newsFeedAdapter;
    private HomeCategoryAdapter homeCategoryAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rvNewsFeed = rootView.findViewById(R.id.rvNewsFeed);
        rvCategories = rootView.findViewById(R.id.rvCategories);
        rvNewsFeed.setNestedScrollingEnabled(false);
        rvCategories.setNestedScrollingEnabled(false);
        newsFeedAdapter = new NewsFeedAdapter(getActivity());
        homeCategoryAdapter = new HomeCategoryAdapter(getActivity());
        rvNewsFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNewsFeed.setAdapter(newsFeedAdapter);
        rvCategories.setAdapter(homeCategoryAdapter);
        return rootView;
    }

}
