package com.ureview.wsutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.StaticUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WSCallBacksListener {

    public static void requestForJsonObject(final Context context, final int requestCode, Call<JsonElement> call, final IParserListener<JsonElement> iParserListener) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode);
            } else {
                Callback<JsonElement> callback = new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.body() != null && /*response.body() instanceof JsonObject && */(response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED))
                            iParserListener.successResponse(requestCode, response.body());
                        else
                            iParserListener.errorResponse(requestCode, response.message());
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable throwable) {
                        iParserListener.errorResponse(requestCode, throwable.toString());
                    }
                };
                call.enqueue(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestForJsonArray(final Context context, final int requestCode, Call<JsonElement> call, final IParserListener<JsonElement> iParserListener) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode);
            } else {
                Callback<JsonElement> callback = new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.body() != null && (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) {
                            if (response.body() instanceof JsonObject)
                                iParserListener.successResponse(requestCode, response.body().getAsJsonObject());
                            else if (response.body() instanceof JsonArray)
                                iParserListener.successResponse(requestCode, response.body().getAsJsonArray());
                            else iParserListener.successResponse(requestCode, response.body());
                        } else if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            Log.e("response : ", response.message());
                            iParserListener.successResponse(requestCode, null);
                        } else {
                            String errorText = null;
                            try {
                                errorText = (response.errorBody() != null && response.errorBody().string() != null) ? response.errorBody().string() : response.message();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            iParserListener.errorResponse(requestCode, errorText);
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable throwable) {
                        iParserListener.errorResponse(requestCode, throwable.toString());
                    }
                };
                call.enqueue(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestForEmptyBody(final Context context, final int requestCode, Call<Void> call, final IParserListener<JsonElement> iParserListener) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode);
            } else {
                Callback<Void> callback = new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if ((response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) {
                            iParserListener.successResponse(requestCode, null);
                        } else if (response.errorBody() != null) {
                            try {
                                iParserListener.errorResponse(requestCode, response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (response.body() != null && response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                            iParserListener.successResponse(requestCode, null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        iParserListener.errorResponse(requestCode, t.toString());
                    }
                };
                call.enqueue(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
