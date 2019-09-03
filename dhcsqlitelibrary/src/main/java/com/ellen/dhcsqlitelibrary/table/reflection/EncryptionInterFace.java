package com.ellen.dhcsqlitelibrary.table.reflection;

public interface EncryptionInterFace {

    //加密
    Object encryption(String classFieldName, String sqlFieldName, Class typeClass, Object value);
    //解密
    Object decrypt(String classFieldName, String sqlFieldName, Class typeClass, Object value);
}
