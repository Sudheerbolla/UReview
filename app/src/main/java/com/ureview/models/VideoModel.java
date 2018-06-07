package com.ureview.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoModel {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("video_owner_id")
    @Expose
    public String videoOwnerId;
    @SerializedName("video_title")
    @Expose
    public String videoTitle;
    @SerializedName("video")
    @Expose
    public String video;
    @SerializedName("video_poster_image")
    @Expose
    public String videoPosterImage;
    @SerializedName("video_duration")
    @Expose
    public String videoDuration;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("video_description")
    @Expose
    public String videoDescription;
    @SerializedName("video_tags")
    @Expose
    public String videoTags;
    @SerializedName("video_rating")
    @Expose
    public String videoRating;
    @SerializedName("user_latitude")
    @Expose
    public String userLatitude;
    @SerializedName("user_longitude")
    @Expose
    public String userLongitude;
    @SerializedName("user_location")
    @Expose
    public String userLocation;
    @SerializedName("video_latitude")
    @Expose
    public String videoLatitude;
    @SerializedName("video_longitude")
    @Expose
    public String videoLongitude;
    @SerializedName("video_location")
    @Expose
    public String videoLocation;
    @SerializedName("video_watched_count")
    @Expose
    public String videoWatchedCount;
    @SerializedName("video_status")
    @Expose
    public String videoStatus;
    @SerializedName("video_privacy")
    @Expose
    public String videoPrivacy;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("user_image")
    @Expose
    public String userImage;
    @SerializedName("user_rating")
    @Expose
    public String userRating;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("category_name")
    @Expose
    public String categoryName;
    @SerializedName("category_image")
    @Expose
    public String categoryImage;
    @SerializedName("category_active_image")
    @Expose
    public String categoryActiveImage;
    @SerializedName("category_bg_image")
    @Expose
    public String categoryBgImage;
    @SerializedName("category_active_bg_image")
    @Expose
    public String categoryActiveBgImage;
    @SerializedName("follow_status")
    @Expose
    public String followStatus;
    @SerializedName("rating_given")
    @Expose
    public Integer ratingGiven;
    @SerializedName("distance")
    @Expose
    public String distance;
    @SerializedName("customer_rating")
    @Expose
    public Integer customerRating;

}