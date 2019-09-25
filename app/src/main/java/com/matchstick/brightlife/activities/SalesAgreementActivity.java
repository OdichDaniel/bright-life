package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.aquery.AQuery;
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

public class SalesAgreementActivity extends AppCompatActivity {
    AQuery aQuery;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Call<ResponseBody> call;
    ApiService service;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_agreement);
        aQuery = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        service = RetrofitBuilder.createService(ApiService.class);
        goToConfirmDocument();
    }

    private void goToConfirmDocument() {
        aQuery.id(R.id.btn_agree_to_terms).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telephone = pref.getString(Constant.REGISTERED_TELEPHONE, "");
                String nin = pref.getString(Constant.NATIONAL_ID_CARD_NUMBER, "");
                boolean nationality;
                if (nin.isEmpty()){
                    nationality = false;
                }else {
                    nationality = true;
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("agreed_to_terms_and_conditions", true);
                jsonObject.addProperty("above_eighteen", true);
                jsonObject.addProperty("ugandan_by_nationality", nationality);
                jsonObject.addProperty("telephone", telephone);

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                call = service.agreeToSalesTerms(requestBody);
                kProgressHUD = KProgressHUD.create(SalesAgreementActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait ...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            aQuery.open(ConfirmApplicaitonActivity.class);
                            kProgressHUD.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        aQuery.toast(getString(R.string.network_error));
                        kProgressHUD.dismiss();
                    }
                });

            }
        });
    }
}
