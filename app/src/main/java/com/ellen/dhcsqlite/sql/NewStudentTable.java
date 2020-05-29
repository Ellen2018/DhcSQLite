package com.ellen.dhcsqlite.sql;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class NewStudentTable extends ZxyTable<Student,MyAutoDesignOperate> {

    public NewStudentTable(SQLiteDatabase db, String tableName) {
        super(db, tableName);
    }

    public NewStudentTable(SQLiteDatabase db) {
        super(db);
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
        return JsonLibraryType.Gson;
    }
}
