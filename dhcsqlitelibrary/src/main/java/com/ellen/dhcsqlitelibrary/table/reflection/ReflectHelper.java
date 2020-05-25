package com.ellen.dhcsqlitelibrary.table.reflection;

import com.ellen.dhcsqlitelibrary.table.annotation.Ignore;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class ReflectHelper<T> {

    List<Field> getClassFieldList(Class<? extends T> dataClass, boolean isAddStatic) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = dataClass.getDeclaredFields();
        if (fields != null && fields.length != 0) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                //过滤掉静态的字段
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (!isStatic) {
                    Ignore ignore = field.getAnnotation(Ignore.class);
                    if (ignore == null) {
                        fieldList.add(field);
                    }
                } else {
                    if (isAddStatic) {
                        Ignore ignore = field.getAnnotation(Ignore.class);
                        if (ignore == null) {
                            fieldList.add(field);
                        }
                    }
                }
            }
        }
        return fieldList;
    }

    /**
     * 判断该field是否为数据结构类型
     * 如果您需要支持更多的数据结构，那么您需要更改此处以便让它告诉整个框架他就是数据结构类型
     * @param field
     * @return
     */
    boolean isDataStructure(Field field){
        //数组
        boolean isDataStructure = false;
        if(field.getType().isArray()){
            isDataStructure = true;
        }
        Class typeClass = field.getType();
        //List
        if(typeClass == List.class ||
                typeClass == ArrayList.class || typeClass == LinkedList.class || typeClass == Vector.class){
           isDataStructure = true;
        }
        //Set
        if(typeClass == Set.class ||
                typeClass == HashSet.class || typeClass == TreeSet.class){
            isDataStructure = true;
        }
        //Map
        if(typeClass == Map.class ||
                typeClass == HashMap.class || typeClass == TreeMap.class){
            isDataStructure = true;
        }
        //Stack
        if(typeClass == Stack.class){
            isDataStructure = true;
        }
        return isDataStructure;
    }


    Object[] getValueArray(Object object, Field targetFiled) {
        Object value = getValue(object,targetFiled);
        if(value == null)return null;
        String name = value.getClass().getSimpleName();
        Object[] objectArray = null;
        if(name.equals("byte[]")){
            byte[] byteArray = (byte[]) getValue(object, targetFiled);
            objectArray = new Byte[byteArray.length];
            for (int i = 0; i < byteArray.length; i++) {
                objectArray[i] = byteArray[i];
            }
        }else if(name.equals("short[]")){
            short[] shortArray = (short[]) getValue(object, targetFiled);
            objectArray = new Short[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) {
                objectArray[i] = shortArray[i];
            }
        } else if (name.equals("int[]")) {
            int[] intArray = (int[]) getValue(object, targetFiled);
            objectArray = new Integer[intArray.length];
            for (int i = 0; i < intArray.length; i++) {
                objectArray[i] = intArray[i];
            }
        }else if(name.equals("long[]")){
            long[] longArray = (long[]) getValue(object, targetFiled);
            objectArray = new Long[longArray.length];
            for (int i = 0; i < longArray.length; i++) {
                objectArray[i] = longArray[i];
            }
        }else if(name.equals("boolean[]")){
            boolean[] booleanArray = (boolean[])getValue(object, targetFiled);
            objectArray = new Boolean[booleanArray.length];
            for (int i = 0; i < booleanArray.length; i++) {
               objectArray[i] = booleanArray[i];
            }
        }else if(name.equals("float[]")){
            float[] floatArray = (float[]) getValue(object, targetFiled);
            objectArray = new Float[floatArray.length];
            for (int i = 0; i < floatArray.length; i++) {
                objectArray[i] = floatArray[i];
            }
        }else if(name.equals("double[]")){
            double[] doubleArray = (double[]) getValue(object, targetFiled);
            objectArray = new Double[doubleArray.length];
            for (int i = 0; i < doubleArray.length; i++) {
                objectArray[i] = doubleArray[i];
            }
        }else if(name.equals("char[]")){
            char[] charArray = (char[]) getValue(object, targetFiled);
            objectArray = new Character[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                objectArray[i] = charArray[i];
            }
        }else {
            objectArray = (Object[]) getValue(object, targetFiled);
        }
        return objectArray;
    }

    Object getValue(Object obj, Field targetField) {
        targetField.setAccessible(true);
        Object value = null;
        try {
            value = targetField.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    boolean isBasicType(Field field) {
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

    Object getDefaultValue(Class classType) {
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
        } else {
            return null;
        }
    }

    SQLFieldTypeEnum getSqlStringType(Class<?> ziDuanJavaType) {
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
