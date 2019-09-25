package com.matchstick.brightlife.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.matchstick.brightlife.BuildConfig;
import com.matchstick.brightlife.entities.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {
    private static final String BASE_URL = "http://brightlifeug.herokuapp.com/api/";
    private static final OkHttpClient client = buildClient();
    private static Retrofit retrofit = buildRetrofit(client);
    private static String token;

    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Connection", "close");
                        request = builder.build();
                        return chain.proceed(request);
                    }
                });
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
            builder.callTimeout(1, TimeUnit.MINUTES);
            builder.connectTimeout(5, TimeUnit.MINUTES);// connect timeout
            builder.writeTimeout(5, TimeUnit.MINUTES); // write timeout
            builder.readTimeout(5, TimeUnit.MINUTES);
        }
        return builder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    public static <T> T createServiceWithAuth(Class<T> service, final SessionManager sessionManager) {
        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                token = sessionManager.getToken();
                if (token != null) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
                request = builder.build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);
    }
}