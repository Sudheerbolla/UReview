package com.ureview.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.utils.views.CustomTextView;

public class SplashFragment extends BaseFragment {

    private View rootView;
    private SplashActivity splashActivity;
    private ImageView imgLogo;
    private CustomTextView img1, img2, img3, txtLogo;

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
    }

    private void initComponents() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, 2800);
    }

    private void navigateToLogin() {
//        if (LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, true)) {
//            splashActivity.replaceFragment(IntroFragment.newInstance(), false, R.id.splashContainer);
//        } else if (LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, true)) {
//            startActivity(new Intent(splashActivity, MainActivity.class));
//            splashActivity.finishAffinity();
//        } else {
//            splashActivity.replaceFragment(LandingFragment.newInstance(new Bundle()), false, R.id.splashContainer);
//        }
    }

}
