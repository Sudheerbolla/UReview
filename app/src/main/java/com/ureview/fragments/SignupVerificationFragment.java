package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.models.CountriesModel;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomDialog;
import com.ureview.utils.views.CustomTextView;

public class SignupVerificationFragment extends BaseFragment implements ISearchClickListener, View.OnClickListener {

    private View rootView;
    private SplashActivity splashActivity;
    private CountriesModel currentCountriesModel;
    private CustomTextView txtCountryCode, txtSendVerfCode;
    private CustomDialog customDialog;

    public static SignupVerificationFragment newInstance() {
        return new SignupVerificationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        currentCountriesModel = StaticUtils.getCurrentCountryModel(splashActivity);
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
        txtCountryCode = rootView.findViewById(R.id.txtCountryCode);
        txtSendVerfCode = rootView.findViewById(R.id.txtSendVerfCode);
        txtSendVerfCode.setOnClickListener(this);
        txtCountryCode.setOnClickListener(this);
        if (currentCountriesModel != null) {
            txtCountryCode.setText(currentCountriesModel.countryCode);
        }
        return rootView;
    }


    @Override
    public void onClick(String text) {
        customDialog.dismiss();
        startActivity(new Intent(splashActivity, MainActivity.class));
        splashActivity.finishAffinity();
    }

    private void openCountriesDialog() {
        CountrySelectionFragment countrySelectionFragment = CountrySelectionFragment.newInstance(currentCountriesModel != null ? currentCountriesModel.countryCode : "");
        countrySelectionFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
        countrySelectionFragment.show(splashActivity.getSupportFragmentManager(), "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCountryCode:
                openCountriesDialog();
                break;
            case R.id.txtSendVerfCode:
                customDialog = new CustomDialog(splashActivity, SignupVerificationFragment.this);
                customDialog.show();
                break;
            default:
                break;
        }
    }

    public static final int DIALOG_FRAGMENT = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    currentCountriesModel = data.getParcelableExtra("countriesModel");
                    txtCountryCode.setText("+" + currentCountriesModel.countryCode);
                }
                break;
        }
    }
}
