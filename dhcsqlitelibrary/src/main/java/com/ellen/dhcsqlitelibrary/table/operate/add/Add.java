package com.ellen.dhcsqlitelibrary.table.operate.add;

import java.util.List;

public interface Add<T> {
    /**
     * 保存条数据
     * @param data 数据
     * @return 保存了多少条数据
     */
    void saveData(T data);

    /**
     * 保存多条数据
     */
    void saveData(List<T> dataList);

    /**
     * 保存之前先清空表
     * @param dataList
     */
    void saveDataAndDeleteAgo(List<T> dataList);

    /**
     * 保存之前先清空表
     * @param data
     */
    void saveDataAndDeleteAgo(T data);

    /**
     * 分段存储您的数据
     * 以防数据过多时，一条SQL语句太大了
     * @param dataList
     * @param segment
     */
    void saveData(List<T> dataList,int segment);
}
