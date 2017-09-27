package com.uicomponent.app;

import android.app.Application;

import com.uicomponents.http.RHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author g0st、
 * @data 17/8/27
 */
public class AppApplication extends Application {

    List<Class> classList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        classList.add(MainActivity.class);
        classList.add(Application.class);

        RHttp.init("", true, classList);
    }
}
