package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import com.ureview.listeners.IVideosClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.CategoryViewHolder> {

    private final boolean only10Items;
    private Context context;
    private IVideosClickListener iClickListener;
    private ArrayList<VideoModel> videoList = new ArrayList<>();

    public VideosAdapter(Context context, IVideosClickListener iClickListener, boolean only10Items) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.only10Items = only10Items;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        boolean val = only10Items && position >= 10;
        if (val) {
            holder.cvMore.setVisibility(position > 10 ? View.GONE : View.VISIBLE);
        } else {
            holder.cvMore.setVisibility(View.GONE);
        }
        holder.imgVideo.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtName.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtTags.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtViewCount.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtDistance.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.ratingBar.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtRatingsNo.setVisibility(val ? View.GONE : View.VISIBLE);

        if (position < 10) {
            final VideoModel videoModel = videoList.get(position);
            if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
                RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(25));
                options.error(R.drawable.ic_profile);
                options.placeholder(R.drawable.ic_profile);
                Glide.with(context)
                        .load(videoModel.videoPosterImage)
                        .apply(options)
                        .into(holder.imgVideo);
            } else holder.imgVideo.setImageResource(R.drawable.ic_profile);

            holder.txtName.setText(videoModel.videoTitle);
            holder.txtTags.setText(videoModel.videoTags);
            holder.txtViewCount.setText(videoModel.videoWatchedCount);
            holder.txtDistance.setText(videoModel.distance);
            try {
                holder.ratingBar.setRating(Float.intBitsToFloat(videoModel.ratingGiven));
            } catch (Exception e) {
                e.printStackTrace();
                holder.ratingBar.setRating(0f);
            }
            holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
            holder.txtDuration.setText(videoModel.videoDuration);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null) {
                        iClickListener.onClick(holder.relItem, videoModel, holder.getAdapterPosition());
                    }
                }
            });
            holder.txtViewCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null)
                        iClickListener.onClick(holder.txtViewCount, videoModel, holder.getAdapterPosition());
                }
            });
            holder.txtDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null)
                        iClickListener.onClick(holder.txtDistance, videoModel, holder.getAdapterPosition());
                }
            });
        }
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
        private CardView cvMore;
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
            cvMore = itemView.findViewById(R.id.cvMore);
            relItem = itemView.findViewById(R.id.relItem);
        }
    }
}
