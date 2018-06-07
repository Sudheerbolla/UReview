package com.ureview.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeopleModel {

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
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("mobile")
    @Expose
    public String mobile;
    @SerializedName("user_description")
    @Expose
    public String userDescription;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("age")
    @Expose
    public String age;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("date_of_birth")
    @Expose
    public String dateOfBirth;
    @SerializedName("uploaded_videos_count")
    @Expose
    public Integer uploadedVideosCount;
    @SerializedName("follow_status")
    @Expose
    public String followStatus;

}