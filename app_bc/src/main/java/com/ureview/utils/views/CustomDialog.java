package com.ureview.utils.views;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.ISearchClickListener;

public class CustomDialog extends Dialog {

    public SplashActivity splashActivity;
    public Dialog d;
    private CustomTextView txtVerify;
    ISearchClickListener iSearchClickListener;

    public CustomDialog(SplashActivity a, ISearchClickListener iSearchClickListener) {
        super(a);
        this.iSearchClickListener = iSearchClickListener;
        this.splashActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(splashActivity, R.drawable.bg_dialog));
        setContentView(R.layout.dialog_confirmation_code);
        txtVerify = findViewById(R.id.txtVerify);
        txtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iSearchClickListener != null) iSearchClickListener.onClick("success");
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.txtVerify:
//
//                break;
//            default:
//                break;
//        }
//        dismiss();
//    }

}