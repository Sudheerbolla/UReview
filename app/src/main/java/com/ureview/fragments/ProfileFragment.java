package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CustomTextView txtFollowersCount, txtFollowers, txtFollowingCount, txtFollowing;
    private MainActivity mainActivity;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);

        viewPager = rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = rootView.findViewById(R.id.profileTabs);

        txtFollowersCount = rootView.findViewById(R.id.txtFollowersCount);
        txtFollowers = rootView.findViewById(R.id.txtFollowers);
        txtFollowingCount = rootView.findViewById(R.id.txtFollowingCount);
        txtFollowing = rootView.findViewById(R.id.txtFollowing);

        tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.colorDarkGrey),
                ContextCompat.getColor(getActivity(), R.color.app_text_color));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.app_text_color));
        tabLayout.setupWithViewPager(viewPager);

        txtFollowing.setOnClickListener(this);
        txtFollowers.setOnClickListener(this);
        txtFollowersCount.setOnClickListener(this);
        txtFollowingCount.setOnClickListener(this);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new StatsFragment(), "Stats");
        adapter.addFragment(new VideosFragment(), "Videos");
        adapter.addFragment(new AboutFragment(), "About");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        mainActivity.setFollowersFragment();
//        mainActivity.replaceFragment(FollowersFragment.newInstance(), true, R.id.mainContainer);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
