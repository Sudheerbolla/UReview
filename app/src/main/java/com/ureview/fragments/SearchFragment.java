package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.listeners.IParserListener;
import com.ureview.utils.LocalStorage;
import com.ureview.wsutils.WSCallBacksListener;
import com.ureview.wsutils.WSUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class SearchFragment extends BaseFragment implements View.OnClickListener, IParserListener<JsonElement> {

    private View rootView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainActivity mainActivity;
    private String userId;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        viewPager = rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = rootView.findViewById(R.id.searchTabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(mainActivity, R.color.colorDarkGrey), ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setupWithViewPager(viewPager);

        mainActivity.edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        // videos
                    } else {
                        //people
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mainActivity.imgClose.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("", "", "", false, false, false, true, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SearchVideosFragment(), "Videos");
//        adapter.addFragment(new FollowersFragment(), "People");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgClose:
                if (mainActivity.edtText != null) {
                    mainActivity.edtText.setText("");
                }
                break;
        }
    }

    @Override
    public void successResponse(int requestCode, JsonElement response) {

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

    private void requestForSearchPeople() {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("user_name", mainActivity.edtText.getText().toString().trim());
        queryMap.put("user_id", userId);
        queryMap.put("startFrom", "0");
        queryMap.put("count", "10");
        Call<JsonElement> call = BaseApplication.getInstance().getWsClientListener().searchUsers(queryMap);
        new WSCallBacksListener().requestForJsonObject(mainActivity, WSUtils.REQ_FOR_SEARCH_PEOPLE, call, this);
    }

}
/*
http://18.216.101.112/search-users?user_name=a&user_id=24&startFrom=0&count=10
{
    "status": "success",
    "message": "Search users data",
    "users_data": [
        {
            "user_id": "1",
            "first_name": "Madhu",
            "last_name": "Sudhan",
            "user_image": "",
            "user_rating": "2",
            "email": "putta.msreddy@gmail.com",
            "mobile": "8121407014",
            "user_description": "",
            "gender": "M",
            "age": "5",
            "status": "A",
            "city": "Hyderabad",
            "address": "",
            "date_of_birth": "31/05/2013",
            "uploaded_videos_count": 10,
            "follow_status": ""
        }
    ]
}*/