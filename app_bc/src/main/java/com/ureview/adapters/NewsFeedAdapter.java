package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.utils.views.CustomTextView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {

    private Context context;
    IClickListener iClickListener;

    public NewsFeedAdapter(Context context, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, final int position) {
        if (position == 4) {
            holder.dividerView.setVisibility(View.GONE);
        }
        holder.relItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) iClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile, imgLocation;
        private CustomTextView txtFollowStatus, txtName, txtLoc, txtLocType, txtSynth,
                txtViewCount, txtShareCount, txtLocBtm, txtRatingsNo;
        private View dividerView;
        private RelativeLayout relItem;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            relItem = itemView.findViewById(R.id.relItem);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
            txtName = itemView.findViewById(R.id.txtName);
            txtLoc = itemView.findViewById(R.id.txtLoc);
            txtLocType = itemView.findViewById(R.id.txtLocType);
            txtSynth = itemView.findViewById(R.id.txtSynth);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtShareCount = itemView.findViewById(R.id.txtShareCount);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }
}
