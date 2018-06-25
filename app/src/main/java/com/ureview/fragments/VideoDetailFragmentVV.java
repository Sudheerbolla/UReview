package com.ureview.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.VideosAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.listeners.IParserListener;
import com.ureview.listeners.IVideosClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;

public class VideoDetailFragmentVV extends BaseFragment implements IClickListener, View.OnClickListener, IParserListener<JsonElement>, IVideosClickListener {

    private View rootView;
    private ImageView imgCatBg, imgProfile, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5, btnPlay, imgback, imgFullScreen;
    private CustomTextView txtVideoTitle, txtCategory, txtViewCount, txtDistance, txtRatingno, txtLocation, txtTags, txtFollowStatus, txtUserName, txtUserLoc, txtNoData, timeCurrent, playerEndTime;
    private LinearLayout llRate, llShare, llDirection, llReport;
    private CustomRecyclerView rvRelatedVideos;
    private VideosAdapter videosAdapter;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private VideoModel feedVideo;
    private NestedScrollView nestedScrollView;
    private MainActivity mainActivity;
    private VideoView svPlayer;
    private SeekBar seekBar;
    private RelativeLayout linMediaController;
    private FrameLayout playerFrameLayout;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private String userId;
    private Handler handler;

    public static VideoDetailFragmentVV newInstance(ArrayList<VideoModel> feedVideoList, int position) {
        VideoDetailFragmentVV videoDetailFragmentVV = new VideoDetailFragmentVV();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("news_feed", feedVideoList);
        bundle.putInt("position", position);
        videoDetailFragmentVV.setArguments(bundle);
        return videoDetailFragmentVV;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        getBundleData();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_video_detail, container, false);
        initComponents();
        return rootView;
    }

    private void initVideoPlayer() {

        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MediaController mediaController = new MediaController(mainActivity);
        mediaController.setAnchorView(svPlayer);
        mediaController.setMediaPlayer(svPlayer);
        Uri uri = Uri.parse(feedVideo.video);
        svPlayer.setMediaController(mediaController);
        mediaController.hide();

        svPlayer.setVideoURI(uri);

        svPlayer.requestFocus();

        initSeekBar();
        svPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                svPlayer.start();
            }
        });

        btnPlay.setSelected(true);
        setProgress();
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        seekBar.setProgress(0);
        seekBar.setMax(svPlayer.getDuration() / 1000);
        handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (svPlayer != null && btnPlay.isSelected()) {
                    seekBar.setMax(svPlayer.getDuration() / 1000);
                    int mCurrentPosition = svPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    timeCurrent.setText(stringForTime(svPlayer.getCurrentPosition()));
                    playerEndTime.setText(stringForTime(svPlayer.getDuration()));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        seekBar.requestFocus();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                svPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setMax(0);
        seekBar.setMax(svPlayer.getDuration() / 1000);
    }

    private void toggleMediaControls() {
        if (linMediaController.getVisibility() == View.VISIBLE) {
            linMediaController.setVisibility(View.GONE);
        } else {
            linMediaController.setVisibility(View.VISIBLE);
            setProgress();
        }
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("news_feed")) {
                feedVideoList = bundle.getParcelableArrayList("news_feed");
                if (bundle.containsKey("position") && feedVideoList.size() > bundle.getInt("position")) {
                    feedVideo = feedVideoList.get(bundle.getInt("position"));
                    feedVideoList.remove(feedVideo);
                }
            }
        }
    }

    private void initComponents() {
//        mainActivity.hideTopbar();
        txtVideoTitle = rootView.findViewById(R.id.txtVideoTitle);
        imgCatBg = rootView.findViewById(R.id.imgCatBg);
        imgback = rootView.findViewById(R.id.imgback);
        imgFullScreen = rootView.findViewById(R.id.imgFullScreen);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        txtViewCount = rootView.findViewById(R.id.txtViewCount);
        txtDistance = rootView.findViewById(R.id.txtDistance);
        imgStar1 = rootView.findViewById(R.id.imgStar1);
        imgStar2 = rootView.findViewById(R.id.imgStar2);
        imgStar3 = rootView.findViewById(R.id.imgStar3);
        imgStar4 = rootView.findViewById(R.id.imgStar4);
        imgStar5 = rootView.findViewById(R.id.imgStar5);
        txtRatingno = rootView.findViewById(R.id.txtRatingno);
        llRate = rootView.findViewById(R.id.llRate);
        llShare = rootView.findViewById(R.id.llShare);
        llDirection = rootView.findViewById(R.id.llDirection);
        llReport = rootView.findViewById(R.id.llReport);
        txtLocation = rootView.findViewById(R.id.txtLocation);
        txtTags = rootView.findViewById(R.id.txtTags);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        txtFollowStatus = rootView.findViewById(R.id.txtFollowStatus);
        txtUserName = rootView.findViewById(R.id.txtUserName);
        txtUserLoc = rootView.findViewById(R.id.txtUserLoc);
        rvRelatedVideos = rootView.findViewById(R.id.rvRelatedVideos);
        txtNoData = rootView.findViewById(R.id.txtNoData);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        nestedScrollView.smoothScrollTo(0, 0);
        svPlayer = rootView.findViewById(R.id.sv_player);
        btnPlay = rootView.findViewById(R.id.btnPlay);
        timeCurrent = rootView.findViewById(R.id.time_current);
        seekBar = rootView.findViewById(R.id.mediacontroller_progress);
        playerEndTime = rootView.findViewById(R.id.player_end_time);
        linMediaController = rootView.findViewById(R.id.lin_media_controller);
        playerFrameLayout = rootView.findViewById(R.id.player_frame_layout);

        svPlayer.setOnClickListener(view -> toggleMediaControls());
        btnPlay.setOnClickListener(view -> {
            if (btnPlay.isSelected()) {
                if (svPlayer.canPause()) svPlayer.pause();
            } else {
                svPlayer.start();
//                svPlayer.resume();
                setProgress();
            }
            btnPlay.setSelected(!btnPlay.isSelected());
        });

        llRate.setOnClickListener(this);
        llDirection.setOnClickListener(this);
        llReport.setOnClickListener(this);
        llShare.setOnClickListener(this);
        imgback.setOnClickListener(this);
        imgFullScreen.setOnClickListener(this);
        txtFollowStatus.setOnClickListener(this);

        rvRelatedVideos.setNestedScrollingEnabled(false);
        videosAdapter = new VideosAdapter(mainActivity, this, false);
        rvRelatedVideos.setAdapter(videosAdapter);

        if (feedVideo != null)
            setVideoDetails();
    }

    private void setProfileRating(float v) {
        switch ((int) v) {
            case 0:
                setSelectedStar(false, false, false, false, false);
                break;
            case 1:
                setSelectedStar(true, false, false, false, false);
                break;
            case 2:
                setSelectedStar(true, true, false, false, false);
                break;
            case 3:
                setSelectedStar(true, true, true, false, false);
                break;
            case 4:
                setSelectedStar(true, true, true, true, false);
                break;
            case 5:
                setSelectedStar(true, true, true, true, true);
                break;
        }
    }

    private void setSelectedStar(boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        imgStar1.setSelected(b);
        imgStar2.setSelected(b1);
        imgStar3.setSelected(b2);
        imgStar4.setSelected(b3);
        imgStar5.setSelected(b4);
    }

    private void setVideoDetails() {
        txtVideoTitle.setText(feedVideo.videoTitle);
        txtViewCount.setText(feedVideo.videoWatchedCount);
        txtDistance.setText(feedVideo.distance);
        if (!TextUtils.isEmpty(feedVideo.videoRating))
            setProfileRating(Float.parseFloat(feedVideo.videoRating));
        txtRatingno.setText("(".concat(String.valueOf(feedVideo.ratingGiven == null ? 0 : feedVideo.ratingGiven).concat(")")));
        txtCategory.setText(feedVideo.categoryName);
        Glide.with(this).load(feedVideo.categoryBgImage).into(imgCatBg);
        txtLocation.setText(feedVideo.videoLocation);
        txtTags.setText(feedVideo.videoTags);
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_user_placeholder).error(R.drawable.ic_user_placeholder);
        Glide.with(this).load(feedVideo.userImage).apply(requestOptions).into(imgProfile);
        txtUserName.setText(feedVideo.firstName.concat(" ").concat(feedVideo.lastName));
        txtUserLoc.setText(feedVideo.city);

        txtFollowStatus.setVisibility(userId.equalsIgnoreCase(feedVideo.userId) ? View.GONE : View.VISIBLE);

        if (TextUtils.isEmpty(feedVideo.followStatus)) {
            txtFollowStatus.setText("Follow");
            txtFollowStatus.setSelected(false);
        } else {
            txtFollowStatus.setText("Unfollow");
            txtFollowStatus.setSelected(true);
        }

        if (feedVideoList.size() > 0) {
            videosAdapter.addVideos(feedVideoList);
            rvRelatedVideos.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            rvRelatedVideos.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        initVideoPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        svPlayer.stopPlayback();
        mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onClick(View view, ArrayList<VideoModel> videoModels, VideoModel videoModel, int position) {
        VideoModel toBeAdded = feedVideo;
        feedVideo = feedVideoList.get(position);
        feedVideoList.remove(position);
        feedVideoList.add(toBeAdded);
        videosAdapter.addVideos(feedVideoList);
        if (feedVideo != null) {
            setVideoDetails();
//            initMp4Player();
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtFollowStatus:
                String followStatus = txtFollowStatus.getText().toString().trim();
                if (TextUtils.isEmpty(followStatus) || followStatus.equalsIgnoreCase("Unfollow")) {
                    askConfirmationAndProceed();
                } else {
                    requestForFollowUser();
                }
                break;
            case R.id.llRate:
                showRatingDialog();
                break;
            case R.id.llShare:
                showShareDialog();
                break;
            case R.id.llDirection:
                showDirectionMaps();
                break;
            case R.id.llReport:
                mainActivity.replaceFragment(ReportVideoFragment.newInstance(feedVideo.id), true, R.id.mainContainer);
                break;
            case R.id.imgback:
                mainActivity.popBackStack();
                break;
            case R.id.imgFullScreen:
                break;
        }
    }

    private void askConfirmationAndProceed() {
        DialogUtils.showUnFollowConfirmationPopup(mainActivity, feedVideo.firstName.concat(" ").concat(feedVideo.lastName),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestForUnFollowUser();
                    }
                });
    }

    private void showDirectionMaps() {
        String url = "http://maps.google.com/maps?saddr=" + feedVideo.userLatitude + "," + feedVideo.userLongitude
                + "&daddr=" + feedVideo.videoLatitude + "," + feedVideo.videoLongitude;
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(mapIntent);
    }

    private void showShareDialog() {
        DialogUtils.showDropDownListStrings(mainActivity, new String[]{"Share on your profile",
                "Share with your friends",
                "Share Link",
                "Cancel"}, rootView.findViewById(R.id.llShare), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch ((String) view.getTag()) {
                    case "Share on your profile":
                        requestForShareVideo();
                        break;
                    case "Share with your friends":
                        shareVideoWithFriends();
                        break;
                    case "Share Link":
                        shareLinkWithFriends();
                        break;
                    case "Cancel":
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void shareVideoWithFriends() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("video/3gp");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(feedVideo.video));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
        startActivity(Intent.createChooser(sendIntent, "Email:"));
    }

    private void shareLinkWithFriends() {
        try {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedVideo.video);
            startActivity(Intent.createChooser(sharingIntent, "share video"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRatingDialog() {
        final TextView textView = new TextView(mainActivity);
        DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.showRatingDialog(mainActivity, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForGiveRating(textView.getText().toString().trim());
            }
        }, textView);
    }

    private void requestForShareVideo() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
        hashMap.put("video_id", feedVideo.id);
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().shareVideo(hashMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_SHARE_VIDEO, call, this);
    }

    private void requestForGiveRating(String rating) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("user_id", LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
            jsonObjectReq.put("video_id", feedVideo.id);
            jsonObjectReq.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().videoRatingByUser(StaticUtils.getRequestBody(jsonObjectReq));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_RATING_VIDEO, call, this);

    }

    private void requestForFollowUser() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject());
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private void requestForUnFollowUser() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().unFollowUser(getRequestBodyObject());
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_UN_FOLLOW_USER, call, this);
    }

    private RequestBody getRequestBodyObject() {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("id", Integer.parseInt(userId));
            jsonObjectReq.put("follow_id", Integer.parseInt(feedVideo.userId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StaticUtils.getRequestBody(jsonObjectReq);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_RATING_VIDEO:
                parseVideoRatingResponse();
                break;
            case WSUtils.REQ_FOR_SHARE_VIDEO:
                parseShareVideoResponse(response);
                break;
            case WSUtils.REQ_FOR_FOLLOW_USER:
                parseFollowUserResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_UN_FOLLOW_USER:
                parseUnFollowUser((JsonObject) response);
                break;
        }
    }

    private void parseShareVideoResponse(JsonElement res) {
        try {
            JSONObject response = new JSONObject(res.toString());
            if (response.has("message")) {
                StaticUtils.showToast(mainActivity, response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    txtFollowStatus.setText("Unfollow");
                    feedVideo.followStatus = "follow";
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseUnFollowUser(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    txtFollowStatus.setText("Follow");
                    feedVideo.followStatus = "follow";
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseVideoRatingResponse() {
        DialogUtils.showSimpleDialog(mainActivity, "Rating submitted successfully", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, true);
    }

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

}
