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
     * 如果没有特殊的更改，此方法无须重写
     *
     * 但是笔者有特殊的要求，就是将isMan字段在数据库中存"男' & "女" 的需求
     * 那么这个地方我就只能将isMan属性的字段映射为数据库中TEXT类型，并且数据位数调整为1位
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    @Override
    protected SQLFieldType getSqlFieldType(String classFieldName, Class typeClass) {
        if(classFieldName.equals("isMan")){
            //将isMan的boolean映射为TEXT类型，且长度为1位
            return new SQLFieldType(SQLFieldTypeEnum.TEXT,1);
        }else {
            return super.getSqlFieldType(classFieldName,typeClass);
        }
    }

    /**
     * boolean类型值转化为数据库中的存储
     *
     * 如果你的bean类中没有boolean类型的存储，此方法返回null即可
     *
     * 如果有:
     * 默认的没有实现getSqlFieldType方法将它映射成你想要的类型的情况下:需要返回 0 和 1，或者 其他的int类型即可，只要不相同
     * 如果有实现getSqlFieldType方法并将它映射成你想要的类型的情况下:需要返回true和false存储的值，例如笔者这里返回"男" 和 "女"
     *
     * 这么实现有什么意义呢？为的是让数据库中的数据变得更加易懂，通常清空下没有这种需求，可万一有这个需求呢
     * @param classFieldName
     * @param value
     * @return
     */
    @Override
    protected Object setBooleanValue(String classFieldName, boolean value) {
        if(value){
            return "男";
        }else {
            return "女";
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
