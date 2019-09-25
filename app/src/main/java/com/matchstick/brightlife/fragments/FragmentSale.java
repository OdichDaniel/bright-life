package com.matchstick.brightlife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquery.AQuery;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.activities.LeadActivity;
import com.matchstick.brightlife.activities.LeadListActivity;
import com.matchstick.brightlife.activities.SaleActivity;
import com.matchstick.brightlife.activities.SaleListActivity;

public class FragmentSale extends Fragment {
    AQuery aQuery;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aQuery = new AQuery(getActivity());
        goToNewSaleActivity();
        goToLeadListActivity();
    }

    private void goToNewSaleActivity(){
        aQuery.id(R.id.btn_create_new_lead).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(LeadActivity.class);
            }
        });

        aQuery.id(R.id.btn_direct_sale).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(SaleActivity.class);
            }
        });
    }

    private void goToLeadListActivity(){
        aQuery.id(R.id.leads_layout).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(LeadListActivity.class);
            }
        });

        aQuery.id(R.id.sales_layout).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(SaleListActivity.class);
            }
        });
    }
}