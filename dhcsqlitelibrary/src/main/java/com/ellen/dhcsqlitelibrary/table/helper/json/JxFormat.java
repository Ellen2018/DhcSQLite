package com.ellen.dhcsqlitelibrary.table.helper.json;

/**
 * 解析器
 * Json
 * xml
 * 其它格式都行
 */
public interface JxFormat {

    String toJxString(Object obj);
    <E> E toObject(String json,Class jsonClass);
}
