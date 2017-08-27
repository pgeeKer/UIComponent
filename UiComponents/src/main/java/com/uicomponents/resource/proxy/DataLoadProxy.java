package com.uicomponents.resource.proxy;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.uicomponents.http.response.CommonResponse;
import com.uicomponents.resource.Resource;

/**
 * 封装数据加载的代理类
 *
 * @author g0st、
 * @data 17/8/27
 */
public abstract class DataLoadProxy<Request, Result extends CommonResponse> {

    protected final MediatorLiveData<Resource<Result>> result = new MediatorLiveData<>();

    /**
     * 加载本地数据
     *
     * @param request 请求携带的参数
     */
    @MainThread
    protected abstract LiveData<Result> loadLocalData(Request request);

    /**
     * 加载远程数据
     *
     * @param request 请求参数
     */
    @MainThread
    protected abstract LiveData<Result> loadNetworkData(Request request);

    /**
     * 根据本地结果判断是否需要进行网络拉取数据
     *
     * @param requests 请求参数
     * @param localResult 本地数据
     */
    @MainThread
    protected abstract boolean shouldNetworkLoad(Request requests, Result localResult);

    /**
     * 缓存远程数据到本地
     */
    @WorkerThread
    protected abstract void saveNetworkResult(Request request, Result result);

    /**
     * 开启数据请求操作
     */
    @MainThread
    public abstract void startLoad(Request request);

    /**
     * 返回数据结果
     */
    public final LiveData<Resource<Result>> getAsLiveData() {
        return result;
    }
}
