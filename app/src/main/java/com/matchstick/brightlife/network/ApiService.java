package com.matchstick.brightlife.network;

import com.matchstick.brightlife.models.Branch;
import com.matchstick.brightlife.models.Product;
import com.matchstick.brightlife.models.VerificationStatus;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("application")
    Call<ResponseBody> postApplication(@Body RequestBody body);

    @POST("create-pin")
    Call<ResponseBody> createPin(@Body RequestBody requestBody);

    @POST("agent-login")
    Call<ResponseBody> login(@Body RequestBody requestBody);

    @POST("leads")
    Call<ResponseBody> createLead(@Body RequestBody requestBody);

    @POST("application")
    Call<ResponseBody> updateApplication(@Body RequestBody requestBody);

    @POST("application")
    Call<ResponseBody> uploadImages(@Body RequestBody body);

    @POST("application")
    Call<ResponseBody> agreeToSalesTerms(@Body RequestBody body);

    @GET("status/{telephone}")
    Call<VerificationStatus> checkVerificationStatus(@Path(value = "telephone") String telephone);

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("branches")
    Call<List<Branch>> getBranches();
}
