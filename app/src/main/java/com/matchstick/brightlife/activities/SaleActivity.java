package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.aquery.AQuery;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.gson.JsonObject;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleActivity extends AppCompatActivity {
    Toolbar tb;
    Call<ResponseBody> leadCall;
    ApiService service;
    String customerName, customerContact, customerLocation, productBought, quantityBought, transactionDate;
    AQuery aQuery;
    AwesomeValidation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);
        tb = (Toolbar) findViewById(R.id.toolbar);
        toolbarSetUp();
        aQuery = new AQuery(this);
        validation = new AwesomeValidation(ValidationStyle.COLORATION);
        getTransactionDate();
    }

    private void toolbarSetUp() {
        if (tb != null) {
            setSupportActionBar(tb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Record a sale");
            tb.setTitleTextColor(Color.WHITE);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void getTransactionDate() {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                //tilDateOfBirth.setText(sdf.format(myCalendar.getTime()));
                aQuery.id(R.id.et_transaction_date).text(sdf.format(myCalendar.getTime()));
            }

        };
        aQuery.id(R.id.et_transaction_date).click(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SaleActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private void recordSale() {
        customerContact = aQuery.id(R.id.et_customer_telephone).text();
        customerLocation = aQuery.id(R.id.et_customer_location).text();
        customerName = aQuery.id(R.id.et_customer_name).text();
        productBought = aQuery.id(R.id.et_product_name).text();
        quantityBought = aQuery.id(R.id.et_product_name).text();
        transactionDate = aQuery.id(R.id.et_transaction_date).text();

        if (customerName.isEmpty() || customerLocation.isEmpty() || quantityBought.isEmpty() || customerContact.isEmpty()){
            aQuery.toast(getString(R.string.missing_fields));
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_name", customerName);
        jsonObject.addProperty("customer_contact", customerContact);
        jsonObject.addProperty("customer_location", customerLocation);
        jsonObject.addProperty("quantity_bought", quantityBought);
        jsonObject.addProperty("transaction_date", transactionDate);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        leadCall = service.createLead(requestBody);
        leadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    aQuery.toast("Sales lead successfully created");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
