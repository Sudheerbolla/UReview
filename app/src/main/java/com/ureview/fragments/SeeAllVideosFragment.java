package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ureview.utils.Constants;
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
import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;

public class SeeAllVideosFragment extends DialogFragment implements IParserListener<JsonElement>, Paginate.Callbacks, IClickListener, View.OnClickListener {
    private View rootView;
    private CustomRecyclerView rvSearchVideo;
    private SearchVideosAdapter searchVideosAdapter;
    private CustomTextView txtNoData, txtTitle;
    private MainActivity mainActivity;
    private String userId;
    private ArrayList<VideoModel> videosArrList = new ArrayList<>();
    private ArrayList<VideoModel> tempVideosArrList = new ArrayList<>();
    private String currLat = "", currLng = "", locMaxRange = "50", locMinRange = "0";
    private RelativeLayout rlProgress, rlTopBar;
    private ImageView imgBack, imgNotf;

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    //Search People Pagination
    private boolean isLoading = false, hasLoadedAllItems;
    private int startFrom = 0, count = 5;
    protected String searchText = "";
    private int follPos = -1;
    private String videoType = "";
    private String catId = "";
    private int mResumeWindow;
    private long mResumePosition;
    private boolean mExoPlayerFullscreen;
    private ConstraintLayout layout;
    private int screenWidth, screenHeight, containerHeight;

    public static SeeAllVideosFragment newInstance(String videoType, String catId) {
        SeeAllVideosFragment seeAllVideosFragment = new SeeAllVideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoType", videoType);
        bundle.putString("catId", catId);
        seeAllVideosFragment.setArguments(bundle);
        return seeAllVideosFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (MainActivity.mLastLocation != null) {
            currLat = String.valueOf(MainActivity.mLastLocation.getLatitude());
            currLng = String.valueOf(MainActivity.mLastLocation.getLongitude());
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;
        containerHeight = Math.round(StaticUtils.convertDpToPixel(240, mainActivity));
        getBundleData();
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("videoType") && bundle.containsKey("catId")) {
            videoType = bundle.getString("videoType");
            catId = bundle.getString("catId");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_video, container, false);

        rvSearchVideo = rootView.findViewById(R.id.rvSearchVideo);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        rlProgress = rootView.findViewById(R.id.rlProgress);
        layout = rootView.findViewById(R.id.rootView);
        rlTopBar = rootView.findViewById(R.id.rlTopBar);
        txtTitle = rootView.findViewById(R.id.txtTitle);
        imgBack = rootView.findViewById(R.id.imgBack);
        imgNotf = rootView.findViewById(R.id.imgNotf);
        rlTopBar.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        imgNotf.setOnClickListener(this);
        searchVideosAdapter = new SearchVideosAdapter(getActivity(), this);
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchVideo.setAdapter(searchVideosAdapter);
        rvSearchVideo.setListPagination(this);
        requestForVideoList();
        return rootView;
    }

    private void requestForVideoList() {
        String title = "All Videos";
        switch (videoType) {
            case Constants.NEARBY:
                title = "All Near By Videos";
                requestForNearByVideos();
                break;
            case Constants.TOPRATED:
                title = "All Top Rated Videos";
                requestForTopRatedVideos();
                break;
            case Constants.POPULAR:
                title = "All Videos";
                requestForPopularVideos();
                break;
            default:
                break;
        }
        txtTitle.setText(title);
    }

    private void requestForNearByVideos() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", String.valueOf(startFrom));
        queryMap.put("count", String.valueOf(count));
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", currLat);
        queryMap.put("current_longitude", currLng);
        queryMap.put("max_range", locMaxRange);
        queryMap.put("min_range", locMinRange);
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllNearVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEAR_BY_VIDEOS, call, this);
    }

    private void requestForTopRatedVideos() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", String.valueOf(startFrom));
        queryMap.put("count", String.valueOf(count));
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", currLat);
        queryMap.put("current_longitude", currLng);
//        queryMap.put("max_range", locMaxRange);
//        queryMap.put("min_range", locMinRange);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllTopRatedVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_TOP_RATED_VIDEOS, call, this);
    }

    private void requestForPopularVideos() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", String.valueOf(startFrom));
        queryMap.put("count", String.valueOf(count));
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", currLat);
        queryMap.put("current_longitude", currLng);
//        queryMap.put("max_range", locMaxRange);
//        queryMap.put("min_range", locMinRange);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllPopularVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_POPULAR_VIDEOS, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_NEAR_BY_VIDEOS:
            case WSUtils.REQ_FOR_TOP_RATED_VIDEOS:
            case WSUtils.REQ_FOR_POPULAR_VIDEOS:
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

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    @Override
    public void onLoadMore() {
        isLoading = true;
        requestForVideoList();
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
        switch (view.getId()) {
            case R.id.imgLocation:
//                VideoDetailFragment countrySelectionFragment = VideoDetailFragment.newInstance(videosArrList, position);
//                countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
//                countrySelectionFragment.show(mainActivity.getSupportFragmentManager(), "VideoDetailFragment");
//                mainActivity.replaceFragment(VideoDetailFragment.newInstance(videosArrList, position), true, R.id.mainContainer);
                ArrayList<VideoModel> tempList = new ArrayList<>(videosArrList);
                mainActivity.showVideoDetails(VideoDetailFragment.newInstance(tempList, position), SeeAllVideosFragment.this);
                break;
            case R.id.txtViewCount:
                VideoViewedPeopleFragment videoViewedPeopleFragment = VideoViewedPeopleFragment.newInstance(videosArrList.get(position).id);
                videoViewedPeopleFragment.show(mainActivity.getSupportFragmentManager(), videoViewedPeopleFragment.getTag());
                break;
            case R.id.txtDistance:
                VideoModel videoModel = videosArrList.get(position);
                String url = "http://maps.google.com/maps?saddr=" + MainActivity.mLastLocation.getLatitude() + "," +
                        MainActivity.mLastLocation.getLongitude() + "&daddr=" + videoModel.videoLatitude + "," + videoModel.videoLongitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mapIntent);
                break;
            case R.id.txtFollowStatus:
                follPos = position;
                VideoModel vid = videosArrList.get(position);
                if (view.isSelected()) {
                    askConfirmationAndProceed("Do you want to Unfollow ".concat(vid.firstName)
                            .concat(" ").concat(vid.lastName).concat("?"), vid.videoOwnerId);
                } else requestForFollowUser(vid.videoOwnerId);
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

    private void parseNewsFeedVideo(JsonElement response) {
        if (!isLoading) {
            tempVideosArrList.clear();
        }
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
                    tempVideosArrList.clear();
                    if (feedVidArr.length() > 0) {
                        for (int i = 0; i < feedVidArr.length(); i++) {
                            Gson gson = new Gson();
                            VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                            videosArrList.add(videoModel);
                            tempVideosArrList.add(videoModel);
                        }
                        startFrom += feedVidArr.length();
                        if (startFrom % count != 0) {
                            hasLoadedAllItems = true;
                            isLoading = false;
                        }
                        searchVideosAdapter.addVideos(tempVideosArrList);
                        txtNoData.setVisibility(View.GONE);
                        rvSearchVideo.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
                        rvSearchVideo.setVisibility(View.GONE);
                    }
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data.hasExtra("position")) {
                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    videosArrList.get(position).videoWatchedCount =
                            String.valueOf(Integer.parseInt(videosArrList.get(position).videoWatchedCount) + 1);
                    searchVideosAdapter.notifyItemChanged(position, videosArrList.get(position));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                dismiss();
                break;
            case R.id.imgNotf:
                dismiss();
                mainActivity.replaceFragment(NotificationsFragment.newInstance(), true, R.id.mainContainer);
                break;
        }
    }
}