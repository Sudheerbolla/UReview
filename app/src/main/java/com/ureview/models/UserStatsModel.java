package com.ureview.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

public class UserStatsModel implements Parcelable {

    public String month, count;

    public UserStatsModel(JsonObject notificationObject) {
        month = notificationObject.get("month").getAsString();
        count = notificationObject.get("count").getAsString();
    }

    protected UserStatsModel(Parcel in) {
        month = in.readString();
        count = in.readString();
    }

    public static final Creator<UserStatsModel> CREATOR = new Creator<UserStatsModel>() {
        @Override
        public UserStatsModel createFromParcel(Parcel in) {
            return new UserStatsModel(in);
        }

        @Override
        public UserStatsModel[] newArray(int size) {
            return new UserStatsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(month);
        parcel.writeString(count);
    }
}
