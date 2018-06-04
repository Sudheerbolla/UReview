package com.ureview.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.models.CountriesModel;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomDialog;
import com.ureview.utils.views.CustomTextView;

import java.util.Calendar;

public class Signup1Fragment extends BaseFragment implements View.OnClickListener, ISearchClickListener {

    private View rootView;
    private SplashActivity splashActivity;
    private CustomTextView txtNext, txtCountryCode, txtDob;
    private CountriesModel currentCountriesModel;
    private RadioButton rbMale, rbFeMale;
    private RadioGroup rgGender;
    private DatePickerDialog mDatePickerDialog;
    private Calendar myCalendar = Calendar.getInstance();

    public static Signup1Fragment newInstance() {
        return new Signup1Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        currentCountriesModel = StaticUtils.getCurrentCountryModel(splashActivity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup1, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtNext = rootView.findViewById(R.id.txtNext);
        txtCountryCode = rootView.findViewById(R.id.txtCountryCode);
        txtDob = rootView.findViewById(R.id.txtDob);
        rbFeMale = rootView.findViewById(R.id.rbFeMale);
        rbMale = rootView.findViewById(R.id.rbMale);
        rgGender = rootView.findViewById(R.id.rgGender);

        txtCountryCode.setOnClickListener(this);
        txtDob.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        if (currentCountriesModel != null) {
            txtCountryCode.setText(currentCountriesModel.countryCode);
        }
        initDatePicker();

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
                txtDob.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
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

    private CustomDialog customDialog;

    private void openCountriesDialog() {
        CountrySelectionFragment countrySelectionFragment = CountrySelectionFragment.newInstance(currentCountriesModel != null ? currentCountriesModel.countryCode : "");
        countrySelectionFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
        countrySelectionFragment.show(splashActivity.getSupportFragmentManager(), "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNext:
//                splashActivity.replaceFragment(Signup3Fragment.newInstance(), true, R.id.splashContainer);
                customDialog = new CustomDialog(splashActivity, Signup1Fragment.this);
                customDialog.show();
                break;
            case R.id.txtCountryCode:
                openCountriesDialog();
                break;
            case R.id.txtDob:
                showDOBDialog();
                break;
            default:
                break;
        }
    }

    private void showDOBDialog() {
        if (!mDatePickerDialog.isShowing()) mDatePickerDialog.show();
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
    public void onClick(String text) {
        customDialog.dismiss();
//        startActivity(new Intent(splashActivity, MainActivity.class));
//        splashActivity.finishAffinity();
        splashActivity.replaceFragment(Signup3Fragment.newInstance(), true, R.id.splashContainer);
    }
}

