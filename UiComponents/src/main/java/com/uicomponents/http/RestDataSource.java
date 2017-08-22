package com.uicomponents.http;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author g0st、
 * @data 17/8/22
 */
public class RestDataSource {

    public static final String baseUrl = "";

    @Getter private static Retrofit retrofitInstance;

    public RestDataSource(boolean isRelease) {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (!isRelease) {
            clientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        OkHttpClient okHttpClient = clientBuilder.build();

        retrofitInstance = new Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        initHttpServiceInterface(okHttpClient);
    }

    private void initHttpServiceInterface(OkHttpClient okHttpClient) {

    }

    private static <T> T createService(OkHttpClient okHttpClient, String baseUrl, Class<T> clazz) {
        return new Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(clazz);
    }
}
