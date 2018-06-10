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

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PeopleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PeopleViewHolder holder, final int position) {
        PeopleModel peopleModel = peopleArrayList.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        Glide.with(context).load(peopleModel.userImage).into(holder.imgProfile);
        holder.txtName.setText(peopleModel.firstName.concat(" ").concat(peopleModel.lastName));
//        if (!TextUtils.isEmpty(peopleModel.userRating))
//            holder.ratingBar.setRating(Float.parseFloat(peopleModel.userRating));
        holder.ratingBar.setVisibility(View.GONE);
//        holder.txtReviewCount.setText("Review Upload : ".concat(String.valueOf(peopleModel.uploadedVideosCount)));
        holder.llViewCount.setVisibility(showViewCount ? View.VISIBLE : View.GONE);
        holder.imgClear.setVisibility(View.GONE);
        holder.txtReviewCount.setVisibility(View.GONE);
        holder.txtFollowStatus.setText(TextUtils.isEmpty(peopleModel.followStatus) ? "Follow" : "Following");
        holder.txtFollowStatus.setSelected(!TextUtils.isEmpty(peopleModel.followStatus));
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

    public class PeopleViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount;
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
        }
    }
}
