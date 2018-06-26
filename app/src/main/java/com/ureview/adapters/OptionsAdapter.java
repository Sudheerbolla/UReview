package com.ureview.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.listeners.ISearchClickListener;
import com.ureview.utils.views.CustomTextView;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.IssueViewHolder> {

    private final Dialog alertDialog;
    private String[] options;
    private ISearchClickListener iClickListener;

    public OptionsAdapter(String[] options, ISearchClickListener iClickListener, Dialog alertDialog) {
        this.options = options;
        this.iClickListener = iClickListener;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_options, null);
        return new IssueViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txtOption.setText(options[position]);
        holder.txtOption.setOnClickListener(view -> {
            if (iClickListener != null) {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
                iClickListener.onClick(options[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    public class IssueViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtOption;

        private IssueViewHolder(View itemView) {
            super(itemView);
            txtOption = itemView.findViewById(R.id.txtOption);
        }
    }
}
