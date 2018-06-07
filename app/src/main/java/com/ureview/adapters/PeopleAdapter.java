package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.PeopleModel;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<PeopleModel> peopleArrayList;

    public PeopleAdapter(Context context, ArrayList<PeopleModel> peopleArrayList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.peopleArrayList = peopleArrayList;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PeopleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, final int position) {
        PeopleModel peopleModel = peopleArrayList.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        Glide.with(context).load(peopleModel.userImage).into(holder.imgProfile);
        holder.txtName.setText(peopleModel.firstName.concat(" ").concat(peopleModel.lastName));
        if (!TextUtils.isEmpty(peopleModel.userRating))
            holder.ratingBar.setRating(Float.parseFloat(peopleModel.userRating));
        holder.txtReviewCount.setText("Review Upload : ".concat(String.valueOf(peopleModel.uploadedVideosCount)));
        holder.imgClear.setVisibility(View.GONE);
        holder.txtFollowStatus.setText(TextUtils.isEmpty(peopleModel.followStatus) ? "Follow" : "Following");
        holder.txtFollowStatus.setSelected(!TextUtils.isEmpty(peopleModel.followStatus));
    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount;
        private RatingBar ratingBar;
        private CircleImageView imgProfile;
        private ImageView imgClear;

        public PeopleViewHolder(View itemView) {
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
