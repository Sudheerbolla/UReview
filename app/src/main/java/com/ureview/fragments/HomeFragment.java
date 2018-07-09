package com.ureview.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
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
import com.ureview.utils.Constants;
import com.ureview.utils.LocalData;
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
    private int maxLimit = 10;

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
    private int selectedPosition;
    private boolean autoCall;
    private String catId = "";

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
        if (!TextUtils.isEmpty(LocalData.homCatId)) {
            catId = LocalData.homCatId;
            lastUpdatedPos = LocalData.homCatPos;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalData.homCatId = catId;
        LocalData.homCatPos = lastUpdatedPos;
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
        nearByVideosAdapter = new VideosAdapter(getActivity(), this, true, Constants.NEARBY);
        topRatedVideosAdapter = new VideosAdapter(getActivity(), this, true, Constants.TOPRATED);
        popularVideosAdapter = new VideosAdapter(getActivity(), this, true, Constants.POPULAR);

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

        if (MainActivity.mLastLocation != null) {
            lat = String.valueOf(MainActivity.mLastLocation.getLatitude());
            lng = String.valueOf(MainActivity.mLastLocation.getLongitude());
        }

        requestForCategoryList();
        return rootView;
    }

    private void setScrollListeners() {
        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null &&
                        categoryList.get(lastUpdatedPos).categoryName.equalsIgnoreCase("New Feed")) {
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
        txtSeeAllVideos.setVisibility(!b && nearByVideoList.size() > maxLimit ? View.VISIBLE : View.GONE);
        relTopRated.setVisibility(!b && topRatedVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        rvTopRated.setVisibility(!b && topRatedVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        txtSeeAllTopRated.setVisibility(!b && topRatedVideoList.size() > maxLimit ? View.VISIBLE : View.GONE);
        relPopularSearch.setVisibility(!b && popularVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        rvPopularsearch.setVisibility(!b && popularVideoList.size() > 0 ? View.VISIBLE : View.GONE);
        txtSeeAllPopularSearch.setVisibility(!b && popularVideoList.size() > maxLimit ? View.VISIBLE : View.GONE);
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
        if (missingAnyAttribute())
            return;
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("startFrom", String.valueOf(startFrom));
        queryMap.put("count", String.valueOf(count));
        queryMap.put("user_id", userId);
        queryMap.put("current_latitude", lat);
        queryMap.put("current_longitude", lng);

        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getNewsFeedVideos(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEWS_FEED_VIDEOS, call, this);
    }

    private boolean missingAnyAttribute() {
        if (userId == null || TextUtils.isEmpty(userId)) {
            return true;
        } else if (lat == null || TextUtils.isEmpty(lat)) {
            return true;
        } else if (lng == null || TextUtils.isEmpty(lng)) {
            return true;
        }
        return false;
    }

    private void requestForNearByVideos() {
        if (missingAnyAttribute())
            return;
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

    private void requestForTopRatedVideos() {
        if (missingAnyAttribute())
            return;
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

    private void requestForPopularVideos() {
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
    public void onClick(View view, ArrayList<VideoModel> videoModels, VideoModel videoModel, int position, String vidType) {
        selectedPosition = position;
        LocalData.homCatId = catId;
        LocalData.homCatPos = lastUpdatedPos;
        switch (view.getId()) {
            case R.id.relItem:

                if (!TextUtils.isEmpty(vidType) && position >= maxLimit) {
                    SeeAllVideosFragment seeAllVideosFragment = SeeAllVideosFragment.newInstance(vidType, catId);
                    seeAllVideosFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
                    seeAllVideosFragment.show(mainActivity.getSupportFragmentManager(), "SeeAllVideosFragment");
                } else {
//<<<<<<< HEAD
//                    VideoDetailFragment videoDetailFragment = VideoDetailFragment.newInstance(videoModels, position, vidType);
//                    videoDetailFragment.setTargetFragment(this, Constants.DIALOG_FRAGMENT);
//                    videoDetailFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Slide slideTransition = new Slide(Gravity.START);
//                        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
//                        videoDetailFragment.setReenterTransition(slideTransition);
//                        videoDetailFragment.setExitTransition(slideTransition);
//                        videoDetailFragment.setSharedElementEnterTransition(new ChangeBounds());
//                    }
//                    videoDetailFragment.show(mainActivity.getSupportFragmentManager(), "VideoDetailFragment");
//=======
//                    VideoDetailFragment videoDetailFragment = VideoDetailFragment.newInstance(videoModels, position, vidType);
//                    videoDetailFragment.setTargetFragment(this, Constants.DIALOG_FRAGMENT);
//                    videoDetailFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
//                    videoDetailFragment.show(mainActivity.getSupportFragmentManager(), "VideoDetailFragment");
                    mainActivity.replaceFragment(VideoDetailFragmentNew.newInstance(videoModels, position), true, R.id.mainContainer);
//>>>>>>> bd36296978967fc02a32a6580610350eecbe2544
                }
                break;
            case R.id.imgProfile:
            case R.id.txtName:
            case R.id.txtLoc:
                mainActivity.replaceFragment(ProfileFragment.newInstance(feedVideoList.get(position).userId, feedVideoList.get(position).firstName.concat(" ").concat(feedVideoList.get(position).lastName)), true, R.id.mainContainer);
                break;
            case R.id.txtViewCount:
//                VideoViewedPeopleFragment videoViewedPeopleFragment = VideoViewedPeopleFragment.newInstance(videoModel.id);
//                videoViewedPeopleFragment.show(mainActivity.getSupportFragmentManager(), videoViewedPeopleFragment.getTag());

                VideoViewedPeopleFragmentNew videoViewedPeopleFragmentNew = VideoViewedPeopleFragmentNew.newInstance(videoModel.id);
                mainActivity.replaceFragment(videoViewedPeopleFragmentNew, true, R.id.mainContainer);
                break;
            case R.id.txtDistance:
                String url = "http://maps.google.com/maps?saddr=" + MainActivity.mLastLocation.getLatitude() + "," +
                        MainActivity.mLastLocation.getLongitude() + "&daddr=" + videoModel.videoLatitude + "," + videoModel.videoLongitude;
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
            rvCategories.scrollToPosition(position);
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
                autoCall = false;
                requestForNewsFeedVideos();
            } else {
                nearByData = false;
                topRatedData = false;
                popularData = false;
                loadedDataCount = 0;
                catId = categoryList.get(position).id;
                requestForNearByVideos();
                requestForTopRatedVideos();
                requestForPopularVideos();
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
        switch (view.getId()) {
            case R.id.txtSeeAllVideos:
                SeeAllVideosFragment seeAllVideosFragment = SeeAllVideosFragment.newInstance(Constants.NEARBY, catId);
                seeAllVideosFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
                seeAllVideosFragment.show(mainActivity.getSupportFragmentManager(), "SeeAllVideosFragment");
                break;
            case R.id.txtSeeAllTopRated:
                SeeAllVideosFragment seeAll1 = SeeAllVideosFragment.newInstance(Constants.TOPRATED, catId);
                seeAll1.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
                seeAll1.show(mainActivity.getSupportFragmentManager(), "SeeAllVideosFragment");
                break;
            case R.id.txtSeeAllPopularSearch:
                SeeAllVideosFragment seeAll2 = SeeAllVideosFragment.newInstance(Constants.POPULAR, catId);
                seeAll2.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
                seeAll2.show(mainActivity.getSupportFragmentManager(), "SeeAllVideosFragment");
                break;
        }
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
            if (TextUtils.isEmpty(LocalData.homCatId) || LocalData.homCatPos == -1) {
                lastUpdatedPos = 0;
                startFrom = 0;
                feedVideoList.clear();
                newsFeedAdapter.clearAllVideos();
                hasLoadedAllItems = false;
                autoCall = true;
                requestForNewsFeedVideos();
            } else {
                if (categoryList.size() > 1 && autoCall) {
                    updateCategoryList(LocalData.homCatPos, true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //"No Reviews available for this category.Try changing location or choose another category."

    private void parseNewsFeedVideo(JsonElement response) {
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            txtNoData.setText("No Reviews. Start following YouReviews for more videos");
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
                    if (categoryList.size() > 1 && autoCall)
                        updateCategoryList(1, true);
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
            txtNoData.setText("No Reviews available for this category.Try changing location or choose another category.");
            JSONObject jsonObject = new JSONObject(response.toString());
            nearByVideoList.clear();
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
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
            topRatedVideoList.clear();
            txtNoData.setText("No Reviews available for this category.Try changing location or choose another category.");
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
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
            txtNoData.setText("No Reviews available for this category.Try changing location or choose another category.");
            JSONObject jsonObject = new JSONObject(response.toString());
            popularVideoList.clear();
            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                if (jsonObject.has("videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("videos");
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
        autoCall = false;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra("position")) {
                        int position = data.getIntExtra("position", -1);
                        if (position != -1 && data.hasExtra("vidType")) {
                            String vidType = data.getStringExtra("vidType");
                            switch (vidType) {
                                case Constants.NEARBY:
                                    nearByVideoList.get(position).videoWatchedCount =
                                            String.valueOf(Integer.parseInt(nearByVideoList.get(position).videoWatchedCount) + 1);
                                    nearByVideosAdapter.notifyItemChanged(position, nearByVideoList.get(position));
                                    break;
                                case Constants.TOPRATED:
                                    topRatedVideoList.get(position).videoWatchedCount =
                                            String.valueOf(Integer.parseInt(topRatedVideoList.get(position).videoWatchedCount) + 1);
                                    topRatedVideosAdapter.notifyItemChanged(position, topRatedVideoList.get(position));
                                    break;
                                case Constants.POPULAR:
                                    popularVideoList.get(position).videoWatchedCount =
                                            String.valueOf(Integer.parseInt(popularVideoList.get(position).videoWatchedCount) + 1);
                                    popularVideosAdapter.notifyItemChanged(position, popularVideoList.get(position));
                                    break;
                                case "":
                                    feedVideoList.get(position).videoWatchedCount =
                                            String.valueOf(Integer.parseInt(feedVideoList.get(position).videoWatchedCount) + 1);
                                    newsFeedAdapter.notifyItemChanged(position, feedVideoList.get(position));
                                    break;
                            }
                        } else if (position != -1) {
                            feedVideoList.get(position).videoWatchedCount =
                                    String.valueOf(Integer.parseInt(feedVideoList.get(position).videoWatchedCount) + 1);
                            newsFeedAdapter.notifyItemChanged(position, feedVideoList.get(position));
                        }
                    }
                }
                break;
        }
    }
}
