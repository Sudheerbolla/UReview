package com.ureview.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.LoginManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.activities.SplashActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.Constants;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private CustomTextView txtShare, txtPrivacy, txtTerms, txtRateApp,
            txtHelp, txtContactUs, txtLogout, txtDelAcc, txtVersion;
    private MainActivity mainActivity;
    private String baseUrl = "";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        baseUrl = WSUtils.BASE_URL + "/pages?slug_name=";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtShare = rootView.findViewById(R.id.txtShare);
        txtPrivacy = rootView.findViewById(R.id.txtPrivacy);
        txtTerms = rootView.findViewById(R.id.txtTerms);
        txtDelAcc = rootView.findViewById(R.id.txtDelAcc);
        txtHelp = rootView.findViewById(R.id.txtHelp);
        txtLogout = rootView.findViewById(R.id.txtLogout);
        txtContactUs = rootView.findViewById(R.id.txtContactUs);
        txtVersion = rootView.findViewById(R.id.txtVersion);
        txtRateApp = rootView.findViewById(R.id.txtRateApp);

        txtShare.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        txtHelp.setOnClickListener(this);
        txtDelAcc.setOnClickListener(this);
        txtTerms.setOnClickListener(this);
        txtPrivacy.setOnClickListener(this);
        txtContactUs.setOnClickListener(this);
        txtRateApp.setOnClickListener(this);
        getVersion();
//        sendNotification("Test");
    }

    private void getVersion() {
        try {
            PackageInfo pInfo = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersion.setText("Version ".concat(version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("Settings", "", "", false, false, true, false, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtTerms:
                mainActivity.replaceFragment(StaticPagesFragment.newInstance("Terms & Conditions", "terms-conditions"), true, R.id.mainContainer);
                break;
            case R.id.txtDelAcc:
                DialogUtils.showLogoutDialog(mainActivity, "Delete Account", "Are you sure you want to delete the account? This is a permanent action and cannot be reverted.", "Delete", "Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestForDeleteAccountWS();
                    }
                }, null);
                break;
            case R.id.txtContactUs:
            case R.id.txtHelp:
                openEmailIntent();
                break;
            case R.id.txtLogout:
                DialogUtils.showLogoutDialog(mainActivity, "Logout", "Are you sure you want to Logout? ", "Yes", "No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutFromApp();
                    }
                }, null);
                break;
            case R.id.txtShare:
                shareViaIntent("YouReview App", "Haven't tried YouReview yet? Signup and watch into the reviews App Link. https://play.google.com/store/apps/details?id=" + mainActivity.getPackageName());
                break;
            case R.id.txtPrivacy:
                mainActivity.replaceFragment(StaticPagesFragment.newInstance("Privacy Policies", "privacy-policy"), true, R.id.mainContainer);
                break;
            case R.id.txtRateApp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mainActivity.getPackageName())));
                break;
            default:
                break;
        }
    }

    private void openEmailIntent() {
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("message/rfc822");
        intent2.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@ureview.com"});
        intent2.putExtra(Intent.EXTRA_SUBJECT, "Contact YouReview");
        intent2.putExtra(Intent.EXTRA_TEXT, "Write your message for us.");
        startActivity(intent2);
    }

    public void shareViaIntent(String subject, String shareContent) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
        startActivity(Intent.createChooser(sharingIntent, "Share Using"));
    }

    private void requestForDeleteAccountWS() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().deleteProfile(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_DELETE_ACCOUNT, call, this);

    }

    private void logoutFromApp() {

        LocalStorage.getInstance(mainActivity).clearLocalStorage();

        LocalStorage.getInstance(mainActivity).putBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, false);

        LoginManager.getInstance().logOut();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.deleteNotificationChannel(Constants.NOTIFICATION_CHANNEL);
        }

        startActivity(new Intent(mainActivity, SplashActivity.class));
        mainActivity.finishAffinity();

    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        if (response != null) {
            Log.e("response: ", response.toString());
            switch (requestCode) {
                case WSUtils.REQ_FOR_DELETE_ACCOUNT:
                    parseDeleteAccountResponse((JsonObject) response);
                    break;
                default:
                    break;
            }
        }
    }

    private void parseDeleteAccountResponse(JsonObject response) {
        if (response.has("status")) {
            if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                if (response.has("message")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
                logoutFromApp();
            } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                StaticUtils.showToast(mainActivity, response.get("message").getAsString());
            }
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error: ", error);
    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    private void sendNotification(String messageBody) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent intent = new Intent(mainActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String largeText, smallText;

        if (messageBody.length() > 25) {
            smallText = messageBody.substring(0, 24);
            largeText = messageBody;
        } else {
            largeText = smallText = messageBody;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mainActivity, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(smallText)
                .setLargeIcon(image)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(largeText))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

}
