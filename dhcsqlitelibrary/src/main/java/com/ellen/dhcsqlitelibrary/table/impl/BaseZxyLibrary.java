package com.ellen.dhcsqlitelibrary.table.impl;

import java.util.List;

public interface BaseZxyLibrary {

    /**
     * 重命名表
     * @param oldTableName 旧的名字
     * @param newTableName 新的名字
     */
    void reNameTable(String oldTableName,String newTableName);

    /**
     * 清空表tableName的数据
     * @param tableName
     */
    void clearTable(String tableName);

    /**
     * 是否存在tableName的表
     * @param tableName
     * @return
     */
    boolean isExist(String tableName);

    /**
     * 将整个库里的表全部删除
     */
    void clearLibrary();

    /**
     * 获取所有的库中所有表名
     * @return
     */
    String[] getAllTableName();

    /**
     * 获取表的个数
     * @return
     */
    int getTableCount();
}
