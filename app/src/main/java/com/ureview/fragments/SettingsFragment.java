package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.login.LoginManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private RelativeLayout relShareApp, relPrivacyPolicy, relTermsAndConditions,
            relHelpCenter, relContactUs, relLogout, relDeleteAccount;
    private MainActivity mainActivity;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        relShareApp = rootView.findViewById(R.id.relShareApp);
        relPrivacyPolicy = rootView.findViewById(R.id.relPrivacyPolicy);
        relTermsAndConditions = rootView.findViewById(R.id.relTermsAndConditions);
        relDeleteAccount = rootView.findViewById(R.id.relDeleteAccount);
        relHelpCenter = rootView.findViewById(R.id.relHelpCenter);
        relLogout = rootView.findViewById(R.id.relLogout);
        relContactUs = rootView.findViewById(R.id.relContactUs);

        relShareApp.setOnClickListener(this);
        relLogout.setOnClickListener(this);
        relHelpCenter.setOnClickListener(this);
        relDeleteAccount.setOnClickListener(this);
        relTermsAndConditions.setOnClickListener(this);
        relPrivacyPolicy.setOnClickListener(this);
        relContactUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relContactUs:
                break;
            case R.id.relTermsAndConditions:
                break;
            case R.id.relDeleteAccount:
                DialogUtils.showSimpleDialog(mainActivity, "Delete Account", "Are you sure you want to delete the account? This is a permanant action and cannot be reverted.", "Delete", "Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestForDeleteAccountWS();
                    }
                }, null, false, false);
                break;
            case R.id.relHelpCenter:
                break;
            case R.id.relLogout:
                DialogUtils.showSimpleDialog(mainActivity, "Logout", "Are you sure you want to Logout? ", "Yes", "No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutFromApp();
                    }
                }, null, false, false);
                break;
            case R.id.relShareApp:
                break;
            case R.id.relPrivacyPolicy:
                break;
            default:
                break;
        }
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
}
