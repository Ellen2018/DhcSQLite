package com.ellen.dhcsqlitelibrary.table.reflection;

import android.util.Log;

import com.ellen.dhcsqlitelibrary.table.reflection.annotation.Ignore;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflactionHelper<T> {

    public List<Field> getClassFieldList(Class<? extends T> dataClass, boolean isAddStatic){
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = dataClass.getDeclaredFields();
        if(fields != null && fields.length != 0) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                //过滤掉静态的字段
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if(!isStatic) {
                    Ignore ignore = field.getAnnotation(Ignore.class);
                    if(ignore == null){
                        fieldList.add(field);
                    }
                }else {
                    if(isAddStatic){
                        Ignore ignore = field.getAnnotation(Ignore.class);
                        if(ignore == null){
                            fieldList.add(field);
                        }
                    }
                }
            }
        }
        return fieldList;
    }

    public Object getValue(Object obj, Field targetField) {
        targetField.setAccessible(true);
        Object value = null;
        try {
            value = targetField.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public boolean isBasicType(Field field) {
        boolean b = false;
        if (field.getType() == Byte.class || field.getType().getName().equals("byte")) {
            b = true;
        } else if (field.getType() == Short.class || field.getType().getName().equals("short")) {
            b = true;
        } else if (field.getType() == Integer.class || field.getType().getName().equals("int")) {
            b = true;
        } else if (field.getType() == Long.class || field.getType().getName().equals("long")) {
            b = true;
        } else if (field.getType() == Float.class || field.getType().getName().equals("float")) {
            b = true;
        } else if (field.getType() == Double.class || field.getType().getName().equals("double")) {
            b = true;
        } else if (field.getType() == Boolean.class || field.getType().getName().equals("boolean")) {
            b = true;
        } else if (field.getType() == Character.class || field.getType().getName().equals("char")) {
            b = true;
        } else if (field.getType() == String.class) {
            b = true;
        }
        return b;
    }

    public Object getDefaultValue(Class classType) {
        if (classType == Byte.class || classType.getName().equals("byte")) {
            return 0;
        } else if (classType == Short.class || classType.getName().equals("short")) {
            return 0;
        } else if (classType == Integer.class || classType.getName().equals("int")) {
            return 0;
        } else if (classType == Long.class || classType.getName().equals("long")) {
            return 0;
        } else if (classType == Float.class || classType.getName().equals("float")) {
            return 0;
        } else if (classType == Double.class || classType.getName().equals("double")) {
            return 0;
        } else if (classType == Boolean.class || classType.getName().equals("boolean")) {
            return false;
        } else if (classType == Character.class || classType.getName().equals("char")) {
            return '0';
        } else if (classType == String.class) {
           return null;
        }else {
            return null;
        }
    }

    public SQLFieldTypeEnum getSqlStringType(Class<?> ziDuanJavaType) {
        SQLFieldTypeEnum sqlType = null;
        if (ziDuanJavaType == Byte.class || ziDuanJavaType.getName().equals("byte")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (ziDuanJavaType == Short.class || ziDuanJavaType.getName().equals("short")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (ziDuanJavaType == Integer.class || ziDuanJavaType.getName().equals("int")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (ziDuanJavaType == Long.class || ziDuanJavaType.getName().equals("long")) {
            sqlType = SQLFieldTypeEnum.BIG_INT;
        } else if (ziDuanJavaType == Float.class || ziDuanJavaType.getName().equals("float")) {
            sqlType = SQLFieldTypeEnum.REAL;
        } else if (ziDuanJavaType == Double.class || ziDuanJavaType.getName().equals("double")) {
            sqlType = SQLFieldTypeEnum.REAL;
        } else if (ziDuanJavaType == Boolean.class || ziDuanJavaType.getName().equals("boolean")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (ziDuanJavaType == Character.class || ziDuanJavaType.getName().equals("char")) {
            sqlType = SQLFieldTypeEnum.TEXT;
        } else if (ziDuanJavaType == String.class) {
            sqlType = SQLFieldTypeEnum.TEXT;
        } else {
            sqlType = SQLFieldTypeEnum.TEXT;
        }
        return sqlType;
    }

}
