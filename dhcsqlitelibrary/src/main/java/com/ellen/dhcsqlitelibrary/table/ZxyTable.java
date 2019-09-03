package com.ellen.dhcsqlitelibrary.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.ZxySQLite;

public class ZxyTable extends ZxySQLite {
    private SQLiteDatabase db;

    public ZxyTable(SQLiteDatabase db){
        this.db = db;
    }

    public SQLiteDatabase getSQLiteDatabase(){
        return db;
    }

    public void exeSQL(String sql){
        getSQLiteDatabase().execSQL(sql);
    }

    public Cursor serachBySQL(String sql){
        return getSQLiteDatabase().rawQuery(sql,null);
    }

}
