package com.ellen.dhcsqlitelibrary.table.json;

import java.util.List;

public interface JsonFormat {

    String toJson(Object obj);
    <E> E toObject(String json,Class jsonClass);
    <T> String toJsonByList(List<T> tList);
}
