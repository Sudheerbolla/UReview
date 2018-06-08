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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.fragments.BaseFragment;
import com.ureview.fragments.EditProfileFragment;
import com.ureview.fragments.HomeFragment;
import com.ureview.fragments.LocationBottomSheetFragment;
import com.ureview.fragments.LocationFilterFragment;
import com.ureview.fragments.NotificationsFragment;
import com.ureview.fragments.ProfileFragment;
import com.ureview.fragments.ReviewMapsFragment;
import com.ureview.fragments.SearchFragment;
import com.ureview.fragments.SettingsFragment;
import com.ureview.fragments.UploadVideoCompletedFragment;
import com.ureview.fragments.UploadVideoFragment;
import com.ureview.fragments.VideoReviewFragment;
import com.ureview.models.FilterModel;
import com.ureview.models.LocationModel;
import com.ureview.utils.Constants;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.RuntimePermissionUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocationListener, /*LocationBottomSheetFragment.OnLocationFilterOptionSelected*/LocationFilterFragment.OnLocationFilterOptionSelected {

    public CustomTextView txtTitle, txtRight, txtLeft;
    public ImageView imgBack, imgLoc, imgNotf, imgEdit, imgSearch, imgClose;
    public EditText edtText;
    public RelativeLayout rlEditView, relGenTopBar;
    public ImageView imgHome, imgSearchB, imgVideo, imgProfile, imgSettings,
            imgHomeView, imgSearchView, imgProfileView, imgSettingsView;
    public static Location mLastLocation;
    public LocationManager mLocationManager;
    private boolean isLocationAlreadyFetched = false;
    private LocationBottomSheetFragment locationBottomSheetFragment;
    private int DIALOG_FRAGMENT = 1;
    private FragmentManager fragmentManager;
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        try {
            if (TextUtils.isEmpty(LocalStorage.getInstance(this).getString(LocalStorage.PREF_USER_INFO_DATA, ""))) {
                if (BaseApplication.userInfoModel != null) {
                    String json = BaseApplication.userInfoModel.serialize();
                    LocalStorage.getInstance(this).putString(LocalStorage.PREF_USER_INFO_DATA, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (mLastLocation != null && BaseApplication.locationModel == null) {
            try {
                if (TextUtils.isEmpty(LocalStorage.getInstance(this).getString(LocalStorage.PREF_LOCATION_INFO, ""))) {
                    BaseApplication.locationModel = new LocationModel(this, mLastLocation);
                    String json = BaseApplication.locationModel.serialize();
                    LocalStorage.getInstance(this).putString(LocalStorage.PREF_LOCATION_INFO, json);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mLastLocation != null && !isLocationAlreadyFetched) {
            setTextToAddress();
        }
    }

    public void setTextToAddress() {
        if (mLastLocation != null) {
            txtLeft.setText(StaticUtils.getAddress(this, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            txtLeft.setSelected(true);
        }
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
        setToolBar("", "", "", true, false, true, false, false);
        replaceFragmentWithoutAnimation(HomeFragment.newInstance(), R.id.mainContainer, false);
    }

    public void setToolBar(String title, String leftText, String rightText, boolean showLoc, boolean showBack, boolean showNotf, boolean showEdtView, boolean showEdt) {
        txtTitle.setVisibility(!TextUtils.isEmpty(title) ? View.VISIBLE : View.GONE);
        txtTitle.setText(title);
        txtTitle.setSelected(true);
        txtLeft.setVisibility(!TextUtils.isEmpty(leftText) ? View.VISIBLE : View.GONE);
        txtLeft.setText(leftText);
        txtLeft.setSelected(true);
        txtRight.setVisibility(!TextUtils.isEmpty(rightText) ? View.VISIBLE : View.GONE);
        txtRight.setText(rightText);
        imgLoc.setVisibility(showLoc ? View.VISIBLE : View.GONE);
        imgBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
        imgNotf.setVisibility(showNotf ? View.VISIBLE : View.GONE);
        rlEditView.setVisibility(showEdtView ? View.VISIBLE : View.GONE);
        relGenTopBar.setVisibility(showEdtView ? View.GONE : View.VISIBLE);
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
        relGenTopBar = findViewById(R.id.relGenTopBar);
        rlEditView = findViewById(R.id.rlEditView);

        imgNotf.setOnClickListener(this);
        txtLeft.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
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
                if (!imgHome.isSelected())
                    setHomeFragment();
                break;
            case R.id.llSearch:
                if (!imgSearchB.isSelected())
                    setSearchFragment();
                break;
            case R.id.llVideo:
                if (!imgVideo.isSelected()) {
                    setSelectedTab(imgVideo, null);
//                setUploadVideoFragment();
                    openVideoIntent();
                }
                break;
            case R.id.llProfile:
                if (!imgProfile.isSelected())
                    setProfileFragment();
                break;
            case R.id.llSettings:
                if (!imgSettings.isSelected())
                    setSettingsFragment();
                break;
            case R.id.imgBack:
                popBackStack();
                break;
            case R.id.txtLeft:
//                showLocationBottomSheet();
                setLocationRadiusFragment();
                break;
            case R.id.imgEdit:
                replaceFragment(EditProfileFragment.newInstance(), true, R.id.mainContainer);
                break;
            case R.id.imgNotf:
                setNotificationsFragment();
                break;
            default:
                break;
        }
    }

    private BaseFragment getCurrentFragment() {
        return (BaseFragment) fragmentManager.findFragmentById(R.id.mainContainer);
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = getCurrentFragment();
        if (fragment instanceof HomeFragment) {
            if (backPressed + Constants.BACK_PRESSED_TIME > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                StaticUtils.showToast(this, "Press once again to exit");
            }
            backPressed = System.currentTimeMillis();
        } else {
            if (fragmentManager.getBackStackEntryCount() >= 1) {
                popBackStack();
            } else {
//                super.onBackPressed();
                clearBackStackCompletely();
                setHomeFragment();
            }
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

        imgHomeView.setVisibility(View.INVISIBLE);
        imgSearchView.setVisibility(View.INVISIBLE);
        imgProfileView.setVisibility(View.INVISIBLE);
        imgSettingsView.setVisibility(View.INVISIBLE);

        selectedView.setSelected(true);
        if (imgH != null)
            imgH.setVisibility(View.VISIBLE);
    }

    private void setReviewMapFragment() {
        setToolBar("Review Maps", "", "", false, true, true, false, false);
        replaceFragment(ReviewMapsFragment.newInstance(), R.id.mainContainer);
    }

    public void setHomeFragment() {
        setSelectedTab(imgHome, imgHomeView);
        replaceFragment(HomeFragment.newInstance(), false, R.id.mainContainer);
    }

    private void setLocationRadiusFragment() {
        LocationFilterFragment countrySelectionFragment = LocationFilterFragment.newInstance(0);
//        countrySelectionFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
        countrySelectionFragment.show(getSupportFragmentManager(), "");
    }

    private void setSearchFragment() {
        setSelectedTab(imgSearchB, imgSearchView);
        replaceFragment(SearchFragment.newInstance(), false, R.id.mainContainer);
    }

    private void setNotificationsFragment() {
        replaceFragment(NotificationsFragment.newInstance(), true, R.id.mainContainer);
    }

    private void setSettingsFragment() {
        setSelectedTab(imgSettings, imgSettingsView);
        replaceFragment(SettingsFragment.newInstance(), false, R.id.mainContainer);
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
        setSelectedTab(imgProfile, imgProfileView);
        replaceFragment(ProfileFragment.newInstance(), false, R.id.mainContainer);
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
    public void locationCallback(FilterModel value) {
        if (value != null) {
            if (HomeFragment.getInstance() != null) {
                HomeFragment.getInstance().loadRelatedData(value);
            }
        }
    }
}
