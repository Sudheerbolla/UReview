package com.ureview.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.models.CountriesModel;

import java.util.ArrayList;

public class CountrySelectionAdapter extends RecyclerView.Adapter<CountrySelectionAdapter.IssueViewHolder> {

    private ArrayList<CountriesModel> searchList;
    private IClickListener iClickListener;

    public CountrySelectionAdapter(ArrayList<CountriesModel> searchList, IClickListener iClickListener) {
        this.searchList = searchList;
        this.iClickListener = iClickListener;
    }

    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country_selection, null);
        return new IssueViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder holder, final int position) {
        CountriesModel countriesModel = searchList.get(position);
        holder.txtCountryCode.setText("+" + countriesModel.countryCode);
        holder.txtCountryName.setText(countriesModel.countryName);
        holder.llSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iClickListener != null)
                    iClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class IssueViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCountryName, txtCountryCode;
        private LinearLayout llSearchItem;

        private IssueViewHolder(View itemView) {
            super(itemView);
            txtCountryName = itemView.findViewById(R.id.txtCountryName);
            txtCountryCode = itemView.findViewById(R.id.txtCountryCode);
            llSearchItem = itemView.findViewById(R.id.llSearchItem);
        }
    }
}
