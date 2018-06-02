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
import com.ureview.utils.views.CustomTextView;

public class LoginFragment extends BaseFragmentNew implements View.OnClickListener {
    private View rootView;
    private CustomTextView txtTwitterLogin, txtFbLogin, txtGplusLogin;
    private SplashActivity splashActivity;

    public static LoginFragment newInstance() {
        return new LoginFragment();
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
            splashActivity.setTopBar(Signup1Fragment.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        txtTwitterLogin = rootView.findViewById(R.id.txtTwitterLogin);
        txtFbLogin = rootView.findViewById(R.id.txtFbLogin);
        txtGplusLogin = rootView.findViewById(R.id.txtGplusLogin);

        txtTwitterLogin.setOnClickListener(this);
        txtFbLogin.setOnClickListener(this);
        txtGplusLogin.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(splashActivity, MainActivity.class));
        splashActivity.finishAffinity();
    }
}
