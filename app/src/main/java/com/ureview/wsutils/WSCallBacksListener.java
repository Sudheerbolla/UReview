package com.ureview.wsutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.StaticUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
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

    public static void requestForResponseBody(final Context context, final int requestCode,
                                              Call<ResponseBody> call, final IParserListener<ResponseBody> iParserListener) {
        try {
            if (!StaticUtils.checkInternetConnection(context)) {
                iParserListener.noInternetConnection(requestCode);
            } else {
                Callback<ResponseBody> callback = new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean writtenToDisk = writeResponseBodyToDisk(context, response.body());
                            iParserListener.successResponse(requestCode, response.body());
                        } else
                            iParserListener.errorResponse(requestCode, response.message());
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                        iParserListener.errorResponse(requestCode, throwable.toString());
                    }
                };
                call.enqueue(callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean writeResponseBodyToDisk(Context context, ResponseBody body) {
        try {
            File futureStudioIconFile = new File(context.getExternalFilesDir(null) +
                    File.separator + "download.mp4");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.e("filesize: ", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
