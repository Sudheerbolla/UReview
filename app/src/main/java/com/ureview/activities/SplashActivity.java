package com.ureview.activities;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.fragments.LoginFragment;
import com.ureview.fragments.SplashFragment;
import com.ureview.utils.Constants;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.RuntimePermissionUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout relTopBar;
    public CustomTextView txtTitle, txtRight;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColorToAppColorLight();
        setContentView(R.layout.activity_splash);
        StaticUtils.getHeightAndWidth(this);
        initComps();
//        checkInternetConnectionAndProceed();
    }

    private void checkPermissions() {
        if (RuntimePermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RuntimePermissionUtils.requestForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.LOCATION_PERMISSION);
        } else checkInternetConnectionAndProceed();
    }

    private void initComps() {
        relTopBar = findViewById(R.id.relTopBar);
        txtTitle = findViewById(R.id.txtTitle);
        txtRight = findViewById(R.id.txtRight);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        txtRight.setOnClickListener(this);
        checkPermissions();
    }

    public void setTopBar(String screen) {
        relTopBar.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        txtRight.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
        switch (screen) {
            case "Signup1Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Sign Up");
                break;
            case "Signup3Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Congratulations");
                break;
            case "LoginFragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Login");
                break;
            default:
                txtTitle.setText("Sign Up");
                txtRight.setText("Log In");
                break;
        }
    }

    private void checkInternetConnectionAndProceed() {
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

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash Key: ", hashKey + "");
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }

    private void proceedWithFlow() {
        replaceFragmentWithoutAnimation(SplashFragment.newInstance(), R.id.splashContainer, false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION) {
            if (StaticUtils.isAllPermissionsGranted(grantResults)) {
                checkInternetConnectionAndProceed();
            } else {
                StaticUtils.showToast(this, "Location permission is mandatory to access your location");
                checkPermissions();
            }

        }
    }

}
