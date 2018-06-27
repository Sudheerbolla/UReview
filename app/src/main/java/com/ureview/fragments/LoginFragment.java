package com.ureview.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
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
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 300;
    private static final int FACEBOOK_SIGN_IN = 301;
    private static final int TWITTER_SIGN_IN = 302;
    private View rootView;
    private CustomTextView txtTwitterLogin, txtFbLogin, txtInstagramLogin, txtVersion;
    private TwitterLoginButton txtTwitterLoginBtn;
    private TwitterAuthClient twitterAuthClient;
    private SplashActivity splashActivity;
    CallbackManager mFacebookCallbackManager;
    private String email, firstName, lastName, id;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int loginType;

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
        initiateGoogleIntegration();
        initiateTwitterIntegration();
        initiateGoogleIntegration();
    }

    private void initiateTwitterIntegration() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig("MXIAuVVgwoijQj4ZJtY9RSj4Y", "aQlWXNlMGdjuwk2kIKdbSYgKqdADNfGvqg3padj2ngIkLPMQFJ");

        TwitterConfig twitterConfig = new TwitterConfig.Builder(splashActivity)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();

        Twitter.initialize(twitterConfig);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initiateGoogleIntegration() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(splashActivity, gso);
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.e(TAG, "handleTwitterSession:" + session);
        Log.e(TAG, "getId: " + session.getId() + "getUserId: " + session.getUserId() + "getUserName: " + session.getUserName());
        id = String.valueOf(session.getId());
        twitterAuthClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                email = result.data;
                requestForCheckUserWS(id, Constants.TWITTER);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e(TAG, "email request failed: " + exception);
                requestForCheckUserWS(id, Constants.TWITTER);
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        Log.e("user", user.getEmail() + " n " + user.getDisplayName());
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void onTwitterBtnClicked() {
        txtTwitterLoginBtn.performClick();
        loginType = TWITTER_SIGN_IN;
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getToken() instead.
//            String uid = user.getUid();
//        }
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
        txtTwitterLoginBtn = rootView.findViewById(R.id.txtTwitterLoginBtn);
        txtFbLogin = rootView.findViewById(R.id.txtFbLogin);
        txtInstagramLogin = rootView.findViewById(R.id.txtInstagramLogin);
        txtVersion = rootView.findViewById(R.id.txtVersion);

        txtTwitterLogin.setOnClickListener(this);
        txtFbLogin.setOnClickListener(this);
        txtInstagramLogin.setOnClickListener(this);
        splashActivity.changeStatusBarColorToWhite();

//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.e("Twitter", user.getEmail() + ", " + user.getDisplayName() + ", " + user.getPhoneNumber() + ", " + user.getProviderId() + ", " + user.getUid());
//                } else {
//                    Log.e("Twitter", "user is null");
//                }
//            }
//        };

        txtFbLogin = rootView.findViewById(R.id.txtFbLogin);
        txtInstagramLogin = rootView.findViewById(R.id.txtInstagramLogin);
        txtVersion = rootView.findViewById(R.id.txtVersion);

        txtFbLogin.setOnClickListener(this);
        txtInstagramLogin.setOnClickListener(this);
        splashActivity.changeStatusBarColorToWhite();
        setTwitterBtnCallBack();
        twitterAuthClient = new TwitterAuthClient();
        getVersion();
        return rootView;
    }

    private void setTwitterBtnCallBack() {
        txtTwitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e(TAG, "twitterLogin:failure", exception);
                updateUI(null);
            }
        });
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
                signIn();
                StaticUtils.showToast(splashActivity, "Module Under Development");
                break;
            case R.id.txtTwitterLogin:
                onTwitterBtnClicked();
                break;
            case R.id.txtFbLogin:
                handleFacebookLogin();
                break;
            default:
                break;
        }
    }

    private void signIn() {
        loginType = GOOGLE_SIGN_IN;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void handleFacebookLogin() {
        loginType = FACEBOOK_SIGN_IN;
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (loginType) {
            case FACEBOOK_SIGN_IN:
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                setFacebookData();
                break;
            case GOOGLE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case TWITTER_SIGN_IN:
                txtTwitterLoginBtn.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            email = account.getEmail();
            firstName = account.getGivenName();
            lastName = account.getFamilyName();
            id = account.getId();
            requestForCheckUserWS(id, Constants.GOOGLE);
        } catch (ApiException e) {
            Log.w("G+", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void setFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
            try {
                JSONObject res = response.getJSONObject();
                if (res != null) {
                    if (res.has("email")) email = res.getString("email");
                    if (res.has("email"))
                        firstName = response.getJSONObject().getString("first_name");
                    if (res.has("email"))
                        lastName = response.getJSONObject().getString("last_name");
                    if (res.has("email")) id = response.getJSONObject().getString("id");
                }
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
                splashActivity.replaceFragment(Signup1Fragment.newInstance(bundle), true, R.id.splashContainer);
            } else if (response.get("status").getAsString().equalsIgnoreCase("success")) {
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
