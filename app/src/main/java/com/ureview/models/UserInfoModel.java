package com.ureview.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

public class UserInfoModel implements Parcelable {
    public String id, first_name, last_name, user_name, email, gender, date_of_birth, age, country_code, mobile, otp,
            user_image, user_description, auth_id, auth_type, user_rating, status, videos_range, city, address, platform,
            device_token, created_date, userid, follow_status, follow_you_count, you_follow_count;

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
            if (jsonObject.has("otp")) otp = jsonObject.get("otp").getAsString();
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

            if (jsonObject.has("follow_status"))
                follow_status = jsonObject.get("follow_status").getAsString();
            if (jsonObject.has("follow_you_count"))
                otp = jsonObject.get("follow_you_count").getAsString();
            if (jsonObject.has("you_follow_count"))
                otp = jsonObject.get("you_follow_count").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected UserInfoModel(Parcel in) {
        id = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        user_name = in.readString();
        email = in.readString();
        gender = in.readString();
        date_of_birth = in.readString();
        age = in.readString();
        country_code = in.readString();
        mobile = in.readString();
        otp = in.readString();
        user_image = in.readString();
        user_description = in.readString();
        auth_id = in.readString();
        auth_type = in.readString();
        user_rating = in.readString();
        status = in.readString();
        videos_range = in.readString();
        city = in.readString();
        address = in.readString();
        platform = in.readString();
        device_token = in.readString();
        created_date = in.readString();
        userid = in.readString();
        follow_status = in.readString();
        follow_you_count = in.readString();
        you_follow_count = in.readString();
    }

    public static final Creator<UserInfoModel> CREATOR = new Creator<UserInfoModel>() {
        @Override
        public UserInfoModel createFromParcel(Parcel in) {
            return new UserInfoModel(in);
        }

        @Override
        public UserInfoModel[] newArray(int size) {
            return new UserInfoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(user_name);
        parcel.writeString(email);
        parcel.writeString(gender);
        parcel.writeString(date_of_birth);
        parcel.writeString(age);
        parcel.writeString(country_code);
        parcel.writeString(mobile);
        parcel.writeString(otp);
        parcel.writeString(user_image);
        parcel.writeString(user_description);
        parcel.writeString(auth_id);
        parcel.writeString(auth_type);
        parcel.writeString(user_rating);
        parcel.writeString(status);
        parcel.writeString(videos_range);
        parcel.writeString(city);
        parcel.writeString(address);
        parcel.writeString(platform);
        parcel.writeString(device_token);
        parcel.writeString(created_date);
        parcel.writeString(userid);
        parcel.writeString(follow_status);
        parcel.writeString(follow_you_count);
        parcel.writeString(you_follow_count);
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