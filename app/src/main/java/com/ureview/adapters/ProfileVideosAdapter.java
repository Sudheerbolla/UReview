package com.ureview.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.CategoryViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<VideoModel> videoList;
    private boolean isOwner;
    private String currUserId;

    public ProfileVideosAdapter(Context context) {
        this.context = context;
    }

    public ProfileVideosAdapter(Context context, ArrayList<VideoModel> videoList, IClickListener iClickListener, boolean isOwner) {
        this.context = context;
        this.isOwner = isOwner;
        this.iClickListener = iClickListener;
        this.videoList = videoList;
        currUserId = LocalStorage.getInstance(context).getString(LocalStorage.PREF_USER_ID, "");
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        VideoModel videoModel = videoList.get(position);
        if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.video_placeholder)
                    .fitCenter()
                    .error(R.drawable.video_placeholder);

            Glide.with(context)
                    .load(videoModel.videoPosterImage)
                    .apply(options)
                    .into(holder.imgVideo);
        } else holder.imgVideo.setImageResource(R.drawable.video_placeholder);

        holder.txtName.setText(videoModel.videoTitle);
        holder.txtTags.setText(videoModel.videoTags);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText(videoModel.distance);
        setProfileRating(holder, Float.parseFloat(videoModel.videoRating));
        holder.txtRatingsNo.setText("(".concat(String.valueOf(videoModel.customerRating)).concat(")"));
        holder.txtDuration.setVisibility(TextUtils.isEmpty(videoModel.videoDuration) ? View.GONE : View.VISIBLE);
        holder.txtDuration.setText(videoModel.videoDuration);
        if (videoModel.videoOwnerId.equalsIgnoreCase(currUserId))
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
        private ImageView imgVideo, imgDeleteVideo, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
        private CustomTextView txtName, txtTags, txtViewCount, txtDistance, txtRatingsNo, txtDuration;
        //        private RatingBar ratingBar;
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
//            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
            relItem = itemView.findViewById(R.id.relItem);
            imgDeleteVideo = itemView.findViewById(R.id.imgDeleteVideo);
        }
    }

    private void setProfileRating(CategoryViewHolder holder, float v) {
        switch ((int) v) {
            case 0:
                setSelectedStar(holder, false, false, false, false, false);
                break;
            case 1:
                setSelectedStar(holder, true, false, false, false, false);
                break;
            case 2:
                setSelectedStar(holder, true, true, false, false, false);
                break;
            case 3:
                setSelectedStar(holder, true, true, true, false, false);
                break;
            case 4:
                setSelectedStar(holder, true, true, true, true, false);
                break;
            case 5:
                setSelectedStar(holder, true, true, true, true, true);
                break;
        }
    }

    private void setSelectedStar(CategoryViewHolder holder, boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        holder.imgStar1.setSelected(b);
        holder.imgStar2.setSelected(b1);
        holder.imgStar3.setSelected(b2);
        holder.imgStar4.setSelected(b3);
        holder.imgStar5.setSelected(b4);
    }
}
