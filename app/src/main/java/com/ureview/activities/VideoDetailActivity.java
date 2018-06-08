package com.ureview.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
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
import com.ureview.R;
import com.ureview.adapters.VideosAdapter;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomRecyclerView;
import com.ureview.utils.views.CustomTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class VideoDetailActivity extends BaseActivity implements VideoRendererEventListener, AdaptiveMediaSourceEventListener {

    private ImageView imgCatBg, imgProfile;
    private CustomTextView txtVideoTitle, txtCategory, txtViewCount, txtRatingno, txtLocation,
            txtTags, txtFollowStatus, txtUserName, txtUserLoc, txtNoData;
    private RatingBar ratingBar;
    private LinearLayout llRate, llShare, llDirection, llReport;
    private CustomRecyclerView rvRelatedVideos;
    private VideosAdapter videosAdapter;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();
    private VideoModel feedVideo;
    private NestedScrollView nestedScrollView;
    //    private SimpleExoPlayer player;
    LoopingMediaSource loopingSource;

    //video player
    SurfaceView svPlayer;
    ImageButton prev;
    ImageButton rew;
    ImageButton btnPlay;
    ImageButton ffwd, next;
    TextView timeCurrent;
    SeekBar mediacontrollerProgress;
    TextView playerEndTime;
    ImageButton fullscreen;
    LinearLayout linMediaController;
    FrameLayout playerFrameLayout;

    private SimpleExoPlayer exoPlayer;
    private boolean bAutoplay = true;
    private boolean bIsPlaying = false;
    private boolean bControlsActive = true;

    private Handler handler;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private DataSource.Factory dataSourceFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        getBundleData();
        initComponents();
//        initVideoPlayer();
        initVideoPlayer2();
    }

    private void initVideoPlayer2() {
        svPlayer = findViewById(R.id.sv_player);
        prev = findViewById(R.id.prev);
        rew = findViewById(R.id.rew);
        btnPlay = findViewById(R.id.btnPlay);
        ffwd = findViewById(R.id.ffwd);
        next = findViewById(R.id.next);
        timeCurrent = findViewById(R.id.time_current);
        mediacontrollerProgress = findViewById(R.id.mediacontroller_progress);
        playerEndTime = findViewById(R.id.player_end_time);
        fullscreen = findViewById(R.id.fullscreen);
        linMediaController = findViewById(R.id.lin_media_controller);
        playerFrameLayout = findViewById(R.id.player_frame_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler();
        initDataSource();
        initMp4Player(feedVideo.video);

        if (bAutoplay) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(true);
                bIsPlaying = true;
                setProgress();
            }
        }
    }

    private void initDataSource() {
        dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"),
                        new DefaultBandwidthMeter());
    }

    private void initMediaControls() {
        initSurfaceView();
        initPlayButton();
        initSeekBar();
        initFwd();
        initPrev();
        initRew();
        initNext();
    }

    private void initNext() {
        next.requestFocus();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getDuration());
            }
        });
    }

    private void initRew() {
        rew.requestFocus();
        rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
            }
        });
    }

    private void initPrev() {
        prev.requestFocus();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(0);
            }
        });
    }

    private void initFwd() {
        ffwd.requestFocus();
        ffwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
            }
        });
    }


    private void initSurfaceView() {
        svPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMediaControls();
            }
        });
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
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideMediaController() {
        linMediaController.setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            }
        });
    }

    private void initMp4Player(String mp4URL) {

        MediaSource sampleSource =
                new ExtractorMediaSource(Uri.parse(mp4URL), dataSourceFactory, new DefaultExtractorsFactory(),
                        handler, new ExtractorMediaSource.EventListener() {
                    @Override
                    public void onLoadError(IOException error) {

                    }
                });


        initExoPlayer(sampleSource);
    }

    private void initExoPlayer(MediaSource sampleSource) {
        if (exoPlayer == null) {
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }

        exoPlayer.prepare(sampleSource);

        exoPlayer.setVideoSurfaceView(svPlayer);

        exoPlayer.setPlayWhenReady(true);

        initMediaControls();
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

    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                                int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                                long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                               int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                               long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                            int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                            long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded,
                            IOException error, boolean wasCanceled) {

    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                          Object trackSelectionData, long mediaTimeMs) {

    }

  /*  private void initVideoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//        playerView.setUseController(true);
//        playerView.requestFocus();
//        playerView.setPlayer(player);

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
            public void onPlayerError(ExoPlaybackException error) {
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity() {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

        player.setPlayWhenReady(true);
        player.setVideoDebugListener(this);
    }*/

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
//        playerView = findViewById(R.id.playerView);
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
        nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.smoothScrollTo(0, 0);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        player.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        player.stop();
    }

    @Override
    protected void onStart() {
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

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
}
