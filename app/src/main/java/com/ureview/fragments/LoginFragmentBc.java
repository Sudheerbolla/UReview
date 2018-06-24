package com.ureview.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.utils.views.CustomTextView;

import java.util.Arrays;

public class LoginFragmentBc extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private CustomTextView txtTwitterLogin, txtFbLogin, txtInstagramLogin;
    private SplashActivity splashActivity;
    CallbackManager mFacebookCallbackManager;
    LoginManager mLoginManager;

    public static LoginFragmentBc newInstance() {
        return new LoginFragmentBc();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.setTopBar(LoginFragmentBc.class.getSimpleName());
        setupFacebookStuff();
    }

    private void setupFacebookStuff() {
        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mLoginManager.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("LoginResult : ", loginResult.getAccessToken() + "");

            }

            @Override
            public void onCancel() {
                Log.e("onCancel : ", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("onError : ", error.toString());
            }
        });
    }

    private void handleFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (!isLoggedIn) {
//            mLoginManager.logOut();
//        }else{
            mLoginManager.logInWithReadPermissions(splashActivity, Arrays.asList("email", "public_profile", "user_birthday"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(LoginFragmentBc.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        txtTwitterLogin = rootView.findViewById(R.id.txtTwitterLogin);
        txtFbLogin = rootView.findViewById(R.id.txtFbLogin);
        txtInstagramLogin = rootView.findViewById(R.id.txtInstagramLogin);

        txtTwitterLogin.setOnClickListener(this);
        txtFbLogin.setOnClickListener(this);
        txtInstagramLogin.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtInstagramLogin:
                break;
            case R.id.txtTwitterLogin:
                break;
            case R.id.txtFbLogin:
//                if (!LocalStorage.getInstance(splashActivity).getBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, true)) {
//                    splashActivity.replaceFragment(Signup1Fragment.newInstance(), true, R.id.splashContainer);
//                } else {
//                    startActivity(new Intent(splashActivity, MainActivity.class));
//                    splashActivity.finishAffinity();
//                }
                handleFacebookLogin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
