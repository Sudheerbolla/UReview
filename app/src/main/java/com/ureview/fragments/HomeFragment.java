package com.ureview.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.HomeCategoryAdapter;
import com.ureview.adapters.NewsFeedAdapter;
import com.ureview.adapters.VideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.listeners.IVideosClickListener;
import com.ureview.models.CategoryModel;
import com.ureview.models.FilterModel;
import com.ureview.models.VideoModel;
import com.ureview.utils.LocalStorage;
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

import retrofit2.Call;

public class HomeFragment extends BaseFragment implements IClickListener, View.OnClickListener, IParserListener<JsonElement>, Paginate.Callbacks, IVideosClickListener {
    private static HomeFragment instance;
    private View rootView;
    private NestedScrollView nestedScroll;
    private CustomRecyclerView rvCategories, rvNewsFeed, rvNearByVideos, rvTopRated, rvPopularsearch;
    private LinearLayoutManager linearLayoutManager;
    private NewsFeedAdapter newsFeedAdapter;
    private HomeCategoryAdapter homeCategoryAdapter;
    private MainActivity mainActivity;
    private CustomTextView txtSeeAllVideos, txtSeeAllTopRated, txtSeeAllPopularSearch, txtNoData;
    private VideosAdapter nearByVideosAdapter, topRatedVideosAdapter, popularVideosAdapter;
    private RelativeLayout relVideos, relTopRated, relPopularSearch;
    private boolean nearByData, topRatedData, popularData;
    private int loadedDataCount;
    private String lat = "", lng = "", locMaxRange = "50", locMinRange = "0";

    //    News Feed Pagination
    private boolean isLoading, hasLoadedAllItems;
    private int startFrom = 0, count = 5;

    private ArrayList<CategoryModel> categoryList = new ArrayList<>();
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> tempFeedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> nearByVideoList = new ArrayList<>();
    private ArrayList<VideoModel> topRatedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> popularVideoList = new ArrayList<>();
    private String userId;
    private int lastUpdatedPos = -1;
    private int visibleItemCount, totalItemCount, pastVisiblesItems;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static HomeFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("", "fetching location...", "", true, false, true, false, false);
        mainActivity.setTextToAddress();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        instance = this;
        nestedScroll = rootView.findViewById(R.id.nestedScroll);
        rvNewsFeed = rootView.findViewById(R.id.rvNewsFeed);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rvNewsFeed.setLayoutManager(linearLayoutManager);
        rvCategories = rootView.findViewById(R.id.rvCategories);
        rvNearByVideos = rootView.findViewById(R.id.rvNearByVideos);
        rvTopRated = rootView.findViewById(R.id.rvTopRated);
        rvPopularsearch = rootView.findViewById(R.id.rvPopularsearch);

        ViewCompat.setNestedScrollingEnabled(rvNewsFeed, false);
        ViewCompat.setNestedScrollingEnabled(rvCategories, false);
        ViewCompat.setNestedScrollingEnabled(rvNearByVideos, false);
        ViewCompat.setNestedScrollingEnabled(rvTopRated, false);

        newsFeedAdapter = new NewsFeedAdapter(getActivity(), this);
        homeCategoryAdapter = new HomeCategoryAdapter(getActivity(), this);
        nearByVideosAdapter = new VideosAdapter(getActivity(), this, true);
        topRatedVideosAdapter = new VideosAdapter(getActivity(), this, true);
        popularVideosAdapter = new VideosAdapter(getActivity(), this, true);

        rvNewsFeed.setAdapter(newsFeedAdapter);
        rvNewsFeed.setListPagination(this);
        rvCategories.setAdapter(homeCategoryAdapter);
        rvNearByVideos.setAdapter(nearByVideosAdapter);
        rvTopRated.setAdapter(topRatedVideosAdapter);
        rvPopularsearch.setAdapter(popularVideosAdapter);

        setScrollListeners();
        rvNewsFeed.setLayoutManager(new LinearLayoutManager(mainActivity));

        relVideos = rootView.findViewById(R.id.relVideos);
        relTopRated = rootView.findViewById(R.id.relTopRated);
        relPopularSearch = rootView.findViewById(R.id.relPopularSearch);

        txtNoData = rootView.findViewById(R.id.txtNoData);
        txtSeeAllVideos = rootView.findViewById(R.id.txtSeeAllVideos);
        txtSeeAllTopRated = rootView.findViewById(R.id.txtSeeAllTopRated);
        txtSeeAllPopularSearch = rootView.findViewById(R.id.txtSeeAllPopularSearch);

        txtSeeAllTopRated.setOnClickListener(this);
        txtSeeAllVideos.setOnClickListener(this);
        txtSeeAllPopularSearch.setOnClickListener(this);

        lat = String.valueOf(MainActivity.mLastLocation.getLatitude());
        lng = String.valueOf(MainActivity.mLastLocation.getLongitude());

        requestForCategoryList();

        return rootView;
    }

    private void setScrollListeners() {
        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
//                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                            scrollY > oldScrollY) {
//                        visibleItemCount = linearLayoutManager.getChildCount();
//                        totalItemCount = linearLayoutManager.getItemCount();
//                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                        if (!isLoading()) {
//                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                                onLoadMore();
//                            }
//                        }
//                    }
                    View view = (View) nestedScroll.getChildAt(nestedScroll.getChildCount() - 1);
                    int diff = (view.getBottom() - (nestedScroll.getHeight() + nestedScroll
                            .getScrollY()));
                    //Log.e("lav diff", diff + "");// Compared with 30 as it is the Least diff.
                    if (diff <= 30 && !isLoading() && !hasLoadedAllItems())
                        onLoadMore();
                }
            }
        });
//        nestedScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                View view = (View) nestedScroll.getChildAt(nestedScroll.getChildCount() - 1);
//                int diff = (view.getBottom() - (nestedScroll.getHeight() + nestedScroll
//                        .getScrollY()));
//                if (diff <= 10) {
//                    if (!isLoading() && !hasLoadedAllItems()) {
//                        Toast.makeText(mainActivity, "Need to call API", Toast.LENGTH_SHORT).show();
//                        onLoadMore();
//                    }
//                }
//            }
//        });
    }

    private void setData(String categoryName) {
        boolean b = categoryName.equalsIgnoreCase("New Feed");
        rvNewsFeed.setVisibility(b && feedVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        relVideos.setVisibility(!b && nearByVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        rvNearByVideos.setVisibility(!b && nearByVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        txtSeeAllVideos.setVisibility(!b && nearByVideoList.size() > 3 ? View.VISIBLE : View.GONE);
        relTopRated.setVisibility(!b && topRatedVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        rvTopRated.setVisibility(!b && topRatedVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        txtSeeAllTopRated.setVisibility(!b && topRatedVideoList.size() > 3 ? View.VISIBLE : View.GONE);
        relPopularSearch.setVisibility(!b && popularVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        rvPopularsearch.setVisibility(!b && popularVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        txtSeeAllPopularSearch.setVisibility(!b && popularVideoList.size() > 3 ? View.VISIBLE : View.GONE);
    }

    public void loadRelatedData(FilterModel value) {
        lat = value.locationLat;
        lng = value.locationLng;
        locMaxRange = value.locationMax;
        locMinRange = value.locationMin;
        updateCategoryList(lastUpdatedPos, true);
    }

    private void requestForCategoryList() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllCategories(userId);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_CATEGORY_LIST, call, this);
    }

    private void requestForNewsFeedVideos() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("startFrom", String.valueOf(startFrom));
        queryMap.put("count", String.valueOf(count));
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", lat);
        queryMap.put("current_longitude", lng);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getNewsFeedVideos(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEWS_FEED_VIDEOS, call, this);
    }

    private void requestForNearByVideos(String catId) {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", "0");
        queryMap.put("count", "11");
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", lat);
        queryMap.put("current_longitude", lng);
        queryMap.put("max_range", locMaxRange);
        queryMap.put("min_range", locMinRange);
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllNearVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEAR_BY_VIDEOS, call, this);
    }

    private void requestForTopRatedVideos(String catId) {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", "0");
        queryMap.put("count", "11");
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", lat);
        queryMap.put("current_longitude", lng);
//        queryMap.put("max_range", locMaxRange);
//        queryMap.put("min_range", locMinRange);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllTopRatedVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_TOP_RATED_VIDEOS, call, this);
    }

    private void requestForPopularVideos(String catId) {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category_id", catId);
        queryMap.put("startFrom", "0");
        queryMap.put("count", "11");
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", lat);
        queryMap.put("current_longitude", lng);
//        queryMap.put("max_range", locMaxRange);
//        queryMap.put("min_range", locMinRange);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllPopularVideosByCategory(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_POPULAR_VIDEOS, call, this);
    }

    @Override
    public void onClick(View view, VideoModel videoModel, int position) {
        switch (view.getId()) {
            case R.id.relItem:
                mainActivity.replaceFragment(VideoDetailFragment.newInstance(feedVideoList, position), true, R.id.mainContainer);
                break;
            case R.id.imgProfile:
            case R.id.txtName:
            case R.id.txtLoc:
                mainActivity.replaceFragment(ProfileFragment.newInstance(feedVideoList.get(position).userId), true, R.id.mainContainer);
                break;
            case R.id.txtViewCount:
                //        mainActivity.replaceFragment(VideoViewedPeopleFragment.newInstance(feedVideoList.get(position).id), true, R.id.mainContainer);
                VideoViewedPeopleFragment videoViewedPeopleFragment = VideoViewedPeopleFragment.newInstance(videoModel.id);
                videoViewedPeopleFragment.show(mainActivity.getSupportFragmentManager(), videoViewedPeopleFragment.getTag());
                break;
            case R.id.txtDistance:
                String url = "http://maps.google.com/maps?saddr=" + videoModel.userLatitude + "," + videoModel.userLongitude
                        + "&daddr=" + videoModel.videoLatitude + "," + videoModel.videoLongitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mapIntent);
                break;
            default:
                break;
        }
    }

    private void updateCategoryList(int position, boolean callApi) {
        if (callApi || lastUpdatedPos != position) {
            lastUpdatedPos = position;
            for (int i = 0; i < categoryList.size(); i++) {
                categoryList.get(i).isSelected = i == position;
            }
            homeCategoryAdapter.addCategories(categoryList);
            if (categoryList.get(position).categoryName.equalsIgnoreCase("New Feed")) {
                rvNewsFeed.scrollToPosition(0);
                startFrom = 0;
                feedVideoList.clear();
                newsFeedAdapter.clearAllVideos();
                hasLoadedAllItems = false;
                requestForNewsFeedVideos();
            } else {
                nearByData = false;
                topRatedData = false;
                popularData = false;
                loadedDataCount = 0;
                requestForNearByVideos(categoryList.get(position).id);
                requestForTopRatedVideos(categoryList.get(position).id);
                requestForPopularVideos(categoryList.get(position).id);
            }
        }
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.imgCatBg:
            case R.id.txtCategory:
                updateCategoryList(position, false);
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        Log.e("Response", response.toString());
        switch (requestCode) {
            case WSUtils.REQ_FOR_CATEGORY_LIST:
                parseCategoryList(response);
                break;
            case WSUtils.REQ_FOR_NEWS_FEED_VIDEOS:
                parseNewsFeedVideo(response);
                break;
            case WSUtils.REQ_FOR_NEAR_BY_VIDEOS:
                parseNearByVideoList(response);
                break;
            case WSUtils.REQ_FOR_TOP_RATED_VIDEOS:
                parseTopRatedVideoList(response);
                break;
            case WSUtils.REQ_FOR_POPULAR_VIDEOS:
                parsePopularVideoList(response);
                break;
        }
    }

    private void parseCategoryList(JsonElement response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray catArr = jsonObject.getJSONArray("categories");
            categoryList.clear();
            for (int i = 0; i < catArr.length(); i++) {
                Gson gson = new Gson();
                CategoryModel categoryModel = gson.fromJson(catArr.get(i).toString(), CategoryModel.class);
                categoryModel.isSelected = i == 0;
                categoryList.add(categoryModel);
            }
            homeCategoryAdapter.addCategories(categoryList);
            MainActivity.categoryListStatic.clear();
            MainActivity.categoryListStatic.addAll(categoryList);
            lastUpdatedPos = 0;
            startFrom = 0;
            feedVideoList.clear();
            newsFeedAdapter.clearAllVideos();
            hasLoadedAllItems = false;
            requestForNewsFeedVideos();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseNewsFeedVideo(JsonElement response) {
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                txtNoData.setVisibility(View.GONE);
                rvNewsFeed.setVisibility(View.VISIBLE);
                if (jsonObject.has("news_feed_videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("news_feed_videos");
                    tempFeedVideoList.clear();
                    for (int i = 0; i < feedVidArr.length(); i++) {
                        Gson gson = new Gson();
                        VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                        feedVideoList.add(videoModel);
                        tempFeedVideoList.add(videoModel);
                    }
                    startFrom += feedVidArr.length();
                    if (startFrom % count != 0) {
                        hasLoadedAllItems = true;
                        isLoading = false;
                    }
                    newsFeedAdapter.addVideos(tempFeedVideoList);
                }
            } else {
                if (startFrom == 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                    rvNewsFeed.setVisibility(View.GONE);
                } else {
                    hasLoadedAllItems = true;
                    isLoading = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (categoryList != null && categoryList.size() > 0)
                setData(categoryList.get(0).categoryName);
        }
    }

    private void parseNearByVideoList(JsonElement response) {
        loadedDataCount++;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
                    nearByVideoList.clear();
                    relVideos.setVisibility(View.VISIBLE);
                    rvNearByVideos.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                    if (feedVidArr.length() > 0) {
                        for (int i = 0; i < feedVidArr.length(); i++) {
                            Gson gson = new Gson();
                            VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                            nearByVideoList.add(videoModel);
                        }
                        nearByData = true;
                    } else {
                        relVideos.setVisibility(View.GONE);
                        rvNearByVideos.setVisibility(View.GONE);
                        nearByData = false;
                        if (loadedDataCount >= 3 && !topRatedData && !popularData) {
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    nearByVideosAdapter.addVideos(nearByVideoList);
                }
            } else {
                relVideos.setVisibility(View.GONE);
                rvNearByVideos.setVisibility(View.GONE);
                nearByData = false;
                if (loadedDataCount >= 3 && !topRatedData && !popularData) {
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            setData(categoryList.get(lastUpdatedPos).categoryName);
        }
    }

    private void parseTopRatedVideoList(JsonElement response) {
        loadedDataCount++;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
                    topRatedVideoList.clear();
                    relTopRated.setVisibility(View.VISIBLE);
                    rvTopRated.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                    if (feedVidArr.length() > 0) {
                        for (int i = 0; i < feedVidArr.length(); i++) {
                            Gson gson = new Gson();
                            VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                            topRatedVideoList.add(videoModel);
                        }
                        topRatedData = true;
                    } else {
                        relTopRated.setVisibility(View.GONE);
                        rvTopRated.setVisibility(View.GONE);
                        topRatedData = false;
                        if (loadedDataCount >= 3 && !nearByData && !popularData) {
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    topRatedVideosAdapter.addVideos(topRatedVideoList);
                }
            } else {
                relTopRated.setVisibility(View.GONE);
                rvTopRated.setVisibility(View.GONE);
                topRatedData = false;
                if (loadedDataCount >= 3 && !nearByData && !popularData) {
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            setData(categoryList.get(lastUpdatedPos).categoryName);
        }
    }

    private void parsePopularVideoList(JsonElement response) {
        loadedDataCount++;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
                    popularVideoList.clear();
                    relPopularSearch.setVisibility(View.VISIBLE);
                    rvPopularsearch.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                    if (feedVidArr.length() > 0) {
                        for (int i = 0; i < feedVidArr.length(); i++) {
                            Gson gson = new Gson();
                            VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                            popularVideoList.add(videoModel);
                        }
                        popularData = true;
                        setData(categoryList.get(lastUpdatedPos).categoryName);
                    } else {
                        relPopularSearch.setVisibility(View.GONE);
                        rvPopularsearch.setVisibility(View.GONE);
                        popularData = false;
                        if (loadedDataCount >= 3 && !nearByData && !topRatedData) {
                            txtNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    popularVideosAdapter.addVideos(popularVideoList);
                }
            } else {
                relPopularSearch.setVisibility(View.GONE);
                rvPopularsearch.setVisibility(View.GONE);
                popularData = false;
                if (loadedDataCount >= 3 && !nearByData && !topRatedData) {
                    txtNoData.setVisibility(View.VISIBLE);
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
        requestForNewsFeedVideos();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }

}
