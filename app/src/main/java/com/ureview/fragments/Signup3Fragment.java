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

public class Signup3Fragment extends BaseFragment {
    private View rootView;
    private SplashActivity splashActivity;

    public static Signup3Fragment newInstance() {
        return new Signup3Fragment();
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
            splashActivity.setTopBar(Signup3Fragment.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup3, container, false);
        rootView.findViewById(R.id.txtDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(splashActivity, MainActivity.class));
                splashActivity.finishAffinity();
            }
        });
        return rootView;
    }

}
