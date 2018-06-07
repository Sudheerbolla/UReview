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
import com.ureview.utils.StaticUtils;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

public class VideosFragment extends BaseFragment implements IParserListener<JsonElement>, IClickListener {
    private View rootView;
    private RecyclerView rvVideos;
    private ProfileVideosAdapter videosAdapter;
    private MainActivity mainActivity;
    private ArrayList<VideoModel> userVideosModelArrayList;
    private String userId;

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userVideosModelArrayList = new ArrayList<>();
//        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        userId = "1";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_videos, container, false);
        rvVideos = rootView.findViewById(R.id.rvVideos);
        videosAdapter = new ProfileVideosAdapter(mainActivity, userVideosModelArrayList, this);
        rvVideos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvVideos.setAdapter(videosAdapter);

        ViewCompat.setNestedScrollingEnabled(rootView.findViewById(R.id.nestedScrollView), true);
        ViewCompat.setNestedScrollingEnabled(rvVideos, false);
        requestForGetVideosByUserId();
        return rootView;
    }

    private void requestForGetVideosByUserId() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        queryMap.put("current_longitude", "78.4398");
        queryMap.put("current_latitude", "17.4138");
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getVideosByUserId(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_PROFILE_VIDEOS, call, this);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_PROFILE_VIDEOS:
                parseGetProfileVideosResponse((JsonObject) response);
                break;
            default:
                break;
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
                            }
                            videosAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
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

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
/*

{
    "status": "success",
    "message": "User videos data",
    "videos": [
        {
            "id": "4",
            "video_owner_id": "1",
            "video_title": "Dance Club",
            "video": "http://18.216.101.112/uploads/videos/128304524.mp4",
            "video_poster_image": "http://18.216.101.112/uploads/video_thumbnails/290767264.png",
            "video_duration": "0:08",
            "category_id": "6",
            "video_description": "",
            "video_tags": "Dance#Dj#Club#Music",
            "video_rating": "0",
            "user_latitude": "17.489853421578",
            "user_longitude": "78.371855837809",
            "user_location": "Block 4, Hafeezpet, Hyderabad, Telangana 500049, India",
            "video_latitude": "17.489853",
            "video_longitude": "78.371856",
            "video_location": "Banjara Hills, Hyderabad, Telangana, India",
            "video_watched_count": "22",
            "video_status": "A",
            "video_privacy": "public",
            "created_date": "2018-06-04 17:06:22",
            "user_id": "1",
            "first_name": "Madhu",
            "last_name": "Sudhan",
            "user_image": "",
            "user_rating": "2",
            "city": "Hyderabad",
            "category_name": "Pubs/Clubs",
            "category_image": "http://18.216.101.112/uploads/category_images/pubs_clubs.png",
            "category_active_image": "http://18.216.101.112/uploads/category_images/pubs_clubs_active.png",
            "category_bg_image": "http://18.216.101.112/uploads/category_images/pubs_clubs_bg.png",
            "category_active_bg_image": "http://18.216.101.112/uploads/category_images/pubs_clubs_active_bg.png",
            "customer_rating": 0,
            "distance": "11.11 kms"
        },
        {
            "id": "23",
            "video_owner_id": "1",
            "video_title": "Busy day ",
            "video": "http://18.216.101.112/uploads/videos/1148224058.mp4",
            "video_poster_image": "http://18.216.101.112/uploads/video_thumbnails/2029374306.png",
            "video_duration": "0:07",
            "category_id": "7",
            "video_description": "",
            "video_tags": "#busyday #busylife",
            "video_rating": "3",
            "user_latitude": "22.278778152811",
            "user_longitude": "114.17314815668",
            "user_location": "498, Rd Number 36, Venkatagiri, Jubilee Hills, Hyderabad, Telangana 500033, India",
            "video_latitude": "17.432523",
            "video_longitude": "78.407015",
            "video_location": "Jubilee Hills, Hyderabad, Telangana, India",
            "video_watched_count": "2",
            "video_status": "A",
            "video_privacy": "public",
            "created_date": "2018-05-31 16:42:12",
            "user_id": "1",
            "first_name": "Madhu",
            "last_name": "Sudhan",
            "user_image": "",
            "user_rating": "2",
            "city": "Hyderabad",
            "category_name": "Happy Hours",
            "category_image": "http://18.216.101.112/uploads/category_images/happy_hours_active.png",
            "category_active_image": "http://18.216.101.112/uploads/category_images/happy_hours.png",
            "category_bg_image": "http://18.216.101.112/uploads/category_images/happy_hours_bg.png",
            "category_active_bg_image": "http://18.216.101.112/uploads/category_images/happy_hours_active_bg.png",
            "customer_rating": 1,
            "distance": "4.05 kms"
        }
    ]
}*/