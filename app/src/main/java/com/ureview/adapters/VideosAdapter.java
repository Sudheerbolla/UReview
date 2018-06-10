package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.CategoryViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<VideoModel> videoList;

    public VideosAdapter(Context context) {
        this.context = context;
    }

    public VideosAdapter(Context context, ArrayList<VideoModel> videoList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        VideoModel videoModel = videoList.get(position);

        if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .fitCenter()
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(videoModel.videoPosterImage)
                    .apply(options)
                    .into(holder.imgVideo);
        } else holder.imgVideo.setImageResource(R.drawable.ic_profile);

        holder.txtName.setText(videoModel.videoTitle);
        holder.txtTags.setText(videoModel.videoTags);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText(videoModel.distance);
        holder.ratingBar.setRating(Float.intBitsToFloat(videoModel.ratingGiven));
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.txtDuration.setText(videoModel.videoDuration);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void addVideos(ArrayList<VideoModel> list) {
        this.videoList = list;
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
