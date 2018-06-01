package com.ureview.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ureview.R;
import com.ureview.adapters.NotificationsAdapter;

public class NotificationsFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rvNotifications;
    private NotificationsAdapter notificationsAdapter;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        rvNotifications = rootView.findViewById(R.id.rvNotifications);
        rvNotifications.setNestedScrollingEnabled(false);
        notificationsAdapter = new NotificationsAdapter(getActivity());
        rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotifications.setAdapter(notificationsAdapter);
        return rootView;
    }

}
