package com.ureview.models;

import com.google.gson.JsonObject;

public class UserInfoModel {
    public String id, first_name, last_name, user_name, email, gender, date_of_birth, age, country_code, mobile, otp, user_image, user_description, auth_id, auth_type, user_rating, status, videos_range, city, address, platform, device_token, created_date, userid;

    public UserInfoModel(JsonObject jsonObject) {
        try {
            if (jsonObject.has("id")) id = jsonObject.get("id").getAsString();
            first_name = jsonObject.get("first_name").getAsString();
            last_name = jsonObject.get("last_name").getAsString();
            user_name = jsonObject.get("user_name").getAsString();
            email = jsonObject.get("email").getAsString();
            gender = jsonObject.get("gender").getAsString();
            date_of_birth = jsonObject.get("date_of_birth").getAsString();
            age = jsonObject.get("age").getAsString();
            country_code = jsonObject.get("country_code").getAsString();
            mobile = jsonObject.get("mobile").getAsString();
            otp = jsonObject.get("otp").getAsString();
            user_image = jsonObject.get("user_image").getAsString();
            user_description = jsonObject.get("user_description").getAsString();
            auth_id = jsonObject.get("auth_id").getAsString();
            auth_type = jsonObject.get("auth_type").getAsString();
            user_rating = jsonObject.get("user_rating").getAsString();
            status = jsonObject.get("status").getAsString();
            videos_range = jsonObject.get("videos_range").getAsString();
            city = jsonObject.get("city").getAsString();
            address = jsonObject.get("address").getAsString();
            platform = jsonObject.get("platform").getAsString();
            device_token = jsonObject.get("device_token").getAsString();
            created_date = jsonObject.get("created_date").getAsString();
            userid = jsonObject.get("userid").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/*"id": "7",
    "first_name": "Rajani",
    "last_name": "Karukola",
    "user_name": "",
    "email": "karukolarajani@gmail.com",
    "gender": "F",
    "date_of_birth": "24/12/1994",
    "age": "23",
    "country_code": "+91",
    "mobile": "7661001918",
    "otp": "1234",
    "user_image": "",
    "user_description": "",
    "auth_id": "1187884791349699",
    "auth_type": "Facebook",
    "user_rating": "",
    "status": "A",
    "videos_range": "30",
    "city": "Hyderabad",
    "address": "",
    "platform": "ios",
    "device_token": "4F1F4666ADFD3E3BC33C4E70F1EFAFDF60236029105DDDA300134983403B841D",
    "created_date": "2018-06-04 07:31:48",
    "userid": "7"*/