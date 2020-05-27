package com.ellen.dhcsqlite.sql;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class NewStudentTable extends ZxyTable<Student,MyAutoDesignOperate> {


    public NewStudentTable(SQLiteDatabase db, Class<Student> dataClass, Class<MyAutoDesignOperate> autoClass, String tableName) {
        super(db, dataClass, autoClass, tableName);
    }

    public NewStudentTable(SQLiteDatabase db, Class<Student> dataClass, Class<MyAutoDesignOperate> autoClass) {
        super(db, dataClass, autoClass);
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

    @Override
    protected JsonLibraryType getJsonLibraryType() {
        return JsonLibraryType.FastJson;
    }
}
