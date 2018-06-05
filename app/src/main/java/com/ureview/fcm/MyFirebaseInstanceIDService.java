package com.ureview.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ureview.utils.LocalStorage;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        LocalStorage.getInstance(this).putString(LocalStorage.PREF_DEVICE_TOKEN, token);
        //You can implement this method to store the token on your server
        //Not required for current project
    }

}