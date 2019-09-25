package com.matchstick.brightlife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.adapters.MessageAdapter;

public class FragmentMessage extends Fragment {
    MessageAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_id);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_id);
        adapter = new MessageAdapter(getActivity().getSupportFragmentManager());


        adapter.addFragment(new FragmentInbox(), "Inbox");
        adapter.addFragment(new FragmentOutbox(), "Outbox");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}