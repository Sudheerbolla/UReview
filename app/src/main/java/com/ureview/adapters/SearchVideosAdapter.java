package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ureview.R;
import com.ureview.utils.views.CustomTextView;

public class SearchVideosAdapter extends RecyclerView.Adapter<SearchVideosAdapter.NewsFeedViewHolder> {

    private Context context;

    public SearchVideosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_video, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, final int position) {
        if (position == 4) {
            holder.dividerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgLocation;
        private CustomTextView txtLocType, txtSynth,
                txtViewCount, txtShareCount, txtLocBtm, txtRatingsNo;
        private View dividerView;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            txtLocType = itemView.findViewById(R.id.txtLocType);
            txtSynth = itemView.findViewById(R.id.txtSynth);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtShareCount = itemView.findViewById(R.id.txtShareCount);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }
}
