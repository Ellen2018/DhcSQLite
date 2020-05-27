package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.dhcsqlitelibrary.table.annotation.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.operate.ReflectHelper;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;

import java.lang.reflect.Field;

/**
 * 基本数据类型支持类
 */
public class BasicTypeSupport implements TypeSupport {

    private ReflectHelper reflectHelper;

    public BasicTypeSupport(ReflectHelper reflectHelper) {
        this.reflectHelper = reflectHelper;
    }

    @Override
    public String setSqlFieldName(Field field) {
        DhcSqlFieldName dhcSqlFieldName = field.getAnnotation(DhcSqlFieldName.class);
        if(dhcSqlFieldName != null){
            if(dhcSqlFieldName.sqlFieldName() != null && dhcSqlFieldName.sqlFieldName().length() > 0){
                return dhcSqlFieldName.sqlFieldName();
            }else {
                return field.getName();
            }
        }else {
            return field.getName();
        }
    }

    @Override
    public SQLFieldType setSQLiteType(Field field) {
        return new SQLFieldType(reflectHelper.getSqlStringType(field.getType()),null);
    }

    @Override
    public boolean isType(Field field) {
        return reflectHelper.isBasicType(field);
    }

    @Override
    public Object toObj(Field field, Object sqlValue) {
        if(reflectHelper.isBooleanType(field)){
            Object trueValue = setBooleanValue(field.getName(),true);
            if(trueValue.equals(sqlValue)){
                return true;
            }else {
                return false;
            }
        }else {
            return sqlValue;
        }
    }

    @Override
    public Object toValue(Field field, Object dataValue) {
        if(reflectHelper.isBooleanType(field)){
            boolean bool = (boolean) dataValue;
            return setBooleanValue(field.getName(),bool);
        } else {
            return dataValue;
        }
    }

    public Object setBooleanValue(String classFieldName, boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

}
