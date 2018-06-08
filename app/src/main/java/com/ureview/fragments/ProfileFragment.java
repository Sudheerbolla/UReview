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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;

public class ProfileFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private CustomTextView txtFollowersCount, txtFollowingCount, txtName, txtLoc, txtFollowStatus;
    private LinearLayout linFollowing, linFollowers;
    private RatingBar ratingBar;
    private MainActivity mainActivity;
    public UserInfoModel userInfoModel;
    private ImageView imgProfile;
    private ViewPagerAdapter adapter;
    private String userId, otherUserId;
    private boolean isDiffUser;
    public static UserInfoModel otherInfoModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment followersFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("otherUserId", userId);
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userInfoModel = BaseApplication.userInfoModel;
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
        Bundle bundle = getArguments();
        if (bundle != null) {
            otherUserId = bundle.getString("otherUserId");
            isDiffUser = !TextUtils.isEmpty(otherUserId) && !otherUserId.equalsIgnoreCase(userId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);
        initComponents();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar(isDiffUser ? "Profile" : "My Profile", "", "", false, isDiffUser, false, false, !isDiffUser);
    }

    private void initComponents() {
        viewPager = rootView.findViewById(R.id.viewpager);

        tabLayout = rootView.findViewById(R.id.profileTabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(mainActivity, R.color.colorDarkGrey), ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(mainActivity, R.color.app_text_color));

        txtFollowersCount = rootView.findViewById(R.id.txtFollowersCount);
        txtFollowingCount = rootView.findViewById(R.id.txtFollowingCount);

        linFollowers = rootView.findViewById(R.id.linFollowers);
        linFollowing = rootView.findViewById(R.id.linFollowing);

        txtName = rootView.findViewById(R.id.txtName);
        txtFollowStatus = rootView.findViewById(R.id.txtFollowStatus);
        txtLoc = rootView.findViewById(R.id.txtLoc);

        ratingBar = rootView.findViewById(R.id.ratingBar);
        imgProfile = rootView.findViewById(R.id.imgProfile);

        setData();
        setListeners();

        requestForGetProfileDataWS();

    }

    private void setData() {
        UserInfoModel userInfoModel = isDiffUser ? otherInfoModel : this.userInfoModel;
        if (userInfoModel != null) {
            txtName.setText(userInfoModel.first_name + " " + userInfoModel.last_name);
            txtLoc.setText(userInfoModel.city + ", " + userInfoModel.address);
            ratingBar.setRating(TextUtils.isEmpty(userInfoModel.user_rating) ? 0f : Float.parseFloat(userInfoModel.user_rating));
            txtFollowersCount.setText(TextUtils.isEmpty(userInfoModel.follow_you_count) ? "0" : userInfoModel.follow_you_count);
            txtFollowingCount.setText(TextUtils.isEmpty(userInfoModel.you_follow_count) ? "0" : userInfoModel.you_follow_count);
            txtFollowStatus.setVisibility(isDiffUser ? View.VISIBLE : View.GONE);
            if (isDiffUser)
                if (!TextUtils.isEmpty(userInfoModel.follow_status)) {
                    txtFollowStatus.setText(userInfoModel.follow_status.equalsIgnoreCase("follow") ? "Following" : "Follow");
                } else {
                    txtFollowStatus.setText(userInfoModel.follow_status.equalsIgnoreCase("follow") ? "Following" : "Follow");
                }

            if (!TextUtils.isEmpty(userInfoModel.user_image)) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .bitmapTransform(new RoundedCorners(7))
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
        txtFollowStatus.setOnClickListener(this);
        linFollowers.setOnClickListener(this);
        linFollowing.setOnClickListener(this);
    }

    private void requestForGetProfileDataWS() {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().getUserData(isDiffUser ? otherUserId : userId);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_GET_USER_PROFILE, call, this);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(AboutFragment.newInstance(isDiffUser ? otherUserId : userId), "About");
        adapter.addFragment(VideosFragment.newInstance(isDiffUser ? otherUserId : userId), "Videos");
        if (!isDiffUser)
            adapter.addFragment(StatsFragment.newInstance(isDiffUser ? otherUserId : userId), "Stats");
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linFollowers:
                mainActivity.replaceFragment(FollowersFragment.newInstance(true, isDiffUser ? otherUserId : userId), true, R.id.mainContainer);
                break;
            case R.id.linFollowing:
                mainActivity.replaceFragment(FollowersFragment.newInstance(false, isDiffUser ? otherUserId : userId), true, R.id.mainContainer);
                break;
            case R.id.txtFollowStatus:
                requestForFollowUser(otherUserId);
                break;
            default:
                break;
        }

    }

    private void requestForFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private RequestBody getRequestBodyObject(String followId) {
        JSONObject jsonObjectReq = new JSONObject();
        try {
            jsonObjectReq.put("id", Integer.parseInt(userId));
            jsonObjectReq.put("follow_id", Integer.parseInt(followId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StaticUtils.getRequestBody(jsonObjectReq);
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {
        switch (requestCode) {
            case WSUtils.REQ_FOR_GET_USER_PROFILE:
                parseGetUserProfileResponse((JsonObject) response);
                break;
            case WSUtils.REQ_FOR_FOLLOW_USER:
                parseFollowUserResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    userInfoModel.follow_status = "follow";
                    txtFollowStatus.setText("Following");
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGetUserProfileResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    if (response.has("user_info")) {
                        try {
                            UserInfoModel userInfoModel = new UserInfoModel(response.get("user_info").getAsJsonObject());
                            if (isDiffUser) {
                                otherInfoModel = userInfoModel;
                            } else {
                                this.userInfoModel = userInfoModel;
                                BaseApplication.userInfoModel = userInfoModel;
                                LocalStorage.getInstance(mainActivity).putString(LocalStorage.PREF_USER_INFO_DATA, userInfoModel.serialize());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
                        setData();
                        if (adapter.getItem(0) != null && adapter.getItem(0) instanceof AboutFragment)
                            ((AboutFragment) adapter.getItem(0)).updateData(isDiffUser ? otherInfoModel : userInfoModel);
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
