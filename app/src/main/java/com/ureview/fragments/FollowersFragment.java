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
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;

public class FollowersFragment extends BaseFragment implements IParserListener<JsonElement>, IClickListener {

    private View rootView;
    private RecyclerView rvFollowers;
    private FollowersAdapter followersAdapter;
    private MainActivity mainActivity;
    private boolean showFollowers;
    private ArrayList<FollowModel> followModelArrayList;
    private CustomTextView txtNoData;
    private String userId;
    private int selectedPosition;

    public static FollowersFragment newInstance(boolean showFollowers, String userId) {
        FollowersFragment followersFragment = new FollowersFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showFollowers", showFollowers);
        bundle.putString("userId", userId);
        followersFragment.setArguments(bundle);
        return followersFragment;
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
            userId = bundle.getString("userId");
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
        followersAdapter = new FollowersAdapter(mainActivity, followModelArrayList, showFollowers, this);
        rvFollowers.setLayoutManager(new LinearLayoutManager(mainActivity));
        rvFollowers.setAdapter(followersAdapter);
    }

    private void requestForGetFollowListWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getFollowList(userId);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_FOLLOW_LIST, call, this);
    }

    private void requestForFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private void requestForUnFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().unFollowUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_UN_FOLLOW_USER, call, this);
    }

    private void requestForBlockUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().blockUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_BLOCK_USER, call, this);
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
            case WSUtils.REQ_FOR_GET_FOLLOW_LIST:
                parseGetFollowListResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_BLOCK_USER:
                parseBlockUserResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_FOLLOW_USER:
                parseFollowUser((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_UN_FOLLOW_USER:
                parseUnFollowUser((JsonObject) response);
                break;
            default:
                break;
        }
    }

    //{"status":"success","message":"You are following this user!...","follow_you_count":1,"you_follow_count":2}
    private void parseFollowUser(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    followModelArrayList.get(selectedPosition).status = "2";
                    followModelArrayList.get(selectedPosition).follow_status = "follow";
                    followersAdapter.notifyDataSetChanged();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseUnFollowUser(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    followModelArrayList.get(selectedPosition).status = "1";
                    followModelArrayList.get(selectedPosition).follow_status = "";
                    followersAdapter.notifyDataSetChanged();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseBlockUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    followModelArrayList.remove(selectedPosition);
                    followersAdapter.notifyDataSetChanged();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGetFollowListResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    JsonArray followArray = response.get(showFollowers ? "follow_you_list" : "you_follow_list").getAsJsonArray();
                    if (followArray.size() > 0) {
                        followModelArrayList.clear();
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
        selectedPosition = position;
        switch (view.getId()) {
            case R.id.txtFollowStatus:
                if (((CustomTextView) view).getText().toString().trim().equalsIgnoreCase("Follow")) {
                    requestForFollowUser(followModelArrayList.get(position).user_id);
                } else if (((CustomTextView) view).getText().toString().trim().equalsIgnoreCase("Unfollow")) {
                    requestForUnFollowUser(followModelArrayList.get(position).user_id);
                }
                break;
            case R.id.relBody:
                mainActivity.replaceFragment(ProfileFragment.newInstance(followModelArrayList.get(position).user_id), true, R.id.mainContainer);
                break;
            case R.id.imgClear:
                requestForBlockUser(followModelArrayList.get(position).user_id);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

}

/*http://18.216.101.112/follow-request
http://18.216.101.112/un-follow-user
http://18.216.101.112/block-user

{"id":1,"follow_id":2}

*/
