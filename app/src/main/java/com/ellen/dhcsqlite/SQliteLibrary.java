package com.ellen.dhcsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.reflection.ZxyLibrary;

public class SQliteLibrary extends ZxyLibrary {

    public SQliteLibrary(Context context, String name, int version) {
        super(context, name, version);
    }

    public SQliteLibrary(Context context, String libraryPath, String name, int version) {
        super(context, libraryPath, name, version);
    }

    @Override
    public void onZxySQLiteCreate(SQLiteDatabase db) {

    }

    @Override
    public void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
