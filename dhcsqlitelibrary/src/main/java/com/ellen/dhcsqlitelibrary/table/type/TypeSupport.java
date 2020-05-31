package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;

import java.lang.reflect.Field;

/**
 *
 * @param <T> T为您要拦截的类型，E为数据库中保存的类型
 * @param <E>
 */
public interface TypeSupport<T,E> {

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
    T toObj(Field field, E sqlValue);

    /**
     * 类型映射为数据库能存储的值
     *
     * @param dataValue
     * @return
     */
    E toValue(Field field, T dataValue);
}
