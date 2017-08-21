package com.uicomponents.framework.mvp;

/**
 * Presenter 的基类, 所有 presenter 继承该类，并重写bind方法，Presenter 会维护一个Model和View的映射关系
 *
 * <p>
 * 将数据 Model 绑定到 View 上的逻辑，Presenter 描述了View的业务逻辑。
 * Presenter 只负责呈现的作用
 * </p>
 *
 * @author panxiangxing
 * @data 17/8/21
 */
public abstract class BasePresenter<V extends BaseView, M> {

    protected V view;

    public BasePresenter(V view) {
        this.view = view;
    }

    /**
     * 将数据 Model 绑定到 View 上
     */
    public abstract void bind(M model);

    /**
     * 解绑时做一些相应的工作
     */
    public void unbind() {}
}
