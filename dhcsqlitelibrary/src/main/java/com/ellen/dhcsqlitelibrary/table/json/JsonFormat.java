package com.ellen.dhcsqlitelibrary.table.json;

public interface JsonFormat {

    String toJson(Object obj);
    <E> E toObject(String json,Class jsonClass);
}
