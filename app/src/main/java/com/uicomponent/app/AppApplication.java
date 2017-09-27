package com.uicomponent.app;

import android.app.Application;

import com.uicomponents.http.RHttp;

import java.util.Arrays;

/**
 * @author g0st、
 * @data 17/8/27
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RHttp.init("", true, Arrays.asList(MainActivity.class));
    }
}
