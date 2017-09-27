package com.uicomponents.http;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author g0st、
 * @data 17/8/27
 */
public final class RHttp {

    private static List<Map<String, Object>> restServiceList = new ArrayList<>();

    private RHttp() {}

    public static <T> void init(String baseUrl, boolean isRelease, List<Class<T>> clazz) {
        initRetrofit(baseUrl, isRelease, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRestService(Class<T> clazz) {
        for (Map<String, Object> map : restServiceList) {
            Object object = map.get(clazz.getName());
            if (object != null) {
                return (T) object;
            }
        }
        throw new RuntimeException("Could not find " + clazz.getSimpleName());
    }

    private static <T> void initRetrofit(String baseUrl, boolean isRelease, List<Class<T>> clazz) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (!isRelease) {
            clientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        OkHttpClient okHttpClient = clientBuilder.build();
        if (clazz == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        for (Class aClass : clazz) {
            map.put(aClass.getName(), createService(okHttpClient, baseUrl, aClass));
            restServiceList.add(map);
        }
    }

    private static <T> T createService(OkHttpClient okHttpClient, String baseUrl, Class<T> clazz) {
        return new Retrofit.Builder().client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(clazz);
    }
}
