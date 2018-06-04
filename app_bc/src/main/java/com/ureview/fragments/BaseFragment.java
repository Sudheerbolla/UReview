package com.ureview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ureview.R;
import com.ureview.utils.FragmentUtils;
import com.ureview.utils.views.CustomTextView;

public class BaseFragment extends Fragment implements View.OnClickListener {

    public CustomTextView txtTitle, txtRight, txtLeft;
    public ImageView imgBack, imgLoc, imgNotf, imgEdit, imgSearch, imgClose;
    public EditText edtText;
    public RelativeLayout rlEditView;
    public ImageView imgHome, imgSearchB, imgVideo, imgProfile, imgSettings,
            imgHomeView, imgSearchView, imgProfileView, imgSettingsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initTopBar(View rootView) {
        txtTitle = rootView.findViewById(R.id.txtTitle);
        txtRight = rootView.findViewById(R.id.txtRight);
        txtLeft = rootView.findViewById(R.id.txtLeft);
        imgBack = rootView.findViewById(R.id.imgBack);
        imgLoc = rootView.findViewById(R.id.imgLoc);
        imgNotf = rootView.findViewById(R.id.imgNotf);
        imgEdit = rootView.findViewById(R.id.imgEdit);
        imgSearch = rootView.findViewById(R.id.imgSearch);
        imgClose = rootView.findViewById(R.id.imgClose);
        edtText = rootView.findViewById(R.id.edtText);
        rlEditView = rootView.findViewById(R.id.rlEditView);
    }

    public void initBottomBar(View rootView) {
        imgHome = rootView.findViewById(R.id.imgHome);
        imgSearchB = rootView.findViewById(R.id.imgSearchB);
        imgVideo = rootView.findViewById(R.id.imgVideo);
        imgProfile = rootView.findViewById(R.id.imgProfile);
        imgSettings = rootView.findViewById(R.id.imgSettings);

        imgHomeView = rootView.findViewById(R.id.imgHomeView);
        imgSearchView = rootView.findViewById(R.id.imgSearchView);
        imgProfileView = rootView.findViewById(R.id.imgProfileView);
        imgSettingsView = rootView.findViewById(R.id.imgSettingsView);

        imgHome.setSelected(true);
        imgHomeView.setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.llHome).setOnClickListener(this);
        rootView.findViewById(R.id.llSearch).setOnClickListener(this);
        rootView.findViewById(R.id.llVideo).setOnClickListener(this);
        rootView.findViewById(R.id.llProfile).setOnClickListener(this);
        rootView.findViewById(R.id.llSettings).setOnClickListener(this);
    }

    public void initTabLayout(Context context, TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(context, R.drawable.selector_home)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(context, R.drawable.selector_search)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(context, R.drawable.video_copy)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(context, R.drawable.selector_profile)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(context, R.drawable.selector_settings)));
    }

    public void setToolBar(String title, String leftText, String rightText, boolean showLoc,
                           boolean showBack, boolean showNotf, boolean showEdtView, boolean showEdt) {
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

    public int checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    public void requestForPermission(String permission, int requestCode) {
        requestPermissions(new String[]{permission}, requestCode);
    }

    public void requestForPermission(String[] permission, int requestCode) {
        requestPermissions(permission, requestCode);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (FragmentUtils.sDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onClick(View view) {

    }
}
