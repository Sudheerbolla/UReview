package com.ureview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ureview.R;
import com.ureview.activities.SplashActivity;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CustomTextView;

public class IntroFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private SplashActivity splashActivity;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private CustomTextView txtDesc, btnNext, txtTitle;

    public IntroFragment() {
    }

    public static IntroFragment newInstance() {
        IntroFragment categoryDetailsFragment = new IntroFragment();
        categoryDetailsFragment.setArguments(new Bundle());
        return categoryDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = (SplashActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        splashActivity.changeStatusBarColorToWhite();
        setReferences();
        setViewPager();
        addBottomDots(0);
        setListeners();
    }

    private void setViewPager() {
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void setReferences() {
        viewPager = rootView.findViewById(R.id.view_pager);
        dotsLayout = rootView.findViewById(R.id.layoutDots);
        btnNext = rootView.findViewById(R.id.btn_next);
        txtDesc = rootView.findViewById(R.id.txt_desc);
        txtTitle = rootView.findViewById(R.id.txt_title);
        layouts = new int[]{R.layout.layout_upload_reviews_slide, R.layout.layout_browse_reviews_slide, R.layout.layout_social_network_slide};
    }

    private void setListeners() {
        btnNext.setOnClickListener(this);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(splashActivity);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        LocalStorage.getInstance(splashActivity).putBoolean(LocalStorage.IS_FIRST_TIME_LAUNCH, false);
        splashActivity.replaceFragment(LoginFragment.newInstance(), false, R.id.splashContainer);
    }

    private int currPos;
    private int count = 0;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            currPos = position;
            switch (position) {
                case 0:
                    txtTitle.setVisibility(View.VISIBLE);
                    txtDesc.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    txtTitle.setText("What you say, matters!");
                    txtDesc.setText("Record your moment, #caption it and upload");
                    btnNext.setText("Skip");
                    break;
                case 1:
                    txtTitle.setVisibility(View.VISIBLE);
                    txtDesc.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    txtTitle.setText("Browse Reviews");
                    txtDesc.setText("Watch unique visual experiences. Use #tags for custom searches.");
                    btnNext.setText("Skip");
                    break;
                case 2:
                    txtTitle.setVisibility(View.VISIBLE);
                    txtDesc.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    txtTitle.setText("Share on Social Network");
                    txtDesc.setText("Share your reviews with your friends or on other social sites");
                    btnNext.setText("Skip");
                    break;
//                case 3:
//                    txtTitle.setVisibility(View.GONE);
//                    txtDesc.setVisibility(View.GONE);
//                    btnNext.setVisibility(View.GONE);
//                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (currPos == 2 && arg0 == 2 && arg2 == 0) {
                count++;
                if (count == 2) {
                    count = 0;
                    launchHomeScreen();
                }
            } else {
                count = 0;
            }

            Log.e("Page Scrolled", arg0 + "/" + arg1 + "/" +
                    "" + arg2);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.e("scroll state changed", arg0 + "");
        }
    };

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) splashActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    public void onClick(View view) {
        int current = getItem(+1);
        switch (view.getId()) {
            case R.id.btn_next:
//                if (current < layouts.length) {
//                    viewPager.setCurrentItem(current);
//                } else {
                launchHomeScreen();
//                }
                break;
            default:
                break;
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

}
