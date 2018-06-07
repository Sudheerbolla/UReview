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

public class SearchVideosAdapter extends RecyclerView.Adapter<SearchVideosAdapter.NewsFeedViewHolder> {

    private Context context;
    private ArrayList<VideoModel> videoArrList = new ArrayList<>();

    public SearchVideosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_video, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, final int position) {
        VideoModel videoModel = videoArrList.get(position);
        RequestOptions reqOptons = RequestOptions.bitmapTransform(new RoundedCorners(10));
        Glide.with(context).load(videoModel.videoPosterImage).apply(reqOptons).into(holder.imgLocation);
        Glide.with(context).load(videoModel.categoryBgImage).into(holder.imgCatBg);
        holder.txtCategory.setText(videoModel.categoryName);
        holder.txtSynth.setText(videoModel.videoTitle);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText(videoModel.distance);
//        holder.txtDuration.setText(videoModel.videoDuration);
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.ratingBarVideo.setRating(Float.intBitsToFloat(videoModel.ratingGiven));
        holder.txtLocBtm.setText(videoModel.city);

        if (position == videoArrList.size() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return videoArrList.size();
    }

    public void addVideos(ArrayList<VideoModel> arrList) {
        int lastPosition = videoArrList.size() > 0 ? videoArrList.size() - 1 : 0;
        videoArrList.addAll(arrList);
        notifyItemRangeInserted(lastPosition, arrList.size() - 1);
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgLocation, imgCatBg;
        private CustomTextView txtCategory, txtSynth, txtLocBtm, txtDistance,
                txtViewCount, txtRatingsNo;
        private RatingBar ratingBarVideo;
        private View dividerView;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtSynth = itemView.findViewById(R.id.txtSynth);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtRatingsNo = itemView.findViewById(R.id.txtRatingsNo);
            txtLocBtm = itemView.findViewById(R.id.txtLocBtm);
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            ratingBarVideo = itemView.findViewById(R.id.ratingBarVideo);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }
}
