package com.ureview.models;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.ureview.utils.StaticUtils;

public class LocationModel {

    public String address, latitude, longitude;

    public LocationModel(Context context, Location location) {
        try {
            address = StaticUtils.getAddress(context, location.getLatitude(), location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public LocationModel create(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, LocationModel.class);
    }
}
