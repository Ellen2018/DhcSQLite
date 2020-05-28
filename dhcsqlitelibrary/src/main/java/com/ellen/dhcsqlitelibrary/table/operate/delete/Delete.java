package com.ellen.dhcsqlitelibrary.table.operate.delete;

public interface Delete {
    boolean deleteTable();
    int deleteReturnCount(String whereSql);
    void delete(String whereSql);
    boolean deleteByMajorKey(Object majorKeyValue);
    void clear();
}
