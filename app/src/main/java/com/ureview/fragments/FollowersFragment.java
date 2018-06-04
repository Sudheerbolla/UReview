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
import com.ureview.adapters.FollowersAdapter;

public class FollowersFragment extends BaseFragment {

    private View rootView;
    private RecyclerView rvFollowers;
    private FollowersAdapter followersAdapter;

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_followers, container, false);

        rvFollowers = rootView.findViewById(R.id.rvFollowers);
        followersAdapter = new FollowersAdapter(getActivity());
        rvFollowers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFollowers.setAdapter(followersAdapter);
        return rootView;
    }

}
