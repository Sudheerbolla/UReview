package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.utils.views.CustomDialog;
import com.ureview.utils.views.CustomTextView;

public class Signup2Fragment extends BaseFragment {
    private View rootView;
    private CustomTextView txtSignup;

    public static Signup2Fragment newInstance() {
        return new Signup2Fragment();
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
                CustomDialog dialog = new CustomDialog(getActivity());
                dialog.show();
                break;
        }
    }
}
