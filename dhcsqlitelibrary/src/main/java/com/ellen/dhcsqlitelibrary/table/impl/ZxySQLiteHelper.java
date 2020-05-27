package com.ellen.dhcsqlitelibrary.table.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.io.File;

class ZxySQLiteHelper extends SQLiteOpenHelper {

    private ZxySQLiteHelperCallback zxySQLiteHelperCallback;

    ZxySQLiteHelper(@Nullable Context context, @Nullable String fatherFile, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context,new File(fatherFile,name+".db").getAbsolutePath(),factory,version);
    }

    ZxySQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(zxySQLiteHelperCallback != null){
            zxySQLiteHelperCallback.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(zxySQLiteHelperCallback != null){
            zxySQLiteHelperCallback.onUpgrade(db,oldVersion,newVersion);
        }
    }

    public ZxySQLiteHelperCallback getZxySQLiteHelperCallback() {
        return zxySQLiteHelperCallback;
    }

    void setZxySQLiteHelperCallback(ZxySQLiteHelperCallback zxySQLiteHelperCallback) {
        this.zxySQLiteHelperCallback = zxySQLiteHelperCallback;
    }

    interface ZxySQLiteHelperCallback{
        void onCreate(SQLiteDatabase db);
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
}
