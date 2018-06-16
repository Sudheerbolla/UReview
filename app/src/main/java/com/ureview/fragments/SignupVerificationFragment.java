package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
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
import com.ureview.utils.views.CustomEditText;
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
    private String token, deviceToken;
    private UserInfoModel userInfoModel;
    private CustomEditText edtMobileNumber;
    public static final int DIALOG_FRAGMENT = 1;

    public static SignupVerificationFragment newInstance() {
        return new SignupVerificationFragment();
    }

    public static SignupVerificationFragment newInstance(String token) {
        SignupVerificationFragment signup1Fragment = new SignupVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        signup1Fragment.setArguments(bundle);
        return signup1Fragment;
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
            token = bundle.getString("token");
        }
        if (BaseApplication.userInfoModel != null) {
            userInfoModel = BaseApplication.userInfoModel;
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
        edtMobileNumber = rootView.findViewById(R.id.edtMobileNumber);
        txtSendVerfCode.setOnClickListener(this);
        txtCountryCode.setOnClickListener(this);
        if (currentCountriesModel != null) {
            txtCountryCode.setText("+" + currentCountriesModel.countryCode);
        }
        return rootView;
    }


    @Override
    public void onClick(String text) {
        customDialog.dismiss();
        if (BaseApplication.userInfoModel != null)
            if (text.equalsIgnoreCase(BaseApplication.userInfoModel.otp)) {
                requestForVerifyOtpWS(text);
            } else {
                StaticUtils.showToast(splashActivity, "Wrong OTP Entered");
            }
        else requestForVerifyOtpWS(text);
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
                String message = checkValidations();
                if (!TextUtils.isEmpty(message)) {
                    StaticUtils.showToast(splashActivity, message);
                } else {
                    requestForRegistrationWS();
                }
                break;
            default:
                break;
        }
    }

    private void requestForRegistrationWS() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("first_name", userInfoModel.first_name);
            jsonObjectReq.put("last_name", userInfoModel.last_name);
            jsonObjectReq.put("gender", userInfoModel.gender);
            jsonObjectReq.put("mobile", edtMobileNumber.getText().toString().trim());
            jsonObjectReq.put("email", userInfoModel.email);
            jsonObjectReq.put("auth_id", token);
            jsonObjectReq.put("auth_type", "facebook");
            jsonObjectReq.put("date_of_birth", userInfoModel.date_of_birth);
            jsonObjectReq.put("platform", "android");
            jsonObjectReq.put("device_token", deviceToken);
            jsonObjectReq.put("country_code", txtCountryCode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().userRegistration(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(splashActivity, WSUtils.REQ_FOR_USER_REGISTRATION, call, this);

    }

    private String checkValidations() {
        String mobileNumber = edtMobileNumber.getText().toString().trim();
        if (TextUtils.isEmpty(mobileNumber)) {
            edtMobileNumber.requestFocus();
            return "Please enter mobile number";
        }
        if (mobileNumber.length() < 10) {
            edtMobileNumber.requestFocus();
            return "Please enter a valid mobile number";
        }

        return "";
    }

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
                case WSUtils.REQ_FOR_USER_REGISTRATION:
                    parseUserRegistrationResponse((JsonObject) response);
                    break;
                default:
                    break;
            }
        }
    }

    private void parseUserRegistrationResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("message")) {
                        StaticUtils.showToast(splashActivity, response.get("message").getAsString());
                    }
                    if (response.has("userid")) {
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_ID, response.get("userid").getAsString());
                    }
                    if (response.has("userInfo")) {
                        BaseApplication.userInfoModel = new UserInfoModel(response.get("userInfo").getAsJsonObject());
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_ID, BaseApplication.userInfoModel.userid);
                        try {
                            String json = BaseApplication.userInfoModel.serialize();
                            LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                    customDialog = new CustomDialog(splashActivity, SignupVerificationFragment.this);
//                    customDialog.show();
                    startActivity(new Intent(splashActivity, MainActivity.class));
                    splashActivity.finishAffinity();

                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(splashActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    try {
                        String json = BaseApplication.userInfoModel.serialize();
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
