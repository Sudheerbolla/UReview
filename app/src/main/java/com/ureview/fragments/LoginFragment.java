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
import com.ureview.utils.views.CustomTextView;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private CustomTextView txtTwitterLogin, txtFbLogin, txtInstagramLogin;
    private SplashActivity splashActivity;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.setTopBar(LoginFragment.class.getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(LoginFragment.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        txtTwitterLogin = rootView.findViewById(R.id.txtTwitterLogin);
        txtFbLogin = rootView.findViewById(R.id.txtFbLogin);
        txtInstagramLogin = rootView.findViewById(R.id.txtInstagramLogin);

        txtTwitterLogin.setOnClickListener(this);
        txtFbLogin.setOnClickListener(this);
        txtInstagramLogin.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtInstagramLogin:
                break;
            case R.id.txtTwitterLogin:
                break;
            case R.id.txtFbLogin:
                if (!LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, true)) {
                    splashActivity.replaceFragment(Signup1Fragment.newInstance(), true, R.id.splashContainer);
                } else {
                    startActivity(new Intent(splashActivity, MainActivity.class));
                    splashActivity.finishAffinity();
                }
                break;
            default:
                break;
        }
    }
}
