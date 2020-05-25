package com.ellen.dhcsqlite;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ellen.dhcsqlitelibrary.table.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyReflectionTable;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
     * 此方法默认情况下无须重写
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

    /**
     * 注意，你映射的bean类必须要有空的构造器，否则就会映射失败
     * 其原因是FastJson无法映射到没有空构造器的bean类
     *
     *
     * JsonLibraryType.Gson --> 使用Gson进行json映射
     *
     * JsonLibraryType.FastJson --> 使用FastJson进行json映射
     *
     * 当你没有重写此方法时候，默认会使用Gson
     *
     * 后期还可以加入其它的json映射类型
     *
     *  非常注意:如果你的项目中没有导入Json解析库:Gson或者是FastJson,那么在映射的时候就会抛出JsonNoCanFormatException
     *  一旦出现这个异常，你需要导入Gson或者FastJson库即可解决这个异常
     *
     * @return
     */
    @Override
    protected JsonLibraryType getJsonLibraryType() {
        return JsonLibraryType.FastJson;
    }

    /**
     * 将json恢复成成数据结构的形式
     *
     * @param classFieldName
     * @param json
     * @return
     */
    @Override
    protected Object resumeDataStructure(String  classFieldName, String json) {
      if(classFieldName.equals("fathers")){
            Type founderSetType = new TypeToken<List<Father>>() {}.getType();
            List<Father> fathers = new Gson().fromJson(json, founderSetType);
            Father[] fathers1 = new Father[fathers.size()];
            for(int i=0;i<fathers.size();i++){
                fathers1[i] = fathers.get(i);
            }
            return fathers1;

        }
        return null;
    }
}
