package com.ellen.dhcsqlitelibrary.table.reflection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.io.File;

public class ZxySQLiteHelper extends SQLiteOpenHelper {

    private ZxySQLiteHelperCallback zxySQLiteHelperCallback;

    public ZxySQLiteHelper(@Nullable Context context, @Nullable String fatherFile, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context,new File(fatherFile,name+".db").getAbsolutePath(),factory,version);
    }

    public ZxySQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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

    public void setZxySQLiteHelperCallback(ZxySQLiteHelperCallback zxySQLiteHelperCallback) {
        this.zxySQLiteHelperCallback = zxySQLiteHelperCallback;
    }

    public interface ZxySQLiteHelperCallback{
        void onCreate(SQLiteDatabase db);
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
}
