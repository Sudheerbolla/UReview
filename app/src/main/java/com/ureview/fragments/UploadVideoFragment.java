package com.ureview.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.CategoryModel;
import com.ureview.utils.Constants;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;

public class UploadVideoFragment extends BaseFragment implements IParserListener {
    private View rootView;
    private CustomTextView txtCompleteVideo, txtLocation, tvLeft, tvRight, txtCategory;
    private ImageView imgPlay, imgPlayPause, imgHashTag;
    private CustomEditText edtVideoTitle, edtTags;
    private MainActivity mainActivity;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private SeekBar seekBar;
    private ProgressDialog progressDialog;
    private String filePath;
    private int stopPosition, duration;
    private String[] catArray;
    private String categoryId = "-1";
    private ArrayList<CategoryModel> categoryModelArrayList;
    private String userId;
    private double longitude, latitude, userLat, userLong;
    private String videoThumb;

    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {
            if (seekBar != null) {
                seekBar.setProgress(videoView.getCurrentPosition() / 1000);
                tvLeft.setText(StaticUtils.getTime(videoView.getCurrentPosition() / 1000));
            }
            if (videoView.isPlaying()) {
                seekBar.postDelayed(onEverySecond, 1000);
            }
            if (videoView.getDuration() - videoView.getCurrentPosition() <= 1000) {
                videoView.pause();
                stopPosition = 0;
                imgPlay.setVisibility(View.VISIBLE);
                imgPlayPause.setSelected(false);
                seekBar.setProgress(0);
                tvLeft.setText("00:00:00");
            }
            Log.e("vid time", (videoView.getCurrentPosition()) + ", " + (videoView.getDuration()));
        }
    };

    public static UploadVideoFragment newInstance(Uri selectedVideoUri) {
        UploadVideoFragment uploadVideoFragment = new UploadVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedVideoUri", selectedVideoUri);
        uploadVideoFragment.setArguments(bundle);
        return uploadVideoFragment;
    }

    public static UploadVideoFragment newInstance(String filePath) {
        UploadVideoFragment uploadVideoFragment = new UploadVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        uploadVideoFragment.setArguments(bundle);
        return uploadVideoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        if (MainActivity.mLastLocation != null) {
            longitude = MainActivity.mLastLocation.getLongitude();
            latitude = MainActivity.mLastLocation.getLatitude();
            userLat = latitude;
            userLong = longitude;
        } else {
            latitude = 0.0;
            longitude = 0.0;
            userLat = latitude;
            userLong = longitude;
        }
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            if (bundle.containsKey("selectedVideoUri"))
                selectedVideoUri = getArguments().getParcelable("selectedVideoUri");
            if (bundle.containsKey("filePath")) filePath = getArguments().getString("filePath");
        }
        categoryModelArrayList = new ArrayList<>();
        categoryModelArrayList.addAll(MainActivity.categoryListStatic);
        catArray = new String[categoryModelArrayList.size()];
        for (int i = 0; i < categoryModelArrayList.size(); i++) {
            if (!categoryModelArrayList.get(i).categoryName.equalsIgnoreCase("New Feed"))
                catArray[i] = categoryModelArrayList.get(i).categoryName;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("New Review", "", "", false, false, false, false, false);
        videoView.seekTo(stopPosition);
        if (stopPosition > 0)
            videoView.start();
        imgPlayPause.setSelected(true);
        seekBar.postDelayed(onEverySecond, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
        videoView.pause();
    }

    private void setSelectedCategoryid(String categoryName) {
        for (CategoryModel categoryModel : categoryModelArrayList) {
            if (categoryModel.categoryName.equalsIgnoreCase(categoryName)) {
                categoryId = categoryModel.id;
                break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload_video, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        seekBar = rootView.findViewById(R.id.mediacontroller_progress);
        videoView = rootView.findViewById(R.id.VideoView);
        txtCompleteVideo = rootView.findViewById(R.id.txtCompleteVideo);
        txtLocation = rootView.findViewById(R.id.txtLocation);
        tvLeft = rootView.findViewById(R.id.time_current);
        tvLeft.setText("00:00:00");
        tvRight = rootView.findViewById(R.id.player_end_time);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        edtVideoTitle = rootView.findViewById(R.id.edtVideoTitle);
        edtTags = rootView.findViewById(R.id.edtTags);
        imgPlay = rootView.findViewById(R.id.imgPlay);
        imgPlayPause = rootView.findViewById(R.id.btnPlay);
        imgPlayPause.setSelected(false);
        imgHashTag = rootView.findViewById(R.id.imgHashTag);
//        imgPlay.setVisibility(View.GONE);
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPlay.setVisibility(View.GONE);
                videoView.seekTo(0);
                videoView.start();
                imgPlayPause.setSelected(true);
                seekBar.postDelayed(onEverySecond, 1000);
            }
        });

        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    stopPosition = videoView.getCurrentPosition();
                    imgPlayPause.setSelected(false);
                    videoView.pause();
                } else {
                    if (stopPosition > 0) {
                        imgPlayPause.setSelected(true);
                        videoView.seekTo(stopPosition);
                        videoView.start();
                        seekBar.postDelayed(onEverySecond, 1000);
                    }
                }
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPosition = videoView.getCurrentPosition();
                videoView.pause();
                imgPlayPause.setSelected(false);
                openSearch();
            }
        });

        txtCompleteVideo.setOnClickListener(view -> {
            String message = checkValidations();
            if (TextUtils.isEmpty(message)) {
                ConvertVideoToBytes convertVideoToBytes = new ConvertVideoToBytes();
                convertVideoToBytes.execute();
            } else {
                StaticUtils.showToast(mainActivity, message);
            }
        });
        txtCategory.setOnClickListener(view -> DialogUtils.showDropDownListStrings(mainActivity, catArray, txtCategory, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = (String) view.getTag();
                txtCategory.setText(category);
                setSelectedCategoryid(category);
            }
        }));
        imgHashTag.setOnClickListener(view -> {
            String text = edtTags.getText().toString().trim();
            if (!text.endsWith("#")) {
//                edtTags.setText(text + "#");
//                int start = Math.max(myEditText.getSelectionStart(), 0);
//                int end = Math.max(myEditText.getSelectionEnd(), 0);
//                myEditText.getText().replace(Math.min(start, end), Math.max(start, end),
//                        textToInsert, 0, textToInsert.length());
                edtTags.getText().insert(edtTags.getSelectionStart(), "#");
            }
        });
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        setVideoViewData();
    }

    private void requestForUploadVideoWS(String baseVideo) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", userId);
            jsonObjectReq.put("video_title", edtVideoTitle.getText().toString().trim());
            jsonObjectReq.put("category_id", categoryId);
            jsonObjectReq.put("video_description", "");
            jsonObjectReq.put("video_tags", edtTags.getText().toString().trim());
            jsonObjectReq.put("video_latitude", latitude + "");
            jsonObjectReq.put("video_longitude", longitude + "");
            jsonObjectReq.put("video_location", txtLocation.getText().toString().trim());
            jsonObjectReq.put("user_latitude", userLat + "");
            jsonObjectReq.put("user_longitude", userLong + "");
            jsonObjectReq.put("video_status", "A");
            jsonObjectReq.put("video_privacy", "private");

            if (!TextUtils.isEmpty(videoThumb)) {
                jsonObjectReq.put("video_poster_image", videoThumb);
            } else {
                jsonObjectReq.put("video_poster_image", "");
            }

            if (!TextUtils.isEmpty(baseVideo))
                jsonObjectReq.put("video", baseVideo);
            else
                jsonObjectReq.put("video", "");

            Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().uploadVideo(StaticUtils.getRequestBody(jsonObjectReq));
            new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_UPLOAD_VIDEO, call, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String checkValidations() {
        if (TextUtils.isEmpty(edtVideoTitle.getText().toString().trim())) {
            return "Enter Video Title";
        }
        if (TextUtils.isEmpty(txtLocation.getText().toString().trim())) {
            return "Enter Location";
        }
        if (categoryId.equalsIgnoreCase("-1")) {
            return "Select Video Category";
        }
        if (selectedVideoUri == null) return "Please upload valid video file";
        return "";
    }

    private void openSearch() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .setCountry("IN")
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                    .setBoundsBias(new LatLngBounds(new LatLng(17.445026, 78.376708), new LatLng(17.514235, 78.379242)))
                    .setFilter(typeFilter)
                    .build(mainActivity);
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setVideoViewData() {
        if (selectedVideoUri == null) {
            if (!TextUtils.isEmpty(filePath)) {
                selectedVideoUri = Uri.parse(filePath);
            }
        }
        if (selectedVideoUri != null) {
            videoView.setVideoURI(selectedVideoUri);
            videoView.setOnPreparedListener(mp -> {
                duration = mp.getDuration() / 1000;
//                tvLeft.setText("00:00:00");

                tvRight.setText(StaticUtils.getTime(mp.getDuration() / 1000));
                mp.setLooping(true);
                seekBar.setMax(duration);
//                seekBar.postDelayed(onEverySecond, 500);
//                imgPlayPause.setSelected(true);
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        videoView.seekTo(progress * 1000);
                    }
                    if (progress >= videoView.getDuration())
                        videoView.pause();
                }
            });
        }
        setImageAttachment(selectedVideoUri);
    }

    private void setImageAttachment(Uri cameraFile) {
        videoThumb = "";
        try {
            long videoId = Long.parseLong(cameraFile.getLastPathSegment().split(":")[1]);
            Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(mainActivity.getContentResolver(),
                    videoId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (thumbnail != null) {
                videoThumb = StaticUtils.imageBytes(thumbnail);
            } else {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(cameraFile.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                videoThumb = StaticUtils.imageBytes(bMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(cameraFile.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                videoThumb = StaticUtils.imageBytes(bMap);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void setTextToAddress(String address) {
        txtLocation.setText(address);
    }

    @Override
    public void successResponse(int requestCode, Object response) {
        if (response != null) {
            Log.e("response: ", response.toString());
            switch (requestCode) {
                case WSUtils.REQ_FOR_UPLOAD_VIDEO:
                    parseUploadVideoResponse((JsonObject) response);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void errorResponse(int requestCode, String error) {
        Log.e("error: ", error);
        progressDialog.hide();
    }

    @Override
    public void noInternetConnection(int requestCode) {
        progressDialog.hide();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Place place = PlaceAutocomplete.getPlace(mainActivity, data);
                    Log.e("Place: ", place.getName().toString());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    setTextToAddress(place.getAddress().toString());
                    break;
                case PlaceAutocomplete.RESULT_ERROR:
                    Status status = PlaceAutocomplete.getStatus(mainActivity, data);
                    Log.e("error", status.getStatusMessage());
                    break;
                case Activity.RESULT_CANCELED:
                    // The user canceled the operation.
                    break;
            }
        }
    }

    private void parseUploadVideoResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
//                    if (response.has("message")) {
//                        StaticUtils.showToast(mainActivity, response.get("message").getAsString());
//                    }
                    mainActivity.clearBackStackCompletely();
                    try {
                        File cutFile = new File(filePath);
                        if (cutFile.exists()) cutFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mainActivity.replaceFragment(UploadVideoCompletedFragment.newInstance(), true, R.id.mainContainer);
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            progressDialog.hide();
        }
    }

    private class ConvertVideoToBytes extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                String path = "file://" + selectedVideoUri.getPath();
                inputStream = mainActivity.getContentResolver().openInputStream(Uri.parse(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                try {
                    inputStream = mainActivity.getContentResolver().openInputStream(selectedVideoUri);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    progressDialog.hide();
                }
            }
            if (inputStream != null) {
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int len;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("converted!");

                String videoData = "                                                                                                 ";
                videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
                Log.d("VideoData**>  ", videoData);
                String sinSaltoFinal2 = videoData.trim();
                String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
                Log.d("VideoData**>  ", sinsinSalto2);
                return sinsinSalto2;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            if (TextUtils.isEmpty(result)) {
                StaticUtils.showToast(mainActivity, "Something went wrong");
            } else {
                progressDialog.setMessage("Loading");
                progressDialog.show();
                requestForUploadVideoWS(result);
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Converting and Uploading video.Please wait...");
            progressDialog.show();
        }

    }

    private void performBase64Operation() {
        InputStream inputStream = null;
        try {
            String path = "file://" + selectedVideoUri.getPath();
            inputStream = mainActivity.getContentResolver().openInputStream(Uri.parse(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                inputStream = mainActivity.getContentResolver().openInputStream(selectedVideoUri);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                progressDialog.hide();
            }
        }
        if (inputStream != null) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int len;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("converted!");

            String videoData = "                                                                                                 ";
            videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
            Log.d("VideoData**>  ", videoData);
            String sinSaltoFinal2 = videoData.trim();
            String sinsinSalto2 = sinSaltoFinal2.replaceAll("\n", "");
            Log.d("VideoData**>  ", sinsinSalto2);

            String baseVideo = sinsinSalto2;
            requestForUploadVideoWS(baseVideo);
        } else {
            StaticUtils.showToast(mainActivity, "Something went wrong");
        }
    }

}