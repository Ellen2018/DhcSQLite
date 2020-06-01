package com.ellen.dhcsqlitelibrary.table.operate.table;

public interface Table {
    String getMajorKeyName();
    void exeSql(String sql);

    /**
     * 关闭db,以防资源浪费
     */
    void close();
}
