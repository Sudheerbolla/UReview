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
import android.widget.LinearLayout;
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

    public PeopleAdapter(Context context, ArrayList<PeopleModel> peopleArrayList, IClickListener iClickListener, boolean showViewCount) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.peopleArrayList = peopleArrayList;
        this.showViewCount = showViewCount;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PeopleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_people, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PeopleViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        PeopleModel peopleModel = peopleArrayList.get(position);

        if (!TextUtils.isEmpty(peopleModel.userImage)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .fitCenter()
                    .error(R.drawable.ic_user_placeholder);

            Glide.with(context)
                    .load(peopleModel.userImage)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_user_placeholder);

        if (peopleModel.firstName != null && peopleModel.lastName != null)
            holder.txtName.setText(peopleModel.firstName.concat(" ").concat(peopleModel.lastName));
        if (!TextUtils.isEmpty(peopleModel.userRating))
            setProfileRating(holder, Float.parseFloat(peopleModel.userRating));
        holder.llViewCount.setVisibility(showViewCount ? View.VISIBLE : View.GONE);
        holder.txtReviewCount.setText("Review Upload : ".concat(String.valueOf(peopleModel.uploadedVideosCount)));
        holder.imgClear.setVisibility(View.GONE);
        if (peopleModel.videoViews != null) {
            holder.txtViewNo.setText(peopleModel.videoViews);
            holder.txtViews.setText(Integer.parseInt(peopleModel.videoViews) == 1 ? "View" : "Views");
        }
        if (peopleModel.userId.equalsIgnoreCase(LocalStorage.getInstance(context).getString(LocalStorage.PREF_USER_ID, ""))) {
            holder.txtFollowStatus.setVisibility(View.GONE);
        } else {
            holder.txtFollowStatus.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(peopleModel.followStatus)) {
                holder.txtFollowStatus.setText("Follow");
                holder.txtFollowStatus.setSelected(false);
            } else {
                holder.txtFollowStatus.setText("Unfollow");
                holder.txtFollowStatus.setSelected(true);
            }
        }
        holder.txtFollowStatus.setOnClickListener(view -> {
            if (iClickListener != null) iClickListener.onClick(view, position);
        });
        holder.relBody.setOnClickListener(view -> {
            if (iClickListener != null) iClickListener.onClick(view, position);
        });
    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    public void addItems(ArrayList<PeopleModel> arrList) {
        this.peopleArrayList.clear();
        this.peopleArrayList.addAll(arrList);
        notifyDataSetChanged();
    }

    class PeopleViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus, txtName, txtReviewCount, txtViewNo, txtViews;
        private CircleImageView imgProfile;
        private RelativeLayout relBody;
        private LinearLayout llViewCount;
        private ImageView imgClear, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;

        PeopleViewHolder(View itemView) {
            super(itemView);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            llViewCount = itemView.findViewById(R.id.llViewCount);
            txtReviewCount = itemView.findViewById(R.id.txtReviewCount);
            imgStar1 = itemView.findViewById(R.id.imgStar1);
            imgStar2 = itemView.findViewById(R.id.imgStar2);
            imgStar3 = itemView.findViewById(R.id.imgStar3);
            imgStar4 = itemView.findViewById(R.id.imgStar4);
            imgStar5 = itemView.findViewById(R.id.imgStar5);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgClear = itemView.findViewById(R.id.imgClear);
            relBody = itemView.findViewById(R.id.relBody);
            txtViewNo = itemView.findViewById(R.id.txtViewNo);
            txtViews = itemView.findViewById(R.id.txtViews);
        }
    }

    private void setProfileRating(PeopleViewHolder holder, float v) {
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

    private void setSelectedStar(PeopleViewHolder holder, boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        holder.imgStar1.setSelected(b);
        holder.imgStar2.setSelected(b1);
        holder.imgStar3.setSelected(b2);
        holder.imgStar4.setSelected(b3);
        holder.imgStar5.setSelected(b4);
    }
}
