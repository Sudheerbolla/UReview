package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.ProfileVideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

public class ProfileVideosFragment extends BaseFragment implements IParserListener<JsonElement>, IClickListener {

    private View rootView;
    private RecyclerView rvVideos;
    private ProfileVideosAdapter videosAdapter;
    private MainActivity mainActivity;
    private ArrayList<VideoModel> userVideosModelArrayList;
    private String userId;
    private CustomTextView txtNoDatafound;
    private int clickedPosition = -1;

    public static ProfileVideosFragment newInstance() {
        return new ProfileVideosFragment();
    }

    public static ProfileVideosFragment newInstance(String userId) {
        ProfileVideosFragment followersFragment = new ProfileVideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userVideosModelArrayList = new ArrayList<>();
        if (getArguments() != null) userId = getArguments().getString("userId");
        else
            userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_videos, container, false);
        rvVideos = rootView.findViewById(R.id.rvVideos);
        txtNoDatafound = rootView.findViewById(R.id.txtNoDatafound);
        txtNoDatafound.setText("No videos uploaded");

        videosAdapter = new ProfileVideosAdapter(mainActivity, userVideosModelArrayList, this, userId.equalsIgnoreCase(LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "")));
        rvVideos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvVideos.setAdapter(videosAdapter);

        ViewCompat.setNestedScrollingEnabled(rootView.findViewById(R.id.nestedScrollView), true);
        ViewCompat.setNestedScrollingEnabled(rvVideos, false);
        requestForGetVideosByUserId();
        return rootView;
    }

    private void requestForGetVideosByUserId() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("current_longitude", String.valueOf(MainActivity.mLastLocation.getLongitude()));
        queryMap.put("current_latitude", String.valueOf(MainActivity.mLastLocation.getLatitude()));
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getVideosByUserId(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_PROFILE_VIDEOS, call, this);
    }

    private void requestForDeleteVideoWS(String id) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("video_id", id);
            jsonObjectReq.put("user_id", userId);

            Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().deleteVideo(StaticUtils.getRequestBody(jsonObjectReq));
            new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_DELETE_VIDEO, call, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_PROFILE_VIDEOS:
                parseGetProfileVideosResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_DELETE_VIDEO:
                parseDeleteVideoWSResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseDeleteVideoWSResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                    if (clickedPosition != -1) {
                        userVideosModelArrayList.remove(clickedPosition);
                        videosAdapter.notifyDataSetChanged();
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGetProfileVideosResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                Gson gson = new Gson();
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("videos")) {
                        try {
                            userVideosModelArrayList.clear();
                            JsonArray videoViewsArray = response.get("videos").getAsJsonArray();
                            if (videoViewsArray.size() > 0) {
                                for (int i = 0; i < videoViewsArray.size(); i++) {
                                    userVideosModelArrayList.add(gson.fromJson(videoViewsArray.get(i).toString(), VideoModel.class));
                                }
                                txtNoDatafound.setVisibility(View.GONE);
                                rvVideos.setVisibility(View.VISIBLE);
                            } else {
                                txtNoDatafound.setVisibility(View.VISIBLE);
                                rvVideos.setVisibility(View.GONE);
                            }
                            videosAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
//                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
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
        clickedPosition = position;
        switch (view.getId()) {
            case R.id.imgDeleteVideo:
                String confirmationMsg = "Are you sure, do you want to delete the Video?";
                DialogUtils.showUnFollowConfirmationPopup(mainActivity, confirmationMsg, v -> requestForDeleteVideoWS(userVideosModelArrayList.get(position).id));
                break;
            case R.id.relItem:
                ArrayList<VideoModel> tempList = new ArrayList<>(userVideosModelArrayList);
                mainActivity.replaceFragment(VideoDetailFragmentNew.newInstance(tempList, position), true, R.id.mainContainer);
//                VideoDetailFragment videoDetailFragment = VideoDetailFragment.newInstance(tempList, position);
//                videoDetailFragment.setTargetFragment(getParentFragment(), Constants.DIALOG_FRAGMENT);
//                videoDetailFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
//                videoDetailFragment.show(mainActivity.getSupportFragmentManager(), "VideoDetailFragment");
                break;
            default:
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    public void updateVideoViewCount(int position) {
        if (userVideosModelArrayList.isEmpty() || videosAdapter == null) return;
        userVideosModelArrayList.get(position).videoWatchedCount = String.valueOf(Integer.parseInt(userVideosModelArrayList.get(position).videoWatchedCount) + 1);
        videosAdapter.notifyItemChanged(position, userVideosModelArrayList.get(position));
    }
}