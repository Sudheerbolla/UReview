package com.ureview.utils.views;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.utils.StaticUtils;

public class CustomDialog extends Dialog {

    public SplashActivity splashActivity;
    private CustomTextView txtVerify;
    ISearchClickListener iSearchClickListener;
    private CustomEditText edtCode1, edtCode2, edtCode3, edtCode4;

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

        edtCode1 = findViewById(R.id.edtCode1);
        edtCode2 = findViewById(R.id.edtCode2);
        edtCode3 = findViewById(R.id.edtCode3);
        edtCode4 = findViewById(R.id.edtCode4);

        setListeners();

    }

    private void setListeners() {
        edtCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edtCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edtCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edtCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = getCode();
                if (TextUtils.isEmpty(code)) {
                    edtCode1.requestFocus();
                    StaticUtils.showToast(getOwnerActivity(), "Please enter verification code");
                } else {
                    if (iSearchClickListener != null) iSearchClickListener.onClick(code);
                }
            }
        });
    }

    private String getCode() {
        return edtCode1.getText().toString().trim() + "" + edtCode2.getText().toString().trim() + "" + edtCode3.getText().toString().trim() + "" + edtCode4.getText().toString().trim();
    }

}