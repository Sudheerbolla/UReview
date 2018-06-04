package com.ureview;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.ureview.utils.Constants;
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

    public static synchronized BaseApplication getInstance() {
        if (mInstance == null) mInstance = new BaseApplication();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
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
        return new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
    }


}
