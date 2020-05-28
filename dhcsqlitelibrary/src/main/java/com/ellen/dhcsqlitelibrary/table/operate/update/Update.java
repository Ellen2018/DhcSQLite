package com.ellen.dhcsqlitelibrary.table.operate.update;

import java.util.List;

public interface Update<T>{
    void saveOrUpdateByMajorKey(T t);
    void saveOrUpdateByMajorKey(List<T> tList);
    boolean updateByMajorKeyReturn(T t);
    void updateByMajorKey(T t);
    int updateReturnCount(T t, String whereSQL);
    void update(T t, String whereSQL);
    boolean reNameTable(String newName);
}
