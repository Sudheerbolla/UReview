package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;
import com.ureview.utils.views.CustomTextView;
import com.ureview.utils.views.CustomViewPager;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ProfileFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private CustomTextView txtFollowersCount, txtFollowingCount, txtName, txtLoc;
    private LinearLayout linFollowing, linFollowers;
    private RatingBar ratingBar;
    private MainActivity mainActivity;
    public UserInfoModel userInfoModel;
    private ImageView imgProfile;
    private ViewPagerAdapter adapter;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userInfoModel = BaseApplication.userInfoModel;
    }

/*{
"status":"success","message":"User data","user_info":{"first_name":"Madhu","last_name":"Sudhan","user_name":"",
"email":"putta.msreddy@gmail.com","gender":"M","date_of_birth":"31\/05\/2013","age":"5","country_code":"+91",
"mobile":"8121407014","user_image":"","user_description":"","auth_id":"1862768607112909","auth_type":"Facebook",
"user_rating":"2","status":"A","videos_range":"30","city":"Hyderabad","address":"","platform":"ios",
"device_token":"247CF8F9450EFD6709131935C45E53091DC2783B368F7522C5E6C0F9C5B6B33C","created_date":"2018-06-05 09:20:31",
"userid":"1","follow_status":"","follow_you_count":5,"you_follow_count":3}
}*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);
        initComponents();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("My Profile", "", "", false, false, false, false, true);
    }

    private void initComponents() {
        viewPager = rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = rootView.findViewById(R.id.profileTabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(mainActivity, R.color.colorDarkGrey), ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setupWithViewPager(viewPager);

        txtFollowersCount = rootView.findViewById(R.id.txtFollowersCount);
        txtFollowingCount = rootView.findViewById(R.id.txtFollowingCount);

        linFollowers = rootView.findViewById(R.id.linFollowers);
        linFollowing = rootView.findViewById(R.id.linFollowing);

        txtName = rootView.findViewById(R.id.txtName);
        txtLoc = rootView.findViewById(R.id.txtLoc);

        ratingBar = rootView.findViewById(R.id.ratingBar);
        imgProfile = rootView.findViewById(R.id.imgProfile);

        setData();
        setListeners();

        requestForGetProfileDataWS();

    }

    private void setData() {
        if (userInfoModel != null) {
            txtName.setText(userInfoModel.first_name + " " + userInfoModel.last_name);
            txtLoc.setText(userInfoModel.city + ", " + userInfoModel.address);
            ratingBar.setRating(TextUtils.isEmpty(userInfoModel.user_rating) ? 0f : Float.parseFloat(userInfoModel.user_rating));
            txtFollowersCount.setText(TextUtils.isEmpty(userInfoModel.follow_you_count) ? "0" : userInfoModel.follow_you_count);
            txtFollowingCount.setText(TextUtils.isEmpty(userInfoModel.you_follow_count) ? "0" : userInfoModel.you_follow_count);
            if (!TextUtils.isEmpty(userInfoModel.user_image)) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .fitCenter()
                        .error(R.mipmap.ic_launcher);

                Glide.with(this)
                        .load(userInfoModel.user_image)
                        .apply(options)
                        .into(imgProfile);
            } else imgProfile.setImageResource(R.mipmap.ic_launcher);

        }
    }

    private void setListeners() {
        linFollowers.setOnClickListener(this);
        linFollowing.setOnClickListener(this);
        txtFollowersCount.setOnClickListener(this);
        txtFollowingCount.setOnClickListener(this);
    }

    private void requestForGetProfileDataWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getUserData(LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, ""));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_USER_PROFILE, call, this);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AboutFragment(), "About");
        adapter.addFragment(new VideosFragment(), "Videos");
        adapter.addFragment(new StatsFragment(), "Stats");
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);
    }

    @Override
    public void onClick(View view) {
        mainActivity.setFollowersFragment();
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_GET_USER_PROFILE:
                parseGetUserProfileResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseGetUserProfileResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("user_info")) {
                        try {
                            userInfoModel = new UserInfoModel(response.get("user_info").getAsJsonObject());
                            if (userInfoModel != null) {
                                BaseApplication.userInfoModel = userInfoModel;
                                LocalStorage.getInstance(mainActivity).putString(LocalStorage.PREF_USER_INFO_DATA, userInfoModel.serialize());
                            }
                            LocalStorage.getInstance(mainActivity).putString(LocalStorage.PREF_USER_ID, userInfoModel.userid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (adapter.getItem(0) != null && adapter.getItem(0) instanceof AboutFragment)
                            ((AboutFragment) adapter.getItem(0)).updateData();
                        setData();
                    }
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void errorResponse(int requestCode, String error) {

    }

    @Override
    public void noInternetConnection(int requestCode) {

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
