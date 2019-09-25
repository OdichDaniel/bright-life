package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.aquery.AQuery;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.entities.Constant;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NOKActivity extends AppCompatActivity {
    AQuery aQuery;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AwesomeValidation validator;
    String firstName, lastName, email, telephone;
    ApiService service;
    Call<ResponseBody> call;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nok);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        aQuery = new AQuery(this);
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        service = RetrofitBuilder.createService(ApiService.class);
        setNokBioData();
        goToNext();
        setUpRules();
        goToNextFragment();
        goToPreviousFragment();
        //toolbarSetUp();
    }

    private void storeBioData() {
        editor.putString(Constant.NOK_FIST_NAME, aQuery.id(R.id.et_nok_first_name).text()).commit();
        editor.putString(Constant.NOK_LAST_NAME, aQuery.id(R.id.et_nok_last_name).text()).commit();
        editor.putString(Constant.NOK_EMAIL, aQuery.id(R.id.et_nok_email).text()).commit();
        editor.putString(Constant.NOK_TELEPHONE, aQuery.id(R.id.et_nok_phone_number).text()).commit();
    }

    private void goToNextFragment() {
        aQuery.id(R.id.next_fragment_nok).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nokFirstName = aQuery.id(R.id.et_nok_first_name).text();
                String nokPhoneNumber = aQuery.id(R.id.et_nok_phone_number).text();

                if (nokFirstName.isEmpty() || nokPhoneNumber.isEmpty()) {
                    aQuery.toast("Missing fields, Please fill in all the required fields");
                    return;
                } else {
                    aQuery.openFromRight(NationalityActivity.class);
                    finish();
                }
            }
        });
    }

    private void goToPreviousFragment() {
        aQuery.id(R.id.previous_fragment_nok).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(BioDataActivity.class);
                finish();
            }
        });
    }

    //Picks the data from the shared preference and sets it on the respective views
    private void setNokBioData() {
        String prefFirstName = preferences.getString(Constant.NOK_FIST_NAME, "");
        aQuery.id(R.id.et_nok_first_name).text(prefFirstName);

        String prefLastName = preferences.getString(Constant.NOK_LAST_NAME, "");
        aQuery.id(R.id.et_nok_last_name).text(prefLastName);

        String prefEmail = preferences.getString(Constant.NOK_EMAIL, "");
        aQuery.id(R.id.et_nok_email).text(prefEmail);

        String prefTelephone = preferences.getString(Constant.NOK_TELEPHONE, "");
        aQuery.id(R.id.et_nok_phone_number).text(prefTelephone);
    }


    private void goToNext() {
        aQuery.id(R.id.btn_confirm_nok_bio).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.clear();
                if (validator.validate()) {
                    String applicantTelephone = preferences.getString(Constant.REGISTERED_TELEPHONE, "");
                    storeBioData();
                    firstName = aQuery.id(R.id.et_nok_first_name).text();
                    lastName = aQuery.id(R.id.et_nok_last_name).text();
                    email = aQuery.id(R.id.et_nok_email).text();
                    telephone = aQuery.id(R.id.et_nok_phone_number).text();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("next_of_kin_first_name", firstName);
                    jsonObject.addProperty("next_of_kin_last_name", lastName);
                    jsonObject.addProperty("next_of_kin_telephone", telephone);
                    jsonObject.addProperty("next_of_kin_email", email);
                    jsonObject.addProperty("telephone", applicantTelephone);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    call = service.updateApplication(requestBody);
                    kProgressHUD = KProgressHUD.create(NOKActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait...")
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                aQuery.openFromRight(NationalityActivity.class);
                                finish();
                                kProgressHUD.dismiss();
                            } else {
                                switch (response.code()) {
                                    case 500:
                                        aQuery.toast(getString(R.string.network_error));
                                        kProgressHUD.dismiss();
                                        break;
                                    case 400:
                                        aQuery.toast(getString(R.string.network_error));
                                        kProgressHUD.dismiss();
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            kProgressHUD.dismiss();
                            aQuery.toast(getString(R.string.network_error));
                        }
                    });
                }

            }
        });
    }

    public void setUpRules() {
        validator.addValidation(this, R.id.et_nok_first_name, RegexTemplate.NOT_EMPTY, R.string.error_nok_first_name);
        validator.addValidation(this, R.id.et_nok_last_name, RegexTemplate.NOT_EMPTY, R.string.error_nok_last_name);
        validator.addValidation(this, R.id.et_nok_phone_number, RegexTemplate.NOT_EMPTY, R.string.error_nok_phone_number);
    }
}
