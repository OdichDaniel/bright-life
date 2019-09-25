package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.aquery.AQuery;
import com.google.gson.JsonObject;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.adapters.ProductAdapter;
import com.matchstick.brightlife.models.Product;
import com.matchstick.brightlife.network.ApiService;
import com.matchstick.brightlife.network.RetrofitBuilder;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadActivity extends AppCompatActivity {
    Call<ResponseBody> leadCall;
    ApiService service;
    String customerName, customerContact, customerLocation, productBought, quantityBought;
    AQuery aQuery;
    Toolbar tb;
    Call<List<Product>> call;
    private List<Product> productList;
    Spinner productSpinner;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        service = RetrofitBuilder.createService(ApiService.class);
        aQuery = new AQuery(this);
        productSpinner = (Spinner) findViewById(R.id.spinner_product_name);

        tb = (Toolbar) findViewById(R.id.toolbar);
        openSaleActivity();
        getProductList();
        createLead();
        toolbarSetUp();
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

    private void getProductList(){
        call = service.getProducts();
        aQuery.id(R.id.loader_layout).show();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()){
                    aQuery.id(R.id.loader_layout).hide();
                    productList = response.body();
                    productAdapter = new ProductAdapter(LeadActivity.this, productList);
                    productSpinner.setAdapter(productAdapter);
                    productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    private void createLead() {
        customerContact = aQuery.id(R.id.et_customer_telephone).text();
        customerLocation = aQuery.id(R.id.et_customer_location).text();
        customerName = aQuery.id(R.id.et_customer_name).text();
        productBought = aQuery.id(R.id.et_product_name).text();
        quantityBought = aQuery.id(R.id.et_quantity_bought).text();

        if (customerName.isEmpty() || customerLocation.isEmpty() || quantityBought.isEmpty() || customerContact.isEmpty()){
            aQuery.toast(getString(R.string.missing_fields));
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_name", customerName);
        jsonObject.addProperty("customer_telephone", customerContact);
        jsonObject.addProperty("location", customerLocation);
        jsonObject.addProperty("quantity_bought", quantityBought);

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

    private void openSaleActivity(){
        aQuery.id(R.id.btn_convert_lead_to_sale).click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aQuery.openFromRight(SaleListActivity.class);
            }
        });
    }

}
