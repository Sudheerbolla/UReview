package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;

public class ReportVideoFragment extends BaseFragment implements IParserListener<JsonElement>, View.OnClickListener {

    private View rootView;
    private MainActivity mainActivity;
    private CustomEditText edtDesc;
    private CustomTextView txtNegative, txtPositive;
    private String userId;
    private String videoId = "";

    public static ReportVideoFragment newInstance(String videoId) {
        ReportVideoFragment followersFragment = new ReportVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoId", videoId);
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("videoId"))
            videoId = bundle.getString("videoId");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report_video, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        mainActivity.setToolBar("Report Video", "", "", false,
                false, false, false, false);
        edtDesc = rootView.findViewById(R.id.edtDesc);
        txtNegative = rootView.findViewById(R.id.txtNegative);
        txtPositive = rootView.findViewById(R.id.txtPositive);
        txtNegative.setOnClickListener(this);
        txtPositive.setOnClickListener(this);
    }

    private void requestForReportVideo() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().reportVideo(getRequestBodyObject());
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_REPORT_VIDEO, call, this);
    }

    private RequestBody getRequestBodyObject() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", Integer.parseInt(userId));
            jsonObjectReq.put("video_id", Integer.parseInt(videoId));
            jsonObjectReq.put("message", edtDesc.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StaticUtils.getRequestBody(jsonObjectReq);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_REPORT_VIDEO:
                JsonObject jsonObject = (JsonObject) response;
                if (jsonObject.has("message"))
                    DialogUtils.showSimpleDialog(mainActivity, jsonObject.get("message").getAsString(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainActivity.onBackPressed();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }, true);
                break;
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNegative:
                mainActivity.onBackPressed();
                break;
            case R.id.txtPositive:
                if (TextUtils.isEmpty(edtDesc.getText().toString().trim())) {
                    Toast.makeText(mainActivity, "Write your comments", Toast.LENGTH_LONG).show();
                } else {
                    requestForReportVideo();
                }
                break;
        }
    }
}
