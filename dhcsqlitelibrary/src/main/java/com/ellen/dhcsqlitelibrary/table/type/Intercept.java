package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.dhcsqlitelibrary.table.annotation.field.DhcSqlFieldName;

import java.lang.reflect.Field;

public abstract class Intercept<T,E> implements TypeSupport<T,E>{

    @Override
    public String setSqlFieldName(Field field) {
        DhcSqlFieldName dhcSqlFieldName = field.getAnnotation(DhcSqlFieldName.class);
        if (dhcSqlFieldName != null) {
            if (dhcSqlFieldName.sqlFieldName() != null && dhcSqlFieldName.sqlFieldName().length() > 0) {
                return dhcSqlFieldName.sqlFieldName();
            } else {
                return field.getName();
            }
        } else {
            return field.getName();
        }
    }
}
