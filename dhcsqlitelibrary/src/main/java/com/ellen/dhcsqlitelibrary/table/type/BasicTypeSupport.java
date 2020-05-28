package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.dhcsqlitelibrary.table.annotation.field.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.exception.BoolNoCanSaveException;
import com.ellen.dhcsqlitelibrary.table.helper.ReflectHelper;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;

/**
 * 基本数据类型支持类
 */
public class BasicTypeSupport implements TypeSupport {

    private ReflectHelper reflectHelper;
    private SetBooleanValue setBooleanValue;

    public BasicTypeSupport(ReflectHelper reflectHelper,SetBooleanValue setBooleanValue) {
        this.reflectHelper = reflectHelper;
        this.setBooleanValue = setBooleanValue;
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
        SQLFieldType sqlField = null;
        if(reflectHelper.isBooleanType(field)){
            Object booleanSaveValue = setBooleanValue.setBooleanValue(field.getName(),true);
            if(reflectHelper.isBasicType(booleanSaveValue)){
                //是基本类型
                SQLFieldTypeEnum sqlFieldTypeEnum = reflectHelper.getSqlStringType(booleanSaveValue.getClass());
                sqlField = new SQLFieldType(sqlFieldTypeEnum,null);
            }else {
                //不是 抛出异常
                throw new BoolNoCanSaveException("布尔值类型只能映射为基本类型，您映射的类型:"+booleanSaveValue.getClass().getName()+" 不被支持!");
            }
        }else {
            sqlField = new SQLFieldType(reflectHelper.getSqlStringType(field.getType()), null);
        }
        return sqlField;
    }

    @Override
    public boolean isType(Field field) {
        return reflectHelper.isBasicType(field);
    }

    @Override
    public Object toObj(Field field, Object sqlValue) {
        if(reflectHelper.isBooleanType(field)){
            Object trueValue = setBooleanValue.setBooleanValue(field.getName(),true);
            if(trueValue.getClass() == Character.class){
                String str  = (String) trueValue;
                trueValue = str.charAt(0);
            }else if(trueValue.getClass() == Boolean.class){
                Boolean aBoolean = (Boolean) trueValue;
                if(aBoolean){
                    trueValue = 1;
                }else {
                    trueValue = 0;
                }
            }
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
            if(dataValue != null) {
                boolean bool = (boolean) dataValue;
                Object value = setBooleanValue.setBooleanValue(field.getName(), bool);
                if (value.getClass() == Character.class) {
                    Character character = (Character) value;
                    return character.toString();
                } else if (value.getClass() == Boolean.class) {
                    Boolean b = (Boolean) value;
                    if (b) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    return value;
                }
            }else {
                return null;
            }
        } else {
            return dataValue;
        }
    }

    public interface  SetBooleanValue{
        Object setBooleanValue(String classFieldName, boolean value);
    }
}
