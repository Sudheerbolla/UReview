package com.ureview.listeners;

import android.view.View;

import com.ureview.models.VideoModel;

import java.util.ArrayList;

public interface IVideosClickListener {

    void onClick(View view, ArrayList<VideoModel> videoModels, VideoModel videoModel, int position, String vidType);

    void onLongClick(View view, int position);

}
