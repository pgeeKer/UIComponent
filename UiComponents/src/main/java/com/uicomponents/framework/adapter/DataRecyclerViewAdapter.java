package com.uicomponents.framework.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import lombok.Getter;

/**
 * RecyclerView adapter 数据基类
 *
 * @author g0st、
 */
public abstract class DataRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, M> extends RecyclerView.Adapter<VH> {

    private List<M> dataModelList;

    public List<M> getDataModelList() {
        return dataModelList;
    }

    @Override
    public int getItemCount() {
        return dataModelList == null ? 0 : dataModelList.size();
    }

    /**
     * 给 RecyclerView 设置数据
     */
    public void setDataModelList(List<M> dataModelList) {
        if (dataModelList == null) {
            throw new IllegalStateException("dataModel not allow be null");
        }
        this.dataModelList = dataModelList;
        notifyDataSetChanged();
    }

    /**
     * 根据位置插入某个数据
     */
    public void add(M model, int position) {
        if (dataModelList == null) {
            throw new IllegalStateException("should call method setDataModelList before");
        }
        position = (position < 0 || position > getItemCount()) ? getItemCount() : position;
        dataModelList.add(position, model);
        notifyItemInserted(position);
        notifyItemAfterPosition(position);
    }

    /**
     * 根据位置删除某个数据
     */
    public void remove(int position) {
        if (position < 0 || position > getItemCount()) {
            return;
        }
        dataModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemAfterPosition(position);
    }

    /**
     * 根据位置获取某个数据 Model
     */
    public M getItem(int position) {
        if (position >= getItemCount()) {
            return null;
        }
        return dataModelList.get(position);
    }

    /**
     * 清楚所有数据
     */
    public void clear() {
        dataModelList.clear();
        notifyDataSetChanged();
    }

    private void notifyItemAfterPosition(int position) {
        for (int i = position; i < getItemCount(); i++) {
            notifyItemChanged(position);
        }
    }
}
