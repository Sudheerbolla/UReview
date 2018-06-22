package com.ureview.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;

public class VideoDetailFragment extends BaseFragment implements VideoRendererEventListener,
        AdaptiveMediaSourceEventListener, IClickListener, View.OnClickListener, IParserListener<JsonElement>, IVideosClickListener {

    private ImageView imgCatBg, imgProfile, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5, btnPlay, imgback, imgFullScreen;
    private CustomTextView txtVideoTitle, txtCategory, txtViewCount, txtDistance, txtRatingno, txtLocation,
            txtTags, txtFollowStatus, txtUserName, txtUserLoc, txtNoData, timeCurrent, playerEndTime;
    private LinearLayout llRate, llShare, llDirection, llReport;
    private CustomRecyclerView rvRelatedVideos;
    private VideosAdapter videosAdapter;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private VideoModel feedVideo;
    private NestedScrollView nestedScrollView;
    private View rootView;
    private MainActivity mainActivity;

    private SurfaceView svPlayer;
    private SeekBar mediacontrollerProgress;
    private RelativeLayout linMediaController;
    private FrameLayout playerFrameLayout;

    private SimpleExoPlayer exoPlayer;
    private boolean bAutoplay = true, bIsPlaying = false, bControlsActive = true;

    private Handler handler;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private DataSource.Factory dataSourceFactory;
    private String userId;

    public static VideoDetailFragment newInstance(ArrayList<VideoModel> feedVideoList, int position) {
        VideoDetailFragment videoDetailFragment = new VideoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("news_feed", feedVideoList);
        bundle.putInt("position", position);
        videoDetailFragment.setArguments(bundle);
        return videoDetailFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_video_detail, container, false);
        getBundleData();
        initComponents();
//        initVideoPlayer();
        initVideoPlayer2();
        return rootView;
    }

    private void initVideoPlayer2() {
        svPlayer = rootView.findViewById(R.id.sv_player);
        btnPlay = rootView.findViewById(R.id.btnPlay);
        timeCurrent = rootView.findViewById(R.id.time_current);
        mediacontrollerProgress = rootView.findViewById(R.id.mediacontroller_progress);
        playerEndTime = rootView.findViewById(R.id.player_end_time);
        linMediaController = rootView.findViewById(R.id.lin_media_controller);
        playerFrameLayout = rootView.findViewById(R.id.player_frame_layout);

        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler = new Handler();
        initDataSource();
        initMp4Player();

        if (bAutoplay) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(true);
                btnPlay.setSelected(true);
                bIsPlaying = true;
                setProgress();
            }
        }
    }

    private void initDataSource() {
        dataSourceFactory = new DefaultDataSourceFactory(mainActivity,
                Util.getUserAgent(mainActivity, mainActivity.getPackageName()),
                new DefaultBandwidthMeter());
    }

    private void initMediaControls() {
        initSurfaceView();
        initPlayButton();
        initSeekBar();
    }

    private void initSurfaceView() {
        svPlayer.setOnClickListener(view -> toggleMediaControls());
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
        mediacontrollerProgress.setProgress(0);
        mediacontrollerProgress.setMax(0);
        mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);

        handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (exoPlayer != null && bIsPlaying) {
                    btnPlay.setSelected(bIsPlaying);
                    mediacontrollerProgress.setMax(0);
                    mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    mediacontrollerProgress.setProgress(mCurrentPosition);
                    timeCurrent.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    playerEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        mediacontrollerProgress.requestFocus();

        mediacontrollerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediacontrollerProgress.setMax(0);
        mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);
    }

    private void toggleMediaControls() {

        if (bControlsActive) {
            hideMediaController();
            bControlsActive = false;
        } else {
            showController();
            bControlsActive = true;
            setProgress();
        }
    }

    private void showController() {
        linMediaController.setVisibility(View.VISIBLE);
        mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideMediaController() {
        linMediaController.setVisibility(View.GONE);
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initPlayButton() {
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bIsPlaying) {
                    exoPlayer.setPlayWhenReady(false);
                    bIsPlaying = false;
                } else {
                    exoPlayer.setPlayWhenReady(true);
                    bIsPlaying = true;
                    setProgress();
                }
                btnPlay.setSelected(bIsPlaying);
            }
        });
    }

    private void initMp4Player() {
        if (feedVideo.video != null) {
            MediaSource sampleSource = new ExtractorMediaSource(Uri.parse(feedVideo.video), dataSourceFactory, new DefaultExtractorsFactory(),
                    handler, error -> {

            });

            initExoPlayer(sampleSource);
        }
    }

    private void initExoPlayer(MediaSource sampleSource) {
        if (exoPlayer == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            exoPlayer = ExoPlayerFactory.newSimpleInstance(mainActivity, trackSelector);
        }
        initMediaControls();
        startVideoPlaying(sampleSource);
    }

    private void startVideoPlaying(MediaSource sampleSource) {
        exoPlayer.prepare(sampleSource);

        exoPlayer.setVideoSurfaceView(svPlayer);
        exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        exoPlayer.setPlayWhenReady(true);
    }

    private void initHLSPlayer(String dashUrl) {

        MediaSource sampleSource = new HlsMediaSource(Uri.parse(dashUrl), dataSourceFactory, handler,
                this);


        initExoPlayer(sampleSource);
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                              int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                              long mediaEndTimeMs, long elapsedRealtimeMs) {
        Toast.makeText(mainActivity, "on load started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                                int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                                long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        Toast.makeText(mainActivity, "on load completed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                               int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                               long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        Toast.makeText(mainActivity, "on load cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                            int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                            long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded,
                            IOException error, boolean wasCanceled) {
        Toast.makeText(mainActivity, "on load error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                          Object trackSelectionData, long mediaTimeMs) {

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
        mainActivity.hideTopbar();
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

        llRate.setOnClickListener(this);
        llDirection.setOnClickListener(this);
        llReport.setOnClickListener(this);
        llShare.setOnClickListener(this);
        imgback.setOnClickListener(this);
        imgFullScreen.setOnClickListener(this);

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
//        txtLocation.setText(feedVideo.city);
        txtTags.setText(feedVideo.videoTags);
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_user_placeholder).error(R.drawable.ic_user_placeholder);
        Glide.with(this).load(feedVideo.userImage).apply(requestOptions).into(imgProfile);
        txtUserName.setText(feedVideo.firstName.concat(" ").concat(feedVideo.lastName));
        txtUserLoc.setText(feedVideo.city);
        if (userId.equalsIgnoreCase(feedVideo.userId)) {
            txtFollowStatus.setVisibility(View.GONE);
        } else {
            txtFollowStatus.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(feedVideo.followStatus)) {
            txtFollowStatus.setText("Follow");
            txtFollowStatus.setSelected(false);
        } else {
            txtFollowStatus.setText("Unfollow");
            txtFollowStatus.setSelected(true);
        }
        txtFollowStatus.setOnClickListener(this);

        if (feedVideoList.size() > 0) {
            videosAdapter.addVideos(feedVideoList);
            rvRelatedVideos.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            rvRelatedVideos.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayer.stop();
        mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (player != null) {
//            if (loopingSource != null) player.prepare(loopingSource);
//            player.setPlayWhenReady(true);
//        }
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//        if (!mRatioAlreadyCalculated && mVideoWidthHeightRatio != (float) width / height) {
//            mVideoWidthHeightRatio = ((float) width / height) * pixelRatio;
//            mRatioAlreadyCalculated = true;
//        }
//        updateVideoRatio();
//        med.setVideoWidthHeightRatio(height == 0 ? 1 : (pixelWidthHeightRatio * width) / height);
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

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
            initMp4Player();
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
                if (TextUtils.isEmpty(followStatus) || followStatus.equalsIgnoreCase("unfollow")) {
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
                    feedVideo.followStatus = "Unfollow";
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
