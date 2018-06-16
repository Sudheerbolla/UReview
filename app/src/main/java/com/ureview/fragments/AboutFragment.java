package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CustomTextView;

public class AboutFragment extends BaseFragment {

    private View rootView;
    private MainActivity mainActivity;
    private UserInfoModel userInfoModel;
    private CustomTextView txtEmail, txtAge, txtPhone, txtAboutMe, txtAbout;
    private String userId;
    private LinearLayout linPersonal;

    public static AboutFragment newInstance(String userId) {
        AboutFragment followersFragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            if (!bundle.getString("userId").equalsIgnoreCase(userId)) {
                userInfoModel = ProfileFragment.otherInfoModel;
                hideSensitiveData = false;
            } else {
                userInfoModel = BaseApplication.userInfoModel;
                hideSensitiveData = true;
            }
        }
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

    boolean hideSensitiveData;

    private void initComponents() {
        txtAbout = rootView.findViewById(R.id.txtAbout);
        txtAboutMe = rootView.findViewById(R.id.txtAboutMe);
        txtAge = rootView.findViewById(R.id.txtAge);
        txtEmail = rootView.findViewById(R.id.txtEmail);
        txtPhone = rootView.findViewById(R.id.txtPhone);
        linPersonal = rootView.findViewById(R.id.linPersonal);

        if (getArguments().getString("userId").equalsIgnoreCase(userId)) {
            hideSensitiveData = false;
            userInfoModel = BaseApplication.userInfoModel;
        } else {
            hideSensitiveData = true;
            userInfoModel = ProfileFragment.otherInfoModel;
        }
        updateData(userInfoModel);
    }

    public void updateData(UserInfoModel userInfoModel) {
        if (hideSensitiveData) {
            if (userInfoModel != null) {
                if (txtAboutMe != null && !TextUtils.isEmpty(userInfoModel.user_description))
                    txtAboutMe.setText(userInfoModel.user_description);
                linPersonal.setVisibility(View.GONE);
            }
            txtAbout.setText("Description");
            return;
        } else {
            txtAbout.setText("About Me");
            linPersonal.setVisibility(View.VISIBLE);
        }
        if (userInfoModel != null) {
            if (txtPhone != null) txtPhone.setText(userInfoModel.mobile);
            if (txtEmail != null) txtEmail.setText(userInfoModel.email);
            if (txtAge != null) txtAge.setText(userInfoModel.age);
            if (txtAboutMe != null && !TextUtils.isEmpty(userInfoModel.user_description))
                txtAboutMe.setText(userInfoModel.user_description);
        }
    }
}
