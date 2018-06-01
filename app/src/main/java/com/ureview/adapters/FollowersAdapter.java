package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.utils.views.CustomTextView;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {

    private Context context;

    public FollowersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FollowersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FollowersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_followers, parent, false));
    }

    @Override
    public void onBindViewHolder(FollowersViewHolder holder, final int position) {
//        holder.txtFollowStatus.setSelected(true);
//        holder.txtFollowStatus.setText("Following");
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class FollowersViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtFollowStatus;

        public FollowersViewHolder(View itemView) {
            super(itemView);
            txtFollowStatus = itemView.findViewById(R.id.txtFollowStatus);
        }
    }
}
