package com.ellen.dhcsqlitelibrary.table.helper.json;

public interface JsonFormat {

    String toJson(Object obj);
    <E> E toObject(String json,Class jsonClass);
}
