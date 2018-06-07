package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.VideoViewsModel;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class VideoViewRankingAdapter extends RecyclerView.Adapter<VideoViewRankingAdapter.FollowersViewHolder> {

    private Context context;
    private IClickListener iClickListener;
    private ArrayList<VideoViewsModel> videoViewsModelArrayList;

    public VideoViewRankingAdapter(Context context, ArrayList<VideoViewsModel> videoViewsModelArrayList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.videoViewsModelArrayList = videoViewsModelArrayList;
    }

    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_rankings, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder holder, final int position) {
        VideoViewsModel videoViewsModel = videoViewsModelArrayList.get(position);
        holder.txtName.setText(videoViewsModel.name);
        holder.txtSno.setText(videoViewsModel.rank);
        holder.txtViews.setText(videoViewsModel.video_views);
        holder.relBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoViewsModelArrayList.size();
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtSno, txtName, txtViews;
        private CircleImageView imgProfile;
        private RelativeLayout relBody;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            txtSno = itemView.findViewById(R.id.txtSno);
            txtName = itemView.findViewById(R.id.txtName);
            txtViews = itemView.findViewById(R.id.txtViews);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            relBody = itemView.findViewById(R.id.relBody);
        }
    }
}