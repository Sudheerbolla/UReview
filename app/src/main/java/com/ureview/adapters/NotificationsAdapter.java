package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.NotificationsModel;
import com.ureview.utils.swipeUtils.CustomSwipeView;
import com.ureview.utils.swipeUtils.ViewBinderHelper;
import com.ureview.utils.views.CircleImageView;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private Context context;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ArrayList<NotificationsModel> notificationsModelArrayList;
    private IClickListener iClickListener;

    public NotificationsAdapter(Context context, ArrayList<NotificationsModel> notificationsModelArrayList, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
        this.notificationsModelArrayList = notificationsModelArrayList;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notifications, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, final int position) {
        NotificationsModel notificationsModel = notificationsModelArrayList.get(position);
        viewBinderHelper.bind(holder.swipeLayout, holder.getAdapterPosition() + "");
        viewBinderHelper.closeAll();
        holder.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(view, position);
                }
            }
        });
        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null) {
                    iClickListener.onClick(view, position);
                }
            }
        });
        holder.txtTime.setText(notificationsModel.message_date);
        holder.txtName.setText(notificationsModel.user_name);
        holder.txtAction.setText(notificationsModel.message);

        if (!TextUtils.isEmpty(notificationsModel.user_image)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .fitCenter()
                    .error(R.drawable.ic_user_placeholder);

            Glide.with(context)
                    .load(notificationsModel.user_image)
                    .apply(options)
                    .into(holder.imgProfile);
        } else holder.imgProfile.setImageResource(R.drawable.ic_user_placeholder);

    }

    @Override
    public int getItemCount() {
        return notificationsModelArrayList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        private CustomSwipeView swipeLayout;
        private CircleImageView imgProfile;
        private CustomTextView txtName, txtAction, txtTime;
        private RelativeLayout rlMain, rlDelete;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            rlMain = itemView.findViewById(R.id.rlMain);
            rlDelete = itemView.findViewById(R.id.rlDelete);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtAction = itemView.findViewById(R.id.txtAction);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
