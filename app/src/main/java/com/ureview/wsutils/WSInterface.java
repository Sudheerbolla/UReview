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


    @POST("/user-registration")
    Call<JsonElement> userRegistration(@Body RequestBody params);

    @GET("/check-user-profile")
    Call<JsonElement> checkUserProfile(@Query("auth_id") String userId);

    @POST("/check-user-otp")
    Call<JsonElement> checkUserOTP(@Body RequestBody params);

    @POST("/login-verify-otp")
    Call<JsonElement> loginVerifyOTP(@Body RequestBody params);

    @POST("/delete-profile")
    Call<JsonElement> deleteProfile(@Body RequestBody params);

    @GET("/get_employees/")
    Call<JsonElement> dummyCall();

    @GET("/get-categories")
    Call<JsonElement> getAllCategories(@Query(value = "user_id", encoded = false) String userId);

    @GET("/get-all-videos-by-category")
    Call<JsonElement> getAllVideosByCategory(@Query(value = "category_id") String catId, @Query(value = "startFrom") String startFrom
            , @Query(value = "count") String court, @Query(value = "user_id") String userId);

    @GET("/news-feed-videos")
    Call<JsonElement> getNewsFeedVideos(@Query(value = "startFrom") String startFrom
            , @Query(value = "count") String count, @Query(value = "user_id") String userId
            , @Query(value = "current_latitude") String currentLatitude, @Query(value = "current_longitude") String currentLongitude);

    @GET("/get-all-near-videos-by-category")
    Call<JsonElement> getAllNearVideosByCategory(@Query(value = "category_id") String categoryId,
                                                 @Query(value = "startFrom") String startFrom
            , @Query(value = "count") String count, @Query(value = "user_id") String userId
            , @Query(value = "current_latitude") String currentLatitude, @Query(value = "current_longitude") String currentLongitude
            , @Query(value = "max_range") String maxRange, @Query(value = "min_range") String minRange);

    @GET("/get-all-top-rated-videos-by-category")
    Call<JsonElement> getAllTopRatedVideosByCategory(@Query(value = "category_id") String categoryId,
                                                     @Query(value = "startFrom") String startFrom
            , @Query(value = "count") String count, @Query(value = "user_id") String userId
            , @Query(value = "current_latitude") String currentLatitude, @Query(value = "current_longitude") String currentLongitude);

    @GET("/get-all-popular-videos-by-category")
    Call<JsonElement> getAllPopularVideosByCategory(@Query(value = "category_id") String categoryId,
                                                    @Query(value = "startFrom") String startFrom
            , @Query(value = "count") String count, @Query(value = "user_id") String userId
            , @Query(value = "current_latitude") String currentLatitude, @Query(value = "current_longitude") String currentLongitude);

    @GET("/user-notications")
    Call<JsonElement> getNotifications(@Query("user_id") String userId);

    @POST("/delete-notification")
    Call<JsonElement> deleteNotification(@Body RequestBody params);

    @POST("/read-notification")
    Call<JsonElement> readNotification(@Body RequestBody params);

    @GET("/get-userdata-by-id")
    Call<JsonElement> getUserData(@Query("user_id") String userId);

    @GET("/pages")
    Call<JsonElement> getStaticPagesContent(@Query("slug_name") String userId);

    @GET("/follow_you_follow_list")
    Call<JsonElement> getFollowList(@Query("user_id") String userId);

    @POST("/follow-request")
    Call<JsonElement> followUser(@Body RequestBody params);

    @POST("/un-follow-user")
    Call<JsonElement> unFollowUser(@Body RequestBody params);

    @POST("/block-user")
    Call<JsonElement> blockUser(@Body RequestBody params);

    @POST("/search-videos")
    Call<JsonElement> searchVideos(@Body RequestBody params);

    @GET("/search-users")
    Call<JsonElement> searchUsers(@QueryMap(encoded = true) HashMap<String, String> params);

//    @GET("/get_employees/")
//    Call<JsonElement> dummyCall();
//
//    @GET("/get_categories/")
//    Call<JsonElement> getAllCategories();
//
//    @POST("/clients")
//    Call<JsonElement> createClient(@Body RequestBody params);
//
//    @POST("/oauth2/token")
//    Call<JsonElement> loginOrGetAuthToken(@Query("cid") String cid, @Body RequestBody params);
//
//    @POST("/consumers/{consumerId}/vcode/{code}")
//    Call<JsonElement> validateOtp(@Path("consumerId") String userId, @Path("code") String code, @Body RequestBody params);
//
//    @PUT("/consumers/vcode/forgotpassword")
//    Call<Void> validateForgotPasswordOtp(@Query(value = "phone", encoded = true) String phone, @Body RequestBody params);
//
//    @GET("/consumers/{consumerId}/vcode")
//    Call<JsonElement> resendOtp(@Path("consumerId") String userId, @QueryMap(encoded = true) HashMap<String, String> params);
//
//    @GET("/consumers/vcode/resend")
//    Call<JsonElement> resendOtpForgotPwd(@QueryMap(encoded = true) HashMap<String, String> params);
//
//    @GET("/consumers/vcode/forgotpassword")
//    Call<JsonElement> forgotPassword(@QueryMap(encoded = true) HashMap<String, String> params);
//
//    @POST("/users/{uid}/devices")
//    Call<JsonElement> createUserDevice(@Path("uid") String userId, @Body RequestBody params);
//
//    @POST("/users/{userId}/devices/{deviceId}/profiles")
//    Call<JsonElement> createUserProfile(@Path("userId") String userId, @Path("deviceId") String deviceId, @Body RequestBody params);
//
//    @GET("/users/{uid}/devices")
//    Call<JsonElement> getUserDevices(@Path("uid") String userId);
//
//    @GET("/users/{userId}/devices/{deviceId}/profiles")
//    Call<JsonElement> getUserProfiles(@Path("userId") String userId, @Path("deviceId") String deviceId);
//
//    @GET("/consumers/{consumer_id}")
//    Call<JsonElement> getUserProfile(@Path("consumer_id") String consumer_id);
//
//    @GET("/consumerprofiles/{consumerProfileId}/token")
//    Call<JsonElement> createJWSToken(@Path("consumerProfileId") String consumerProfileId);
//
//    @GET("/couponcode/validate")
//    Call<JsonElement> validateCouponCode(@QueryMap(encoded = true) HashMap<String, String> params);
//
//    @POST("/couponcode/used")
//    Call<Void> updateUsedCouponCode(@Body RequestBody params);


}
