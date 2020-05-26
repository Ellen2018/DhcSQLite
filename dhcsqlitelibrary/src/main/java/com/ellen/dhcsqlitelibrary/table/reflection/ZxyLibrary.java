package com.ellen.dhcsqlitelibrary.table.reflection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.sqlitecreate.createsql.delete.DeleteTable;
import com.ellen.sqlitecreate.createsql.delete.DeleteTableDataRow;
import com.ellen.sqlitecreate.createsql.serach.SerachTableExist;
import com.ellen.sqlitecreate.createsql.update.UpdateTableName;

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

    public void deleteTable(String tableName){
        String deleteTableSql = DeleteTable.getInstance().setTableName(tableName).createSQL();
        getWriteDataBase().execSQL(deleteTableSql);
    }

    public void reNameTable(String oldTableName,String newTableName){
        String updateTableName = UpdateTableName.getInstance().setOldTableName(oldTableName).setNewTableName(newTableName).createSQL();
        getWriteDataBase().execSQL(updateTableName);
    }

    public void clearTable(String tableName){
        String clearTableSql = DeleteTableDataRow.getInstance().setTableName(tableName).createDeleteAllDataSQL();
        getWriteDataBase().execSQL(clearTableSql);
    }

    /**
     * 判断表是否存在
     * @return
     */
    public boolean isExist(String tableName){
        String searchTableExistSql = SerachTableExist.getInstance()
                .setTableName(tableName)
                .createSQL();
        Cursor cursor = zxySQLiteHelper.getWritableDatabase().rawQuery(searchTableExistSql,null);
        return cursor.getCount() != 0;
    }

}
