package com.ureview;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.squareup.picasso.Picasso;

public class VideoSampleActivity extends FragmentActivity {

    private static final String APPLICATION_RAW_PATH = "android.resource://com.venbi.UReview.app/";
    private static final String VIDEO_POSTER = "http://wac.450f.edgecastcdn.net/80450F/screencrush.com/files/2013/11/the-amazing-spider-man-2-poster-rhino.jpg";
    private static final String VIDEO_THUMBNAIL = "http://wac.450f.edgecastcdn.net/80450F/screencrush.com/files/2013/11/the-amazing-spider-man-2-poster-green-goblin.jpg";
    private static final String VIDEO_TITLE = "The Amazing Spider-Man 2: Rise of Electro";

    private DraggableView draggableView;
    private VideoView videoView;
    private ImageView thumbnailImageView, posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sample);

        videoView = findViewById(R.id.video_view);
        thumbnailImageView = findViewById(R.id.iv_thumbnail);
        posterImageView = findViewById(R.id.iv_poster);
        draggableView = findViewById(R.id.draggable_view);

        thumbnailImageView.setOnClickListener(view -> Toast.makeText(VideoSampleActivity.this, VIDEO_TITLE, Toast.LENGTH_SHORT).show());
        posterImageView.setOnClickListener(view -> draggableView.maximize());
        initializeVideoView();
        initializePoster();
        hookDraggableViewListener();
    }

    private void hookDraggableViewListener() {
        draggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                startVideo();
            }

            //Empty
            @Override
            public void onMinimized() {
                //Empty
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
            }
        });
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    /**
     * Resume the VideoView content.
     */
    private void startVideo() {
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    /**
     * Initialize ViedeoView with a video by default.
     */
    private void initializeVideoView() {
        Uri path = Uri.parse(APPLICATION_RAW_PATH + R.raw.video);
        videoView.setVideoURI(path);
        videoView.start();
    }

    /**
     * Initialize some ImageViews with a video poster and a video thumbnail.
     */
    private void initializePoster() {
        Picasso.with(this)
                .load(VIDEO_POSTER)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(posterImageView);
        Picasso.with(this)
                .load(VIDEO_THUMBNAIL)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(thumbnailImageView);
    }
}
