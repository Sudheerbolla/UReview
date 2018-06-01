package com.ureview.utils.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;

import com.ureview.R;

public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    private CustomTextView txtVerify;

    public CustomDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(c, R.drawable.bg_dialog));
        setContentView(R.layout.dialog_confirmation_code);
        txtVerify = findViewById(R.id.txtVerify);
        txtVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVerify:
                c.finish();
                break;
            default:
                break;
        }
        dismiss();
    }
}