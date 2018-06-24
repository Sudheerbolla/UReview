package com.ureview.models;

import com.google.gson.JsonObject;

public class NotificationsModel {

    public String id, user_id, opponent_id, message, message_date, mark_as_read, status, user_name, user_image;

    public NotificationsModel(JsonObject notificationObject) {
        id = notificationObject.get("id").getAsString();
        user_id = notificationObject.get("user_id").getAsString();
        opponent_id = notificationObject.get("opponent_id").getAsString();
        message = notificationObject.get("message").getAsString();
        message_date = notificationObject.get("message_date").getAsString();
        mark_as_read = notificationObject.get("mark_as_read").getAsString();
        status = notificationObject.get("status").getAsString();
        user_name = notificationObject.get("user_name").getAsString();
        user_image = notificationObject.get("user_image").getAsString();
    }

}
/*{
      "id": "65",
      "user_id": "2",
      "opponent_id": "1",
      "message": "Congratulations!.. Madhu following you",
      "message_date": "1 day ago",
      "mark_as_read": "0",
      "status": "A",
      "user_name": "Madhu Sudhan",
      "user_image": "http:\/\/18.216.101.112\/uploads\/noimage.jpg"
    }*/