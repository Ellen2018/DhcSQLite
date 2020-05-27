package com.ellen.dhcsqlite.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.impl.ZxyLibrary;

public class AppLibrary extends ZxyLibrary {

    /**
     *
     * @param context 上下文
     * @param name 数据库的名字
     * @param version 数据库的版本
     */
    public AppLibrary(Context context, String name, int version) {
        super(context, name, version);
    }

    /**
     *
     * @param context 上下文
     * @param libraryPath 数据库所在的父目录
     * @param name 数据库的名字
     * @param version 数据库版本号
     */
    public AppLibrary(Context context, String libraryPath, String name, int version) {
        super(context, libraryPath, name, version);
    }

    /**
     * 创建数据库时回调
     * @param db
     */
    @Override
    public void onZxySQLiteCreate(SQLiteDatabase db) {

    }

    /**
     *  数据库版本升级时回调
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
