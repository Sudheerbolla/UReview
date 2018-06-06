package com.ureview.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.fragments.FollowersFragment;
import com.ureview.fragments.HomeCompleteFragment;
import com.ureview.fragments.HomeFragment;
import com.ureview.fragments.LocationBottomSheetFragment;
import com.ureview.fragments.LocationRadiusFragment;
import com.ureview.fragments.NotificationsFragment;
import com.ureview.fragments.ProfileFragment;
import com.ureview.fragments.ReviewMapsFragment;
import com.ureview.fragments.SearchFragment;
import com.ureview.fragments.SettingsFragment;
import com.ureview.fragments.UploadVideoCompletedFragment;
import com.ureview.fragments.UploadVideoFragment;
import com.ureview.fragments.VideoReviewFragment;
import com.ureview.utils.Constants;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.RuntimePermissionUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocationListener, LocationBottomSheetFragment.OnLocationOptionSelected {

    public CustomTextView txtTitle, txtRight, txtLeft;
    public ImageView imgBack, imgLoc, imgNotf, imgEdit, imgSearch, imgClose;
    public EditText edtText;
    public RelativeLayout rlEditView;
    public ImageView imgHome, imgSearchB, imgVideo, imgProfile, imgSettings,
            imgHomeView, imgSearchView, imgProfileView, imgSettingsView;
    public static Location mLastLocation;
    public LocationManager mLocationManager;
    private boolean isLocationAlreadyFetched = false;
    private LocationBottomSheetFragment locationBottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalStorage.getInstance(this).putBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, true);
        checkPermissions();
    }

    private void checkPermissions() {
        if (RuntimePermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RuntimePermissionUtils.requestForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.LOCATION_PERMISSION);
        } else {
            initBottomBar();
            initTopBar();
            proceedWithFlow();
            getCurrentLocation();
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(isGPSEnabled || isNetworkEnabled))
            StaticUtils.showToast(this, "Error fetching location from the provider");
        else {
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (mLastLocation != null && !isLocationAlreadyFetched) {
            setTextToAddress();
        }
    }

    public void setTextToAddress() {
        if (mLastLocation != null)
            txtLeft.setText(StaticUtils.getAddress(this, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (StaticUtils.isAllPermissionsGranted(grantResults)) {
            initBottomBar();
            initTopBar();
            proceedWithFlow();
            getCurrentLocation();
        } else {
            StaticUtils.showToast(this, "Location permission is mandatory to access your location");
            checkPermissions();
        }
    }

    private void proceedWithFlow() {
        setToolBar("", "Mirpur 10, Dhaka", "", true, false, true, false, false);
        replaceFragmentWithoutAnimation(HomeFragment.newInstance(), R.id.mainContainer, false);
    }

    public void setToolBar(String title, String leftText, String rightText, boolean showLoc, boolean showBack, boolean showNotf, boolean showEdtView, boolean showEdt) {
        txtTitle.setVisibility(!TextUtils.isEmpty(title) ? View.VISIBLE : View.GONE);
        txtTitle.setText(title);
        txtLeft.setVisibility(!TextUtils.isEmpty(leftText) ? View.VISIBLE : View.GONE);
        txtLeft.setText(leftText);
        txtRight.setVisibility(!TextUtils.isEmpty(rightText) ? View.VISIBLE : View.GONE);
        txtRight.setText(rightText);
        imgLoc.setVisibility(showLoc ? View.VISIBLE : View.GONE);
        imgBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
        imgNotf.setVisibility(showNotf ? View.VISIBLE : View.GONE);
        rlEditView.setVisibility(showEdtView ? View.VISIBLE : View.GONE);
        imgEdit.setVisibility(showEdt ? View.VISIBLE : View.GONE);
    }

    public void initTopBar() {
        txtTitle = findViewById(R.id.txtTitle);
        txtRight = findViewById(R.id.txtRight);
        txtLeft = findViewById(R.id.txtLeft);
        imgBack = findViewById(R.id.imgBack);
        imgLoc = findViewById(R.id.imgLoc);
        imgNotf = findViewById(R.id.imgNotf);
        imgEdit = findViewById(R.id.imgEdit);
        imgSearch = findViewById(R.id.imgSearch);
        imgClose = findViewById(R.id.imgClose);
        edtText = findViewById(R.id.edtText);
        rlEditView = findViewById(R.id.rlEditView);
        imgNotf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationsFragment();
            }
        });
        txtLeft.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void initBottomBar() {
        imgHome = findViewById(R.id.imgHome);
        imgSearchB = findViewById(R.id.imgSearchB);
        imgVideo = findViewById(R.id.imgVideo);
        imgProfile = findViewById(R.id.imgProfile);
        imgSettings = findViewById(R.id.imgSettings);

        imgHomeView = findViewById(R.id.imgHomeView);
        imgSearchView = findViewById(R.id.imgSearchView);
        imgProfileView = findViewById(R.id.imgProfileView);
        imgSettingsView = findViewById(R.id.imgSettingsView);

        imgHome.setSelected(true);
        imgHomeView.setVisibility(View.VISIBLE);

        findViewById(R.id.llHome).setOnClickListener(this);
        findViewById(R.id.llSearch).setOnClickListener(this);
        findViewById(R.id.llVideo).setOnClickListener(this);
        findViewById(R.id.llProfile).setOnClickListener(this);
        findViewById(R.id.llSettings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        clearBackStackCompletely();
        switch (view.getId()) {
            case R.id.llHome:
                setSelectedTab(imgHome, imgHomeView);
                setHomeFragment();
                break;
            case R.id.llSearch:
                setSelectedTab(imgSearchB, imgSearchView);
                setSearchFragment();
                break;
            case R.id.llVideo:
                setSelectedTab(imgVideo, null);
//                setUploadVideoFragment();
                openVideoIntent();
                break;
            case R.id.llProfile:
                setSelectedTab(imgProfile, imgProfileView);
                setProfileFragment();
                break;
            case R.id.llSettings:
                setSelectedTab(imgSettings, imgSettingsView);
                setSettingsFragment();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtLeft:
                showLocationBottomSheet();
                break;
            default:
                break;
        }
    }

    private void showLocationBottomSheet() {
        locationBottomSheetFragment = new LocationBottomSheetFragment();
        locationBottomSheetFragment.show(getSupportFragmentManager(), locationBottomSheetFragment.getTag());
    }

    private void openVideoIntent() {
//        Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
//        startActivityForResult(captureVideoIntent,VIDEO_CAPTURED);
        startActivity(new Intent(this, VideoRecorder.class));
    }

    private void setSelectedTab(ImageView selectedView, ImageView imgH) {
        imgHome.setSelected(false);
        imgSearchB.setSelected(false);
        imgVideo.setSelected(false);
        imgProfile.setSelected(false);
        imgSettings.setSelected(false);

        imgHomeView.setVisibility(View.GONE);
        imgSearchView.setVisibility(View.GONE);
        imgProfileView.setVisibility(View.GONE);
        imgSettingsView.setVisibility(View.GONE);

        selectedView.setSelected(true);
        if (imgH != null)
            imgH.setVisibility(View.VISIBLE);
    }

    private void setReviewMapFragment() {
        setToolBar("Review Maps", "", "", false, true, true, false, false);
        replaceFragment(ReviewMapsFragment.newInstance(), R.id.mainContainer);
    }

    public void setHomeFragment() {
        setToolBar("", "Mirpur 10, Dhaka", "", true, false,
                true, false, false);
        replaceFragment(HomeFragment.newInstance(), R.id.mainContainer);
    }

    public void setHomeFragmentComplete() {
        setToolBar("", "Mirpur 10, Dhaka", "", true, false,
                true, false, false);
//        replaceFragment(HomeFragment.newInstance(), R.id.mainContainer);
        replaceFragment(HomeCompleteFragment.newInstance(), R.id.mainContainer);
    }

    private void setLocationRadiusFragment() {
        setToolBar("", "Mirpur 10, Dhaka", "", true, false,
                true, false, false);
        replaceFragment(LocationRadiusFragment.newInstance(), R.id.mainContainer);
    }

    private void setSearchFragment() {
        setToolBar("", "", "", false, false,
                false, true, false);
        replaceFragment(SearchFragment.newInstance(), R.id.mainContainer);
    }

    private void setNotificationsFragment() {
        setToolBar("Notifications", "", "", false, true, false, false, false);
        replaceFragment(NotificationsFragment.newInstance(), true, R.id.mainContainer);
    }

    private void setSettingsFragment() {
        setToolBar("Settings", "", "", false, false, true, false, false);
        replaceFragment(SettingsFragment.newInstance(), R.id.mainContainer);
    }

    public void setFollowersFragment() {
        setToolBar("Followers", "", "", false, true,
                true, false, false);
        replaceFragment(FollowersFragment.newInstance(), R.id.mainContainer);
    }

    public void setUploadVideoFragment() {
        setToolBar("Upload video", "", "", false, true,
                true, false, false);
        replaceFragment(UploadVideoFragment.newInstance(), R.id.mainContainer);
    }

    public void setUploadVideoCompletedFragment() {
        setToolBar("Upload video", "", "", false, true,
                true, false, false);
        replaceFragment(UploadVideoCompletedFragment.newInstance(), R.id.mainContainer);
    }

    public void setVideoReviewFragment() {
        setToolBar("Video Review", "", "", false, true,
                true, false, false);
        replaceFragment(VideoReviewFragment.newInstance(), R.id.mainContainer);
    }

    private void setProfileFragment() {
        replaceFragment(ProfileFragment.newInstance(), R.id.mainContainer);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null && !isLocationAlreadyFetched) {
            setTextToAddress();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(this);
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void locationCallback(String value) {

    }

}
