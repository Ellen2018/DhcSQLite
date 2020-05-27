package com.ellen.dhcsqlitelibrary.table.operate.update;

import java.util.List;

public interface Update<T>{
    void saveOrUpdateByMajorKey(T t);
    void saveOrUpdateByMajorKey(List<T> tList);
    boolean updateByMajorKey(T t);
    int update(T t, String whereSQL);
    boolean reNameTable(String newName);
}
