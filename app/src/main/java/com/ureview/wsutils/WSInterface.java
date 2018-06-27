package com.ureview.wsutils;

import com.google.gson.JsonElement;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface WSInterface {

    @POST("user-registration")
    Call<JsonElement> userRegistration(@Body RequestBody params);

    @GET("check-user-profile")
    Call<JsonElement> checkUserProfile(@Query("auth_id") String userId, @Query("auth_type") String authType);

    @POST("check-user-otp")
    Call<JsonElement> checkUserOTP(@Body RequestBody params);

    @POST("login-verify-otp")
    Call<JsonElement> loginVerifyOTP(@Body RequestBody params);

    @POST("delete-profile")
    Call<JsonElement> deleteProfile(@Body RequestBody params);

    @GET("get-categories")
    Call<JsonElement> getAllCategories(@Query(value = "user_id", encoded = false) String userId);

    @GET("get-all-videos-by-category")
    Call<JsonElement> getAllVideosByCategory(@Query(value = "category_id") String catId, @Query(value = "startFrom") String startFrom
            , @Query(value = "count") String court, @Query(value = "user_id") String userId);

    @GET("news-feed-videos")
    Call<JsonElement> getNewsFeedVideos(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("get-all-near-videos-by-category")
    Call<JsonElement> getAllNearVideosByCategory(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("get-all-top-rated-videos-by-category")
    Call<JsonElement> getAllTopRatedVideosByCategory(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("get-all-popular-videos-by-category")
    Call<JsonElement> getAllPopularVideosByCategory(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("search-users")
    Call<JsonElement> getSearchUsers(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("get-video-viewed-userlist")
    Call<JsonElement> getVideoViewedUserList(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("user-notications")
    Call<JsonElement> getNotifications(@Query("user_id") String userId);

    @POST("video-rating-by-user")
    Call<JsonElement> videoRatingByUser(@Body RequestBody params);

    @GET("get-video-by-id")
    Call<JsonElement> getRelatedVideos(@QueryMap(encoded = true) HashMap<String, String> params);

    @POST("report-video")
    Call<JsonElement> reportVideo(@Body RequestBody params);

    @POST("delete-notification")
    Call<JsonElement> deleteNotification(@Body RequestBody params);

    @POST("read-notification")
    Call<JsonElement> readNotification(@Body RequestBody params);

    @GET("get-userdata-by-id")
    Call<JsonElement> getUserData(@Query("user_id") String userId, @Query("follower_id") String follower_id);

    //user_id=3&login_user_id=4
    @GET("pages")
    Call<JsonElement> getStaticPagesContent(@Query("slug_name") String userId);

    @GET("follow_you_follow_list")
    Call<JsonElement> getFollowList(@Query("user_id") String userId, @Query("login_user_id") String loginUserId);

    @POST("follow-request")
    Call<JsonElement> followUser(@Body RequestBody params);

    @POST("un-follow-user")
    Call<JsonElement> unFollowUser(@Body RequestBody params);

    @POST("block-user")
    Call<JsonElement> blockUser(@Body RequestBody params);

    @POST("search-videos")
    Call<JsonElement> searchVideos(@Body RequestBody params);

    @GET("share-video")
    Call<JsonElement> shareVideo(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("search-users")
    Call<JsonElement> searchUsers(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("user-videoview-statistics")
    Call<JsonElement> getUserStatistics(@Query("user_id") String userId, @Query("year") String year);

    @GET("follow-user-videoview-statistics")
    Call<JsonElement> getRankings(@Query("user_id") String userId);

    @GET("get-videos-by-user-id")
    Call<JsonElement> getVideosByUserId(@QueryMap(encoded = true) HashMap<String, String> params);

    @POST("edit-profile")
    Call<JsonElement> editProfile(@Body RequestBody params);

    @POST("video-upload")
    Call<JsonElement> uploadVideo(@Body RequestBody params);

    @POST("delete-video")
    Call<JsonElement> deleteVideo(@Body RequestBody params);

}