package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;

public class StatsFragment extends BaseFragment {
    private View rootView;

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        return rootView;
    }

}
/*
http://18.216.101.112/user-videoview-statistics?user_id=1&year=2018
{
    "status": "success",
    "message": "Video view statistics!..",
    "video_views": [
        {
            "month": "Apr",
            "count": 0
        },
        {
            "month": "May",
            "count": 90
        },
        {
            "month": "Jun",
            "count": 31
        }
    ]
}*/