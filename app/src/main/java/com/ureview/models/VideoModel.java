package com.ureview.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoModel implements Parcelable {

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
    @SerializedName("shared_text")
    @Expose
    public String shared_text;
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

    protected VideoModel(Parcel in) {
        id = in.readString();
        videoOwnerId = in.readString();
        videoTitle = in.readString();
        video = in.readString();
        videoPosterImage = in.readString();
        videoDuration = in.readString();
        categoryId = in.readString();
        videoDescription = in.readString();
        videoTags = in.readString();
        videoRating = in.readString();
        userLatitude = in.readString();
        userLongitude = in.readString();
        userLocation = in.readString();
        videoLatitude = in.readString();
        videoLongitude = in.readString();
        videoLocation = in.readString();
        videoWatchedCount = in.readString();
        videoStatus = in.readString();
        videoPrivacy = in.readString();
        createdDate = in.readString();
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        userImage = in.readString();
        userRating = in.readString();
        city = in.readString();
        categoryName = in.readString();
        categoryImage = in.readString();
        categoryActiveImage = in.readString();
        categoryBgImage = in.readString();
        categoryActiveBgImage = in.readString();
        followStatus = in.readString();
        if (in.readByte() == 0) {
            ratingGiven = null;
        } else {
            ratingGiven = in.readInt();
        }
        distance = in.readString();
        if (in.readByte() == 0) {
            customerRating = null;
        } else {
            customerRating = in.readInt();
        }
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(videoOwnerId);
        parcel.writeString(videoTitle);
        parcel.writeString(video);
        parcel.writeString(videoPosterImage);
        parcel.writeString(videoDuration);
        parcel.writeString(categoryId);
        parcel.writeString(videoDescription);
        parcel.writeString(videoTags);
        parcel.writeString(videoRating);
        parcel.writeString(userLatitude);
        parcel.writeString(userLongitude);
        parcel.writeString(userLocation);
        parcel.writeString(videoLatitude);
        parcel.writeString(videoLongitude);
        parcel.writeString(videoLocation);
        parcel.writeString(videoWatchedCount);
        parcel.writeString(videoStatus);
        parcel.writeString(videoPrivacy);
        parcel.writeString(createdDate);
        parcel.writeString(userId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(userImage);
        parcel.writeString(userRating);
        parcel.writeString(city);
        parcel.writeString(categoryName);
        parcel.writeString(categoryImage);
        parcel.writeString(categoryActiveImage);
        parcel.writeString(categoryBgImage);
        parcel.writeString(categoryActiveBgImage);
        parcel.writeString(followStatus);
        if (ratingGiven == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(ratingGiven);
        }
        parcel.writeString(distance);
        if (customerRating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(customerRating);
        }
    }
}