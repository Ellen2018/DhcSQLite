package com.ellen.dhcsqlitelibrary.table.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ellen.sqlitecreate.createsql.delete.DeleteTable;
import com.ellen.sqlitecreate.createsql.delete.DeleteTableDataRow;
import com.ellen.sqlitecreate.createsql.serach.SerachTableData;
import com.ellen.sqlitecreate.createsql.serach.SerachTableExist;
import com.ellen.sqlitecreate.createsql.update.UpdateTableName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class ZxyLibrary implements BaseZxyLibrary {

    private ZxySQLiteHelper zxySQLiteHelper;
    private String name = null;
    private String filePath = null;

    public ZxyLibrary(Context context, String name, int version) {
        zxySQLiteHelper = new ZxySQLiteHelper(context, name, null, version);
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

    public ZxyLibrary(Context context, String libraryPath, String name, int version) {
        zxySQLiteHelper = new ZxySQLiteHelper(context, libraryPath, name, null, version);
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

    public SQLiteDatabase getWriteDataBase() {
        return zxySQLiteHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadDataBase() {
        return zxySQLiteHelper.getReadableDatabase();
    }

    public String getDatabaseName() {
        return zxySQLiteHelper.getDatabaseName();
    }

    public ZxySQLiteHelper getZxySQLiteHelper() {
        return zxySQLiteHelper;
    }

    public abstract void onZxySQLiteCreate(SQLiteDatabase db);

    public abstract void onZxySQLiteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public void deleteTable(String tableName) {
        String deleteTableSql = DeleteTable.getInstance().setTableName(tableName).createSQL();
        getWriteDataBase().execSQL(deleteTableSql);
    }

    public void reNameTable(String oldTableName, String newTableName) {
        String updateTableName = UpdateTableName.getInstance().setOldTableName(oldTableName).setNewTableName(newTableName).createSQL();
        getWriteDataBase().execSQL(updateTableName);
    }

    public void clearTable(String tableName) {
        String clearTableSql = DeleteTableDataRow.getInstance().setTableName(tableName).createDeleteAllDataSQL();
        getWriteDataBase().execSQL(clearTableSql);
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public boolean isExist(String tableName) {
        String searchTableExistSql = SerachTableExist.getInstance()
                .setTableName(tableName)
                .createSQL();
        Cursor cursor = zxySQLiteHelper.getWritableDatabase().rawQuery(searchTableExistSql, null);
        int count = cursor.getCount();
        if(cursor != null){
            cursor.close();
        }
        return count != 0;
    }

    @Override
    public void clearLibrary() {
        String[] tableNames = getAllTableName();
        if(tableNames != null && tableNames.length > 0) {
            for (String tableName : tableNames) {
                getWriteDataBase().execSQL(DeleteTable.getInstance().setTableName(tableName).createSQL());
            }
        }
    }

    @Override
    public String[] getAllTableName() {
        //先查询到所有的表名
        String searchSql = SerachTableData.getInstance().
                setTableName("sqlite_master")
                .setIsAddField(true)
                .addSelectField("name")
                .getTableAllDataSQL(null);
        List<String> stringList = new ArrayList<>();
        Cursor cursor = zxySQLiteHelper.getWritableDatabase().rawQuery(searchSql, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex("name");
            String name = cursor.getString(index);
            if (!(name.equals("android_metadata") || name.equals("sqlite_sequence"))) {
                //属于系统的表
                stringList.add(name);
            }

        }
        if(cursor != null){
            cursor.close();
        }
        if (stringList.size() > 0) {
            String[] stringArray = new String[stringList.size()];
            for (int i = 0; i < stringArray.length; i++) {
                stringArray[i] = stringList.get(i);
            }
            return stringArray;
        } else {
            return null;
        }
    }

    @Override
    public int getTableCount() {
        String[] s = getAllTableName();
        return s == null ? 0 : s.length;
    }

}
