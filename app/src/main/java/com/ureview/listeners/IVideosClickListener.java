package com.ureview.listeners;

import android.view.View;

import com.ureview.models.VideoModel;

public interface IVideosClickListener {

    void onClick(View view, VideoModel videoModel, int position);

    void onLongClick(View view, int position);

}
