package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.FollowersAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.FollowModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;

import retrofit2.Call;

public class FollowersFragment extends BaseFragment implements IParserListener<JsonElement>, IClickListener {

    private View rootView;
    private RecyclerView rvFollowers;
    private FollowersAdapter followersAdapter;
    private MainActivity mainActivity;
    private boolean showFollowers;
    private ArrayList<FollowModel> followModelArrayList;
    private CustomTextView txtNoData;

    public static FollowersFragment newInstance(boolean showFollowers) {
        FollowersFragment followersFragment = new FollowersFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showFollowers", showFollowers);
        followersFragment.setArguments(bundle);
        return new FollowersFragment();
    }

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar(showFollowers ? "Followers" : "Following", "", "", false, true, false, false, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        followModelArrayList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            showFollowers = bundle.getBoolean("showFollowers");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_followers, container, false);

        rvFollowers = rootView.findViewById(R.id.rvFollowers);
        txtNoData = rootView.findViewById(R.id.txtNoData);

        setAdapter();

        requestForGetFollowListWS();

        return rootView;
    }

    private void setAdapter() {
        followersAdapter = new FollowersAdapter(mainActivity, followModelArrayList, this);
        rvFollowers.setLayoutManager(new LinearLayoutManager(mainActivity));
        rvFollowers.setAdapter(followersAdapter);
    }

    private void requestForGetFollowListWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getFollowList(LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_FOLLOW_LIST, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_GET_FOLLOW_LIST:
                parseGetFollowListResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseGetFollowListResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    JsonArray followArray = response.get(showFollowers ? "follow_you_list" : "you_follow_list").getAsJsonArray();
                    if (followArray.size() > 0) {
                        for (int i = 0; i < followArray.size(); i++) {
                            FollowModel followModel = new FollowModel(followArray.get(i).getAsJsonObject());
                            followModelArrayList.add(followModel);
                        }
                        txtNoData.setVisibility(View.GONE);
                        rvFollowers.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvFollowers.setVisibility(View.GONE);
                    }
                    followersAdapter.notifyDataSetChanged();
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

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }
}

/*{
    "status": "success",
    "message": "Follow You and You Follow List",
    "follow_you_list": [
        {
            "user_id": "1",
            "first_name": "Madhu",
            "last_name": "Sudhan",
            "email": "putta.msreddy@gmail.com",
            "user_image": "",
            "gender": "M",
            "date_of_birth": "31/05/2013",
            "age": "5",
            "mobile": "8121407014",
            "user_description": "",
            "user_rating": "2",
            "address": "",
            "follow_status": "",
            "status": "1",
            "uploaded_videos_count": 10
        }
    ],
    "you_follow_list": [
        {
            "user_id": "5",
            "first_name": "Manish",
            "last_name": "Pathak",
            "email": "manishvijaykantpathak@gmail.com",
            "user_image": "",
            "gender": "M",
            "date_of_birth": "01/06/2017",
            "age": "1",
            "mobile": "9820680804",
            "user_description": "",
            "user_rating": "0",
            "address": "",
            "follow_status": "follow",
            "status": "1",
            "uploaded_videos_count": 4
        }
    ]
}*/