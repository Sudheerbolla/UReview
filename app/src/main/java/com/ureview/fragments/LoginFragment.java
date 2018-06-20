package com.ureview.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.Constants;
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
    private CustomTextView txtTwitterLogin, txtFbLogin, txtInstagramLogin, txtVersion;
    private SplashActivity splashActivity;
    CallbackManager mFacebookCallbackManager;
    private String email, firstName, lastName, id;
    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
        splashActivity.setTopBar(LoginFragment.class.getSimpleName());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

//        TwitterLoginButton mLoginButton = rootView.findViewById(R.id.login_button);
//        mLoginButton.setCallback(new Callback<TwitterSession>() {
//            @Override
//            public void success(Result<TwitterSession> result) {
//                Log.d("twitter", "twitterLogin:success" + result);
//                handleTwitterSession(result.data);
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Log.w("twitter", "twitterLogin:failure", exception);
//                updateUI(null);
//            }
//        });
    }

//    private void handleTwitterSession(TwitterSession session) {
//        Log.d("twitter login", "handleTwitterSession:" + session);
//
//        AuthCredential credential = TwitterAuthProvider.getCredential(
//                session.getAuthToken().token,
//                session.getAuthToken().secret);
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(splashActivity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("twitter login", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("twitter login", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(splashActivity, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }

    private void updateUI(FirebaseUser user) {
        Log.e("user", user.getEmail() + " n " + user.getDisplayName());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void onTwitterBtnClicked() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
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
        txtVersion = rootView.findViewById(R.id.txtVersion);

        txtTwitterLogin.setOnClickListener(this);
        txtFbLogin.setOnClickListener(this);
        txtInstagramLogin.setOnClickListener(this);
        splashActivity.changeStatusBarColorToWhite();
        getVersion();
        return rootView;
    }

    private void getVersion() {
        try {
            PackageInfo pInfo = splashActivity.getPackageManager().getPackageInfo(splashActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersion.setText("Version ".concat(version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtInstagramLogin:
                StaticUtils.showToast(splashActivity, "Module Under Development");
                break;
            case R.id.txtTwitterLogin:
                StaticUtils.showToast(splashActivity, "Module Under Development");
                break;
            case R.id.txtFbLogin:
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
                            if (Profile.getCurrentProfile() != null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                id = profile.getId();
                                if (!TextUtils.isEmpty(id))
                                    requestForCheckUserWS(id, Constants.FACEBOOK);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void requestForCheckUserWS(String id, String authType) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().checkUserProfile(id, authType);
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
//                if (response.has("message")) {
//                    StaticUtils.showToast(splashActivity, response.get("message").getAsString());
//                }
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

//                splashActivity.replaceFragment(SignupVerificationFragment.newInstance(id), true, R.id.splashContainer);
                startActivity(new Intent(splashActivity, MainActivity.class));
                splashActivity.finishAffinity();
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
