package com.ellen.dhcsqlitelibrary.table.helper;

import com.ellen.dhcsqlitelibrary.table.annotation.field.Ignore;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Default;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectHelper<T> {

    public List<Field> getClassFieldList(Class<? extends T> dataClass, boolean isAddStatic) {
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

    public Object[] getValueArray(Object value) {
        if(value == null)return null;
        String name = value.getClass().getSimpleName();
        Object[] objectArray = null;
        if(name.equals("byte[]")){
            byte[] byteArray = (byte[]) value;
            objectArray = new Byte[byteArray.length];
            for (int i = 0; i < byteArray.length; i++) {
                objectArray[i] = byteArray[i];
            }
        }else if(name.equals("short[]")){
            short[] shortArray = (short[]) value;
            objectArray = new Short[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) {
                objectArray[i] = shortArray[i];
            }
        } else if (name.equals("int[]")) {
            int[] intArray = (int[]) value;
            objectArray = new Integer[intArray.length];
            for (int i = 0; i < intArray.length; i++) {
                objectArray[i] = intArray[i];
            }
        }else if(name.equals("long[]")){
            long[] longArray = (long[]) value;
            objectArray = new Long[longArray.length];
            for (int i = 0; i < longArray.length; i++) {
                objectArray[i] = longArray[i];
            }
        }else if(name.equals("boolean[]")){
            boolean[] booleanArray = (boolean[])value;
            objectArray = new Boolean[booleanArray.length];
            for (int i = 0; i < booleanArray.length; i++) {
               objectArray[i] = booleanArray[i];
            }
        }else if(name.equals("float[]")){
            float[] floatArray = (float[]) value;
            objectArray = new Float[floatArray.length];
            for (int i = 0; i < floatArray.length; i++) {
                objectArray[i] = floatArray[i];
            }
        }else if(name.equals("double[]")){
            double[] doubleArray = (double[]) value;
            objectArray = new Double[doubleArray.length];
            for (int i = 0; i < doubleArray.length; i++) {
                objectArray[i] = doubleArray[i];
            }
        }else if(name.equals("char[]")){
            char[] charArray = (char[]) value;
            objectArray = new Character[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                objectArray[i] = charArray[i];
            }
        }else {
            objectArray = (Object[]) value;
        }
        return objectArray;
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

    public boolean isBooleanType(Field field){
        return field.getType() == Boolean.class || field.getType().getName().equals("boolean");
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

    /**
     * 根据Value值判断它的类型
     * @param value
     * @return
     */
    public boolean isBasicType(Object value) {
        boolean b;
        Class classType = value.getClass();
        if (classType == Byte.class) {
          b = true;
        } else if (classType == Short.class) {
            b = true;
        } else if (classType == Integer.class) {
            b = true;
        } else if (classType == Long.class) {
            b = true;
        } else if (classType == Float.class) {
            b = true;
        } else if (classType == Double.class) {
            b = true;
        } else if (classType == Boolean.class) {
            b = true;
        } else if (classType == Character.class) {
            b = true;
        } else if (classType == String.class) {
            b = true;
        } else {
            b = false;
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
        } else {
            return null;
        }
    }

    public SQLFieldTypeEnum getSqlStringType(Class<?> type) {
        SQLFieldTypeEnum sqlType = null;
        if (type == Byte.class || type.getName().equals("byte")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (type == Short.class || type.getName().equals("short")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (type == Integer.class || type.getName().equals("int")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (type == Long.class || type.getName().equals("long")) {
            sqlType = SQLFieldTypeEnum.BIG_INT;
        } else if (type == Float.class || type.getName().equals("float")) {
            sqlType = SQLFieldTypeEnum.REAL;
        } else if (type == Double.class || type.getName().equals("double")) {
            sqlType = SQLFieldTypeEnum.REAL;
        } else if (type == Boolean.class || type.getName().equals("boolean")) {
            sqlType = SQLFieldTypeEnum.INTEGER;
        } else if (type == Character.class || type.getName().equals("char")) {
            sqlType = SQLFieldTypeEnum.TEXT;
        } else if (type == String.class) {
            sqlType = SQLFieldTypeEnum.TEXT;
        } else {
            sqlType = SQLFieldTypeEnum.TEXT;
        }
        return sqlType;
    }

    public Object getDefaultAValue(Default d){
        Object obj = null;
        switch (d.defaultValueEnum()){
            case BYTE:
                obj = d.byteValue();
                break;
            case SHORT:
                obj = d.shortValue();
                break;
            case INT:
                obj = d.intValue();
                break;
            case LONG:
                obj = d.longValue();
                break;
            case FLOAT:
                obj = d.floatValue();
                break;
            case DOUBLE:
                obj = d.doubleValue();
                break;
            case CHAR:
                obj = d.charValue();
                break;
            case STRING:
                obj = d.strValue();
                break;
        }
        return obj;
    }

    public <E> E getT(Class dataClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] constructors = dataClass.getDeclaredConstructors();
        Constructor constructor = constructors[0];
        constructor.setAccessible(true);
        Class[] classArray = constructor.getParameterTypes();
        if (classArray != null && classArray.length > 0) {
            Object[] objects = new Object[classArray.length];
            for (int i = 0; i < classArray.length; i++) {
                Object value = getDefaultValue(classArray[i]);
                objects[i] = value;
            }
            return (E) constructor.newInstance(objects);
        } else {
            return (E) constructor.newInstance();
        }
    }

}
