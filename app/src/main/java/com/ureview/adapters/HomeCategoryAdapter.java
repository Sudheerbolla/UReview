package com.ureview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.CategoryModel;
import com.ureview.utils.views.CustomTextView;

import java.util.ArrayList;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder> {

    private final IClickListener iClickListener;
    private Context context;
    private ArrayList<CategoryModel> categoryList = new ArrayList<>();
    private int height, originalHeight;
    private boolean heightCalculated;

    public HomeCategoryAdapter(Context context, IClickListener iClickListener) {
        this.context = context;
        this.iClickListener = iClickListener;
    }

    public void addCategories(ArrayList<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        CategoryModel categoryModel = categoryList.get(position);
        holder.txtCategory.setText(categoryModel.categoryName);
        holder.txtCategory.setSelected(categoryModel.isSelected);

        Glide.with(context).load(categoryModel.isSelected ? categoryModel.categoryActiveBgImage : categoryModel.categoryBgImage).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(holder.imgCatBg);
        Glide.with(context).load(categoryModel.isSelected ? categoryModel.categoryActiveImage : categoryModel.categoryImage)
                .into(holder.imgCat);

        holder.txtCategory.setOnClickListener(view -> {
            if (iClickListener != null)
                iClickListener.onClick(holder.txtCategory, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtCategory;
        private ImageView imgCat, imgCatBg;

        CategoryViewHolder(View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgCatBg = itemView.findViewById(R.id.imgCatBg);
            imgCat = itemView.findViewById(R.id.imgCat);
        }
    }
}
