package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.views.CustomTextView;

public class AboutFragment extends BaseFragment {

    private View rootView;
    private MainActivity mainActivity;
    private UserInfoModel userInfoModel;
    private CustomTextView txtEmail, txtAge, txtPhone, txtAboutMe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userInfoModel = BaseApplication.userInfoModel;
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtAboutMe = rootView.findViewById(R.id.txtAboutMe);
        txtAge = rootView.findViewById(R.id.txtAge);
        txtEmail = rootView.findViewById(R.id.txtEmail);
        txtPhone = rootView.findViewById(R.id.txtPhone);
        updateData();
    }

    public void updateData() {
        if (txtPhone != null) txtPhone.setText(userInfoModel.mobile);
        if (txtEmail != null) txtEmail.setText(userInfoModel.email);
        if (txtAge != null) txtAge.setText(userInfoModel.age);
        if (txtAboutMe != null && !TextUtils.isEmpty(userInfoModel.user_description))
            txtAboutMe.setText(userInfoModel.user_description);
    }
}
