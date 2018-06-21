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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
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
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        boolean val = only10Items && position >= 5;
        if (val) {
            holder.cvMore.setVisibility(position > 5 ? View.GONE : View.VISIBLE);
        } else {
            holder.cvMore.setVisibility(View.GONE);
        }
        holder.imgVideo.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtName.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtTags.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtViewCount.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtDistance.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.llRatingBar.setVisibility(val ? View.GONE : View.VISIBLE);
        holder.txtRatingsNo.setVisibility(val ? View.GONE : View.VISIBLE);

        if (position < 10) {
            final VideoModel videoModel = videoList.get(position);
            if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
                RequestOptions options = new RequestOptions().
                        error(R.drawable.ic_user_placeholder).placeholder(R.drawable.ic_user_placeholder);
                Glide.with(context)
                        .load(videoModel.videoPosterImage)
                        .apply(options)
                        .into(holder.imgVideo);
            } else holder.imgVideo.setImageResource(R.drawable.ic_user_placeholder);

            holder.txtName.setText(videoModel.videoTitle);
            holder.txtTags.setText(videoModel.videoTags);
            holder.txtViewCount.setText(videoModel.videoWatchedCount);
            holder.txtDistance.setText(videoModel.distance);
            try {
                setProfileRating(holder, Float.intBitsToFloat(videoModel.ratingGiven));
            } catch (Exception e) {
                e.printStackTrace();
                setProfileRating(holder, 0f);
            }
            holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
            holder.txtDuration.setText(videoModel.videoDuration);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null) {
                        iClickListener.onClick(holder.relItem, videoList, videoModel, holder.getAdapterPosition());
                    }
                }
            });
            holder.txtViewCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null)
                        iClickListener.onClick(holder.txtViewCount, videoList, videoModel, holder.getAdapterPosition());
                }
            });
            holder.txtDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iClickListener != null)
                        iClickListener.onClick(holder.txtDistance, videoList, videoModel, holder.getAdapterPosition());
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
        private ImageView imgVideo, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
        private CustomTextView txtName, txtTags, txtViewCount, txtDistance, txtRatingsNo, txtDuration;
        //        private RatingBar ratingBar;
        private CardView cvMore;
        private LinearLayout llRatingBar;
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
            llRatingBar = itemView.findViewById(R.id.llRatingBar);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
            cvMore = itemView.findViewById(R.id.cvMore);
            relItem = itemView.findViewById(R.id.relItem);
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
