package com.ellen.dhcsqlitelibrary.table.operate.delete;

public interface Delete {
    boolean deleteTable();
    int delete(String whereSQL);
    boolean deleteByMajorKey(Object majorKeyValue);
    void clear();
}
