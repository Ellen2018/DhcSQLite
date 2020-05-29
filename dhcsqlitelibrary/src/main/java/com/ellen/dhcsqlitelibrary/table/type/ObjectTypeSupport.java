package com.ellen.dhcsqlitelibrary.table.type;

import com.ellen.dhcsqlitelibrary.table.annotation.field.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.annotation.field.Operate;
import com.ellen.dhcsqlitelibrary.table.annotation.field.OperateEnum;
import com.ellen.dhcsqlitelibrary.table.annotation.field.SqlType;
import com.ellen.dhcsqlitelibrary.table.exception.NoCanSaveToSqlException;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.helper.ReflectHelper;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 引用类型支持
 */
public class ObjectTypeSupport implements TypeSupport {

    private ReflectHelper reflectHelper;
    private JsonHelper jsonHelper;

    public ObjectTypeSupport(ReflectHelper reflectHelper, JsonHelper jsonHelper) {
        this.reflectHelper = reflectHelper;
        this.jsonHelper = jsonHelper;
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
        Operate operate = field.getAnnotation(Operate.class);
        SqlType sqlType = field.getAnnotation(SqlType.class);
        SQLFieldType sqlFieldType = null;
        if (operate != null) {
            if (operate.operate() == OperateEnum.JSON) {
                if (sqlType != null) {
                    if (sqlType.sqlFiledType() == SQLFieldTypeEnum.TEXT) {
                        if(sqlType.length() > 0) {
                            sqlFieldType = new SQLFieldType(sqlType.sqlFiledType(), sqlType.length());
                        }else {
                            sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
                        }
                    } else {
                        sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
                    }
                } else {
                    sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
                }
            } else {
                //如果不是以Json方式保存

                //先进行纠错
                String filedName = operate.valueName();
                Field targetField = null;
                try {
                    targetField = field.getType().getDeclaredField(filedName);
                    targetField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    String errMessage = field.getType().getCanonicalName()+"下不存在属性："+filedName;
                    throw new NoCanSaveToSqlException(errMessage);
                }

                //判断是否为基本类型
                if (!reflectHelper.isBasicType(targetField)) {
                    String errMessage = field.getType().getCanonicalName() + "." + targetField.getName() + "不是基本类型，无法映射到数据库中";
                    throw new NoCanSaveToSqlException(errMessage);
                }
                //获取到对应的存储类型
                SQLFieldTypeEnum sqlFieldTypeEnum = reflectHelper.getSqlStringType(targetField.getType());
                if (sqlType == null) {
                    sqlFieldType = new SQLFieldType(sqlFieldTypeEnum,null);
                } else {
                    if(sqlType.sqlFiledType() == sqlFieldTypeEnum){
                        if(sqlType.length() > 0) {
                            sqlFieldType = new SQLFieldType(sqlFieldTypeEnum, sqlType.length());
                        }else {
                            sqlFieldType = new SQLFieldType(sqlFieldTypeEnum, null);
                        }
                    }else {
                        //进行容错
                        sqlFieldType = new SQLFieldType(sqlFieldTypeEnum,null);
                    }
                }
            }
        } else {
            //没有写，那就按照Json进行处理
            if (sqlType == null) {
                sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
            } else {
                //容错机制
                if (sqlType.sqlFiledType() == SQLFieldTypeEnum.TEXT) {
                    if(sqlType.length() > 0) {
                        sqlFieldType = new SQLFieldType(sqlType.sqlFiledType(), sqlType.length());
                    }else {
                        sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
                    }
                } else {
                    sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT, null);
                }
            }
        }

        return sqlFieldType;
    }

    @Override
    public boolean isType(Field field) {
        return true;
    }

    @Override
    public Object toObj(Field field, Object sqlValue) {
        Operate operate = field.getAnnotation(Operate.class);
        Class typeClass = field.getType();
        Object object = null;
        if(sqlValue == null){
            return null;
        }
        if (operate != null) {
            String filedName = operate.valueName();
            OperateEnum operateEnum = operate.operate();
            if (operateEnum == OperateEnum.VALUE) {
                try {
                    object = reflectHelper.getT(typeClass);
                    Field targetField = typeClass.getDeclaredField(filedName);
                    targetField.setAccessible(true);
                    if (sqlValue != null && object != null) {
                        targetField.set(object, sqlValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else {
                object = jsonHelper.toObject((String) sqlValue, typeClass);
            }
        } else {
            object = jsonHelper.toObject((String) sqlValue, typeClass);
        }
        return object;
    }

    @Override
    public Object toValue(Field field, Object dataValue) {
        Operate operate = field.getAnnotation(Operate.class);
        Object value = null;
        Class typeClass = field.getType();
        if(dataValue == null){
            return null;
        }
        if (operate != null) {
            //先看转换类型的操作
            OperateEnum operateEnum = operate.operate();
            if (operateEnum == OperateEnum.VALUE) {
                //仅仅存值
                String valueName = operate.valueName();
                Field valueField = null;
                try {
                    valueField = typeClass.getDeclaredField(valueName);
                    valueField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                value = reflectHelper.getValue(dataValue, valueField);
            } else {
                //Json存储
                value = jsonHelper.toJson(dataValue);
            }
        } else {
            //Json存储
            value = jsonHelper.toJson(dataValue);
        }
        return value;
    }
}
