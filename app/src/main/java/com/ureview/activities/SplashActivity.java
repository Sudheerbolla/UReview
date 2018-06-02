package com.ureview.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.ureview.R;
import com.ureview.fragments.IntroFragment;
import com.ureview.fragments.MainFragment;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);
        checkInternetConnectionAndProceed();
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
        replaceFragment(LocalStorage.getInstance(this).getBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, false) ? IntroFragment.newInstance() : MainFragment.newInstance(), false, R.id.splashContainer);
    }

}
