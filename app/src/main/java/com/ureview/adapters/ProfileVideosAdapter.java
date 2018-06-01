package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ureview.R;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.CategoryViewHolder> {

    private Context context;

    public ProfileVideosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_video, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
//        int width = StaticUtils.screen_width - ((int) (Resources.getSystem().getDimension(R.dimen.pad_38)));
//        holder.imgVideo.setLayoutParams(new ViewGroup.LayoutParams((int) (width / 3), (int) (width / 3)));
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgVideo;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
        }
    }
}
