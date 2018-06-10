package com.ureview.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.SearchVideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.models.VideoModel;
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

    //Search People Pagination
    private boolean isLoading, hasLoadedAllItems;
    private int startFrom = 0;
    protected String searchText = "";

    public static SearchVideosFragment newInstance() {
        return new SearchVideosFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity.mLastLocation != null) {
            currLat = String.valueOf(mainActivity.mLastLocation.getLatitude());
            currLng = String.valueOf(mainActivity.mLastLocation.getLongitude());
        }
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_video, container, false);

        rvSearchVideo = rootView.findViewById(R.id.rvSearchVideo);
        rvSearchVideo.setListPagination(this);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        searchVideosAdapter = new SearchVideosAdapter(getActivity(), this);
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchVideo.setAdapter(searchVideosAdapter);
        videosArrList.clear();
        hasLoadedAllItems = false;
        startFrom = 0;
        requestForSearchVideos();
        return rootView;
    }

    private void requestForSearchVideos() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", userId);
            jsonObjectReq.put("video_name", mainActivity.edtText.getText().toString().trim());
            jsonObjectReq.put("latitude", "17.325400");
            jsonObjectReq.put("longitude", "78.362000");
            jsonObjectReq.put("startFrom", String.valueOf(startFrom));
            jsonObjectReq.put("count", 10);
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
        }
    }

    private void parseNewsFeedVideo(JsonElement response) {
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                txtNoData.setVisibility(View.GONE);
                rvSearchVideo.setVisibility(View.VISIBLE);
                if (jsonObject.has("search_videos")) {
                    JSONArray feedVidArr = jsonObject.getJSONArray("search_videos");
                    tempVideosArrList.clear();
                    for (int i = 0; i < feedVidArr.length(); i++) {
                        Gson gson = new Gson();
                        VideoModel videoModel = gson.fromJson(feedVidArr.get(i).toString(), VideoModel.class);
                        videosArrList.add(videoModel);
                        tempVideosArrList.add(videoModel);
                    }
                    startFrom += feedVidArr.length();
                    searchVideosAdapter.addVideos(tempVideosArrList);
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
        switch (view.getId()) {
            case R.id.imgLocation:
                mainActivity.replaceFragment(VideoDetailFragment.newInstance(videosArrList, position), true, R.id.mainContainer);
                break;
            case R.id.txtViewCount:
                mainActivity.addFragment(VideoViewedPeopleFragment.newInstance(videosArrList.get(position).id), true, R.id.mainContainer);
                break;
            case R.id.txtDistance:
                VideoModel videoModel = videosArrList.get(position);
                String url = "http://maps.google.com/maps?saddr=" + videoModel.userLatitude + "," + videoModel.userLongitude
                        + "&daddr=" + videoModel.videoLatitude + "," + videoModel.videoLongitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(mapIntent);
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
/*http://18.216.101.112/search-videos

{
    "status": "success",
    "message": "Search videos data",
    "search_videos": [
        {
            "id": "2",
            "video_owner_id": "1",
            "video_title": "Club Lounge",
            "video": "http://18.216.101.112/uploads/videos/1200196517.mp4",
            "video_poster_image": "http://18.216.101.112/uploads/video_thumbnails/970873236.png",
            "video_duration": "0:15",
            "category_id": "6",
            "video_description": "",
            "video_tags": "Club#Lounge#Awesome#Brew#music#Dj",
            "video_rating": "0",
            "user_latitude": "17.489945721446",
            "user_longitude": "78.371963853256",
            "user_location": "",
            "video_latitude": "17.431286",
            "video_longitude": "78.406967",
            "video_location": "Amnesia Lounge Bar, Road Number 36, CBI Colony, Jubilee Hills, Hyderabad, Telangana, India",
            "video_watched_count": "11",
            "video_status": "A",
            "video_privacy": "public",
            "created_date": "2018-06-06 08:48:10",
            "user_id": "1",
            "first_name": "Madhu",
            "last_name": "Sudhan",
            "user_image": "",
            "user_rating": "2",
            "city": "Hyderabad",
            "category_name": "Pubs/Clubs",
            "category_image": "pubs_clubs.png",
            "category_active_image": "pubs_clubs_active.png",
            "category_bg_image": "pubs_clubs_bg.png",
            "category_active_bg_image": "pubs_clubs_active_bg.png",
            "distance": "3.99 kms",
            "follow_status": "",
            "rating_given": 0,
            "customer_rating": 0
        }
    ]
}
*/