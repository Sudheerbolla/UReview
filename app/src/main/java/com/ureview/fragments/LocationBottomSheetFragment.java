package com.ureview.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import com.ureview.BaseApplication;
import com.ureview.R;
import com.ureview.activities.MainActivity;

public class LocationBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private MainActivity searchActivity;
    private BaseApplication baseApplication;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BottomSheetDialog dialog;
    private View rootView;

    private OnLocationOptionSelected mListener;

    public LocationBottomSheetFragment() {
        // Required empty public constructor
    }

    public static LocationBottomSheetFragment newInstance(String param1, String param2) {
        LocationBottomSheetFragment fragment = new LocationBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LocationBottomSheetFragment newInstance(int param1) {
        LocationBottomSheetFragment fragment = new LocationBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchActivity = (MainActivity) getActivity();
        baseApplication = (BaseApplication) searchActivity.getApplication();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        try {
            if (rootView == null) {
                rootView = View.inflate(getContext(), R.layout.fragment_location_radius, null);
            }
            dialog.setContentView(rootView);
            initComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    private void initComponents() {

    }

    public void onButtonPressed(String value) {
        if (mListener != null) {
            mListener.locationCallback(value);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationOptionSelected) {
            mListener = (OnLocationOptionSelected) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFilterOptionSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.relByGod:
////                searchActivity.startActivity(new Intent(searchActivity, SelectGodsActivity.class));
//                break;
            default:
                dismiss();
                break;
        }
    }

    public interface OnLocationOptionSelected {
        void locationCallback(String value);
    }

}
