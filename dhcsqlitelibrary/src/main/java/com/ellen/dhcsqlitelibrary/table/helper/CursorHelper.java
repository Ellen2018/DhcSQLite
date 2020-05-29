package com.ellen.dhcsqlitelibrary.table.helper;

import android.database.Cursor;
import android.util.Log;

import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;

public class CursorHelper<T> {

    public Object readValueFromCursor(Cursor cursor, Field field, SQLField sqlField, String sqlDataType){
        Object value = null;
        int index = cursor.getColumnIndex(sqlField.getName());
        if(index < 0)return null;
        Class type = field.getType();
        if (sqlDataType.equals(SQLFieldTypeEnum.INTEGER.getTypeName())) {
            value = cursor.getInt(index);
        } else if (sqlDataType.equals(SQLFieldTypeEnum.BIG_INT.getTypeName())) {
            value = cursor.getLong(index);
        } else if (sqlDataType.equals(SQLFieldTypeEnum.REAL.getTypeName())) {
            if (type == Float.class || type.getName().equals("float")) {
                value = cursor.getFloat(index);
            } else if (type == Double.class || type.getName().equals("double")) {
                value = cursor.getDouble(index);
            } else {
                value = cursor.getDouble(index);
            }
        } else if(sqlDataType.equals(SQLFieldTypeEnum.MEDIUM_TEXT.getTypeName())){
            if (type == Character.class || type.getName().equals("char")) {
                String str = cursor.getString(index);
                if (str != null) {
                    value = cursor.getString(index).charAt(0);
                } else {
                    value = null;
                }
            } else {
                value = cursor.getString(index);
            }
        } else if(sqlDataType.equals(SQLFieldTypeEnum.LONG_TEXT.getTypeName())){
            if (type == Character.class || type.getName().equals("char")) {
                String str = cursor.getString(index);
                if (str != null) {
                    value = cursor.getString(index).charAt(0);
                } else {
                    value = null;
                }
            } else {
                value = cursor.getString(index);
            }
        } else if (sqlDataType.equals(SQLFieldTypeEnum.TEXT.getTypeName())) {
            if (type == Character.class || type.getName().equals("char")) {
                String str = cursor.getString(index);
                if (str != null) {
                    value = cursor.getString(index).charAt(0);
                } else {
                    value = null;
                }
            } else {
                value = cursor.getString(index);
            }
        } else if (sqlDataType.equals(SQLFieldTypeEnum.BLOB.getTypeName())) {
            value = cursor.getBlob(index);
        } else if (sqlDataType.equals(SQLFieldTypeEnum.DATE.getTypeName())) {
            value = cursor.getString(index);
        } else if (sqlDataType.equals(SQLFieldTypeEnum.NUMERIC.getTypeName())) {
            value = cursor.getString(index);
        }
        return value;
    }

}
