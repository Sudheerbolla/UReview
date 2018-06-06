package com.ureview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.CategoryModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder> {

    private final IClickListener iClickListener;
    private Context context;
    private ArrayList<CategoryModel> categoryList = new ArrayList<>();

    public HomeCategoryAdapter(Context context, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
    }

    public void addCategories(ArrayList<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, final int position) {
        CategoryModel categoryModel = categoryList.get(position);
        holder.txtCategory.setText(categoryModel.categoryName);
        holder.txtCategory.setSelected(categoryModel.isSelected);
        Glide.with(context).load(categoryModel.isSelected ? categoryModel.categoryActiveBgImage : categoryModel.categoryBgImage)
                .into(holder.imgCatBg);
        Glide.with(context).load(categoryModel.isSelected ? categoryModel.categoryActiveImage : categoryModel.categoryImage)
                .into(holder.imgCat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(holder.imgCatBg, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtCategory;
        private ImageView imgCat, imgCatBg;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgCat = itemView.findViewById(R.id.imgCat);
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
        }
    }
}
