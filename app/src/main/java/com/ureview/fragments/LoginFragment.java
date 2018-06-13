package com.ureview.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;

public class LoginFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private CustomTextView txtTwitterLogin, txtFbLogin, txtInstagramLogin;
    private SplashActivity splashActivity;
    CallbackManager mFacebookCallbackManager;
    private String email, firstName, lastName, id;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.setTopBar(LoginFragment.class.getSimpleName());
        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (splashActivity != null) {
            splashActivity.setTopBar(LoginFragment.class.getSimpleName());
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
                requestForCheckUserWS("179833959388215");
                break;
            case R.id.txtTwitterLogin:
                startActivity(new Intent(splashActivity, MainActivity.class));
                splashActivity.finishAffinity();
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

    private void handleFacebookLogin() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        setFacebookData();
    }

    private void setFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            email = response.getJSONObject().getString("email");
                            firstName = response.getJSONObject().getString("first_name");
                            lastName = response.getJSONObject().getString("last_name");
                            id = response.getJSONObject().getString("id");
                            Profile profile = Profile.getCurrentProfile();
                            id = profile.getId();
                            if (Profile.getCurrentProfile() != null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(id)) requestForCheckUserWS(id);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("onSuccess >> ", "" + loginResult.toString());
            }

            @Override
            public void onCancel() {
                Log.e("onCancel ", "onCancel >> ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("error", "error >> " + error.toString());
            }
        });

    }

    private void requestForCheckUserWS(String id) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().checkUserProfile(id);
        new WSCallBacksListener().requestForJsonObject(splashActivity, WSUtils.REQ_FOR_CHECK_USER, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_CHECK_USER:
                parseCheckUserResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseCheckUserResponse(JsonObject response) {
        Log.e("response: ", response.toString());
        if (response.has("status")) {
            Bundle bundle = new Bundle();
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("token", id);
            bundle.putString("email", email);
            if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
//register
                splashActivity.replaceFragment(Signup1Fragment.newInstance(bundle), true, R.id.splashContainer);
            } else if (response.get("status").getAsString().equalsIgnoreCase("success")) {
//login
                if (response.has("message")) {
                    StaticUtils.showToast(splashActivity, response.get("message").getAsString());
                }
                if (response.has("user_info")) {
                    BaseApplication.userInfoModel = new UserInfoModel(response.get("user_info").getAsJsonObject());
                    try {
                        String json = BaseApplication.userInfoModel.serialize();
                        LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LocalStorage.getInstance(splashActivity).putString(LocalStorage.PREF_USER_ID, BaseApplication.userInfoModel.userid);
                }

                splashActivity.replaceFragment(SignupVerificationFragment.newInstance(id), true, R.id.splashContainer);

            }
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error: ", error);
    }

    @Override
    public void noInternetConnection(int requestCode) {
        Log.e("internet: ", "noInternetConnection");
    }

}
