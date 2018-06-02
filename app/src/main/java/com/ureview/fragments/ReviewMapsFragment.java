package com.ureview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.ureview.R;

public class ReviewMapsFragment extends BaseFragment implements OnMapReadyCallback {
    private View rootView;
    private GoogleMap mMap;
    private FragmentActivity myContext;

    public static ReviewMapsFragment newInstance() {
        return new ReviewMapsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_review_maps, container, false);
        MapFragment mapFragment = (MapFragment) myContext.getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.play_button_copy)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        addCustomeMarker();
    }

    private void addCustomeMarker() {
        LatLng newLatLngTemp = new LatLng(17.4452, 78.3766);

        MarkerOptions options = new MarkerOptions();
        IconGenerator iconFactory = new IconGenerator(myContext);
        iconFactory.setRotation(0);
        iconFactory.setBackground(null);

        View view = View.inflate(myContext, R.layout.map_marker_text, null);
//        TextView tvVendorTitle;
//        tvVendorTitle = (TextView) view.findViewById(R.id.tv_vendor_title);
//        tvVendorTitle.setText(myDealsList.get(j).rating);
        iconFactory.setContentView(view);

        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        options.position(newLatLngTemp);
//        options.snippet(String.valueOf(j));

        Marker mapMarker = mMap.addMarker(options);
        LatLng latlngOne = new LatLng(17.4452, 78.3766);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngOne, 16));


    }
}
