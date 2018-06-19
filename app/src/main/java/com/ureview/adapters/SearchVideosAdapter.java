package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.VideoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CustomTextView;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;

public class SearchVideosAdapter extends RecyclerView.Adapter<SearchVideosAdapter.NewsFeedViewHolder> {

    private final IClickListener iClickListener;
    private Context context;
    private ArrayList<VideoModel> videoArrList = new ArrayList<>();
    private String currUserId;

    public SearchVideosAdapter(Context context, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        currUserId = LocalStorage.getInstance(context).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_video, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewsFeedViewHolder holder, final int position) {
        VideoModel videoModel = videoArrList.get(position);
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

        if (!TextUtils.isEmpty(videoModel.videoPosterImage)) {
            RequestOptions options = RequestOptions
                    .bitmapTransform(new RoundedCorners(20))
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(videoModel.videoPosterImage)
                    .apply(options)
                    .into(holder.imgLocation);
        } else holder.imgLocation.setImageResource(R.drawable.ic_profile);
        holder.txtCategory.setText(videoModel.categoryName);
        if (!TextUtils.isEmpty(videoModel.categoryBgImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .fitCenter()
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(WSUtils.BASE_URL.concat("/uploads/category_images/").concat(videoModel.categoryBgImage))
                    .apply(options)
                    .into(holder.imgCatBg);
        } else holder.imgCatBg.setImageResource(R.drawable.ic_profile);


        holder.txtSynth.setText(videoModel.videoTitle);
        holder.txtViewCount.setText(videoModel.videoWatchedCount);
        holder.txtDistance.setText(videoModel.distance);
//        holder.txtDuration.setText(videoModel.videoDuration);
        holder.txtRatingsNo.setText("(".concat(videoModel.videoRating).concat(")"));
        setProfileRating(holder, Float.intBitsToFloat(videoModel.ratingGiven));
        holder.txtLocBtm.setText(videoModel.city);

        holder.txtFollowStatus.setVisibility(currUserId.equalsIgnoreCase(videoModel.videoOwnerId) ? View.GONE : View.VISIBLE);
        holder.txtFollowStatus.setText(TextUtils.isEmpty(videoModel.followStatus) ||
                videoModel.followStatus.equalsIgnoreCase("Unfollow") ? "Follow" : "Unfollow");
        holder.txtFollowStatus.setSelected(!(TextUtils.isEmpty(videoModel.followStatus) ||
                videoModel.followStatus.equalsIgnoreCase("Unfollow")));

        if (position == videoArrList.size() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(holder.imgLocation, holder.getAdapterPosition());
                }
            }
        });
        holder.txtViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(holder.txtViewCount, holder.getAdapterPosition());
                }
            }
        });
        holder.txtDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(holder.txtDistance, holder.getAdapterPosition());
                }
            }
        });
        holder.txtFollowStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(holder.txtFollowStatus, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrList.size();
    }

    public void addVideos(ArrayList<VideoModel> arrList) {
        int lastPosition = videoArrList.size() > 0 ? videoArrList.size() - 1 : 0;
        videoArrList.addAll(arrList);
        notifyItemRangeInserted(lastPosition + 1, arrList.size() - 1);
    }

    public void updateItem(VideoModel videoModel, int follPos) {
        notifyItemChanged(follPos, videoModel);
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgLocation, imgCatBg, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5,
                imgProfile;
        private CustomTextView txtCategory, txtSynth, txtLocBtm, txtDistance,
                txtViewCount, txtRatingsNo, txtLoc, txtName, txtFollowStatus;
        //        private RatingBar ratingBarVideo;
        private View dividerView;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtLoc = itemView.findViewById(R.id.txtLoc);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtSynth = itemView.findViewById(R.id.txtSynth);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtRatingsNo = itemView.findViewById(R.id.txtRatingsNo);
            txtLocBtm = itemView.findViewById(R.id.txtLocBtm);
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
            txtDistance = itemView.findViewById(R.id.txtDistance);
//            ratingBarVideo = itemView.findViewById(R.id.ratingBarVideo);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
            dividerView = itemView.findViewById(R.id.dividerView);
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
