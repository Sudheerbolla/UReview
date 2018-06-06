package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {

    private Context context;
    IClickListener iClickListener;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();

    public NewsFeedAdapter(Context context, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, final int position) {
        VideoModel videoModel = feedVideoList.get(position);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners(20));
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(context).load(videoModel.userImage).apply(requestOptions).into(holder.imgProfile);
        holder.txtName.setText(videoModel.firstName.concat(" ").concat(videoModel.lastName));
        holder.txtLoc.setText(videoModel.userLocation);
        Glide.with(context).load(videoModel.videoPosterImage).into(holder.imgLocation);
        Glide.with(context).load(videoModel.categoryBgImage).into(holder.imgCatBg);
        Glide.with(context).load(videoModel.categoryImage).into(holder.imgCat);
        holder.txtCategory.setText(videoModel.categoryName);
        holder.txtSynth.setText(videoModel.videoTitle);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText("12.00 KM");
        holder.txtDuration.setText(videoModel.videoDuration);
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.ratingBarVideo.setRating(Float.intBitsToFloat(videoModel.ratingGiven));

        if (position == feedVideoList.size() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        }
        holder.relItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedVideoList.size();
    }

    public void addVideos(ArrayList<VideoModel> feedVideoList) {
        int lastPosition = this.feedVideoList.size() > 0 ? this.feedVideoList.size() - 1 : 0;
        this.feedVideoList.addAll(feedVideoList);
        notifyItemRangeChanged(lastPosition, this.feedVideoList.size() - 1);
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile, imgLocation;
        private CustomTextView txtName, txtLoc, txtSynth, txtDuration,
                txtViewCount, txtDistance, txtRatingsNo, txtCategory;
        private ImageView imgCat, imgCatBg;
        private View dividerView;
        private RelativeLayout relItem;
        private RatingBar ratingBarVideo;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            relItem = itemView.findViewById(R.id.relItem);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            txtName = itemView.findViewById(R.id.txtName);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtLoc = itemView.findViewById(R.id.txtLoc);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtSynth = itemView.findViewById(R.id.txtSynth);
            txtRatingsNo = itemView.findViewById(R.id.txtRatingsNo);
            imgCat = itemView.findViewById(R.id.imgCat);
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            dividerView = itemView.findViewById(R.id.dividerView);
            ratingBarVideo = itemView.findViewById(R.id.ratingBarVideo);
        }
    }
}
