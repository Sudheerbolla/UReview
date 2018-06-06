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
    private CustomTextView txtSeeAllVideos, txtSeeAllTopRated, txtSeeAllPopularSearch;
    private ProfileVideosAdapter profileVideosAdapter;
    private RelativeLayout relVideos, relTopRated, relPopularSearch;

    //News Feed Pagination
    private boolean isLoading, hasLoadedAllItems;
    private int page = 1, startFrom = 0;

    private ArrayList<CategoryModel> categoryList = new ArrayList<>();
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> tempFeedVideoList = new ArrayList<>();
    private ArrayList<VideoModel> videoList = new ArrayList<>();

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
        profileVideosAdapter = new ProfileVideosAdapter(getActivity());
        rvNewsFeed.setAdapter(newsFeedAdapter);
        rvNewsFeed.setListPagination(this);
        rvCategories.setAdapter(homeCategoryAdapter);
        rvNearByVideos.setAdapter(profileVideosAdapter);
        rvTopRated.setAdapter(profileVideosAdapter);
        rvPopularsearch.setAdapter(profileVideosAdapter);

        relVideos = rootView.findViewById(R.id.relVideos);
        relTopRated = rootView.findViewById(R.id.relTopRated);
        relPopularSearch = rootView.findViewById(R.id.relPopularSearch);

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
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getNewsFeedVideos(/*String.valueOf(startFrom)*/"0", "10", "1");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_NEWS_FEED_VIDEOS, call, this);
    }

    private void requestForVideosByCat(String catId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getAllVideosByCategory(catId, "0", "10", "1");
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_VIDEO_LIST_BY_CAT, call, this);
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
            requestForNewsFeedVideos();
        } else {
            requestForVideosByCat(/*categoryList.get(position).id*/"7");
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
            case WSUtils.REQ_FOR_VIDEO_LIST_BY_CAT:
                parseVideoList(response);
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
                    newsFeedAdapter.addVideos(feedVideoList);
                    setData(categoryList.get(0).categoryName);
                }
            } else {
                if (startFrom == 0) {
                    rvNewsFeed.setVisibility(View.GONE);
                } else {
                    hasLoadedAllItems = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseVideoList(JsonElement response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("videos")) {
                JSONArray feedVidArr = jsonObject.getJSONObject("videos").getJSONArray("videos");
                videoList.clear();
                for (int i = 0; i < feedVidArr.length(); i++) {
                    Gson gson = new Gson();
                    VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                    videoList.add(videoModel);
                }
                profileVideosAdapter.addVideos(videoList);
//                setData(categoryList.get(0).categoryName);
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
