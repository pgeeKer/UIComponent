package com.uicomponents.resource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;

/**
 * 描述数据状态
 *
 * @author g0st、
 * @data 17/8/27
 */
@Getter
public class Resource<T> {

    public enum Status {
        LOCAL_LOADING, LOCAL_SUCCESS, LOCAL_ERROR, REMOTE_LOADING, REMOTE_SUCCESS, REMOTE_ERROR
    }

    private T data;
    private Status status;
    private String message;

    private Resource(Status status, T data, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public static <T> Resource<T> localLoading(@Nullable T data) {
        return new Resource<>(Status.LOCAL_LOADING, data, null);
    }

    public static <T> Resource<T> localSuccess(@NonNull T data) {
        return new Resource<>(Status.LOCAL_SUCCESS, data, null);
    }

    public static <T> Resource<T> localError(String msg, @Nullable T data) {
        return new Resource<>(Status.LOCAL_ERROR, data, msg);
    }

    public static <T> Resource<T> remoteLoading(@Nullable T localData) {
        return new Resource<>(Status.REMOTE_LOADING, localData, null);
    }

    public static <T> Resource<T> remoteSuccess(@NonNull T data) {
        return new Resource<>(Status.REMOTE_SUCCESS, data, null);
    }

    public static <T> Resource<T> remoteError(String msg, @Nullable T localData) {
        return new Resource<>(Status.REMOTE_ERROR, localData, msg);
    }
}
