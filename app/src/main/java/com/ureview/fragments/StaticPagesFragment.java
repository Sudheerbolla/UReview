package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import retrofit2.Call;

public class StaticPagesFragment extends BaseFragment implements IParserListener<JsonElement> {

    private View rootView;
    private MainActivity mainActivity;
    private String heading = "", urlToLoad = "";
    private ProgressBar progress_bar;
    private String content;
    private CustomTextView txtContent;

    public static StaticPagesFragment newInstance() {
        return new StaticPagesFragment();
    }

    public static StaticPagesFragment newInstance(String urlToLoad) {
        StaticPagesFragment staticPagesFragment = new StaticPagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("urlToLoad", urlToLoad);
        staticPagesFragment.setArguments(bundle);
        return staticPagesFragment;
    }

    public static StaticPagesFragment newInstance(String heading, String urlToLoad) {
        StaticPagesFragment staticPagesFragment = new StaticPagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("heading", heading);
        bundle.putString("urlToLoad", urlToLoad);
        staticPagesFragment.setArguments(bundle);
        return staticPagesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            heading = getString(R.string.app_name);
            if (bundle.containsKey("heading ") && !TextUtils.isEmpty(bundle.getString("heading")))
                heading = bundle.getString("heading");
            urlToLoad = bundle.getString("urlToLoad");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_static_content, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        txtContent = rootView.findViewById(R.id.txtContent);
        progress_bar = rootView.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.VISIBLE);
        requestForContentWS();
    }

    private void requestForContentWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getStaticPagesContent(urlToLoad);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_STATIC_CONTENT, call, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar(heading, "", "", false, true, false, false, false);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_GET_STATIC_CONTENT:
                parseStaticContentResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseStaticContentResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    if (response.has("message")) {
                        StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("page_info")) {
                        JsonObject pageInfoObject = response.get("page_info").getAsJsonObject();
                        content = pageInfoObject.get("description").getAsString();
                        heading = pageInfoObject.get("page_title").getAsString();
                        mainActivity.setToolBar(heading, "", "", false, true, false, false, false);
                        txtContent.setText(Html.fromHtml(content));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            progress_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void noInternetConnection(int requestCode) {
        progress_bar.setVisibility(View.GONE);
    }
}
