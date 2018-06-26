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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.StaticUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainActivity mainActivity;
    private String userId;
    private SearchVideosFragment searchVideosFragment;
    private SearchPeopleFragment searchPeopleFragment;
    private boolean isInSearchPeopleFragment;
    private ViewPagerAdapter adapter;
    public static String searchText = "";

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        viewPager = rootView.findViewById(R.id.viewpager);
        if (searchVideosFragment == null) searchVideosFragment = new SearchVideosFragment();
        if (searchPeopleFragment == null) searchPeopleFragment = new SearchPeopleFragment();
        setupViewPager(viewPager);

        tabLayout = rootView.findViewById(R.id.searchTabs);
        tabLayout.setTabTextColors(ContextCompat.getColor(mainActivity, R.color.colorDarkGrey), ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(mainActivity, R.color.app_text_color));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 && searchPeopleFragment != null) {
                    searchPeopleFragment.searchUser(searchText);
                } else if (tab.getPosition() == 0 && searchVideosFragment != null) {
                    searchVideosFragment.searchVideo(searchText);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (TextUtils.isEmpty(searchText)) mainActivity.edtText.setText("");
        else mainActivity.edtText.setText(searchText);
        mainActivity.edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = charSequence.toString();
                if (tabLayout.getSelectedTabPosition() == 0) {
                    if (searchVideosFragment != null) {
                        searchVideosFragment.searchVideo(charSequence.toString());
                    }
                } else {
                    if (searchPeopleFragment != null) {
                        searchPeopleFragment.searchUser(charSequence.toString());
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
        searchText = "";
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity == null) mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBar("", "", "", false, false, false, true, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchText = "";
    }

    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
//        adapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager());
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(searchVideosFragment, "Videos");
        adapter.addFragment(searchPeopleFragment, "People");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgClose:
                if (mainActivity.edtText != null) {
                    searchText = "";
                    mainActivity.edtText.setText("");
                    StaticUtils.hideSoftKeyboard(mainActivity);
                }
                break;
        }
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