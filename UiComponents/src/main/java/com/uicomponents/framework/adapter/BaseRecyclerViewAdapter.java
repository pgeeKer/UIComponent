package com.uicomponents.framework.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.uicomponents.framework.mvp.BasePresenter;
import com.uicomponents.framework.mvp.BaseView;

/**
 * 封装一个常规的 RecyclerView Adapter 基类, 绑定对应的数据 Model 集合
 *
 * - Model 中根据枚举规定不同的类型
 *
 * @author g0st、
 * @data 17/8/21
 */
public abstract class BaseRecyclerViewAdapter<M>
    extends DataRecyclerViewAdapter<BaseRecyclerViewAdapter.ViewHolder, M> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseView baseView = instanceView(parent, viewType);
        BasePresenter basePresenter = instancePresenter(baseView.getView(), viewType);
        return new ViewHolder(baseView.getView(), basePresenter);
    }

    /**
     * 根据 type 实例化对应的 Presenter
     */
    protected abstract BasePresenter instancePresenter(View view, int viewType);

    /**
     * 根据 type 实例化对应的 View
     */
    protected abstract BaseView instanceView(ViewGroup parent, int viewType);

    /**
     * 获取 Item 数据类型
     */
    protected abstract int getItemModelType(int position);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.presenter == null) {
            return;
        }
        doBind(holder.presenter, getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getItemModelType(position);
    }

    @SuppressWarnings("unchecked")
    private void doBind(BasePresenter presenter, M model) {
        presenter.unbind();
        presenter.bind(model);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        BasePresenter presenter;

        ViewHolder(View itemView, BasePresenter presenter) {
            super(itemView);
            this.presenter = presenter;
        }
    }
}
