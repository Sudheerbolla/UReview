package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.NotificationsAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.NotificationsModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;

public class NotificationsFragment extends BaseFragment implements IClickListener, IParserListener<JsonElement> {
    private View rootView;
    private RecyclerView rvNotifications;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<NotificationsModel> notificationsModelArrayList;
    private MainActivity mainActivity;
    private CustomTextView txtNoData;
    private RelativeLayout rlProgress;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        notificationsModelArrayList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        initComponents();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("Notifications", "", "", false, true, false, false, false);
    }

    private void initComponents() {
        rvNotifications = rootView.findViewById(R.id.rvNotifications);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        rlProgress = rootView.findViewById(R.id.rlProgress);

        notificationsAdapter = new NotificationsAdapter(mainActivity, notificationsModelArrayList, this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(mainActivity));
        rvNotifications.setAdapter(notificationsAdapter);

        requestForNotificationsWS();

    }

    private void requestForNotificationsWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getNotifications(LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
//        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getNotifications("2");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NOTIFICATIONS, call, this);
    }

    int deletePos = -1;

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.rlDelete:
                deletePos = position;
                rlProgress.setVisibility(View.VISIBLE);
                requestForDeleteNotification(notificationsModelArrayList.get(position));
                break;
            case R.id.rlMain:
                break;
            default:
                break;
        }
    }

    private void requestForDeleteNotification(NotificationsModel notificationsModel) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("notification_id", notificationsModel.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().deleteNotification(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_DELETE_NOTIFICATION, call, this);

    }

    private void requestForReadNotification(NotificationsModel notificationsModel) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("notification_id", notificationsModel.id);
            jsonObjectReq.put("user_id", LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().readNotification(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_READ_NOTIFICATION, call, this);

    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_NOTIFICATIONS:
                rlProgress.setVisibility(View.GONE);
                parseGetNotificationsResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_DELETE_NOTIFICATION:
//                rlProgress.setVisibility(View.GONE);
                parseDeleteNotificationsResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_READ_NOTIFICATION:
                parseReadNotificationsResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseReadNotificationsResponse(JsonObject response) {
//{"status":"success","message":"Notification read successfully!..","unread_notifications":27}
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
//                    if (deletePos != -1) notificationsModelArrayList.remove(deletePos);
//                    deletePos = -1;
//                    notificationsAdapter.notifyDataSetChanged();
//                    if (notificationsModelArrayList.size() > 0) {
//                        txtNoData.setVisibility(View.GONE);
//                        rvNotifications.setVisibility(View.VISIBLE);
//                    } else {
//                        txtNoData.setVisibility(View.VISIBLE);
//                        rvNotifications.setVisibility(View.GONE);
//                    }
//                    requestForNotificationsWS();
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDeleteNotificationsResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (deletePos != -1) notificationsModelArrayList.remove(deletePos);
                    deletePos = -1;
                    notificationsAdapter.notifyDataSetChanged();
                    if (notificationsModelArrayList.size() > 0) {
                        txtNoData.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    }
                    requestForNotificationsWS();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    rlProgress.setVisibility(View.GONE);
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGetNotificationsResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                notificationsModelArrayList.clear();
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    JsonArray notificationsArray = response.get("notifications").getAsJsonArray();
                    if (notificationsArray.size() > 0) {
                        for (int i = 0; i < notificationsArray.size(); i++) {
                            NotificationsModel notificationsModel = new NotificationsModel(notificationsArray.get(i).getAsJsonObject());
                            notificationsModelArrayList.add(notificationsModel);
                        }
                        txtNoData.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    }
                    notificationsAdapter.notifyDataSetChanged();
//                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
//                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                    rvNotifications.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        rlProgress.setVisibility(View.GONE);
    }

    @Override
    public void noInternetConnection(int requestCode) {
        rlProgress.setVisibility(View.GONE);
    }
}
