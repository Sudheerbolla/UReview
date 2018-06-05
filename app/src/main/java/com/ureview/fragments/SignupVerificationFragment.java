package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.models.CountriesModel;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomDialog;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class SignupVerificationFragment extends BaseFragment implements ISearchClickListener, View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private SplashActivity splashActivity;
    private CountriesModel currentCountriesModel;
    private CustomTextView txtCountryCode, txtSendVerfCode;
    private CustomDialog customDialog;
    private String firstName, lastName, email, token, deviceToken;

    public static SignupVerificationFragment newInstance() {
        return new SignupVerificationFragment();
    }

    public static SignupVerificationFragment newInstance(Bundle bundle) {
        SignupVerificationFragment signup1Fragment = new SignupVerificationFragment();
        signup1Fragment.setArguments(bundle);
        return signup1Fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        currentCountriesModel = StaticUtils.getCurrentCountryModel(splashActivity);
        deviceToken = LocalStorage.getInstance(splashActivity).getString(LocalStorage.PREF_DEVICE_TOKEN, "");
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            token = bundle.getString("token");
            email = bundle.getString("email");
        }
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
        if (text.equalsIgnoreCase(BaseApplication.userInfoModel.otp)) {
            requestForVerifyOtpWS(text);
        } else {
            StaticUtils.showToast(splashActivity, "Wrong OTP Entered");
        }
    }

    private void requestForVerifyOtpWS(String code) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", LocalStorage.getInstance(splashActivity).getString(LocalStorage.PREF_USER_ID, ""));
            jsonObjectReq.put("otp", code);
            jsonObjectReq.put("platform", "android");
            jsonObjectReq.put("device_token", deviceToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().loginVerifyOTP(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(splashActivity, WSUtils.REQ_FOR_CHECK_LOGIN_OTP, call, this);
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

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        if (response != null) {
            Log.e("response: ", response.toString());
            switch (requestCode) {
                case WSUtils.REQ_FOR_CHECK_LOGIN_OTP:
                    parseCheckUserOTPResponse((JsonObject) response);
                    break;
                default:
                    break;
            }
        }
    }

    private void parseCheckUserOTPResponse(JsonObject response) {
        if (response.has("status")) {
            if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                if (response.has("message")) {
                    StaticUtils.showToast(splashActivity, response.get("message").getAsString());
                }
                if (response.has("userInfo")) {
                    BaseApplication.userInfoModel = new UserInfoModel(response.get("userInfo").getAsJsonObject());
                    LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_ID, BaseApplication.userInfoModel.userid);
                }

                startActivity(new Intent(splashActivity, MainActivity.class));
                splashActivity.finishAffinity();

            } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                StaticUtils.showToast(splashActivity, response.get("message").getAsString());
            }
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error: ", error);
    }

    @Override
    public void noInternetConnection(int requestCode) {

    }
}
