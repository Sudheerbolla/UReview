package com.ureview.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryModel {

    @SerializedName("id")
    @Expose
    public String id;
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
    @SerializedName("order")
    @Expose
    public String order;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("created_date")
    @Expose
    public String createdDate;

}