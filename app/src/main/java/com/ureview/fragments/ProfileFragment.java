package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.models.UserInfoModel;
import com.ureview.utils.DialogUtils;
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
    //    private RatingBar ratingBar;
    private MainActivity mainActivity;
    public UserInfoModel userInfoModel;
    private ImageView imgProfile, imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
    private ViewPagerAdapter adapter;
    private String userId, otherUserId, otherUserName = "";
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

    public static ProfileFragment newInstance(String userId, String userName) {
        ProfileFragment followersFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("otherUserId", userId);
        bundle.putString("otherUserName", userName);
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
            if (bundle.containsKey("otherUserName"))
                otherUserName = bundle.getString("otherUserName");
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

//        ratingBar = rootView.findViewById(R.id.ratingBar);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        imgStar1 = rootView.findViewById(R.id.imgStar1);
        imgStar2 = rootView.findViewById(R.id.imgStar2);
        imgStar3 = rootView.findViewById(R.id.imgStar3);
        imgStar4 = rootView.findViewById(R.id.imgStar4);
        imgStar5 = rootView.findViewById(R.id.imgStar5);

        setData();
        setListeners();

        requestForGetProfileDataWS();

    }

    private void setData() {
        UserInfoModel userInfoModel = isDiffUser ? otherInfoModel : this.userInfoModel;
        if (userInfoModel != null) {
            txtName.setText(userInfoModel.first_name + " " + userInfoModel.last_name);
            txtLoc.setText(TextUtils.isEmpty(userInfoModel.city) ? " Location not available" : userInfoModel.city);
            setProfileRating(TextUtils.isEmpty(userInfoModel.user_rating) ? 0f : Float.parseFloat(userInfoModel.user_rating));
//            ratingBar.setRating(TextUtils.isEmpty(userInfoModel.user_rating) ? 0f : Float.parseFloat(userInfoModel.user_rating));
            txtFollowersCount.setText(TextUtils.isEmpty(userInfoModel.follow_you_count) ? "0" : userInfoModel.follow_you_count);
            txtFollowingCount.setText(TextUtils.isEmpty(userInfoModel.you_follow_count) ? "0" : userInfoModel.you_follow_count);
            txtFollowStatus.setVisibility(isDiffUser ? View.VISIBLE : View.GONE);
            if (isDiffUser) {
                setFollowTextAndBg();
            }

            if (!TextUtils.isEmpty(userInfoModel.user_image)) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .fitCenter()
                        .error(R.drawable.ic_user_placeholder);
                Glide.with(this)
                        .load(userInfoModel.user_image)
                        .apply(options)
                        .into(imgProfile);
            } else imgProfile.setImageResource(R.drawable.ic_user_placeholder);
        }
    }

    private void setProfileRating(float v) {
        switch ((int) v) {
            case 0:
                setSelectedStar(false, false, false, false, false);
                break;
            case 1:
                setSelectedStar(true, false, false, false, false);
                break;
            case 2:
                setSelectedStar(true, true, false, false, false);
                break;
            case 3:
                setSelectedStar(true, true, true, false, false);
                break;
            case 4:
                setSelectedStar(true, true, true, true, false);
                break;
            case 5:
                setSelectedStar(true, true, true, true, true);
                break;
        }
    }

    private void setSelectedStar(boolean b, boolean b1, boolean b2, boolean b3, boolean b4) {
        imgStar1.setSelected(b);
        imgStar2.setSelected(b1);
        imgStar3.setSelected(b2);
        imgStar4.setSelected(b3);
        imgStar5.setSelected(b4);
    }

    private void setFollowTextAndBg() {
        if (TextUtils.isEmpty(userInfoModel.follow_status)) {
            txtFollowStatus.setText("Follow");
            txtFollowStatus.setSelected(false);
        } else {
            txtFollowStatus.setText("Unfollow");
            txtFollowStatus.setSelected(true);
        }
    }

    private void setListeners() {
        txtFollowStatus.setOnClickListener(this);
        linFollowers.setOnClickListener(this);
        linFollowing.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
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
        viewPager.setPagingEnabled(false);
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
                if (txtFollowStatus.getText().toString().equalsIgnoreCase("follow")) {
                    askConfirmationAndProceed();
                } else {
                    requestForFollowUser(otherUserId);
                }
                break;
            case R.id.imgProfile:
                if (!TextUtils.isEmpty(userInfoModel.user_image)) {
                    ProfileImageFragment countrySelectionFragment = ProfileImageFragment.newInstance(userInfoModel.user_image);
                    countrySelectionFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.countryCodeDialogStyle);
                    countrySelectionFragment.show(mainActivity.getSupportFragmentManager(), "");
                }
                break;
            default:
                break;
        }

    }


    private void askConfirmationAndProceed() {
        DialogUtils.showUnFollowConfirmationPopup(mainActivity, otherUserName,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestForUnFollowUser(otherUserId);
                    }
                });
    }

    private void requestForFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().followUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_FOLLOW_USER, call, this);
    }

    private void requestForUnFollowUser(String followId) {
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().unFollowUser(getRequestBodyObject(followId));
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_UN_FOLLOW_USER, call, this);
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
            case WSUtils.REQ_FOR_UN_FOLLOW_USER:
                parseUnFollowUserResponse((JsonObject) response);
                break;
            default:
                break;
        }
    }

    private void parseUnFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    userInfoModel.follow_status = "Follow";
                    setFollowTextAndBg();
                } else if (response.get("status").getAsString().equalsIgnoreCase("fail")) {
                    StaticUtils.showToast(mainActivity, response.get("message").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFollowUserResponse(JsonObject response) {
        try {
            if (response.has("status")) {
                if (response.get("status").getAsString().equalsIgnoreCase("success")) {
                    userInfoModel.follow_status = "Unfollow";
                    setFollowTextAndBg();
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
