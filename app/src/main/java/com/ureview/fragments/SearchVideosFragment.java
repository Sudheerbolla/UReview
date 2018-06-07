package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.SearchVideosAdapter;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class SearchVideosFragment extends BaseFragment implements IParserListener<JsonElement> {
    private View rootView;
    private RecyclerView rvSearchVideo;
    private SearchVideosAdapter searchVideosAdapter;
    private MainActivity mainActivity;
    private String userId;

    public static SearchVideosFragment newInstance() {
        return new SearchVideosFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_video, container, false);

        rvSearchVideo = rootView.findViewById(R.id.rvSearchVideo);
        searchVideosAdapter = new SearchVideosAdapter(getActivity());
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchVideo.setAdapter(searchVideosAdapter);
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
            jsonObjectReq.put("startFrom", 0);
            jsonObjectReq.put("count", 10);
            jsonObjectReq.put("current_latitude", "17.4138");
            jsonObjectReq.put("current_longitude", "78.4398");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().searchVideos(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_SEARCH_VIDEOS, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {

    }

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

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