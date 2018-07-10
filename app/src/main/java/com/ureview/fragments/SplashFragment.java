package com.ureview.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.utils.LocalStorage;

public class SplashFragment extends BaseFragment {

    private View rootView;
    private SplashActivity splashActivity;

    public SplashFragment() {
    }

    public static SplashFragment newInstance() {
        SplashFragment categoryDetailsFragment = new SplashFragment();
        categoryDetailsFragment.setArguments(new Bundle());
        return categoryDetailsFragment;
    }

    public static SplashFragment newInstance(Bundle bundle) {
        SplashFragment categoryDetailsFragment = new SplashFragment();
        categoryDetailsFragment.setArguments(bundle);
        return categoryDetailsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_splash, container, false);
        initComponents();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.changeStatusBarColorToAppColorLight();
        splashActivity.setTopBar("SplashFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar("SplashFragment");
        }
    }

    private void initComponents() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        if (LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, true)) {
            splashActivity.replaceFragment(IntroFragment.newInstance(), false, R.id.splashContainer);
        } else if (LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, false)) {
            startActivity(new Intent(splashActivity, MainActivity.class));
            splashActivity.finishAffinity();
        } else {
            splashActivity.replaceFragment(LoginFragment.newInstance(), false, R.id.splashContainer);
        }
    }

}
