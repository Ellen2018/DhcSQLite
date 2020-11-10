package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.dhcsqlitelibrary.table.annotation.field.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.helper.json.JxFormat;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * 基础数据结构支持
 */
public class DataStructureSupport implements TypeSupport {

    private JxFormat jxFormat;
    private ToObject toObject;

    public DataStructureSupport(JxFormat jxFormat, ToObject toObject) {
        this.jxFormat = jxFormat;
        this.toObject = toObject;
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
        return new SQLFieldType(SQLFieldTypeEnum.TEXT,null);
    }

    @Override
    public boolean isType(Field field) {
        Class fieldClass = field.getType();
        //数组
        boolean isDataStructure = false;
        if (fieldClass.isArray()) {
            isDataStructure = true;
        }
        //List
        if (fieldClass == ArrayList.class || fieldClass == LinkedList.class || fieldClass == Vector.class) {
            isDataStructure = true;
        }
        //Set
        if (fieldClass == HashSet.class || fieldClass == TreeSet.class) {
            isDataStructure = true;
        }
        //Map
        if (fieldClass == HashMap.class || fieldClass == TreeMap.class) {
            isDataStructure = true;
        }
        return isDataStructure;
    }

    @Override
    public Object toObj(Field field, Object value) {
        if (value != null) {
            return toObject.toObj(field.getName(),field.getType(), (String) value);
        } else {
            return null;
        }
    }

    @Override
    public Object toValue(Field field,Object dataValue) {
        return dataValue == null ? null : jxFormat.toJxString(dataValue);
    }

    public interface ToObject{
        Object toObj(String fieldName, Class fieldClass, String json);
    }
}
