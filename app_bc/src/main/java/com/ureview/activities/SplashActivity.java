package com.ureview.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.fragments.IntroFragment;
import com.ureview.fragments.LoginFragment;
import com.ureview.fragments.SignupVerificationFragment;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout relTopBar;
    public CustomTextView txtSignup, txtTitle, txtRight;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
        setContentView(R.layout.activity_splash);
        initComps();
        checkInternetConnectionAndProceed();
    }

    private void initComps() {
        relTopBar = findViewById(R.id.relTopBar);
        txtSignup = findViewById(R.id.txtSignup);
        txtTitle = findViewById(R.id.txtTitle);
        txtRight = findViewById(R.id.txtRight);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        txtRight.setOnClickListener(this);
    }

    public void setTopBar(String screen) {
        switch (screen) {
            case "Signup1Fragment":
                txtTitle.setText("Sign Up");
                txtRight.setText("Log In");
                break;
            default:
                txtTitle.setText("Sign Up");
                txtRight.setText("Log In");
                break;
        }
    }

    private void checkInternetConnectionAndProceed() {
        StaticUtils.getHeightAndWidth(this);
        if (StaticUtils.checkInternetConnection(this)) {
            proceedWithFlow();
        } else {
            DialogUtils.showSimpleDialog(this, "", "No Internet Connection", "Retry", "Close", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkInternetConnectionAndProceed();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }, true, false);
        }
    }

    private void proceedWithFlow() {
        if (LocalStorage.getInstance(this).getBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, true)) {
            relTopBar.setVisibility(View.GONE);
//            changeStatusBarColor();
            replaceFragment(IntroFragment.newInstance(), false, R.id.splashContainer);
        } else {
            relTopBar.setVisibility(View.VISIBLE);
            replaceFragment(SignupVerificationFragment.newInstance(), false, R.id.splashContainer);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtRight:
                replaceFragment(LoginFragment.newInstance(), true, R.id.splashContainer);
                break;
            default:
                break;
        }
    }
}
