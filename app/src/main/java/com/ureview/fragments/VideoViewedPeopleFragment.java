package com.ureview.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.PeopleAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.PeopleModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;

public class VideoViewedPeopleFragment extends BottomSheetDialogFragment implements IParserListener<JsonElement>, IClickListener {

    private View rootView;
    private CustomRecyclerView rvPeople;
    private PeopleAdapter peopleAdapter;
    private MainActivity mainActivity;
    private ArrayList<PeopleModel> peopleArrList;
    private CustomTextView txtNoData;
    private String userId;

    private int selectedPosition;
    private BottomSheetDialog dialog;
    private String videoId;

    public static VideoViewedPeopleFragment newInstance(String videoId) {
        VideoViewedPeopleFragment videoViewedPeopleFragment = new VideoViewedPeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoId", videoId);
        videoViewedPeopleFragment.setArguments(bundle);
        return videoViewedPeopleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peopleArrList = new ArrayList<>();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("videoId")) {
                videoId = bundle.getString("videoId");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("Views List", "", "", false, true, false, false, false);
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_followers, container, false);
//
//        rvPeople = rootView.findViewById(R.id.rvFollowers);
//        txtNoData = rootView.findViewById(R.id.txtNoData);
//
//        setAdapter();
//        requestForVideoViewedPeopleListWS();
//
//        return rootView;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        try {
            if (rootView == null) {
                rootView = View.inflate(getContext(), R.layout.fragment_followers, null);
            }
            dialog.setContentView(rootView);
            rvPeople = rootView.findViewById(R.id.rvFollowers);
            txtNoData = rootView.findViewById(R.id.txtNoData);
            setAdapter();
            requestForVideoViewedPeopleListWS();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    private void setAdapter() {
        peopleAdapter = new PeopleAdapter(mainActivity, peopleArrList, this, true);
        rvPeople.setLayoutManager(new LinearLayoutManager(mainActivity));
        rvPeople.setAdapter(peopleAdapter);
    }

    protected void requestForVideoViewedPeopleListWS() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", userId);
        hashMap.put("video_id", videoId);
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getVideoViewedUserList(hashMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_VIDEO_VIEWED_PEOPLE_LIST, call, this);
    }

    private void requestForFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private void requestForUnFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().unFollowUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_UN_FOLLOW_USER, call, this);
    }

    private RequestBody getRequestBodyObject(String followId) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("id", Integer.parseInt(userId));
            jsonObjectReq.put("follow_id", Integer.parseInt(followId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StaticUtils.getRequestBody(jsonObjectReq);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_VIDEO_VIEWED_PEOPLE_LIST:
                parseGetVideoViewedPeopleResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_FOLLOW_USER:
                parseFollowUserResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_UN_FOLLOW_USER:
                parseUnFollowUserResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseGetVideoViewedPeopleResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    peopleArrList.clear();
                    JsonArray usersData = response.get("users_list").getAsJsonArray();
                    if (usersData.size() > 0) {
                        for (int i = 0; i < usersData.size(); i++) {
                            Gson gson = new Gson();
                            PeopleModel peopleModel = gson.fromJson(usersData.get(i).getAsJsonObject(), PeopleModel.class);
                            peopleArrList.add(peopleModel);
                        }
                        peopleAdapter.addItems(peopleArrList);
                        txtNoData.setVisibility(View.GONE);
                        rvPeople.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvPeople.setVisibility(View.GONE);
                    }
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                    rvPeople.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    peopleArrList.get(selectedPosition).followStatus = "follow";
                    peopleAdapter.notifyDataSetChanged();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseUnFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    peopleArrList.get(selectedPosition).followStatus = "Unfollow";
                    peopleAdapter.notifyDataSetChanged();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error", error);
    }

    @Override
    public void noInternetConnection(int requestCode) {
        Log.e("error", "no internet");
    }

    @Override
    public void onClick(View view, int position) {
        selectedPosition = position;
        switch (view.getId()) {
            case R.id.txtFollowStatus:
                if (((CustomTextView) view).getText().toString().trim().equalsIgnoreCase("Follow")) {
                    requestForFollowUser(peopleArrList.get(position).userId);
                } else {
                    requestForUnFollowUser(peopleArrList.get(position).userId);
                }
                break;
            case R.id.relBody:
                mainActivity.replaceFragment(ProfileFragment.newInstance(peopleArrList.get(position).userId), true, R.id.mainContainer);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
