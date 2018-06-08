package com.ureview.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.ureview.R;
import com.ureview.adapters.VideosAdapter;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class VideoDetailActivity extends BaseActivity implements VideoRendererEventListener {

//    private VideoView videoView;
    private ImageView imgPause, imgCatBg, imgProfile;
    private CustomTextView txtVideoTitle, txtCategory, txtViewCount, txtRatingno, txtLocation,
            txtTags, txtFollowStatus, txtUserName, txtUserLoc, txtNoData;
    private RatingBar ratingBar;
    private LinearLayout llRate, llShare, llDirection, llReport;
    private CustomRecyclerView rvRelatedVideos;
    private VideosAdapter videosAdapter;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private VideoModel feedVideo;
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    LoopingMediaSource loopingSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        getBundleData();
        initComponents();
        initVideoPlayer();
    }


    private void initVideoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        playerView.setUseController(true);
        playerView.requestFocus();
        playerView.setPlayer(player);

        Uri mp4VideoUri = Uri.parse(feedVideo.video); //Radnom 540p indian channel

        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "videoplayer"), bandwidthMeterA);

        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        loopingSource = new LoopingMediaSource(videoSource);

        player.prepare(loopingSource);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        player.setPlayWhenReady(true);
        player.setVideoDebugListener(this);
    }

    private void getBundleData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("news_feed")) {
                feedVideoList = bundle.getParcelableArrayList("news_feed");
                if (bundle.containsKey("position")) {
                    feedVideo = feedVideoList.get(bundle.getInt("position"));
                    feedVideoList.remove(feedVideo);
                }
            }
        }
    }

    private void initComponents() {
        playerView = findViewById(R.id.playerView);
//        videoView = findViewById(R.id.videoView);
        imgPause = findViewById(R.id.imgPause);
        txtVideoTitle = findViewById(R.id.txtVideoTitle);
        imgCatBg = findViewById(R.id.imgCatBg);
        txtCategory = findViewById(R.id.txtCategory);
        txtViewCount = findViewById(R.id.txtViewCount);
        ratingBar = findViewById(R.id.ratingBar);
        txtRatingno = findViewById(R.id.txtRatingno);
        llRate = findViewById(R.id.llRate);
        llShare = findViewById(R.id.llShare);
        llDirection = findViewById(R.id.llDirection);
        llReport = findViewById(R.id.llReport);
        txtLocation = findViewById(R.id.txtLocation);
        txtTags = findViewById(R.id.txtTags);
        imgProfile = findViewById(R.id.imgProfile);
        txtFollowStatus = findViewById(R.id.txtFollowStatus);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserLoc = findViewById(R.id.txtUserLoc);
        rvRelatedVideos = findViewById(R.id.rvRelatedVideos);
        txtNoData = findViewById(R.id.txtNoData);

        rvRelatedVideos.setNestedScrollingEnabled(false);
        videosAdapter = new VideosAdapter(this);
        rvRelatedVideos.setAdapter(videosAdapter);

        if (feedVideo != null)
            setVideoDetails();
    }

    private void setVideoDetails() {
        txtVideoTitle.setText(feedVideo.videoTitle);
        txtViewCount.setText(feedVideo.videoWatchedCount);
        if (!TextUtils.isEmpty(feedVideo.videoRating))
            ratingBar.setRating(Float.parseFloat(feedVideo.videoRating));
        txtRatingno.setText("(".concat(String.valueOf(feedVideo.ratingGiven).concat(")")));
        txtCategory.setText(feedVideo.categoryName);
        Glide.with(this).load(feedVideo.categoryBgImage).into(imgCatBg);
        txtLocation.setText(feedVideo.videoLocation);
        txtTags.setText(feedVideo.videoTags);
        Glide.with(this).load(feedVideo.userImage).into(imgProfile);
        txtUserName.setText(feedVideo.firstName.concat(" ").concat(feedVideo.lastName));
        txtUserLoc.setText(feedVideo.userLocation);
        txtFollowStatus.setText(TextUtils.isEmpty(feedVideo.followStatus) ? "Follow" : "Following");
        txtFollowStatus.setSelected(!TextUtils.isEmpty(feedVideo.followStatus));
        if (feedVideoList.size() > 0) {
            videosAdapter.addVideos(feedVideoList);
            rvRelatedVideos.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            rvRelatedVideos.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

//        videoView.setVideoURI(Uri.parse(feedVideo.video));
//        videoView.start();
        imgPause.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            if (loopingSource != null) player.prepare(loopingSource);
            player.setPlayWhenReady(true);
        }
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

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
}
