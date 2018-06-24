package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ureview.R;
import com.ureview.activities.MainActivity;

public class ProfileImageFragment extends DialogFragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private static final String ARG_PARAM1 = "param1";
    private View rootView;
    private ImageView imgBack, imgView;
    private String url;

    public ProfileImageFragment() {
        // Required empty public constructor
    }

    public static ProfileImageFragment newInstance(String param1) {
        ProfileImageFragment fragment = new ProfileImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mainActivity = (MainActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(ARG_PARAM1, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.fragment_profile_image, null);
        }
        initComponents();
        return rootView;
    }

    private void initComponents() {
        setReferences();
        setListeners();
        if (!TextUtils.isEmpty(url)) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .fitCenter()
                    .error(R.drawable.ic_user_placeholder);
            Glide.with(this)
                    .load(url)
                    .apply(options)
                    .into(imgView);
        } else imgView.setImageResource(R.drawable.ic_user_placeholder);

    }

    private void setReferences() {
        imgView = rootView.findViewById(R.id.imgView);
        imgBack = rootView.findViewById(R.id.imgBack);
    }

    private void setListeners() {
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                dismissAllowingStateLoss();
                break;
            default:
                break;
        }
    }


}
