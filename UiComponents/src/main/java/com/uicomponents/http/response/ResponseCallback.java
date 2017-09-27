package com.uicomponents.http.response;

import com.google.gson.JsonParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 自定义的Retrofit Callback
 *
 * @author panxiangxing
 * @data 17/1/2
 */

public abstract class ResponseCallback<T> implements Callback<T> {

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
            if (commonResponse.getStatusCode() == 1) {
                success(response.body());
                return;
            }
        }

        failure(response.code(), "服务器异常，请稍后重试!");
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof JsonParseException) {
            failure(0, "请求失败...");
            return;
        }
        failure(0, "请检查您的网络连接...");
    }
}
