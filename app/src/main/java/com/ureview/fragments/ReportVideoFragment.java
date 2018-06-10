package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.activities.MainActivity;
import com.ureview.utils.LocalStorage;
import com.ureview.utils.views.CustomEditText;
import com.ureview.utils.views.CustomTextView;

public class ReportVideoFragment extends BaseFragment {

    private View rootView;
    private MainActivity mainActivity;
    private CustomEditText edtDesc;
    private CustomTextView txtNegative, txtPositive;
    private String userId;

    public static ReportVideoFragment newInstance() {
        ReportVideoFragment followersFragment = new ReportVideoFragment();
        Bundle bundle = new Bundle();
        followersFragment.setArguments(bundle);
        return followersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        userId = LocalStorage.getInstance(mainActivity).getString(LocalStorage.PREF_USER_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report_video, container, false);
        initComponents();
        return rootView;
    }

    private void initComponents() {
        mainActivity.setToolBar("Report Video", "", "", false,
                false, false, false, false);
        edtDesc = rootView.findViewById(R.id.edtDesc);
        txtNegative = rootView.findViewById(R.id.txtNegative);
        txtPositive = rootView.findViewById(R.id.txtPositive);
    }

}
