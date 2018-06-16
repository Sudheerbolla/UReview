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
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.CategoryViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<VideoModel> videoList;
    private boolean isOwner;

    public ProfileVideosAdapter(Context context) {
        this.context = context;
    }

    public ProfileVideosAdapter(Context context, ArrayList<VideoModel> videoList, IClickListener iClickListener, boolean isOwner) {
        this.context = context;
        this.isOwner = isOwner;
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
                    .bitmapTransform(new RoundedCorners(7))
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
        holder.ratingBar.setRating(Float.parseFloat(videoModel.videoRating));
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.txtDuration.setText(videoModel.videoDuration);
        if (videoModel.videoOwnerId.equalsIgnoreCase(videoModel.userId))
            holder.imgDeleteVideo.setVisibility(View.VISIBLE);
        else holder.imgDeleteVideo.setVisibility(View.GONE);
        holder.imgDeleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });
        holder.relItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgVideo, imgDeleteVideo;
        private CustomTextView txtName, txtTags, txtViewCount, txtDistance, txtRatingsNo, txtDuration;
        private RatingBar ratingBar;
        private RelativeLayout relItem;

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
            relItem = itemView.findViewById(R.id.relItem);
            imgDeleteVideo = itemView.findViewById(R.id.imgDeleteVideo);
        }
    }
}
