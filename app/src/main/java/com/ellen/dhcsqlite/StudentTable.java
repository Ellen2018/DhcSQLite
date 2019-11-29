package com.ellen.dhcsqlite;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.reflection.ZxyReflectionTable;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.google.gson.Gson;

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

    /**
     * boolean类型值转化为数据库中的存储
     * @param classFieldName
     * @param value
     * @return
     */
    @Override
    protected Object setBooleanValue(String classFieldName, boolean value) {
        if(value){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    protected boolean isAutoCreateTable() {
        //true(自动创建表)
        return true;
    }

    /**
     *  检测发现目标类中不可转换类型的属性类型映射为数据库中存储的字段类型
     *  例如:Father --> TEXT
     * @param classFieldName 类属性名字
     * @param typeClass 类属性的类型
     * @return 返回存储在数据库的类型
     */
    @Override
    protected SQLFieldType conversionSQLiteType(String classFieldName, Class typeClass) {
        //将Java类中的Father类型的father字段映射为数据库中的TEXT类型
        if(classFieldName.equals("father")){
            return new SQLFieldType(SQLFieldTypeEnum.TEXT,null);
        }
        return null;
    }

    /**
     * 将目标类对象的非转换类型的属性值映射为数据库中存储的值,与conversionSQLiteType方法配合使用
     * @param student
     * @param classFieldName
     * @param typeClass
     * @param <E>
     * @return
     */
    @Override
    protected <E> E setConversionValue(Student student, String classFieldName, Class typeClass) {
        if(classFieldName.equals("father")){
            Gson gson = new Gson();
            String jsonFather = gson.toJson(student.getFather());
            return (E) jsonFather;
        }
        return null;
    }

    /**
     *  将数据库中相应的非转换类型存储的值映射为相应的类型值
     *  例如： TEXT -> Father
     * @param value
     * @param classFieldName
     * @param typeClass
     * @param <E>
     * @return
     */
    @Override
    protected <E> E resumeConversionObject(Object value, String classFieldName, Class typeClass) {
        if(classFieldName.equals("father")){
            String json = (String) value;
            Gson gson = new Gson();
            Father father = gson.fromJson(json,Father.class);
            return (E) father;
        }
        return null;
    }
}
