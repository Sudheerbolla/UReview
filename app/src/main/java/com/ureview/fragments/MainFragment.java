package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ureview.R;
import com.ureview.activities.SplashActivity;

public class MainFragment extends BaseFragment {
    private View rootView;
    private FrameLayout mainContainer;
//    private TabLayout tabLayout;

    private SplashActivity splashActivity;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        splashActivity = (SplashActivity) getActivity();
        mainContainer = rootView.findViewById(R.id.mainContainer);
//        tabLayout = rootView.findViewById(R.id.tabLayout);
        initTopBar(rootView);
//        initTabLayout(getActivity(), tabLayout);
        initBottomBar(rootView);

        rootView.findViewById(R.id.llHome).setOnClickListener(this);
        rootView.findViewById(R.id.llSearch).setOnClickListener(this);
        rootView.findViewById(R.id.llVideo).setOnClickListener(this);
        rootView.findViewById(R.id.llProfile).setOnClickListener(this);
        rootView.findViewById(R.id.llSettings).setOnClickListener(this);

        setReviewMapFragment();
    }

    private void setHomeFragment() {
        setToolBar("", "Mirpur 10, Dhaka", "", true, false,
                true, false, false);
        splashActivity.replaceFragment(HomeFragment.newInstance(), R.id.mainContainer);
    }

    private void setSearchFragment() {
        setToolBar("", "", "", false, false,
                false, true, false);
        splashActivity.replaceFragment(SearchFragment.newInstance(), R.id.mainContainer);
    }

    private void setNotificationsFragment() {
        setToolBar("Notifications", "", "", false, true,
                false, false, false);
        splashActivity.replaceFragment(NotificationsFragment.newInstance(), R.id.mainContainer);
    }

    private void setSettingsFragment() {
        setToolBar("Settings", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(SettingsFragment.newInstance(), R.id.mainContainer);
    }

    private void setFollowersFragment() {
        setToolBar("Followers", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(FollowersFragment.newInstance(), R.id.mainContainer);
    }

    private void setUploadVideoFragment() {
        setToolBar("Upload video", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(UploadVideoFragment.newInstance(), R.id.mainContainer);
    }

    private void setUploadVideoCompletedFragment() {
        setToolBar("Upload video", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(UploadVideoCompletedFragment.newInstance(), R.id.mainContainer);
    }

    private void setVideoReviewFragment() {
        setToolBar("Video Review", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(VideoReviewFragment.newInstance(), R.id.mainContainer);
    }

    private void setReviewMapFragment() {
        setToolBar("Review Maps", "", "", false, true,
                true, false, false);
        splashActivity.replaceFragment(ReviewMapsFragment.newInstance(), R.id.mainContainer);
    }

    private void setProfileFragment() {
        splashActivity.replaceFragment(ProfileFragment.newInstance(), R.id.splashContainer);
    }

    private void setLoginFragment() {
        setToolBar("Log In", "", "Sign Up", false, true,
                false, false, false);
//        tabLayout.setVisibility(View.GONE);
        rootView.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        splashActivity.replaceFragment(LoginFragment.newInstance(), R.id.mainContainer);
    }

    private void setSignup1Fragment() {
        setToolBar("Sign Up", "", "Log In", false, true,
                false, false, false);
//        tabLayout.setVisibility(View.GONE);
        rootView.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        splashActivity.replaceFragment(Signup1Fragment.newInstance(), R.id.mainContainer);
    }

    private void setSignup2Fragment() {
        setToolBar("Sign Up", "", "Log In", false, true,
                false, false, false);
//        tabLayout.setVisibility(View.GONE);
        rootView.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        splashActivity.replaceFragment(Signup2Fragment.newInstance(), R.id.mainContainer);
    }

    private void setSignupVerificationFragment() {
        setToolBar("Sign Up", "", "Log In", false, true,
                false, false, false);
//        tabLayout.setVisibility(View.GONE);
        rootView.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        splashActivity.replaceFragment(SignupVerificationFragment.newInstance(), R.id.mainContainer);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llHome:
                setSelectedTab(imgHome, imgHomeView);
                break;
            case R.id.llSearch:
                setSelectedTab(imgSearchB, imgSearchView);
                break;
            case R.id.llVideo:
                setSelectedTab(imgVideo, null);
                break;
            case R.id.llProfile:
                setSelectedTab(imgProfile, imgProfileView);
                break;
            case R.id.llSettings:
                setSelectedTab(imgSettings, imgSettingsView);
                break;
        }
    }

    private void setSelectedTab(ImageView selectedView, ImageView imgH) {
        imgHome.setSelected(false);
        imgSearchB.setSelected(false);
        imgVideo.setSelected(false);
        imgProfile.setSelected(false);
        imgSettings.setSelected(false);

        imgHomeView.setVisibility(View.GONE);
        imgSearchView.setVisibility(View.GONE);
        imgProfileView.setVisibility(View.GONE);
        imgSettingsView.setVisibility(View.GONE);

        selectedView.setSelected(true);
        if (imgH != null)
            imgH.setVisibility(View.VISIBLE);
    }
}
