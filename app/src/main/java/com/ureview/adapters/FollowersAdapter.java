package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.FollowModel;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<FollowModel> followModelArrayList;

    public FollowersAdapter(Context context, ArrayList<FollowModel> followModelArrayList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.followModelArrayList = followModelArrayList;
    }

    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder holder, final int position) {
//        holder.txtFollowStatus.setSelected(true);
//        holder.txtFollowStatus.setText("Following");
    }

    @Override
    public int getItemCount() {
        return followModelArrayList.size();
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount;
        private RatingBar ratingBar;
        private CircleImageView imgProfile;
        private ImageView imgClear;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            txtReviewCount = itemView.findViewById(R.id.txtReviewCount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgClear = itemView.findViewById(R.id.imgClear);
        }
    }
}
