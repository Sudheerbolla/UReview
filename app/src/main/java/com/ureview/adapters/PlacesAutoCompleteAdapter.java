package com.ureview.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ureview.R;
import com.ureview.listeners.IClickListener;
import com.ureview.utils.StaticUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "PlacesAutoCompleteAdapter";
    private ArrayList<PlaceAutocomplete> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;

    private Context mContext;
    private int layout;
    IClickListener iClickListener;

    public PlacesAutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter) {
        mContext = context;
        layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    public PlacesAutoCompleteAdapter(Context context, GoogleApiClient googleApiClient, IClickListener iClickListener) {
        mContext = context;
        this.iClickListener = iClickListener;
        mGoogleApiClient = googleApiClient;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
//                    notifyDataSetChanged();
                }

//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                } else {
//                    // The API did not return any results, invalidate the data set.
//                    //notifyDataSetInvalidated();
//                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("", "Starting autocomplete query for: " + constraint);

            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);

            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                StaticUtils.showToast(mContext, "Error contacting API: " + status.toString());
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount() + " predictions.");

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getFullText(null)));
            }
            autocompletePredictions.release();
            return resultList;
        }
        Log.e("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_google_auto_sugg_search, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    public void setGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, @SuppressLint("RecyclerView") final int position) {
        mPredictionHolder.mPrediction.setText(mResultList.get(position).description);
        mPredictionHolder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClickListener != null) {
                    iClickListener.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResultList != null ? mResultList.size() : 0;
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {
        private TextView mPrediction;
        private RelativeLayout mRow;

        public PredictionHolder(View itemView) {

            super(itemView);
            mPrediction = itemView.findViewById(R.id.address);
            mRow = itemView.findViewById(R.id.predictedRow);
        }

    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}