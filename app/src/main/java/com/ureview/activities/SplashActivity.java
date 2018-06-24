package com.ureview.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.fragments.IntroFragment;
import com.ureview.fragments.LoginFragment;
import com.ureview.models.LocationModel;
import com.ureview.utils.Constants;
import com.ureview.utils.DialogUtils;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.RuntimePermissionUtils;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends BaseActivity implements LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private RelativeLayout relTopBar;
    public CustomTextView txtTitle, txtRight;
    private ImageView imgBack;
    public static Location mLastLocation;
    public LocationManager mLocationManager;
    private int counter = 0;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColorToAppColorLight();
        setContentView(R.layout.activity_splash);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.relSplash).setVisibility(View.VISIBLE);
        StaticUtils.getHeightAndWidth(this);
        initComps();
    }

    private void checkPermissions() {
        if (RuntimePermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RuntimePermissionUtils.requestForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.LOCATION_PERMISSION);
        } else checkInternetConnectionAndProceed();
    }

    private void initComps() {
//        mLocationManager.
        relTopBar = findViewById(R.id.relTopBar);
        txtTitle = findViewById(R.id.txtTitle);
        txtRight = findViewById(R.id.txtRight);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        txtRight.setOnClickListener(this);
        checkPermissions();
    }

    public void setTopBar(String screen) {
        relTopBar.setVisibility(View.GONE);
        txtTitle.setVisibility(View.GONE);
        txtRight.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
        switch (screen) {
            case "Signup1Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Sign Up");
                break;
            case "Signup3Fragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Congratulations");
                break;
            case "LoginFragment":
                relTopBar.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText("Login");
                break;
            default:
                txtTitle.setText("Sign Up");
                txtRight.setText("Log In");
                break;
        }
    }

    private void checkInternetConnectionAndProceed() {
        if (StaticUtils.checkInternetConnection(this)) {
            getCurrentLocation();
        } else {
            DialogUtils.showSimpleDialog(this, "", "No Internet Connection", "Retry", "Close", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkInternetConnectionAndProceed();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }, true, false);
        }
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash Key: ", hashKey + "");
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }

    private void proceedWithFlow() {
        if (LocalStorage.getInstance(this).getBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, true)) {
            replaceFragment(IntroFragment.newInstance(), false, R.id.splashContainer);
            findViewById(R.id.relSplash).setVisibility(View.GONE);
        } else if (LocalStorage.getInstance(this).getBoolean(LocalStorage.IS_LOGGED_IN_ALREADY, false)) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            finishAffinity();
        } else {
            replaceFragment(LoginFragment.newInstance(), false, R.id.splashContainer);
            findViewById(R.id.relSplash).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("hyma", "yes getting callback");
        FragmentManager fragment = getSupportFragmentManager();
        if (fragment != null) {
            fragment.findFragmentByTag(LoginFragment.class.getSimpleName()).onActivityResult(requestCode, resultCode, data);
        } else Log.e("Twitter", "fragment is null");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtRight:
                replaceFragment(LoginFragment.newInstance(), true, R.id.splashContainer);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION) {
            if (StaticUtils.isAllPermissionsGranted(grantResults)) {
                checkInternetConnectionAndProceed();
            } else {
                StaticUtils.showToast(this, "Location permission is mandatory to access your location");
                checkPermissions();
            }

        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(isGPSEnabled || isNetworkEnabled))
            StaticUtils.showToast(this, "Error fetching location from the provider");
        else {
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (mLastLocation == null && isGPSEnabled) {
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
        if (mLastLocation != null) {
            new Handler().postDelayed(this::proceedWithFlow, 400);
        } else {
            counter += 1;
            if (counter < 3) getCurrentLocation();
            else if (counter < 5) {
                getLocationFromGoogle();
            } else {
                StaticUtils.showToast(this, "Error Fetching Location.Please retry");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationFromGoogle() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        @SuppressLint("MissingPermission") PendingResult<Status> pendingResult =
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                        (com.google.android.gms.location.LocationListener) this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        mLastLocation = location;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
//        if (mLastLocation != null) {
//            clearBackStackCompletely();
//            proceedWithFlow();
//        }
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
    protected void onStop() {
        super.onStop();
        if (mLocationManager != null) mLocationManager.removeUpdates(this);
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mCurrentLocation = location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
