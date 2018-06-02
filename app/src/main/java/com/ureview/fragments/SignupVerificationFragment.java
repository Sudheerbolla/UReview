package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.utils.views.CustomDialog;

public class SignupVerificationFragment extends BaseFragmentNew implements ISearchClickListener {
    private View rootView;
    private SplashActivity splashActivity;

    public static SignupVerificationFragment newInstance() {
        return new SignupVerificationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(SignupVerificationFragment.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup_verification, container, false);
        rootView.findViewById(R.id.txtSendVerfCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialog(splashActivity, SignupVerificationFragment.this);
                customDialog.show();
//                splashActivity.replaceFragment(Signup1Fragment.newInstance(), false, R.id.splashContainer);
            }
        });
        return rootView;
    }

    CustomDialog customDialog;

    @Override
    public void onClick(String text) {
        customDialog.dismiss();
        splashActivity.replaceFragment(Signup1Fragment.newInstance(), false, R.id.splashContainer);
    }
}
