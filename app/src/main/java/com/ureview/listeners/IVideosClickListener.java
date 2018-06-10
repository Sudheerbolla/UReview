package com.ureview.listeners;

import android.view.View;

public interface IVideosClickListener {

    void onClick(View view, int position);

    void onWatchCountClick(View view, int position);

    void onDistanceClick(View view, int position);

    void onLongClick(View view, int position);

}
