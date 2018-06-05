package com.ureview.wsutils;

import com.google.gson.JsonElement;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @GET("/user-notications")
    Call<JsonElement> getNotifications(@Query("user_id") String userId);

    @POST("/delete-notification")
    Call<JsonElement> deleteNotification(@Body RequestBody params);





    @GET("/get_employees/")
    Call<JsonElement> dummyCall();

    @GET("/get_categories/")
    Call<JsonElement> getAllCategories();

    @POST("/clients")
    Call<JsonElement> createClient(@Body RequestBody params);

    @POST("/consumers")
    Call<JsonElement> createConsumer(@Body RequestBody params);

    @POST("/settings")
    Call<JsonElement> getSettings(@Body RequestBody params);

    @POST("/oauth2/token")
    Call<JsonElement> loginOrGetAuthToken(@Query("cid") String cid, @Body RequestBody params);

    @POST("/consumers/{consumerId}/vcode/{code}")
    Call<JsonElement> validateOtp(@Path("consumerId") String userId, @Path("code") String code, @Body RequestBody params);

    @PUT("/consumers/vcode/forgotpassword")
    Call<Void> validateForgotPasswordOtp(@Query(value = "phone", encoded = true) String phone, @Body RequestBody params);

    @GET("/consumers/{consumerId}/vcode")
    Call<JsonElement> resendOtp(@Path("consumerId") String userId, @QueryMap(encoded = true) HashMap<String, String> params);

    @GET("/consumers/vcode/resend")
    Call<JsonElement> resendOtpForgotPwd(@QueryMap(encoded = true) HashMap<String, String> params);

    @GET("/consumers/vcode/forgotpassword")
    Call<JsonElement> forgotPassword(@QueryMap(encoded = true) HashMap<String, String> params);

    @POST("/users/{uid}/devices")
    Call<JsonElement> createUserDevice(@Path("uid") String userId, @Body RequestBody params);

    @POST("/users/{userId}/devices/{deviceId}/profiles")
    Call<JsonElement> createUserProfile(@Path("userId") String userId, @Path("deviceId") String deviceId, @Body RequestBody params);

    @GET("/users/{uid}/devices")
    Call<JsonElement> getUserDevices(@Path("uid") String userId);

    @GET("/users/{userId}/devices/{deviceId}/profiles")
    Call<JsonElement> getUserProfiles(@Path("userId") String userId, @Path("deviceId") String deviceId);

    @GET("/consumers/{consumer_id}")
    Call<JsonElement> getUserProfile(@Path("consumer_id") String consumer_id);

    @GET("/consumerprofiles/{consumerProfileId}/token")
    Call<JsonElement> createJWSToken(@Path("consumerProfileId") String consumerProfileId);

    @GET("/couponcode/validate")
    Call<JsonElement> validateCouponCode(@QueryMap(encoded = true) HashMap<String, String> params);

    @POST("/couponcode/used")
    Call<Void> updateUsedCouponCode(@Body RequestBody params);


}
