package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ureview.R;
import com.ureview.adapters.HomeCategoryAdapter;
import com.ureview.adapters.NewsFeedAdapter;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

public class LocationRadiusFragment extends BaseFragment {
    private View rootView;
    private RangeSeekBar rangeSeekbar;
    private RecyclerView rvCategories, rvNewsFeed;
    private NewsFeedAdapter newsFeedAdapter;
    private HomeCategoryAdapter homeCategoryAdapter;

    public static LocationRadiusFragment newInstance() {
        return new LocationRadiusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location_radius, container, false);

        rangeSeekbar = rootView.findViewById(R.id.rangeSeekbar);

        rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Toast.makeText(getActivity(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
            }
        });

        rangeSeekbar.setNotifyWhileDragging(true);
        return rootView;
    }

}
