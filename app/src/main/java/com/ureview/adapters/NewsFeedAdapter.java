package com.ureview.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
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

    @NonNull
    @Override
    public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsFeedViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final VideoModel videoModel = feedVideoList.get(position);

        if (!TextUtils.isEmpty(videoModel.userImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .fitCenter()
                    .error(R.drawable.ic_user_placeholder);

            Glide.with(context)
                    .load(videoModel.userImage)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_user_placeholder);
        holder.txtName.setText(videoModel.firstName.concat(" ").concat(videoModel.lastName));
        if (!TextUtils.isEmpty(videoModel.shared_text)) {
            holder.txtName.append(" ");
            if (videoModel.shared_text.contains("shared")) {
                SpannableString spannableString1 = new SpannableString("shared");
                spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.app_text_color)), 0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                spannableString1.setSpan(boldSpan, 0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txtName.append(spannableString1);

                SpannableString spannableString2 = new SpannableString(videoModel.shared_text.replace("shared", ""));
                spannableString2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorBlack)), 0, spannableString2.length(), 0);
                holder.txtName.append(spannableString2);
            }
        }

        holder.txtLoc.setText(videoModel.city);
        int width = StaticUtils.SCREEN_WIDTH;
        holder.imgLocation.getLayoutParams().height = width / 2;
        holder.imgLocation.getLayoutParams().width = width;
        if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.video_placeholder).error(R.drawable.video_placeholder);
            Glide.with(context)
                    .load(videoModel.videoPosterImage)
                    .apply(requestOptions)
                    .into(holder.imgLocation);
        } else holder.imgLocation.setImageResource(R.drawable.video_placeholder);

//        Glide.with(context).load(videoModel.categoryBgImage).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
//                holder.txtCategory.setBackground(resource);
//            }
//        });

//        holder.txtCategory.setBackgroundResource(R.drawable.blue_bg);

        holder.txtCategory.setText(videoModel.categoryName);
        holder.txtSynth.setText(videoModel.videoTitle);
        String count = videoModel.videoWatchedCount;
        DecimalFormat df = new DecimalFormat("0.0");
        if (Integer.parseInt(count) > 1000) {
            count = df.format((Double.valueOf(count) / 1000)).concat("k");
        }
        holder.txtViewCount.setText(count);
        holder.txtDistance.setText(videoModel.distance);
        holder.txtDuration.setVisibility(TextUtils.isEmpty(videoModel.videoDuration) ? View.GONE : View.VISIBLE);
        holder.txtDuration.setText(videoModel.videoDuration);
        holder.txtRatingsNo.setText("(".concat(String.valueOf(videoModel.customerRating)).concat(")"));
        setProfileRating(holder, Float.parseFloat(TextUtils.isEmpty(videoModel.videoRating) ? "0f" : videoModel.videoRating));

        if (position == feedVideoList.size() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        }
        holder.relItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.relItem, feedVideoList, videoModel, position, "");
            }
        });
        holder.txtViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtViewCount, feedVideoList, videoModel, position, "");
            }
        });
        holder.txtDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtDistance, feedVideoList, videoModel, position, "");
            }
        });
        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.imgProfile, feedVideoList, videoModel, position, "");
            }
        });
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtName, feedVideoList, videoModel, position, "");
            }
        });
        holder.txtLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.txtLoc, feedVideoList, videoModel, position, "");
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
        private ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
        private View dividerView;
        private RelativeLayout relItem;
//        private RatingBar ratingBarVideo;

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
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            dividerView = itemView.findViewById(R.id.dividerView);
//            ratingBarVideo = itemView.findViewById(R.id.ratingBarVideo);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
        }
    }

    private void setProfileRating(NewsFeedViewHolder holder, float v) {
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

    private void setSelectedStar(NewsFeedViewHolder holder, boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        holder.imgStar1.setSelected(b);
        holder.imgStar2.setSelected(b1);
        holder.imgStar3.setSelected(b2);
        holder.imgStar4.setSelected(b3);
        holder.imgStar5.setSelected(b4);
    }
}
