package com.ellen.dhcsqlitelibrary.table.operate.create;

/**
 * 加入回调的目的是方便调试
 */
public interface OnCreateTableCallback {
    /**
     * 创建表失败
     * @param errMessage 错误信息
     * @param tableName 表名
     * @param createSQL 创建表的SQL语句
     */
    void onCreateTableFailure(String errMessage, String tableName, String createSQL);

    /**
     * 创建表成功
     * @param tableName 表名
     * @param createSQL 创建表的SQL语句
     */
    void onCreateTableSuccess(String tableName, String createSQL);
}
