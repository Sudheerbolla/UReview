package com.ureview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.adapters.PlacesAutoCompleteAdapter;
import com.ureview.listeners.IClickListener;
import com.ureview.models.FilterModel;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class LocationFilterFragment extends DialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, IClickListener {

    private MainActivity mainActivity;
    private BaseApplication baseApplication;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private RecyclerView recyclerViewAutoComplete;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private CustomEditText mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private ImageView imgClear, imgBack;
    private CustomTextView txtDone, txtUseCurrentLocation;
    private RangeSeekBar<Integer> rangeSeekbar;
    private String selLat, selLong, addressLine;

//    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//        }
//    }

    private OnLocationFilterOptionSelected mListener;

    public LocationFilterFragment() {
        // Required empty public constructor
    }

    public static LocationFilterFragment newInstance(String param1, String param2) {
        LocationFilterFragment fragment = new LocationFilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LocationFilterFragment newInstance(int param1) {
        LocationFilterFragment fragment = new LocationFilterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mainActivity = (MainActivity) getActivity();
        baseApplication = (BaseApplication) mainActivity.getApplication();
        if (mGoogleApiClient == null) mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(mainActivity, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.fragment_location_radius, null);
        }
        initComponents();
        return rootView;
    }

    private void initComponents() {
        setReferences();
        setAdapter();
        setListeners();
    }

    private void setReferences() {
        imgClear = rootView.findViewById(R.id.clear);
        rangeSeekbar = rootView.findViewById(R.id.rangeSeekbar);
        mAutocompleteTextView = rootView.findViewById(R.id.edtAutoCompleteSearch);
        recyclerViewAutoComplete = rootView.findViewById(R.id.recyclerViewAutoComplete);
        imgBack = rootView.findViewById(R.id.imgBack);
        txtDone = rootView.findViewById(R.id.txtDone);
        txtUseCurrentLocation = rootView.findViewById(R.id.txtUseCurrentLocation);
    }

    private void setListeners() {
        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Log.e("error", "client not connected");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
        imgClear.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        txtDone.setOnClickListener(this);
        txtUseCurrentLocation.setOnClickListener(this);

        rangeSeekbar.setRangeValues(1, 100);
        rangeSeekbar.setSelectedMinValue(1);
        rangeSeekbar.setSelectedMaxValue(50);

        rangeSeekbar.setTextAboveThumbsColorResource(R.color.app_color_dark);
    }

    private void setAdapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(mainActivity, mGoogleApiClient, this);
        recyclerViewAutoComplete.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerViewAutoComplete.setAdapter(mAutoCompleteAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewAutoComplete, true);
    }

    public void onButtonPressed(boolean isUseCurrentLocation) {
        FilterModel filterModel = new FilterModel();
        filterModel.isUseCurrentLocation = isUseCurrentLocation;
        filterModel.addressLine = addressLine;
        filterModel.locationLat = selLat == null ? String.valueOf(MainActivity.mLastLocation.getLatitude()) : selLat;
        filterModel.locationLng = selLong == null ? String.valueOf(MainActivity.mLastLocation.getLongitude()) : selLong;
        filterModel.locationMax = rangeSeekbar.getSelectedMaxValue().toString();
        filterModel.locationMin = rangeSeekbar.getSelectedMinValue().toString();
        if (mListener != null) {
            mListener.locationCallback(filterModel);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationFilterOptionSelected) {
            mListener = (OnLocationFilterOptionSelected) context;
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
            case R.id.clear:
                mAutocompleteTextView.setText("");
                break;
            case R.id.txtUseCurrentLocation:
                onButtonPressed(true);
                dismissAllowingStateLoss();
                break;
            case R.id.txtDone:
                onButtonPressed(false);
                dismissAllowingStateLoss();
                break;
            case R.id.imgBack:
                dismissAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }

//        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
//        mainActivity.setToolBar("", "", "", true, true, true, false, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.stopAutoManage(mainActivity);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mAutoCompleteAdapter != null) mAutoCompleteAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mAutoCompleteAdapter != null) mAutoCompleteAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed: ", "Google Places API connection failed with error code: " + connectionResult.getErrorCode());
    }

    @Override
    public void onClick(View view, int position) {
        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);
        Log.i("TAG", "Autocomplete item selected: " + item.description);
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (places.getCount() == 1) {
                    selLat = String.valueOf(places.get(0).getLatLng().latitude);
                    selLong = String.valueOf(places.get(0).getLatLng().longitude);
                    addressLine = places.get(0).getAddress().toString();
                    onButtonPressed(false);
                    dismissAllowingStateLoss();
                } else {
                    StaticUtils.showToast(mainActivity, "something went wrong");
                }
            }
        });
        Log.i("TAG", "Clicked: " + item.description);
        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    public interface OnLocationFilterOptionSelected {
        void locationCallback(FilterModel value);
    }


}
