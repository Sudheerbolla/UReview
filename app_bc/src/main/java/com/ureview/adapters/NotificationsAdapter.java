package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.utils.swipeUtils.CustomSwipeView;
import com.ureview.utils.swipeUtils.ViewBinderHelper;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private Context context;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public NotificationsAdapter(Context context) {
        this.context = context;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notifications, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, final int position) {
        viewBinderHelper.bind(holder.swipeLayout, holder.getAdapterPosition() + "");
        viewBinderHelper.closeAll();
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        private CustomSwipeView swipeLayout;
        private RelativeLayout rlMain;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            rlMain = itemView.findViewById(R.id.rlMain);
        }
    }
}
