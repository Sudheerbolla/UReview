package com.ureview;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ureview.models.LocationModel;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.Constants;
import com.ureview.utils.LocalStorage;
import com.ureview.wsutils.WSInterface;
import com.ureview.wsutils.WSUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApplication extends MultiDexApplication {

    private static BaseApplication mInstance;
    private static WSInterface wsInterface;
    private OkHttpClient okHttpClient;
    public static LocationModel locationModel;
    public static UserInfoModel userInfoModel;

    public static synchronized BaseApplication getInstance() {
        if (mInstance == null) mInstance = new BaseApplication();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
        FacebookSdk.sdkInitialize(getApplicationContext());
        getUserDataIfAvailable();
        getLocationIfAvailable();
    }

    private void getLocationIfAvailable() {
        String json = LocalStorage.getInstance(this).getString(LocalStorage.PREF_LOCATION_INFO, "");
        if (!TextUtils.isEmpty(json)) {
            locationModel = LocationModel.create(json);
        }
    }

    private void getUserDataIfAvailable() {
        String json = LocalStorage.getInstance(this).getString(LocalStorage.PREF_USER_INFO_DATA, "");
        if (!TextUtils.isEmpty(json)) {
            userInfoModel = UserInfoModel.create(json);
        }
    }

    public WSInterface getWsClientListener() {
        return wsInterface;
    }

    public void initRetrofit() {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
                return chain.proceed(requestBuilder.build());
            }
        };

        okHttpClient = new OkHttpClient().newBuilder().
                addInterceptor(headerInterceptor).
                addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).
                readTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).
                connectTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).
                writeTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).
                build();

        wsInterface = buildRetrofitClient(WSUtils.BASE_URL).create(WSInterface.class);
    }

    private Retrofit buildRetrofitClient(String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build();
    }


}
