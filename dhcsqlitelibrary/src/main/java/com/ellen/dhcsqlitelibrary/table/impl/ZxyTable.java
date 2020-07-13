package com.ellen.dhcsqlitelibrary.table.impl;


import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.operate.BaseOperate;
import com.ellen.dhcsqlitelibrary.table.operate.DebugListener;
import com.ellen.dhcsqlitelibrary.table.operate.TotalListener;
import com.ellen.dhcsqlitelibrary.table.proxy.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.operate.SqlOperate;
import com.ellen.dhcsqlitelibrary.table.helper.ReflectHelper;
import com.ellen.dhcsqlitelibrary.table.operate.add.Add;
import com.ellen.dhcsqlitelibrary.table.operate.create.Create;
import com.ellen.dhcsqlitelibrary.table.operate.create.OnCreateTableCallback;
import com.ellen.dhcsqlitelibrary.table.operate.delete.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.search.Search;
import com.ellen.dhcsqlitelibrary.table.operate.table.Table;
import com.ellen.dhcsqlitelibrary.table.operate.update.Update;
import com.ellen.dhcsqlitelibrary.table.proxy.AutoOperateProxy;
import com.ellen.dhcsqlitelibrary.table.type.BasicTypeSupport;
import com.ellen.dhcsqlitelibrary.table.type.DataStructureSupport;
import com.ellen.dhcsqlitelibrary.table.type.Intercept;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ZxyTable<T, O extends AutoDesignOperate> implements Create, Add<T>, Search<T>, Delete, Update<T>, Table {

    private Class<O> autoClass;
    private O autoDesignOperate;

    private String tableName;
    private ReflectHelper<T> reflectHelper;
    private JsonFormat jsonFormat;
    private CommonSetting commonSetting;

    //数据操作
    private SqlOperate<T> sqlOperate;

    private DataStructureSupport dataStructureSupport = null;
    private BasicTypeSupport basicTypeSupport = null;
    protected SQLiteDatabase db;

    public ZxyTable(SQLiteDatabase db, String tableName) {
        init(db, tableName);
    }

    public ZxyTable(SQLiteDatabase db) {
        init(db, null);
    }

    private Class getClassByIndex(int index) {
        Class<? extends ZxyTable> zxyTableClass = this.getClass();
        Type typeZxy = zxyTableClass.getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) typeZxy;
        Type type = parameterizedType.getActualTypeArguments()[index];
        return (Class) type;
    }

    private void init(SQLiteDatabase db, String tableName) {
        this.db = db;
        Class<T> dataClass = getClassByIndex(0);
        this.autoClass = getClassByIndex(1);
        if (tableName == null) {
            this.tableName = dataClass.getSimpleName();
        } else {
            this.tableName = tableName;
        }
        commonSetting = new CommonSetting();
        setting(commonSetting);
        //是否设置多线程安全
        if(commonSetting.isMultiThreadSafety()){
            //设置多线程安全
            this.db.enableWriteAheadLogging();
        }
        reflectHelper = new ReflectHelper<>();
        jsonFormat = commonSetting.getJsonFormat();
        if (jsonFormat == null) {
            jsonFormat = new JsonHelper(commonSetting.getJsonLibraryType());
        }
        dataStructureSupport = new DataStructureSupport(jsonFormat, new DataStructureSupport.ToObject() {
            @Override
            public Object toObj(String fieldName, Class fieldClass, String json) {
                return resumeDataStructure(fieldName, fieldClass, json);
            }
        });
        basicTypeSupport = new BasicTypeSupport(reflectHelper, new BasicTypeSupport.SetBooleanValue() {
            @Override
            public Object setBooleanValue(String classFieldName, boolean value) {
                return ZxyTable.this.setBooleanValue(classFieldName, value);
            }
        });
        sqlOperate = new SqlOperate<>(db, dataClass, jsonFormat, reflectHelper, dataStructureSupport, basicTypeSupport, this);

        //完成代理
        autoDesignOperate = AutoOperateProxy.newMapperProxy(autoClass, this);
    }

    public static void setTotalListener(TotalListener totalListener) {
        BaseOperate.setTotalListener(totalListener);
    }

    protected Object resumeDataStructure(String classFieldName, Class fieldClass, String json) {
        return null;
    }

    protected Object setBooleanValue(String classFieldName, boolean value) {
        if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * 监听Sql语句执行
     * 方便调试
     *
     * @param debugListener
     */
    public void setDebugListener(DebugListener debugListener) {
        sqlOperate.setDebugListener(debugListener);
    }

    /**
     * 添加拦截
     *
     * @param intercept
     */
    public void addIntercept(Intercept intercept) {
        sqlOperate.addIntercept(intercept);
    }

    /**
     * 移除拦截
     *
     * @param intercept
     */
    public void removeIntercept(Intercept intercept) {
        sqlOperate.removeIntercept(intercept);
    }

    public O getAutoDesignOperate() {
        return autoDesignOperate;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void onCreateTable() {
        sqlOperate.onCreateTable();
    }

    @Override
    public void onCreateTable(OnCreateTableCallback onCreateTableCallback) {
        sqlOperate.onCreateTable(onCreateTableCallback);
    }

    @Override
    public void onCreateTableIfNotExits() {
        sqlOperate.onCreateTableIfNotExits();
    }

    @Override
    public void onCreateTableIfNotExits(OnCreateTableCallback onCreateTableCallback) {
        sqlOperate.onCreateTableIfNotExits(onCreateTableCallback);
    }

    @Override
    public void saveData(T data) {
        sqlOperate.saveData(data);
    }

    @Override
    public void saveData(List<T> dataList) {
        sqlOperate.saveData(dataList);
    }

    @Override
    public void saveDataAndDeleteAgo(List<T> dataList) {
        sqlOperate.saveDataAndDeleteAgo(dataList);
    }

    @Override
    public void saveDataAndDeleteAgo(T data) {
        sqlOperate.saveDataAndDeleteAgo(data);
    }

    @Override
    public void saveData(List<T> dataList, int segment) {
        sqlOperate.saveData(dataList, segment);
    }

    @Override
    public List<T> search(String whereSQL, String orderSQL) {
        return sqlOperate.search(whereSQL, orderSQL);
    }

    @Override
    public List<T> getAllData() {
        return sqlOperate.getAllData();
    }

    @Override
    public List<T> getAllData(String orderSql) {
        return sqlOperate.getAllData(orderSql);
    }

    @Override
    public List<T> searchDataBySql(String sql) {
        return sqlOperate.searchDataBySql(sql);
    }

    @Override
    public boolean isContainsByMajorKey(T t) {
        return sqlOperate.isContainsByMajorKey(t);
    }

    @Override
    public T searchByMajorKey(Object value) {
        return sqlOperate.searchByMajorKey(value);
    }

    @Override
    public boolean isExist() {
        return sqlOperate.isExist();
    }

    @Override
    public boolean deleteTable() {
        return sqlOperate.deleteTable();
    }

    @Override
    public int deleteReturnCount(String whereSql) {
        return sqlOperate.deleteReturnCount(whereSql);
    }

    @Override
    public void delete(String whereSql) {
        sqlOperate.delete(whereSql);
    }


    @Override
    public boolean deleteByMajorKey(Object majorKeyValue) {
        return sqlOperate.deleteByMajorKey(majorKeyValue);
    }

    @Override
    public void clear() {
        sqlOperate.clear();
    }

    @Override
    public void saveOrUpdateByMajorKey(T t) {
        sqlOperate.saveOrUpdateByMajorKey(t);
    }

    @Override
    public void saveOrUpdateByMajorKey(List<T> tList) {
        sqlOperate.saveOrUpdateByMajorKey(tList);
    }

    @Override
    public boolean updateByMajorKeyReturn(T t) {
        return sqlOperate.updateByMajorKeyReturn(t);
    }

    @Override
    public void updateByMajorKey(T t) {
        sqlOperate.updateByMajorKey(t);
    }

    @Override
    public int updateReturnCount(T t, String whereSQL) {
        int count = 0;
        count = sqlOperate.updateReturnCount(t, whereSQL);
        return count;
    }

    @Override
    public void update(T t, String whereSQL) {
        sqlOperate.update(t, whereSQL);
    }

    @Override
    public boolean reNameTable(String newName) {
        if (sqlOperate.reNameTable(newName)) {
            tableName = newName;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getMajorKeyName() {
        return sqlOperate.getMajorKeyName();
    }

    @Override
    public void exeSql(String sql) {
        sqlOperate.exeSql(sql);
    }

    @Override
    public void close() {
        sqlOperate.close();
    }

    protected void setting(CommonSetting commonSetting){}
}
