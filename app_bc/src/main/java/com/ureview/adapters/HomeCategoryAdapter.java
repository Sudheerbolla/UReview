package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.utils.views.CustomTextView;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder> {

    private Context context;

    public HomeCategoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        if (position == 0) {
//            holder.dividerView.setVisibility(View.GONE);
        }
        holder.txtCategory.setSelected(position == 0);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtCategory;
//        private View dividerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
//            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }
}
