package com.ureview.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;

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
import com.ureview.utils.Constants;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;

public class Signup1Fragment extends BaseFragment implements View.OnClickListener, ISearchClickListener, IParserListener<JsonElement> {

    private View rootView;
    private SplashActivity splashActivity;
    private CustomTextView txtNext, txtDob, txtAgree;
    private CountriesModel currentCountriesModel;
    private RadioGroup rgGender;
    private DatePickerDialog mDatePickerDialog;
    private Calendar myCalendar = Calendar.getInstance();
    private String firstName, lastName, email, token, gender, deviceToken;
    private CustomEditText edtFirstName, edtLastName, edtEmail, edtLocation;
    private ImageView imgAgree;
    private boolean isAgree;
//    private CustomDialog customDialog;

    public static Signup1Fragment newInstance() {
        return new Signup1Fragment();
    }

    public static Signup1Fragment newInstance(Bundle bundle) {
        Signup1Fragment signup1Fragment = new Signup1Fragment();
        signup1Fragment.setArguments(bundle);
        return signup1Fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.setTopBar(Signup1Fragment.class.getSimpleName());
        deviceToken = LocalStorage.getInstance(splashActivity).getString(LocalStorage.PREF_DEVICE_TOKEN, "");
        currentCountriesModel = StaticUtils.getCurrentCountryModel(splashActivity);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            token = bundle.getString("token");
            email = bundle.getString("email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup1, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        splashActivity.changeStatusBarColorToWhite();
        txtNext = rootView.findViewById(R.id.txtNext);
        txtDob = rootView.findViewById(R.id.txtDob);

        rgGender = rootView.findViewById(R.id.rgGender);

        rgGender.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rbMale) {
                gender = "M";
            } else if (i == R.id.rbFeMale) {
                gender = "F";
            }
        });
        edtFirstName = rootView.findViewById(R.id.edtFirstName);
        edtLastName = rootView.findViewById(R.id.edtLastName);
        edtLocation = rootView.findViewById(R.id.edtLocation);
        edtEmail = rootView.findViewById(R.id.edtEmail);
        imgAgree = rootView.findViewById(R.id.imgAgree);
        txtAgree = rootView.findViewById(R.id.txtAgree);
        txtDob.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        imgAgree.setOnClickListener(this);
        initDatePicker();
        setAgreeText();
        if (!TextUtils.isEmpty(firstName)) {
            edtFirstName.setText(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            edtLastName.setText(lastName);
        }
        if (!TextUtils.isEmpty(email)) {
            edtEmail.setText(email);
            edtEmail.setEnabled(false);
        } else {
            edtEmail.setEnabled(true);
        }
    }

    private void setAgreeText() {
        txtAgree.setText("I have read and agree to the ");
        SpannableString termsAndConditions = new SpannableString(getString(R.string.terms_conditions));
        termsAndConditions.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                onClickTermsConditions();
            }
        }, 0, termsAndConditions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsAndConditions.setSpan(new ForegroundColorSpan(Color.BLUE),
                0, termsAndConditions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsAndConditions.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, termsAndConditions.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtAgree.append(termsAndConditions);

        txtAgree.append(" and ");

        SpannableString privacyPolicy = new SpannableString(getString(R.string.privacy_policy));
        privacyPolicy.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                onClickPrivacyPolicy();
            }
        }, 0, privacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setSpan(new ForegroundColorSpan(Color.parseColor("#303F9F")),
                0, privacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, privacyPolicy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtAgree.append(privacyPolicy);
        txtAgree.append(" of this app");
        txtAgree.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void onClickTermsConditions() {
        splashActivity.replaceFragment(StaticPagesFragment.newInstance("Terms & Conditions", "terms-conditions"),
                true, R.id.splashContainer);
    }

    private void onClickPrivacyPolicy() {
        splashActivity.replaceFragment(StaticPagesFragment.newInstance("Privacy Policies", "privacy-policy"), true, R.id.splashContainer);
    }

    private void initDatePicker() {
        myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(System.currentTimeMillis());
        myCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DATE));
        mDatePickerDialog = new DatePickerDialog(splashActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(Signup1Fragment.class.getSimpleName());
        }
    }

    private void openCountriesDialog() {
        CountrySelectionFragment countrySelectionFragment = CountrySelectionFragment.newInstance(currentCountriesModel != null ? currentCountriesModel.countryCode : "");
        countrySelectionFragment.setTargetFragment(this, Constants.DIALOG_FRAGMENT);
        countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
        countrySelectionFragment.show(splashActivity.getSupportFragmentManager(), "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNext:
                String message = checkValidations();
                if (!TextUtils.isEmpty(message)) {
                    StaticUtils.showToast(splashActivity, message);
                } else {
                    requestForRegistrationWS();
                }
                break;
            case R.id.txtCountryCode:
                openCountriesDialog();
                break;
            case R.id.txtDob:
                showDOBDialog();
                break;
            case R.id.imgAgree:
                isAgree = !isAgree;
                imgAgree.setImageDrawable(isAgree ? ContextCompat.getDrawable(splashActivity, R.drawable.icon_uncheck) : null);
                break;
            default:
                break;
        }
    }

    private void requestForRegistrationWS() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("first_name", edtFirstName.getText().toString().trim());
            jsonObjectReq.put("last_name", edtLastName.getText().toString().trim());
            jsonObjectReq.put("gender", gender);
            jsonObjectReq.put("email", edtEmail.getText().toString().trim());
            jsonObjectReq.put("auth_id", token);
            jsonObjectReq.put("auth_type", "facebook");
            jsonObjectReq.put("date_of_birth", txtDob.getText().toString().trim());
            jsonObjectReq.put("platform", "android");
            jsonObjectReq.put("device_token", deviceToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().userRegistration(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(splashActivity, WSUtils.REQ_FOR_USER_REGISTRATION, call, this);

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
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().checkUserOTP(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(splashActivity, WSUtils.REQ_FOR_CHECK_USER_OTP, call, this);
    }

    private String checkValidations() {
        String emailId = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(emailId)) {
            edtEmail.requestFocus();
            return "Please enter email address";
        }
        if (!StaticUtils.isValidEmail(emailId)) {
            edtEmail.requestFocus();
            return "Please enter a valid email address";
        }

//        String mobileNumber = edtMobileNumber.getText().toString().trim();
//        if (TextUtils.isEmpty(mobileNumber)) {
//            edtMobileNumber.requestFocus();
//            return "Please enter mobile number";
//        }
//        if (mobileNumber.length() < 10) {
//            edtMobileNumber.requestFocus();
//            return "Please enter a valid mobile number";
//        }

        if (TextUtils.isEmpty(edtLocation.getText().toString().trim())) {
            edtLocation.requestFocus();
            return "Please enter Location";
        }
        if (TextUtils.isEmpty(txtDob.getText().toString().trim())) {
            txtDob.requestFocus();
            return "Please enter Date of Birth";
        }
        if (!isAgree) {
            return "Agree to the Terms & conditions and Privacy Policy to continue";
        }
        return "";
    }

    private void showDOBDialog() {
        if (!mDatePickerDialog.isShowing()) mDatePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    currentCountriesModel = data.getParcelableExtra("countriesModel");
//                    txtCountryCode.setText("+" + currentCountriesModel.countryCode);
                }
                break;
        }
    }

    @Override
    public void onClick(String text) {
//        customDialog.dismiss();
        if (BaseApplication.userInfoModel != null)
            if (text.equalsIgnoreCase(BaseApplication.userInfoModel.otp)) {
                requestForVerifyOtpWS(text);
            } else {
                StaticUtils.showToast(splashActivity, "Wrong OTP Entered");
            }
        else requestForVerifyOtpWS(text);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        if (response != null) {
            Log.e("response: ", response.toString());
            switch (requestCode) {
                case WSUtils.REQ_FOR_USER_REGISTRATION:
                    parseUserRegistrationResponse((JsonObject) response);
                    break;
                case WSUtils.REQ_FOR_CHECK_USER_OTP:
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
                    try {
                        String json = BaseApplication.userInfoModel.serialize();
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DialogUtils.showSimpleDialog(splashActivity, "Congratulations! Your sign up is completed. You can watch reviews and make your own reviews with YouReview!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(splashActivity, MainActivity.class));
                        splashActivity.finishAffinity();
                    }
                }, null, true);
            } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                StaticUtils.showToast(splashActivity, response.get("message").getAsString());
            }
        }
    }

    private void parseUserRegistrationResponse(JsonObject response) {
        if (response.has("status")) {
            if (response.get("status").getAsString().equalsIgnoreCase("success")) {
//                if (response.has("message")) {
//                    StaticUtils.showToast(splashActivity, response.get("message").getAsString());
//                }
                if (response.has("userInfo")) {
                    BaseApplication.userInfoModel = new UserInfoModel(response.get("userInfo").getAsJsonObject());
                    try {
                        String json = BaseApplication.userInfoModel.serialize();
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (response.has("userid")) {
                    LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_ID, response.get("userid").getAsString());
                }
//                customDialog = new CustomDialog(splashActivity, Signup1Fragment.this);
//                customDialog.show();
                DialogUtils.showSimpleDialog(splashActivity, "Registration Successful", "Congratulations! Your sign up is completed. You can watch reviews and make your own reviews with YouReview!"
                        , new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(splashActivity, MainActivity.class));
                                splashActivity.finishAffinity();
                            }
                        }, null, true);
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

