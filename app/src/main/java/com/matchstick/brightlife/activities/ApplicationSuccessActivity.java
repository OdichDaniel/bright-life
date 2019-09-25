package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aquery.AQuery;
import com.matchstick.brightlife.R;

public class ApplicationSuccessActivity extends AppCompatActivity {
    AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_success);
        aQuery = new AQuery(this);
        goToConfirmApplicationActivity();
    }

    private void goToConfirmApplicationActivity(){
        aQuery.id(R.id.btn_review).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(ConfirmApplicaitonActivity.class);
                finish();
            }
        });
    }
}
