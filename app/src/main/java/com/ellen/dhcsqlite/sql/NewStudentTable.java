package com.ellen.dhcsqlite.sql;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlite.MyJsonFormat;
import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.impl.CommonSetting;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyLibrary;
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

    public NewStudentTable(SQLiteDatabase db, String tableName) {
        super(db, tableName);
    }

    public NewStudentTable(SQLiteDatabase db) {
        super(db);
    }

    public NewStudentTable(ZxyLibrary zxyLibrary, String tableName) {
        super(zxyLibrary, tableName);
    }

    public NewStudentTable(ZxyLibrary zxyLibrary) {
        super(zxyLibrary);
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
