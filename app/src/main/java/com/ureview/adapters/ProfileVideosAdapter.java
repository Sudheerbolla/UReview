package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<VideoModel> videoList = new ArrayList<>();

    public ProfileVideosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_video, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        VideoModel videoModel = videoList.get(position);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners(7));
//        requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(context).load(videoModel.videoPosterImage).apply(requestOptions).into(holder.imgVideo);
        holder.txtName.setText(videoModel.videoTitle);
        holder.txtTags.setText(videoModel.videoTags);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText("12 KM");
        holder.ratingBar.setRating(Float.intBitsToFloat(videoModel.ratingGiven));
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.txtDuration.setText(videoModel.videoDuration);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void addVideos(ArrayList<VideoModel> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgVideo;
        private CustomTextView txtName, txtTags, txtViewCount, txtDistance, txtRatingsNo, txtDuration;
        private RatingBar ratingBar;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
            txtName = itemView.findViewById(R.id.txtName);
            txtTags = itemView.findViewById(R.id.txtTags);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtRatingsNo = itemView.findViewById(R.id.txtRatingsNo);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
