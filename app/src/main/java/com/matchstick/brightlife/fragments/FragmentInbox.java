package com.matchstick.brightlife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.adapters.NotificationAdapter;
import com.matchstick.brightlife.models.Notification;

import java.util.ArrayList;

public class FragmentInbox extends Fragment {
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;
    private RecyclerView notificationRecyclerView;
    AQuery aQuery;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aQuery =  new AQuery(getActivity());
        notificationRecyclerView = (RecyclerView) view.findViewById(R.id.notification_recycler_view);
        loadNotificationList();
    }
    private void loadNotificationList(){
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("Survey", "23 min", "how would you like to paid","Commission"));
        notificationList.add(new Notification("Survey", "13 min", "how would you like to paid", "Commission"));
        notificationList.add(new Notification("Survey", "4 days", "how would you like to paid", "Commission"));
        notificationList.add(new Notification("Survey", "40 min", "how would you like to paid", "Commission"));
        notificationList.add(new Notification("Survey", "23 hrs", "how would you like to paid", "Commission"));
        notificationList.add(new Notification("Survey", "2 min", "how would you like to paid", "Commission"));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(getActivity(), notificationList);
        notificationRecyclerView.setAdapter(notificationAdapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadNotificationList();
    }
}
