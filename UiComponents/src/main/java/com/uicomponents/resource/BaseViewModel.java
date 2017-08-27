package com.uicomponents.resource;

import android.arch.lifecycle.ViewModel;

/**
 * ViewModel 的基类，负责ViewModel的初始化
 *
 * - 所有的 ViewModel 都继承至该类。
 *
 * @author g0st、
 * @data 17/8/23
 */
public abstract class BaseViewModel extends ViewModel {

    public BaseViewModel() {
        initViewModel();
    }

    /**
     * 供子类 ViewModel 初始化
     */
    protected abstract void initViewModel();
}
