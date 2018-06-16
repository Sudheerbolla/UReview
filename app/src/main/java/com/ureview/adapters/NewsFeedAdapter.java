package com.ureview.adapters;

import android.content.Context;
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
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {

    private Context context;
    IVideosClickListener iClickListener;
    private ArrayList<VideoModel> feedVideoList = new ArrayList<>();

    public NewsFeedAdapter(Context context, IVideosClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewsFeedViewHolder holder, final int position) {
        final VideoModel videoModel = feedVideoList.get(position);

        if (!TextUtils.isEmpty(videoModel.userImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .fitCenter()
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(videoModel.userImage)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_profile);


        holder.txtName.setText(videoModel.firstName.concat(" ").concat(videoModel.lastName));
        holder.txtLoc.setText(videoModel.city);
        int width = StaticUtils.SCREEN_WIDTH;
        holder.imgLocation.getLayoutParams().height = width / 2;
        holder.imgLocation.getLayoutParams().width = width;
        if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
            RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners(25));
//            requestOptions.placeholder(R.drawable.ic_profile);
//            requestOptions.error(R.drawable.ic_profile);
            Glide.with(context)
                    .load(videoModel.videoPosterImage)
                    .apply(requestOptions)
                    .into(holder.imgLocation);
        } else holder.imgLocation.setImageResource(R.drawable.ic_profile);

        if (!TextUtils.isEmpty(videoModel.categoryBgImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .fitCenter()
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(videoModel.categoryBgImage)
                    .apply(options)
                    .into(holder.imgCatBg);
        } else holder.imgCatBg.setImageResource(R.drawable.ic_profile);

        holder.txtCategory.setText(videoModel.categoryName);
        holder.txtSynth.setText(videoModel.videoTitle);
        String count = videoModel.videoWatchedCount;
        DecimalFormat df = new DecimalFormat("0.0");
        if (Integer.parseInt(count) > 1000) {
            count = df.format((Double.valueOf(count) / 1000)).concat("k");
        }
        holder.txtViewCount.setText(count);
        holder.txtDistance.setText(videoModel.distance);
        holder.txtDuration.setText(videoModel.videoDuration);
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        holder.ratingBarVideo.setRating(Float.intBitsToFloat(videoModel.ratingGiven));

        if (position == feedVideoList.size() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        }
        holder.relItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.relItem, videoModel, position);
            }
        });
        holder.txtViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtViewCount, videoModel, position);
            }
        });
        holder.txtDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtDistance, videoModel, position);
            }
        });
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.imgProfile, videoModel, position);
            }
        });
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtName, videoModel, position);
            }
        });
        holder.txtLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtLoc, videoModel, position);
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
        notifyItemRangeInserted(lastPosition + 1, feedVideoList.size() - 1);
    }

    public void clearAllVideos() {
        this.feedVideoList.clear();
        notifyDataSetChanged();
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile, imgLocation;
        private CustomTextView txtName, txtLoc, txtSynth, txtDuration,
                txtViewCount, txtDistance, txtRatingsNo, txtCategory;
        private ImageView imgCatBg;
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
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            dividerView = itemView.findViewById(R.id.dividerView);
            ratingBarVideo = itemView.findViewById(R.id.ratingBarVideo);
        }
    }
}
