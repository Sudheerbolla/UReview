package com.ureview.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.PlaceArrayAdapter;
import com.ureview.utils.StaticUtils;

public class LocationBottomSheetFragmentSpinner extends BottomSheetDialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private MainActivity mainActivity;
    private BaseApplication baseApplication;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BottomSheetDialog dialog;
    private View rootView;
    private RecyclerView recyclerViewAutoComplete;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
//    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private OnLocationOptionSelected mListener;

    public LocationBottomSheetFragmentSpinner() {
        // Required empty public constructor
    }

    public static LocationBottomSheetFragmentSpinner newInstance(String param1, String param2) {
        LocationBottomSheetFragmentSpinner fragment = new LocationBottomSheetFragmentSpinner();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LocationBottomSheetFragmentSpinner newInstance(int param1) {
        LocationBottomSheetFragmentSpinner fragment = new LocationBottomSheetFragmentSpinner();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        baseApplication = (BaseApplication) mainActivity.getApplication();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        try {
            if (rootView == null) {
                rootView = View.inflate(getContext(), R.layout.fragment_location_radius, null);
            }
            rootView.setLayoutParams(new RelativeLayout.LayoutParams(StaticUtils.screen_width, StaticUtils.screen_height));
            dialog.setContentView(rootView);
            initComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    private void initComponents() {
        recyclerViewAutoComplete = rootView.findViewById(R.id.recyclerViewAutoComplete);
        recyclerViewAutoComplete.setLayoutManager(new LinearLayoutManager(mainActivity));
        ViewCompat.setNestedScrollingEnabled(recyclerViewAutoComplete, true);

        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(mainActivity, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = rootView.findViewById(R.id.edtAutoCompleteSearch);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(mainActivity, android.R.layout.simple_list_item_1, /*BOUNDS_MOUNTAIN_VIEW*/null, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("description: ", "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i("place id: ", "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("places status", "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
//place.getName()

        }
    };


    public void onButtonPressed(String value) {
        if (mListener != null) {
            mListener.locationCallback(value);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationOptionSelected) {
            mListener = (OnLocationOptionSelected) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFilterOptionSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.relByGod:
////                mainActivity.startActivity(new Intent(mainActivity, SelectGodsActivity.class));
//                break;
            default:
                dismiss();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed: ", "Google Places API connection failed with error code: " + connectionResult.getErrorCode());
    }

    public interface OnLocationOptionSelected {
        void locationCallback(String value);
    }

}
