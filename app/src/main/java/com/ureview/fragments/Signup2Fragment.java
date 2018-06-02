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

public class Signup2Fragment extends BaseFragmentNew implements View.OnClickListener {
    private View rootView;
    private CustomTextView txtSignup;
    private SplashActivity splashActivity;

    public static Signup2Fragment newInstance() {
        return new Signup2Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup2, container, false);
        txtSignup = rootView.findViewById(R.id.txtSignup);
        txtSignup.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSignup:
//                CustomDialog dialog = new CustomDialog(getActivity());
//                dialog.show();
                splashActivity.replaceFragment(Signup3Fragment.newInstance(), true, R.id.splashContainer);
                break;
        }
    }
}
