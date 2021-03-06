package com.ureview.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.SearchVideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;
import com.ureview.utils.views.recyclerView.Paginate;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;

public class SearchVideosFragment extends BaseFragment implements IParserListener<JsonElement>, Paginate.Callbacks, IClickListener {
    private View rootView;
    private CustomRecyclerView rvSearchVideo;
    private SearchVideosAdapter searchVideosAdapter;
    private CustomTextView txtNoData;
    private MainActivity mainActivity;
    private String userId;
    private ArrayList<VideoModel> videosArrList = new ArrayList<>();
    private ArrayList<VideoModel> tempVideosArrList = new ArrayList<>();
    private String currLat, currLng;
    private RelativeLayout rlProgress, rlTopBar;

    //Search People Pagination
    private boolean isLoading = false, hasLoadedAllItems;
    private int startFrom = 0, count = 5;
    protected String searchText = "";
    private int follPos = -1;

    public static SearchVideosFragment newInstance() {
        return new SearchVideosFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (MainActivity.mLastLocation != null) {
            currLat = String.valueOf(MainActivity.mLastLocation.getLatitude());
            currLng = String.valueOf(MainActivity.mLastLocation.getLongitude());
        }
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_video, container, false);

        rvSearchVideo = rootView.findViewById(R.id.rvSearchVideo);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        rlProgress = rootView.findViewById(R.id.rlProgress);
        rlTopBar = rootView.findViewById(R.id.rlTopBar);
        rlTopBar.setVisibility(View.GONE);
        searchVideosAdapter = new SearchVideosAdapter(getActivity(), this);
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchVideo.setAdapter(searchVideosAdapter);
        rvSearchVideo.setListPagination(this);
        return rootView;
    }

    protected void searchVideo(String searchVideo) {
        SearchFragment.searchText = searchVideo;
        videosArrList.clear();
        startFrom = 0;
        isLoading = false;
        hasLoadedAllItems = false;
        if (TextUtils.isEmpty(searchVideo)) {
            tempVideosArrList.clear();
            if (searchVideosAdapter != null) searchVideosAdapter.notifyDataSetChanged();
            if (txtNoData != null) txtNoData.setVisibility(View.VISIBLE);
            if (rvSearchVideo != null) rvSearchVideo.setVisibility(View.GONE);
        } else {
            requestForSearchVideos();
        }
    }

    private void requestForSearchVideos() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", userId);
            jsonObjectReq.put("video_name", SearchFragment.searchText);
            jsonObjectReq.put("latitude", MainActivity.mLastLocation != null ? String.valueOf(MainActivity.mLastLocation.getLatitude()) : "0.0");
            jsonObjectReq.put("longitude", MainActivity.mLastLocation != null ? String.valueOf(MainActivity.mLastLocation.getLongitude()) : "0.0");
            jsonObjectReq.put("startFrom", String.valueOf(startFrom));
            jsonObjectReq.put("count", String.valueOf(count));
            jsonObjectReq.put("current_latitude", currLat);
            jsonObjectReq.put("current_longitude", currLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().searchVideos(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_SEARCH_VIDEOS, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_SEARCH_VIDEOS:
                parseNewsFeedVideo(response);
                break;
            case WSUtils.REQ_FOR_FOLLOW_USER:
                parseFollowUserResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_UN_FOLLOW_USER:
                parseUnFollowUserResponse((JsonObject) response);
                break;
        }
    }

    private void parseNewsFeedVideo(JsonElement response) {
        if (!isLoading) {
            tempVideosArrList.clear();
        }
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("search_videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("search_videos");
                    tempVideosArrList.clear();
                    if (feedVidArr.length() > 0) {
                        for (int i = 0; i < feedVidArr.length(); i++) {
                            Gson gson = new Gson();
                            VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                            videosArrList.add(videoModel);
                            tempVideosArrList.add(videoModel);
                        }
                        boolean addAll = startFrom == 0;
                        startFrom += feedVidArr.length();
                        if (startFrom % count != 0) {
                            hasLoadedAllItems = true;
                            isLoading = false;
                        }
                        if (addAll) {
                            searchVideosAdapter.addAllVideos(tempVideosArrList);
                        } else {
                            searchVideosAdapter.addVideos(tempVideosArrList);
                        }
                        txtNoData.setVisibility(View.GONE);
                        rvSearchVideo.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvSearchVideo.setVisibility(View.GONE);
                    }
                }
                searchVideosAdapter.notifyDataSetChanged();
            } else {
                if (startFrom == 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                    rvSearchVideo.setVisibility(View.GONE);
                } else {
                    hasLoadedAllItems = true;
                }
            }
        } catch (JSONException e) {
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
    public void onLoadMore() {
        isLoading = true;
        requestForSearchVideos();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }

    @Override
    public void onClick(View view, int position) {
        VideoModel vid = videosArrList.get(position);
        switch (view.getId()) {
            case R.id.imgLocation:
                ArrayList<VideoModel> tempList = new ArrayList<>(videosArrList);
//                VideoDetailFragment videoDetailFragment = VideoDetailFragment.newInstance(tempList, position);
//                videoDetailFragment.setTargetFragment(getParentFragment(), Constants.DIALOG_FRAGMENT);
//                videoDetailFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
//                videoDetailFragment.show(mainActivity.getSupportFragmentManager(), "VideoDetailFragment");
                mainActivity.replaceFragment(VideoDetailFragmentNew.newInstance(tempList, position));
                break;
            case R.id.txtViewCount:
//                VideoViewedPeopleFragment videoViewedPeopleFragment = VideoViewedPeopleFragment.newInstance(vid.id);
//                videoViewedPeopleFragment.show(mainActivity.getSupportFragmentManager(), videoViewedPeopleFragment.getTag());
                VideoViewedPeopleFragmentNew videoViewedPeopleFragmentNew = VideoViewedPeopleFragmentNew.newInstance(vid.id);
                mainActivity.replaceFragment(videoViewedPeopleFragmentNew, true, R.id.mainContainer);
                break;
            case R.id.txtDistance:
                String url = "http://maps.google.com/maps?saddr=" + MainActivity.mLastLocation.getLatitude() + "," +
                        MainActivity.mLastLocation.getLongitude() + "&daddr=" + vid.videoLatitude + "," + vid.videoLongitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mapIntent);
                break;
            case R.id.txtFollowStatus:
                follPos = position;
                if (view.isSelected()) {
                    askConfirmationAndProceed("Do you want to Unfollow ".concat(vid.firstName)
                            .concat(" ").concat(vid.lastName).concat("?"), vid.videoOwnerId);
                } else requestForFollowUser(vid.videoOwnerId);
                break;
            case R.id.relUserDetails:
                mainActivity.replaceFragment(ProfileFragment.newInstance(vid.userId, vid.firstName.concat(" ").concat(vid.lastName)), true, R.id.mainContainer);
                break;
        }
    }

    private void askConfirmationAndProceed(String message, final String id) {
        DialogUtils.showUnFollowConfirmationPopup(mainActivity, message,
                view -> requestForUnFollowUser(id));
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    private void requestForFollowUser(String followId) {
        rlProgress.setVisibility(View.VISIBLE);
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private void requestForUnFollowUser(String followId) {
        rlProgress.setVisibility(View.VISIBLE);
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

    private void parseUnFollowUserResponse(JsonObject response) {
        rlProgress.setVisibility(View.GONE);
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (follPos != -1) {
                        String id = videosArrList.get(follPos).userId;
                        for (int i = 0; i < videosArrList.size(); i++) {
                            if (videosArrList.get(i).userId.equalsIgnoreCase(id)) {
                                videosArrList.get(i).followStatus = "";
                                searchVideosAdapter.updateItem(videosArrList.get(i), i);
                            }
                        }
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFollowUserResponse(JsonObject response) {
        rlProgress.setVisibility(View.GONE);
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (follPos != -1) {
                        String id = videosArrList.get(follPos).userId;
                        for (int i = 0; i < videosArrList.size(); i++) {
                            if (videosArrList.get(i).userId.equalsIgnoreCase(id)) {
                                videosArrList.get(i).followStatus = "follow";
                                searchVideosAdapter.updateItem(videosArrList.get(i), i);
                            }
                        }
//                        searchVideosAdapter.updateItem(videosArrList.get(follPos), follPos);
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateVideoViewCount(int position) {
        if (videosArrList.isEmpty() || searchVideosAdapter == null) return;
        videosArrList.get(position).videoWatchedCount = String.valueOf(Integer.parseInt(videosArrList.get(position).videoWatchedCount) + 1);
        searchVideosAdapter.notifyItemChanged(position, videosArrList.get(position));
    }
}