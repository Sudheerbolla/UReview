package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.utils.views.CustomTextView;

public class Signup1Fragment extends BaseFragmentNew implements View.OnClickListener {

    private View rootView;
    private SplashActivity splashActivity;
    private CustomTextView txtNext;

    public static Signup1Fragment newInstance() {
        return new Signup1Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup1, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtNext = rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(this);
//        splashActivity.txtRight.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(Signup1Fragment.class.getSimpleName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNext:
                splashActivity.replaceFragment(Signup2Fragment.newInstance(), true, R.id.splashContainer);
                break;
//            case R.id.txtRight:
//                splashActivity.replaceFragment(Signup2Fragment.newInstance(), true, R.id.splashContainer);
//                break;
            default:
                break;
        }
    }
}
