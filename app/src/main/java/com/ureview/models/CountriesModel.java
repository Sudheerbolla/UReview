package com.ureview.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CountriesModel implements Parcelable{

    public String completeText, countryCode, countryName, countryIsoCode;

    public CountriesModel(String total) {
        this.completeText = total;
        String[] parts = completeText.split(",");
        countryCode = parts[0];
        countryIsoCode = parts[1];
        countryName = parts[2];
    }

    protected CountriesModel(Parcel in) {
        completeText = in.readString();
        countryCode = in.readString();
        countryName = in.readString();
        countryIsoCode = in.readString();
    }

    public static final Creator<CountriesModel> CREATOR = new Creator<CountriesModel>() {
        @Override
        public CountriesModel createFromParcel(Parcel in) {
            return new CountriesModel(in);
        }

        @Override
        public CountriesModel[] newArray(int size) {
            return new CountriesModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(completeText);
        parcel.writeString(countryCode);
        parcel.writeString(countryName);
        parcel.writeString(countryIsoCode);
    }
}
