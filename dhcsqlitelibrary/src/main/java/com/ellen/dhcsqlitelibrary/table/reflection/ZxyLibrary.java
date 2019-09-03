package com.ellen.dhcsqlitelibrary.table.reflection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class ZxyLibrary {

    private ZxySQLiteHelper zxySQLiteHelper;
    private String name = null;
    private String filePath = null;

    public ZxyLibrary(Context context,String name,int version){
        zxySQLiteHelper = new ZxySQLiteHelper(context,name,null,version);
        zxySQLiteHelper.setZxySQLiteHelperCallback(new ZxySQLiteHelper.ZxySQLiteHelperCallback() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                onZxySQLiteCreate(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onZxySQLiteUpgrade(db, oldVersion, newVersion);
            }
        });
    }

    public ZxyLibrary(Context context,String libraryPath,String name,int version){
        zxySQLiteHelper = new ZxySQLiteHelper(context,libraryPath,name,null,version);
        zxySQLiteHelper.setZxySQLiteHelperCallback(new ZxySQLiteHelper.ZxySQLiteHelperCallback() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                onZxySQLiteCreate(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onZxySQLiteUpgrade(db, oldVersion, newVersion);
            }
        });
    }

    public SQLiteDatabase getWriteDataBase(){
        return zxySQLiteHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadDataBase(){
        return zxySQLiteHelper.getReadableDatabase();
    }

    public String getDatabaseName(){
        return zxySQLiteHelper.getDatabaseName();
    }

    public ZxySQLiteHelper getZxySQLiteHelper() {
        return zxySQLiteHelper;
    }

    public  abstract void onZxySQLiteCreate(SQLiteDatabase db);
    public  abstract void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
