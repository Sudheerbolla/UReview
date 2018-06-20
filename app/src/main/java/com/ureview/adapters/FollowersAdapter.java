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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.FollowModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<FollowModel> followModelArrayList;
    boolean isFollowers;

    public FollowersAdapter(Context context, ArrayList<FollowModel> followModelArrayList, boolean showFollowers, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.followModelArrayList = followModelArrayList;
        this.isFollowers = showFollowers;
    }

    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//        holder.txtFollowStatus.setSelected(true);
//        holder.txtFollowStatus.setText("Following");
        holder.imgClear.setVisibility(View.GONE);
        FollowModel followModel = followModelArrayList.get(position);

        if (LocalStorage.getInstance(context).getString(LocalStorage.PREF_USER_ID, "").equalsIgnoreCase(followModel.user_id)) {
            holder.txtFollowStatus.setVisibility(View.GONE);
        } else {
            holder.txtFollowStatus.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(followModel.follow_status)) {
                if (isFollowers) {
                    if (followModel.status.equalsIgnoreCase("1")) {
                        holder.txtFollowStatus.setText("Follow");
                        holder.txtFollowStatus.setSelected(false);
                        holder.imgClear.setVisibility(View.VISIBLE);
                    } else if (followModel.status.equalsIgnoreCase("2")) {
                        holder.txtFollowStatus.setText("Unfollow");
                        holder.txtFollowStatus.setSelected(true);
                        holder.imgClear.setVisibility(View.GONE);
                    }
                } else {
                    holder.txtFollowStatus.setText("Follow");
                    holder.txtFollowStatus.setSelected(false);
                    holder.imgClear.setVisibility(View.VISIBLE);
                }
            } else {
                holder.txtFollowStatus.setText("Unfollow");
                holder.txtFollowStatus.setSelected(true);
                holder.imgClear.setVisibility(View.GONE);
            }
        }

        holder.txtName.setText(followModel.first_name + " " + followModel.last_name);
        holder.txtReviewCount.setText("Review Upload: " + followModel.uploaded_videos_count);
        setProfileRating(holder, TextUtils.isEmpty(followModel.user_rating) ? 0f : Float.parseFloat(followModel.user_rating));

        holder.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });

        holder.relBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });

        holder.txtFollowStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });

        if (!TextUtils.isEmpty(followModel.user_image)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .bitmapTransform(new RoundedCorners(7))
                    .fitCenter()
                    .error(R.drawable.ic_profile);
            Glide.with(context)
                    .load(followModel.user_image)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_profile);

    }

    @Override
    public int getItemCount() {
        return followModelArrayList.size();
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount;
        //        private RatingBar ratingBar;
        private CircleImageView imgProfile;
        private ImageView imgClear, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
        private RelativeLayout relBody;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            txtReviewCount = itemView.findViewById(R.id.txtReviewCount);
//            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgClear = itemView.findViewById(R.id.imgClear);
            relBody = itemView.findViewById(R.id.relBody);
        }
    }

    private void setProfileRating(FollowersViewHolder holder, float v) {
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

    private void setSelectedStar(FollowersViewHolder holder, boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        holder.imgStar1.setSelected(b);
        holder.imgStar2.setSelected(b1);
        holder.imgStar3.setSelected(b2);
        holder.imgStar4.setSelected(b3);
        holder.imgStar5.setSelected(b4);
    }
}
