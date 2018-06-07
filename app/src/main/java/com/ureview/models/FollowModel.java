package com.ureview.models;

import com.google.gson.JsonObject;

public class FollowModel {

    public String user_id, first_name, last_name, email, gender, date_of_birth, age, mobile, user_description, user_rating, user_image, address, follow_status, status, uploaded_videos_count;

    public FollowModel(JsonObject notificationObject) {
        first_name = notificationObject.get("first_name").getAsString();
        user_id = notificationObject.get("user_id").getAsString();
        last_name = notificationObject.get("last_name").getAsString();
        email = notificationObject.get("email").getAsString();
        gender = notificationObject.get("gender").getAsString();
        date_of_birth = notificationObject.get("date_of_birth").getAsString();
        age = notificationObject.get("age").getAsString();
        status = notificationObject.get("status").getAsString();
        mobile = notificationObject.get("mobile").getAsString();
        user_image = notificationObject.get("user_image").getAsString();
        user_description = notificationObject.get("user_description").getAsString();
        user_rating = notificationObject.get("user_rating").getAsString();
        address = notificationObject.get("address").getAsString();
        follow_status = notificationObject.get("follow_status").getAsString();
        uploaded_videos_count = notificationObject.get("uploaded_videos_count").getAsString();
    }

}