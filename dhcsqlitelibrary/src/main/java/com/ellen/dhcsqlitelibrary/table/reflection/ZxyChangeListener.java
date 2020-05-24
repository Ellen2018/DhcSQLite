package com.ellen.dhcsqlitelibrary.table.reflection;

/**
 * 数据变化监听
 */
public interface ZxyChangeListener {

    /**
     * 当数据变化时回调
     * saveDataAndDeleteAgo此系列方法会触发两次此方法的回调，后期需要更改
     * 希望后期按照增删改查进行架构的改造,让操作单元化，方便api的理解与维护更新
     */
    void onDataChange();
}
