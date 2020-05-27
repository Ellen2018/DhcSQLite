package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;

import java.lang.reflect.Field;

public abstract interface TypeSupport {

    /**
     * 设置该属性映射到数据库中的字段名字
     * @param field
     * @return
     */
    String setSqlFieldName(Field field);

    /**
     * 设置类型在数据库中的存储类型
     * @param field
     * @return
     */
    SQLFieldType setSQLiteType(Field field);

    /**
     * 判断是否为该类型
     *
     * @return
     */
    boolean isType(Field field);

    /**
     * 从数据库获取的值中恢复
     *
     * @param sqlValue
     * @return
     */
    Object toObj(Field field, Object sqlValue);

    /**
     * 类型映射为数据库能存储的值
     *
     * @param dataValue
     * @return
     */
    Object toValue(Field field, Object dataValue);
}
