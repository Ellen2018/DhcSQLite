package com.ellen.dhcsqlite.sql;

import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.type.Intercept;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class NewStudentTable extends ZxyTable<Student,MyAutoDesignOperate> {

    public NewStudentTable(SQLiteDatabase db, String tableName) {
        super(db, tableName);
    }

    public NewStudentTable(SQLiteDatabase db) {
        super(db);
        this.addIntercept(new Intercept<String,String>() {
            @Override
            public SQLFieldType setSQLiteType(Field field) {
                return new SQLFieldType(SQLFieldTypeEnum.LONG_TEXT,null);
            }

            @Override
            public boolean isType(Field field) {
                return field.getType() == String.class;
            }

            @Override
            public String toObj(Field field, String sqlValue) {
                return sqlValue;
            }

            @Override
            public String toValue(Field field, String dataValue) {
                return dataValue;
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

    @Override
    protected JsonLibraryType getJsonLibraryType() {
        return JsonLibraryType.Gson;
    }
}
