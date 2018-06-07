package com.ureview.models;

import com.google.gson.JsonObject;

public class VideoViewsModel {

    public String user_id, name, video_views, rank, user_image;

    public VideoViewsModel(JsonObject notificationObject) {
        name = notificationObject.get("name").getAsString();
        user_id = notificationObject.get("user_id").getAsString();
        video_views = notificationObject.get("video_views").getAsString();
        rank = notificationObject.get("rank").getAsString();
        user_image = notificationObject.get("user_image").getAsString();
    }

}

/*{
    "status": "success",
    "message": "User video view statistics!..",
    "video_views": [
        {
            "user_id": "1",
            "video_views": "43",
            "user_image": "",
            "name": "Madhu Sudhan",
            "rank": 1
        },
        {
            "user_id": "2",
            "video_views": "35",
            "user_image": "",
            "name": "Sanjeev Sumanth",
            "rank": 2
        },
        {
            "user_id": "5",
            "video_views": "19",
            "user_image": "",
            "name": "Manish Pathak",
            "rank": 3
        }
    ]
}*/