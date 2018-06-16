package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.PeopleModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private boolean showViewCount;
    private Context context;
    private IClickListener iClickListener;
    private ArrayList<PeopleModel> peopleArrayList;

    public PeopleAdapter(Context context, ArrayList<PeopleModel> peopleArrayList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.peopleArrayList = peopleArrayList;
    }

    public PeopleAdapter(Context context, ArrayList<PeopleModel> peopleArrayList,
                         IClickListener iClickListener, boolean showViewCount) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.peopleArrayList = peopleArrayList;
        this.showViewCount = showViewCount;
    }

    public void addVideos(ArrayList<PeopleModel> arrList) {
        int lastPosition = peopleArrayList.size() > 0 ? peopleArrayList.size() - 1 : 0;
        peopleArrayList.addAll(arrList);
        notifyItemRangeInserted(lastPosition + 1, arrList.size() - 1);
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PeopleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PeopleViewHolder holder, final int position) {
        PeopleModel peopleModel = peopleArrayList.get(position);

        if (!TextUtils.isEmpty(peopleModel.userImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .fitCenter()
                    .error(R.drawable.ic_profile);

            Glide.with(context)
                    .load(peopleModel.userImage)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_profile);

        if (peopleModel.firstName != null && peopleModel.lastName != null)
            holder.txtName.setText(peopleModel.firstName.concat(" ").concat(peopleModel.lastName));
        if (!TextUtils.isEmpty(peopleModel.userRating))
            holder.ratingBar.setRating(Float.parseFloat(peopleModel.userRating));
        holder.ratingBar.setVisibility(showViewCount ? View.VISIBLE : View.GONE);
//        holder.txtReviewCount.setText("Review Upload : ".concat(String.valueOf(peopleModel.uploadedVideosCount)));
        holder.llViewCount.setVisibility(showViewCount ? View.VISIBLE : View.GONE);
//        holder.ratingBar.setVisibility(View.GONE);
        holder.txtReviewCount.setText("Review Upload : ".concat(String.valueOf(peopleModel.uploadedVideosCount)));
        holder.imgClear.setVisibility(View.GONE);
        if (peopleModel.videoViews != null) {
            holder.txtViewNo.setText(peopleModel.videoViews);
            holder.txtViews.setText(Integer.parseInt(peopleModel.videoViews) == 1 ? "View" : "Views");
        }
//        holder.txtReviewCount.setVisibility(View.GONE);
        if (peopleModel.userId.equalsIgnoreCase(LocalStorage.getInstance(context).getString(LocalStorage.PREF_USER_ID, ""))) {
            holder.txtFollowStatus.setVisibility(View.GONE);
        } else {
            holder.txtFollowStatus.setVisibility(View.VISIBLE);
            holder.txtFollowStatus.setText(TextUtils.isEmpty(peopleModel.followStatus) ||
                    peopleModel.followStatus.equalsIgnoreCase("follow") ? "Follow" : "Unfollow");
            holder.txtFollowStatus.setSelected(!(TextUtils.isEmpty(peopleModel.followStatus) ||
                    peopleModel.followStatus.equalsIgnoreCase("follow")));
        }
        holder.txtFollowStatus.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    public void addItems(ArrayList<PeopleModel> arrList) {
        peopleArrayList = arrList;
        notifyDataSetChanged();
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount, txtViewNo, txtViews;
        private RatingBar ratingBar;
        private CircleImageView imgProfile;
        private RelativeLayout relBody;
        private LinearLayout llViewCount;
        private ImageView imgClear;

        public PeopleViewHolder(View itemView) {
            super(itemView);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            llViewCount = itemView.findViewById(R.id.llViewCount);
            txtReviewCount = itemView.findViewById(R.id.txtReviewCount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgClear = itemView.findViewById(R.id.imgClear);
            relBody = itemView.findViewById(R.id.relBody);
            txtViewNo = itemView.findViewById(R.id.txtViewNo);
            txtViews = itemView.findViewById(R.id.txtViews);
        }
    }
}
