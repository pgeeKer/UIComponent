package com.uicomponents.resource.proxy;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.uicomponents.http.response.CommonResponse;
import com.uicomponents.resource.Resource;

/**
 * 仅通过网络获取数据
 *
 * @author g0st、
 * @data 17/8/27
 */
public abstract class NetworkOnlyProxy<Request, Result extends CommonResponse> extends DataLoadProxy<Request, Result> {

    @Override
    protected boolean shouldNetworkLoad(Request request, Result localResult) {
        return true;
    }

    @Override
    protected void saveNetworkResult(Request request, Result result) {}

    @Override
    public void startLoad(Request request) {
        if (shouldNetworkLoad(request, null)) {
            loadRemoteData(request);
        }
    }

    private void loadRemoteData(final Request request) {
        LiveData<Result> source = loadNetworkData(request);
        result.addSource(source, new Observer<Result>() {
            @Override
            public void onChanged(@Nullable Result data) {
                if (data != null && data.isSuccess()) {
                    result.setValue(Resource.remoteSuccess(data));
                    saveResult(request, data);
                } else {
                    result.setValue(Resource.remoteError("请求数据异常，请稍后重试", data));
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void saveResult(final Request request, final Result data) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveNetworkResult(request, data);
                return null;
            }
        };
    }
}
