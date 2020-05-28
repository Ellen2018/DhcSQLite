package com.ellen.dhcsqlitelibrary.table.operate.table;

public interface Table {
    String getMajorKeyName();
    void exeSql(String sql);
}
