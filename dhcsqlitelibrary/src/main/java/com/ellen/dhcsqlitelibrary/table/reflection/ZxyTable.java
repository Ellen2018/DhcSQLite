package com.ellen.dhcsqlitelibrary.table.reflection;


import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.operate.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.operate.BaseOperate;
import com.ellen.dhcsqlitelibrary.table.operate.ReflectHelper;
import com.ellen.dhcsqlitelibrary.table.operate.add.Add;
import com.ellen.dhcsqlitelibrary.table.operate.create.Create;
import com.ellen.dhcsqlitelibrary.table.operate.create.OnCreateTableCallback;
import com.ellen.dhcsqlitelibrary.table.operate.delete.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.search.Search;
import com.ellen.dhcsqlitelibrary.table.operate.table.Table;
import com.ellen.dhcsqlitelibrary.table.operate.update.Update;
import com.ellen.dhcsqlitelibrary.table.proxy.AutoOperateProxy;
import com.ellen.dhcsqlitelibrary.table.type.DataStructureSupport;

import java.util.List;

public class ZxyTable<T, O extends AutoDesignOperate> implements Create, Add<T>, Search<T>, Delete, Update<T>, Table {

    private Class<O> autoClass;
    private O autoDesignOperate;

    private String tableName;
    private ReflectHelper<T> reflectHelper;
    private JsonHelper jsonHelper;

    //数据操作
    private BaseOperate<T> baseOperate;

    private DataStructureSupport dataStructureSupport;

    public ZxyTable(SQLiteDatabase db, Class<T> dataClass, Class<O> autoClass, String tableName) {
        this.autoClass = autoClass;
        init(db,dataClass,tableName);
    }

    private void init(SQLiteDatabase db, Class<T> dataClass,String tableName) {
        this.tableName = tableName;
        reflectHelper = new ReflectHelper<>();
        jsonHelper = new JsonHelper(getJsonLibraryType());
        dataStructureSupport = new DataStructureSupport(jsonHelper, new DataStructureSupport.ToObject() {
            @Override
            public Object toObj(String fieldName, Class fieldClass, String json) {
                return resumeDataStructure(fieldName,fieldClass,json);
            }
        });
        baseOperate = new BaseOperate<>(db, dataClass, jsonHelper, reflectHelper, dataStructureSupport,this);

        //完成代理
        autoDesignOperate = AutoOperateProxy.newMapperProxy(autoClass, this);
    }

    public ZxyTable(SQLiteDatabase db, Class<T> dataClass, Class<O> autoClass) {
        this.autoClass = autoClass;
        init(db,dataClass,dataClass.getSimpleName());
    }

    protected JsonLibraryType getJsonLibraryType() {
        return JsonLibraryType.Gson;
    }

    protected Object resumeDataStructure(String classFieldName, Class fieldClass, String json) {
        return null;
    }


    public O getAutoDesignOperate() {
        return autoDesignOperate;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void onCreateTable() {
        baseOperate.onCreateTable();
    }

    @Override
    public void onCreateTable(OnCreateTableCallback onCreateTableCallback) {
        baseOperate.onCreateTable(onCreateTableCallback);
    }

    @Override
    public void onCreateTableIfNotExits() {
        baseOperate.onCreateTableIfNotExits();
    }

    @Override
    public void onCreateTableIfNotExits(OnCreateTableCallback onCreateTableCallback) {
        baseOperate.onCreateTableIfNotExits(onCreateTableCallback);
    }

    @Override
    public void saveData(T data) {
        baseOperate.saveData(data);
    }

    @Override
    public void saveData(List<T> dataList) {
        baseOperate.saveData(dataList);
    }

    @Override
    public List<T> search(String whereSQL, String orderSQL) {
        return baseOperate.search(whereSQL, orderSQL);
    }

    @Override
    public List<T> getAllData() {
        return baseOperate.getAllData();
    }

    @Override
    public List<T> getAllData(String orderSql) {
        return baseOperate.getAllData(orderSql);
    }

    @Override
    public List<T> searchDataBySql(String sql) {
        return baseOperate.searchDataBySql(sql);
    }

    @Override
    public boolean isContainsByMajorKey(T t) {
        return baseOperate.isContainsByMajorKey(t);
    }

    @Override
    public T getDataByMajorKey(Object value) {
        return baseOperate.getDataByMajorKey(value);
    }

    @Override
    public boolean isExist() {
        return baseOperate.isExist();
    }

    @Override
    public boolean deleteTable() {
        return baseOperate.deleteTable();
    }

    @Override
    public int delete(String whereSQL) {
        return baseOperate.delete(whereSQL);
    }

    @Override
    public boolean deleteByMajorKey(Object majorKeyValue) {
        return baseOperate.deleteByMajorKey(majorKeyValue);
    }

    @Override
    public void clear() {
        baseOperate.clear();
    }

    @Override
    public void saveOrUpdateByMajorKey(T t) {
        baseOperate.saveOrUpdateByMajorKey(t);
    }

    @Override
    public void saveOrUpdateByMajorKey(List<T> tList) {
        baseOperate.saveOrUpdateByMajorKey(tList);
    }

    @Override
    public boolean updateByMajorKey(T t) {
        return baseOperate.updateByMajorKey(t);
    }

    @Override
    public int update(T t, String whereSQL) {
        return baseOperate.update(t, whereSQL);
    }

    @Override
    public boolean reNameTable(String newName) {
        if (baseOperate.reNameTable(newName)) {
            tableName = newName;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getMajorKeyName() {
        return baseOperate.getMajorKeyName();
    }

    @Override
    public void exeSql(String sql) {
        baseOperate.exeSql(sql);
    }
}
