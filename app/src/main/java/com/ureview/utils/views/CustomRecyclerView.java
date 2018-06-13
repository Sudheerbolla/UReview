package com.ureview.utils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ureview.R;
import com.ureview.utils.views.recyclerView.Paginate;
import com.ureview.utils.views.recyclerView.RecyclerPaginate;

public class CustomRecyclerView extends RecyclerView {

    private Context context;
    private int loadMoreType;

    private boolean needPagination = true;

    private int listOrientation;
    private int listType;
    private int layoutOrientation;
    private boolean addLoadingRow = true;
    private boolean isNotParent = true;
    private boolean customLoadingListItem = false;
    private int grid_span = 1;


    /**
     * Constructor with 1 parameter context and attrs
     *
     * @param context
     */
    public CustomRecyclerView(Context context) {
        super(context);
    }

    /**
     * Constructor with 2 parameters context and attrs
     *
     * @param context
     * @param attrs
     */
    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initCustomText(context, attrs);
    }

    /**
     * Initializes all the attributes and respective methods are called based on the attributes
     *
     * @param context
     * @param attrs
     */
    private void initCustomText(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomRecyclerView);

        needPagination = ta.getBoolean(R.styleable.CustomRecyclerView_pagination, false);
        addLoadingRow = ta.getBoolean(R.styleable.CustomRecyclerView_loadmore_visibility, true);
        isNotParent = ta.getBoolean(R.styleable.CustomRecyclerView_is_not_parent, true);
        loadMoreType = ta.getInt(R.styleable.CustomRecyclerView_loadmore_type, 0);
        listOrientation = ta.getInt(R.styleable.CustomRecyclerView_list_orientation, 0);
        listType = ta.getInt(R.styleable.CustomRecyclerView_list_type, 0);
        grid_span = ta.getInt(R.styleable.CustomRecyclerView_gird_span, 3);

        /**
         * A custom view uses isInEditMode() to determine whether or not it is being rendered inside the editor
         * and if so then loads test data instead of real data.
         */
        LayoutManager layoutManager = null;
        if (!isInEditMode()) {
            switch (listOrientation) {
                case 0:
                    layoutOrientation = OrientationHelper.VERTICAL;
                    break;
                case 1:
                    layoutOrientation = OrientationHelper.HORIZONTAL;
                    break;
                default:
                    layoutOrientation = OrientationHelper.VERTICAL;
            }

            switch (listType) {
                case 0:
                    layoutManager = new LinearLayoutManager(context, layoutOrientation, false);
                    break;
                case 1:
                    layoutManager = new GridLayoutManager(context, grid_span, layoutOrientation, false);
                    break;
                case 2:
                    layoutManager = new StaggeredGridLayoutManager(grid_span, layoutOrientation);
                    break;
            }

            switch (loadMoreType) {
                case 0:
                    setLoadMoreType(layoutManager, false);
                    break;
                case 1:
                    setLoadMoreType(layoutManager, true);
                    break;
                default:
                    break;
            }

            setLayoutManager(layoutManager);
        }
    }

    private void setLoadMoreType(LayoutManager layoutManager, boolean isLoadMoreFromBottom) {
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).setReverseLayout(isLoadMoreFromBottom);
        } else {
            ((StaggeredGridLayoutManager) layoutManager).setReverseLayout(isLoadMoreFromBottom);
        }
    }

    public void setListPagination(Paginate.Callbacks callbacks) {
        if (needPagination) {
            Paginate.with(this, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .addLoadingListItem(addLoadingRow)
                    .isNotParent(isNotParent)
                    .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                    .setLoadingListItemSpanSizeLookup(new RecyclerPaginate.LoadingListItemSpanLookup() {
                        @Override
                        public int getSpanSize() {
                            return grid_span;
                        }
                    })
                    .build();
        }
    }

    private class CustomLoadingListItemCreator implements RecyclerPaginate.LoadingListItemCreator {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            VH vh = (VH) holder;
//            vh.tvLoading.setText(String.format("Total items loaded: %d.\nLoading more...", adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading_text);
        }
    }

    public static class SimpleDividerItemDecoration extends ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context, int white) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
            GradientDrawable gradientDrawable = (GradientDrawable) mDivider;
            gradientDrawable.setColor(context.getResources().getColor(white));
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                LayoutParams params = (LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}