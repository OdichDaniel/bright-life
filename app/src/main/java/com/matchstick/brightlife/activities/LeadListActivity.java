package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.matchstick.brightlife.R;
import com.matchstick.brightlife.adapters.LeadSaleAdapter;
import com.matchstick.brightlife.models.LeadSale;

import java.util.ArrayList;

public class LeadListActivity extends AppCompatActivity {
    RecyclerView leadSaleRecyclerView;
    private LeadSaleAdapter leadSaleAdapter;
    private ArrayList<LeadSale> leadSaleArrayList;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_list);
        leadSaleRecyclerView = (RecyclerView) findViewById(R.id.lead_sale_recycler_view);
        tb = (Toolbar) findViewById(R.id.toolbar);
        getLeadList();
        toolbarSetUp();
    }

    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Sale Leads");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  finish();
                }
            });
        }
    }

    private void getLeadList(){
        leadSaleArrayList = new ArrayList<>();
        leadSaleArrayList.add(new LeadSale("Orech Daniel", "0788903403", "solar lamp"));
        leadSaleArrayList.add(new LeadSale("Owino Solomon", "0788903403", "Motocycle"));
        leadSaleArrayList.add(new LeadSale("Aremo Isacc", "0788903403", "solar lamp"));
        leadSaleArrayList.add(new LeadSale("Ouma Moses", "0788903403", "Flat screens"));
        leadSaleArrayList.add(new LeadSale("Pius Daniel", "0788903403", "Phones"));
        leadSaleArrayList.add(new LeadSale("Ambrose Mwaka", "0788903403", "stove"));
        leadSaleArrayList.add(new LeadSale("Kimathi Newton", "0788903403", "solar panel"));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LeadListActivity.this);
        leadSaleRecyclerView.setLayoutManager(layoutManager);
        leadSaleAdapter = new LeadSaleAdapter(LeadListActivity.this, leadSaleArrayList);
        leadSaleRecyclerView.setAdapter(leadSaleAdapter);
    }
}
