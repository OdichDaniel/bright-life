package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.aquery.AQuery;
import com.matchstick.brightlife.R;

public class RequirementActivity extends AppCompatActivity {
    AQuery aQuery;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirement);
        tb = (Toolbar) findViewById(R.id.toolbar);
        toolbarSetUp();
        aQuery = new AQuery(this);
        aQuery.id(R.id.btn_confirm).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               aQuery.open(BioDataActivity.class);
            }
        });
    }
    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Application Requirements");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
