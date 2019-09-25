package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aquery.AQuery;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.AppUtils;
import com.matchstick.brightlife.entities.Constant;

public class SelectActionActivity extends AppCompatActivity {
    AQuery aQuery;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String firstName, phoneNumber, nok_firstName, nok_phone_number;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        setContentView(R.layout.activity_select_action);
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        aQuery = new AQuery(this);
        String firstName = preferences.getString(Constant.FIRST_NAME, "");

        AppUtils.log("FIRST NAME => " + firstName);

        if (firstName.isEmpty()) {
            aQuery.id(R.id.layout_first_time).show();
            aQuery.id(R.id.card_check_application_status).hide();
        } else {
            aQuery.id(R.id.btn_resume_application).show();
        }
        startApplication();
        checkApplicantStatus();
        checkStatusApplicationStatus();
    }

    private void startApplication() {
        aQuery.id(R.id.btn_resume_application).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkApplicantStatus();
            }
        });

        aQuery.id(R.id.btn_become_an_agent).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aQuery.openFromRight(NationalityActivity.class);
            }
        });

        aQuery.id(R.id.btn_go_to_login_activity).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(LoginActivity.class);
            }
        });
    }

    //making statusbar tranparent
    private void setStatusBarTransparent() {
        //check the min SDK
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }



    private void checkApplicantStatus() {
        aQuery.id(R.id.btn_resume_application).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateOfBirth = preferences.getString(Constant.DATE_OF_BIRTH, "");
                firstName = preferences.getString(Constant.FIRST_NAME, "");
                phoneNumber = preferences.getString(Constant.REGISTERED_TELEPHONE, "");
                nok_firstName = preferences.getString(Constant.NOK_FIST_NAME, "");
                nok_phone_number = preferences.getString(Constant.NOK_TELEPHONE, "");
                String cardNumber = preferences.getString(Constant.NATIONAL_ID_CARD_NUMBER, "");
                String passportNumber = preferences.getString(Constant.PASSPORT_NUMBER, "");
                String cardBackImageUri = preferences.getString(Constant.BACK_ID_PHOTO_URI, "");
                String resultSlipUri = preferences.getString(Constant.RESULT_SLIP_URI, "");
                String LC1 = preferences.getString(Constant.LC1_LETTER_URI, "");

                if (firstName.isEmpty() || dateOfBirth.isEmpty()) {
                    aQuery.open(BioDataActivity.class);
                }
                else if (nok_firstName.isEmpty() || nok_phone_number.isEmpty()){
                    aQuery.open(NOKActivity.class);
                }
                else if (cardNumber.isEmpty() || passportNumber.isEmpty() || cardBackImageUri.isEmpty()){
                    aQuery.open(NationalityActivity.class);
                }
                else if (resultSlipUri.isEmpty() || LC1.isEmpty()){
                    aQuery.openFromRight(DocumentActivity.class);
                }else{
                    aQuery.open(SalesAgreementActivity.class);
                }
            }
        });

    }

    private void checkStatusApplicationStatus(){
        aQuery.id(R.id.card_check_application_status).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.open(ConfirmApplicaitonActivity.class);
            }
        });
    }
}
