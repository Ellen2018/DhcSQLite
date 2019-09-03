package com.ellen.dhcsqlite;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.reflection.ZxyReflectionTable;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

public class StudentTable extends ZxyReflectionTable<Student> {

    public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass) {
        super(db, dataClass);
    }

    public StudentTable(SQLiteDatabase db, Class<? extends Student> dataClass, String autoTableName) {
        super(db, dataClass, autoTableName);
    }

    /**
     * 设置属性对应的数据库字段的类型
     * @param classFieldName
     * @param typeClass
     * @return
     */
    @Override
    protected SQLFieldType getSQLFieldType(String classFieldName, Class typeClass) {
        if(classFieldName.equals("isMan")){
            //将isMan的boolean映射为Integer类型，且长度为1位
            return new SQLFieldType(SQLFieldTypeEnum.INTEGER,1);
        }else {
            return new SQLFieldType(getSQlStringType(typeClass), null);
        }
    }

    /**
     *  设置数据库中对应的属性名
     */
    @Override
    protected String getSQLFieldName(String classFieldName, Class typeClass) {
        return classFieldName;
    }

    @Override
    protected Object setBooleanValue(String classFieldName, boolean value) {
        if(classFieldName.equals("isMan")){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    protected SQLFieldType conversionSQLiteType(String classFieldName, Class typeClass) {
        return null;
    }

    @Override
    protected <E> E setConversionValue(Student student, String classFieldName, Class typeClass) {
        return null;
    }

    @Override
    protected <E> E resumeConversionObject(Object value, String classFieldName, Class typeClass) {
        return null;
    }
}
