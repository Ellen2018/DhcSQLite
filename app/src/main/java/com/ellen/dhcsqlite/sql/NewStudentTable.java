package com.ellen.dhcsqlite.sql;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlite.MyJsonFormat;
import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.impl.CommonSetting;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.type.Intercept;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class NewStudentTable extends ZxyTable<Student,MyAutoDesignOperate> {

    private SQLiteDatabase db;

    public NewStudentTable(SQLiteDatabase db) {
        super(db);
        this.db = db;
        this.addIntercept(new Intercept() {
            @Override
            public SQLFieldType setSQLiteType(Field field) {
                if(field.getType() == Father.class){
                    return new SQLFieldType(SQLFieldTypeEnum.LONG_TEXT, null);
                }else {
                    return new SQLFieldType(SQLFieldTypeEnum.LONG_TEXT, null);
                }
            }

            @Override
            public boolean isType(Field field) {
                return field.getType() == String.class || field.getType() == Father.class;
            }

            @Override
            public Object toObj(Field field, Object sqlValue) {
                if(field.getType() == String.class){
                    //恢复sqlValue
                    return sqlValue;
                }else {
                    //因为笔者上面用的Json进行的映射，所以这里使用json进行解析
                    if(sqlValue != null) {
                        String json = (String) sqlValue;
                        Father father = new Gson().fromJson(json, Father.class);
                        return father;
                    }else {
                        return null;
                    }
                }
            }

            @Override
            public Object toValue(Field field, Object dataValue) {
                if(field.getType() == String.class){
                    //String类型的保存
                    return dataValue;
                }else {
                    //Father类型的保存
                    //注意有些类型一定要进行null判断，如果不判断，可能引起异常
                    if(dataValue != null) {
                        Father father = (Father) dataValue;
                        return new Gson().toJson(father);
                    }else {
                        return null;
                    }
                }
            }

        });
    }

    @Override
    protected Object resumeDataStructure(String classFieldName, Class fieldClass, String json) {
        if(classFieldName.equals("fathers")){
            Type founderSetType = new TypeToken<Father[]>() {}.getType();
            Father[] fathers = new Gson().fromJson(json, founderSetType);
            return fathers;

        }
        return null;
    }

    /**
     * 库内部公共设置
     * @param commonSetting
     */
    @Override
    protected void setting(CommonSetting commonSetting) {
        super.setting(commonSetting);
        //是否设置为多线程模式
        //true:设置为多线程模式，false：设置为非多线程模式
        commonSetting.setMultiThreadSafety(true);
        //设置库内部的Json解析器为Gson
        commonSetting.setJsonLibraryType(JsonLibraryType.Gson);
        //设置库内部的Json解析器为FastJson
        commonSetting.setJsonLibraryType(JsonLibraryType.FastJson);
        //设置库内部的Json解析为自定义的MyJsonFormat
        commonSetting.setJsonFormat(new MyJsonFormat());
    }
}
