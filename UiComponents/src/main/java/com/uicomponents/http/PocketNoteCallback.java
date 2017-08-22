package com.uicomponents.http;

import com.google.gson.JsonParseException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 自定义的Retrofit Callback
 *
 * @author panxiangxing
 * @data 17/1/2
 */

public abstract class PocketNoteCallback<T> implements Callback<T> {

    /**
     * 请求成功的回调
     */
    public abstract void success(T result);

    /**
     * 请求失败回调
     *
     * @param errorCode 状态码
     * @param msg 失败信息
     */
    @SuppressWarnings("unused")
    public void failure(int errorCode, String msg) {}

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            if (response.body() != null && !(response.body() instanceof CommonResponse)) {
                throw new IllegalStateException("api response must extends CommonResponse");
            }

            //204 body是空，也算是成功
            if (response.body() == null) {
                success(null);
                return;
            }

            CommonResponse commonResponse = (CommonResponse) response.body();
            if (commonResponse.getStatus() == 1) {
                success(response.body());
                return;
            }
        }

        CommonResponse commonResponse = parseMessageFromResponse(response);
        if (commonResponse == null) {
            failure(0, "服务器异常，请稍后重试!");
        } else {
            failure(commonResponse.getStatus(), commonResponse.getInfo());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof JsonParseException) {
            failure(0, "请求失败...");
            return;
        }
        failure(0, "请检查您的网络连接...");
    }

    private CommonResponse parseMessageFromResponse(Response<T> response) {
        if (response.isSuccessful()) {
            return (CommonResponse) response.body();
        }
        Retrofit retrofit = RestDataSource.getRetrofitInstance();
        try {
            return (CommonResponse) retrofit.responseBodyConverter(CommonResponse.class,
                CommonResponse.class.getAnnotations()).convert(response.errorBody());
        } catch (IOException ignored) {
        }
        return null;
    }
}
