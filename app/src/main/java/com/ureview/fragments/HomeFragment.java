package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
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
import com.ureview.adapters.ProfileVideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.CategoryModel;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;
import com.ureview.utils.views.recyclerView.Paginate;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;

public class HomeFragment extends BaseFragment implements IClickListener, View.OnClickListener, IParserListener<JsonElement>, Paginate.Callbacks {
    private View rootView;
    private CustomRecyclerView rvCategories, rvNewsFeed, rvNearByVideos, rvTopRated, rvPopularsearch;
    private NewsFeedAdapter newsFeedAdapter;
    private HomeCategoryAdapter homeCategoryAdapter;
    private MainActivity mainActivity;
    private CustomTextView txtSeeAllVideos, txtSeeAllTopRated, txtSeeAllPopularSearch, txtNoData;
    private ProfileVideosAdapter nearByVideosAdapter, topRatedVideosAdapter, popularVideosAdapter;
    private RelativeLayout relVideos, relTopRated, relPopularSearch;
    private boolean nearByData, topRatedData, popularData;
    private int loadedDataCount;

    //    News Feed Pagination
    private boolean isLoading, hasLoadedAllItems;
    private int startFrom = 0;

    private ArrayList<CategoryModel> categoryList = new ArrayList<>();
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> tempFeedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> nearByVideoList = new ArrayList<>();
    private ArrayList<VideoModel> topRatedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> popularVideoList = new ArrayList<>();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rvNewsFeed = rootView.findViewById(R.id.rvNewsFeed);
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
        nearByVideosAdapter = new ProfileVideosAdapter(getActivity());
        topRatedVideosAdapter = new ProfileVideosAdapter(getActivity());
        popularVideosAdapter = new ProfileVideosAdapter(getActivity());
        rvNewsFeed.setAdapter(newsFeedAdapter);
        rvNewsFeed.setListPagination(this);
        rvCategories.setAdapter(homeCategoryAdapter);
        rvNearByVideos.setAdapter(nearByVideosAdapter);
        rvTopRated.setAdapter(topRatedVideosAdapter);
        rvPopularsearch.setAdapter(popularVideosAdapter);

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

        requestForCategoryList();

        return rootView;
    }

    private void setData(String categoryName) {
        boolean b = categoryName.equalsIgnoreCase("New Feed");
        rvNewsFeed.setVisibility(b ? View.VISIBLE : View.GONE);
        relVideos.setVisibility(b ? View.GONE : View.VISIBLE);
        rvNearByVideos.setVisibility(b ? View.GONE : View.VISIBLE);
        relTopRated.setVisibility(b ? View.GONE : View.VISIBLE);
        rvTopRated.setVisibility(b ? View.GONE : View.VISIBLE);
        relPopularSearch.setVisibility(b ? View.GONE : View.VISIBLE);
        rvPopularsearch.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    private void requestForCategoryList() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllCategories("1");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_CATEGORY_LIST, call, this);
    }

    private void requestForNewsFeedVideos() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener()
                .getNewsFeedVideos(String.valueOf(startFrom), "5", "1", "17.4138", "78.4398");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEWS_FEED_VIDEOS, call, this);
    }

    private void requestForNearByVideos(String catId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener()
                .getAllNearVideosByCategory(catId, "0", "10", "1", "17.4138", "78.4398",
                        "", "");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEAR_BY_VIDEOS, call, this);
    }

    private void requestForTopRatedVideos(String catId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener()
                .getAllTopRatedVideosByCategory(catId, "0", "10", "1", "17.4138", "78.4398");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_TOP_RATED_VIDEOS, call, this);
    }

    private void requestForPopularVideos(String catId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener()
                .getAllPopularVideosByCategory(catId, "0", "10", "1", "17.4138", "78.4398");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_POPULAR_VIDEOS, call, this);
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.imgCatBg:
                updateCategoryList(position);
                break;
            default:
                mainActivity.setVideoReviewFragment();
                break;
        }
    }

    private void updateCategoryList(int position) {
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).isSelected = i == position;
        }
        homeCategoryAdapter.addCategories(categoryList);
        if (categoryList.get(position).categoryName.equalsIgnoreCase("New Feed")) {
            startFrom = 0;
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
        setData(categoryList.get(position).categoryName);
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
            setData(categoryList.get(0).categoryName);
            startFrom = 0;
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
                    newsFeedAdapter.addVideos(tempFeedVideoList);
                    setData(categoryList.get(0).categoryName);
                }
            } else {
                if (startFrom == 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                    rvNewsFeed.setVisibility(View.GONE);
                } else {
                    hasLoadedAllItems = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                    nearByVideosAdapter.addVideos(popularVideoList);
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
                    topRatedVideosAdapter.addVideos(popularVideoList);
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
