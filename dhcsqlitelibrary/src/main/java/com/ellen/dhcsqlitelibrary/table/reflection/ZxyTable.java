package com.ellen.dhcsqlitelibrary.table.reflection;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.operate.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.Proxy.AutoOperateProxy;
import com.ellen.dhcsqlitelibrary.table.annotation.MajorKey;
import com.ellen.dhcsqlitelibrary.table.annotation.Operate;
import com.ellen.dhcsqlitelibrary.table.exception.NoMajorKeyException;
import com.ellen.dhcsqlitelibrary.table.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.annotation.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.annotation.SqlType;
import com.ellen.dhcsqlitelibrary.table.annotation.NotNull;
import com.ellen.dhcsqlitelibrary.table.annotation.OperateEnum;
import com.ellen.sqlitecreate.createsql.add.AddManyRowToTable;
import com.ellen.sqlitecreate.createsql.add.AddSingleRowToTable;
import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.ellen.sqlitecreate.createsql.helper.Value;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.serach.SerachTableData;
import com.ellen.sqlitecreate.createsql.update.UpdateTableDataRow;
import com.ellen.sqlitecreate.createsql.where.Where;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 此类是基于反射对类进行自动建表
 * 完全类似于LitePal的用法
 */
public abstract class ZxyTable<T,O extends AutoDesignOperate> extends BaseZxyTable {

    private Class dataClass;

    private List<SQLField> sqlFieldList;
    private String tableName;
    private O autoDesignOperate;
    private ReflectHelper<T> reflectHelper;
    private HashMap<SQLField, Field> sqlNameMap;
    private Field majorKeyField = null;
    private SQLField majorKeySqlField = null;
    private JsonHelper jsonHelper;

    private ZxyChangeListener zxyChangeListener;
    private Class<? extends AutoDesignOperate> autoClass;

    public ZxyTable(SQLiteDatabase db, Class<? extends T> dataClass,Class<? extends AutoDesignOperate> autoClass) {
        super(db);
        init(dataClass.getSimpleName(), dataClass,autoClass);
    }

    public O getAutoDesignOperate() {
        return autoDesignOperate;
    }

    /**
     * 获取主键的字段名字
     * @return
     */
    public String getMajorKeyName(){
        String majorKeyName = null;
        if(majorKeySqlField != null){
            majorKeyName = majorKeySqlField.getName();
        }
        return majorKeyName;
    }

    public ZxyTable(SQLiteDatabase db, Class<? extends T> dataClass, String autoTableName,Class<? extends AutoDesignOperate> autoClass) {
        super(db);
        init(autoTableName, dataClass,autoClass);
    }

    private void init(String tableName, Class<? extends T> dataClass,Class<? extends AutoDesignOperate> autoClass) {
        this.dataClass = dataClass;
        this.tableName = tableName;
        reflectHelper = new ReflectHelper<>();
        sqlNameMap = new HashMap<>();
        getFields();
        jsonHelper = new JsonHelper(getJsonLibraryType());
        autoDesignOperate = (O) AutoOperateProxy.newMapperProxy(autoClass,this);
    }

    public void setZxyChangeListener(ZxyChangeListener zxyChangeListener) {
        this.zxyChangeListener = zxyChangeListener;
    }

    /**
     * 通过反射获取类的所有属性
     */
    private void getFields() {
        sqlFieldList = new ArrayList<>();
        List<Field> fieldList = reflectHelper.getClassFieldList(dataClass, false);
        for (Field field : fieldList) {
            String fieldType = null;
            String fieldName = null;
            if (reflectHelper.isBasicType(field)) {
                fieldType = getSqlFieldType(field.getName(), field.getType()).getSQLFieldTypeString();
            } else {
                //不是基本类型
                fieldType = conversionSQLiteType(field).getSQLFieldTypeString();
            }
            DhcSqlFieldName dhcSqlFieldName = field.getAnnotation(DhcSqlFieldName.class);
            if (dhcSqlFieldName == null) {
                fieldName = getSQLFieldName(field.getName(), field.getType());
            } else {
                fieldName = dhcSqlFieldName.sqlFieldName();
            }
            MajorKey majorKey = field.getAnnotation(MajorKey.class);
            SQLField sqlField = null;
            if (majorKey != null) {
                //这里是主键
                boolean isAutoIncrement = majorKey.isAutoIncrement();
                if(isAutoIncrement){
                    sqlField = SQLField.getPrimaryKeyField(fieldName, fieldType, true);
                }else {
                    sqlField = SQLField.getPrimaryKeyField(fieldName, fieldType, false);
                }
                majorKeyField = field;
                majorKeySqlField = sqlField;
            } else {
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null) {
                    sqlField = SQLField.getNotNullValueField(fieldName, fieldType);
                } else {
                    sqlField = SQLField.getOrdinaryField(fieldName, fieldType);
                }
            }
            sqlNameMap.put(sqlField, field);
            sqlFieldList.add(sqlField);
        }
    }

    public void onCreateTable() {
        String createTableSql = getCreateTable()
                .setTableName(tableName)
                .addField(sqlFieldList)
                .createSQL();
        getSQLiteDatabase().execSQL(createTableSql);
    }

    public void onCreateTableIfNotExits() {
        String createTableSql = getCreateTable()
                .setTableName(tableName)
                .addField(sqlFieldList)
                .createSQLIfNotExists();

        getSQLiteDatabase().execSQL(createTableSql);
    }

    public void onCreateTable(OnCreateSQLiteCallback onCreateSQLiteCallback) {
        boolean isException = false;
        String createTableSql = getCreateTable()
                .setTableName(tableName)
                .addField(sqlFieldList)
                .createSQL();
        onCreateSQLiteCallback.onCreateTableBefore(tableName, sqlFieldList, createTableSql);
        try {
            getSQLiteDatabase().execSQL(createTableSql);
        } catch (SQLException e) {
            onCreateSQLiteCallback.onCreateTableFailure(e.getMessage(), tableName, sqlFieldList, createTableSql);
            isException = true;
        }
        if (!isException)
            onCreateSQLiteCallback.onCreateTableSuccess(tableName, sqlFieldList, createTableSql);
    }

    public void onCreateTableIfNotExits(OnCreateSQLiteCallback onCreateSQLiteCallback) {
        boolean isException = false;
        String createTableSql = getCreateTable()
                .setTableName(tableName)
                .addField(sqlFieldList)
                .createSQLIfNotExists();
        onCreateSQLiteCallback.onCreateTableBefore(tableName, sqlFieldList, createTableSql);
        try {
            getSQLiteDatabase().execSQL(createTableSql);
        } catch (SQLException e) {
            onCreateSQLiteCallback.onCreateTableFailure(e.getMessage(), tableName, sqlFieldList, createTableSql);
            isException = true;
        }
        if (!isException)
            onCreateSQLiteCallback.onCreateTableSuccess(tableName, sqlFieldList, createTableSql);
    }

    /**
     * 重命名表
     */
    public void reNameTable(String newName) {
        boolean isException = false;
        String reNameTableSql = getUpdateTableName()
                .setOldTableName(tableName)
                .setNewTableName(newName)
                .createSQL();
        try {
            exeSQL(reNameTableSql);
        } catch (SQLException e) {
            isException = true;
        }
        if (!isException) {
            this.tableName = newName;
        }
    }

    /**
     * 重命名表
     *
     * @param newName
     * @param onRenameTableCallback
     */
    public void reNameTable(String newName, OnRenameTableCallback onRenameTableCallback) {
        boolean isException = false;
        String reNameTableSql = getUpdateTableName()
                .setOldTableName(tableName)
                .setNewTableName(newName)
                .createSQL();
        try {
            exeSQL(reNameTableSql);
        } catch (SQLException e) {
            isException = true;
            onRenameTableCallback.onRenameFailure(e.getMessage(), tableName, newName, reNameTableSql);
        }
        if (!isException) {
            this.tableName = newName;
            onRenameTableCallback.onRenameSuccess(tableName, newName, reNameTableSql);
        }
    }

    public void deleteTable() {
        deleteTable(tableName);
    }

    public void deleteTable(OnDeleteTableCallback onDeleteTableCallback) {
        deleteTable(tableName, onDeleteTableCallback);
    }

    /**
     * 删除表 by 表名字
     *
     * @param tableName
     */
    private void deleteTable(String tableName) {
        String deleteTableSQL = getDeleteTable().setTableName(tableName).createSQL();
        exeSQL(deleteTableSQL);
    }

    /**
     * 删除表 by 表名字 & 带回调
     *
     * @param tableName
     */
    private void deleteTable(String tableName, OnDeleteTableCallback onDeleteTableCallback) {
        boolean isException = false;
        String deleteTableSQL = getDeleteTable().setTableName(tableName).createSQL();
        try {
            exeSQL(deleteTableSQL);
        } catch (Exception e) {
            isException = true;
            onDeleteTableCallback.onDeleteTableFailure(e.getMessage(), deleteTableSQL);
        }
        if (!isException) {
            onDeleteTableCallback.onDeleteTableSuccess(deleteTableSQL);
        }
    }

    /**
     * 保存数据(单条)
     *
     * @param data
     */
    public void saveData(T data) {
        if (data == null) {
            return;
        }
        AddSingleRowToTable addSingleRowToTable = getAddSingleRowToTable();
        addSingleRowToTable.setTableName(tableName);
        for (int i = 0; i < sqlFieldList.size(); i++) {
            Field field = sqlNameMap.get(sqlFieldList.get(i));
            Object value = null;
            if (reflectHelper.isBasicType(field)) {
                value = reflectHelper.getValue(data, field);
                if (value instanceof Boolean) {
                    value = setBooleanValue(field.getName(), (Boolean) value);
                }
            } else {
                value = setConversionValue(field, data);
            }

            //此处可以添加记录数据库数据之前的操作:加密数据等

            addSingleRowToTable.addData(new Value(sqlFieldList.get(i).getName(), value));
        }
        String addDataSql = addSingleRowToTable.createSQL();
        exeSQL(addDataSql);
        if (zxyChangeListener != null) {
            zxyChangeListener.onDataChange();
        }
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * 保存数据(多条)
     *
     * @param dataList
     */
    public void saveData(List<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        AddManyRowToTable addManyRowToTable = getAddManyRowToTable();
        addManyRowToTable.setTableName(tableName);
        addManyRowToTable.addFieldList(sqlFieldList);
        for (int i = 0; i < dataList.size(); i++) {
            List list = new ArrayList();
            for (int j = 0; j < sqlFieldList.size(); j++) {
                Field field = sqlNameMap.get(sqlFieldList.get(j));
                Object value = null;
                if (reflectHelper.isBasicType(field)) {
                    value = reflectHelper.getValue(dataList.get(i), field);
                    if (value instanceof Boolean) {
                        value = setBooleanValue(field.getName(), (Boolean) value);
                    }
                } else {
                    value = setConversionValue(field, dataList.get(i));
                }

                //此处可以添加记录数据库数据之前的操作:加密数据等

                list.add(value);
            }
            addManyRowToTable.addValueList(list);
        }
        String addDataSql = addManyRowToTable.createSQL();
        exeSQL(addDataSql);
        if (zxyChangeListener != null) {
            zxyChangeListener.onDataChange();
        }
    }

    /**
     * 存储之前先清空表的所有数据
     *
     * @param dataList
     */
    public void saveDataAndClearAgo(List<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        clear(false);
        saveData(dataList);
    }

    /**
     * 存储之前先清空表的所有数据
     *
     * @param
     */
    public void saveDataAndClearAgo(T data) {
        if (data == null) {
            return;
        }
        clear(false);
        saveData(data);
    }

    /**
     * 删除
     * 建议使用Where系列类生产Where SQL语句
     *
     * @param whereSQL
     */
    public void delete(String whereSQL) {
        String deleteSQL = getDeleteTableDataRow().setTableName(tableName).createSQLAutoWhere(whereSQL);
        exeSQL(deleteSQL);
        if (zxyChangeListener != null) {
            zxyChangeListener.onDataChange();
        }
    }

    /**
     * 根据主键删除数据
     * @param majorKeyValue
     */
    public void deleteByMajorKey(Object majorKeyValue){
        if(majorKeyField == null){
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键删除数据!");
        }else {
           String whereSql =  getWhere(false)
                   .addAndWhereValue(majorKeySqlField.getName(),WhereSymbolEnum.EQUAL,majorKeyValue)
                   .createSQL();
           delete(whereSql);
        }
    }

    public void clear() {
        clear(true);
    }

    /**
     * 清空数据
     */
    private void clear(boolean isResponseChange) {
        String clearTableSQL = getDeleteTableDataRow().setTableName(tableName).createDeleteAllDataSQL();
        exeSQL(clearTableSQL);
        if (isResponseChange) {
            if (zxyChangeListener != null) {
                zxyChangeListener.onDataChange();
            }
        }
    }

    /**
     * 根据主键选择更新或者保存
     * 如果存在就选择更新
     * 如果不存在选择保存
     *
     * @param t
     */
    public void saveOrUpdateByMajorKey(T t) {
        if (isContainsByMajorKey(t)) {
            //进行更新
            updateByMajorKey(t);
        } else {
            saveData(t);
        }
    }

    /**
     * 根据主键选择更新或者保存
     * 如果存在就选择更新
     * 如果不存在选择保存
     *
     * @param tList
     */
    public void saveOrUpdateByMajorKey(List<T> tList) {
        List<T> saveList = new ArrayList<>();
        for (T t : tList) {
            if (isContainsByMajorKey(t)) {
                updateByMajorKey(t);
            } else {
                saveList.add(t);
            }
        }
        saveData(saveList);
    }

    /**
     * 按照主键进行修改数据
     *
     * @param t
     */
    public void updateByMajorKey(T t) {
        if (majorKeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键更新数据!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, reflectHelper.getValue(t, majorKeyField))
                    .createSQL();
            update(t, whereSql);
        }
    }

    /**
     * 更具主建查询数据
     * @param value
     * @return
     */
    public T getDataByMajorKey(Object value){
        T t = null;
        if (majorKeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键查询数据!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, value)
                    .createSQL();
            List<T> tList = search(whereSql,null);
            if(tList != null & tList.size() > 0){
                t = tList.get(0);
            }
        }
        return t;
    }

    /**
     * 判断是否存在该条数据(根据主键判断)
     *
     * @param t
     * @return
     */
    public boolean isContainsByMajorKey(T t) {
        boolean isContains = false;
        if (majorKeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键查询数据的存在!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, reflectHelper.getValue(t, majorKeyField))
                    .createSQL();
            List<T> tList = search(whereSql, null);
            if (tList != null && tList.size() > 0) {
                isContains = true;
            } else {
                isContains = false;
            }
        }
        return isContains;
    }

    /**
     * 修改数据
     * 建议使用Where系列类生产Where SQL语句
     *
     * @param t
     * @param whereSQL
     */
    public void update(T t, String whereSQL) {
        UpdateTableDataRow updateTableDataRow = getUpdateTableDataRow();
        updateTableDataRow.setTableName(tableName);
        for (int i = 0; i < sqlFieldList.size(); i++) {
            String fieldName = sqlFieldList.get(i).getName();
            Field field = sqlNameMap.get(sqlFieldList.get(i));
            Object value = null;
            if (reflectHelper.isBasicType(field)) {
                value = reflectHelper.getValue(t, field);
                if (value instanceof Boolean) {
                    value = setBooleanValue(field.getName(), (Boolean) value);
                }
            } else {
                value = setConversionValue(field, t);
            }

            //此处可以添加记录数据库数据之前的操作:加密数据等

            updateTableDataRow.addSetValue(fieldName, value);
        }
        String updateSql = updateTableDataRow.createSQLAutoWhere(whereSQL);
        exeSQL(updateSql);
        if (zxyChangeListener != null) {
            zxyChangeListener.onDataChange();
        }
    }

    /**
     * 获取表中所有数据
     *
     * @param orderSQL
     * @return
     */
    public List<T> getAllData(String orderSQL) {
        SerachTableData serachTableData = getSearchTableData().setTableName(tableName);
        serachTableData.setIsAddField(false);
        String getAllTableDataSQL = serachTableData.getTableAllDataSQL(orderSQL);
        return searchDataBySql(getAllTableDataSQL);
    }

    /**
     * 查询数据通过一整段sql语句
     * @param searchSql
     * @return
     */
    public List<T> searchByTotalSql(String searchSql){
        return searchDataBySql(searchSql);
    }

    /**
     * 根据主键查询数据
     * @param majorKeyValue
     * @return
     */
    public T searchByMajorKey(Object majorKeyValue){
        if(majorKeyField == null){
            throw new NoMajorKeyException("没有主键,无法根据主键删除数据!");
        }else {
            String whereSql = getWhere(false)
                    .addAndWhereValue(majorKeySqlField.getName(),WhereSymbolEnum.EQUAL,majorKeyValue)
                    .createSQL();
            T t = null;
            List<T> tList = search(whereSql,null);
            if(tList != null && tList.size() > 0){
                t = tList.get(0);
            }
            return t;
        }
    }

    public List<T> search(String whereSQL, String orderSQL) {
        List<T> dataList = new ArrayList<>();
        SerachTableData serachTableData = getSearchTableData().setTableName(tableName);
        serachTableData.setIsAddField(false);
        String serachSQL = null;
        if (orderSQL != null) {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL, orderSQL);
        } else {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL);
        }
        return searchDataBySql(serachSQL);
    }

    /**
     * 更具Cursor获取数据
     * @param cursor
     * @return
     */
    private List<T> getDataListByCursor(Cursor cursor){
        List<T> dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            T t = null;
            try {
                t = getT(dataClass);
            } catch (Exception e) {

            }
            for (int i = 0; i < sqlFieldList.size(); i++) {
                Field field = sqlNameMap.get(sqlFieldList.get(i));
                int index = cursor.getColumnIndex(sqlFieldList.get(i).getName());
                String sqlDataType = null;
                if (reflectHelper.isBasicType(field)) {
                    sqlDataType = getSqlFieldType(field.getName(), field.getType()).getTypeString();
                } else {
                    sqlDataType = conversionSQLiteType(field).getTypeString();
                }
                Class type = field.getType();
                Object value = null;
                if (sqlDataType.equals(SQLFieldTypeEnum.INTEGER.getTypeName())) {
                    value = cursor.getInt(index);
                } else if (sqlDataType.equals(SQLFieldTypeEnum.BIG_INT.getTypeName())) {
                    value = cursor.getLong(index);
                } else if (sqlDataType.equals(SQLFieldTypeEnum.REAL.getTypeName())) {
                    if (type == Float.class || type.getName().equals("float")) {
                        value = cursor.getFloat(index);
                    } else if (type == Double.class || type.getName().equals("double")) {
                        value = cursor.getDouble(index);
                    } else {
                        value = cursor.getDouble(index);
                    }
                } else if (sqlDataType.equals(SQLFieldTypeEnum.TEXT.getTypeName())) {
                    if (type == Character.class || type.getName().equals("char")) {
                        String str = cursor.getString(index);
                        if (str != null) {
                            value = cursor.getString(index).charAt(0);
                        } else {
                            value = null;
                        }
                    } else {
                        value = cursor.getString(index);
                    }
                } else if (sqlDataType.equals(SQLFieldTypeEnum.BLOB.getTypeName())) {
                    value = cursor.getBlob(index);
                } else if (sqlDataType.equals(SQLFieldTypeEnum.DATE.getTypeName())) {
                    value = cursor.getString(index);
                } else if (sqlDataType.equals(SQLFieldTypeEnum.NUMERIC.getTypeName())) {
                    value = cursor.getString(index);
                }

                //此处可以进行解密类型操作

                try {
                    if (reflectHelper.isBasicType(field)) {
                        if (field.getType() == Boolean.class || field.getType().getName().equals("boolean")) {
                            Object booleanTrueValue = setBooleanValue(field.getName(), true);
                            if (booleanTrueValue.equals(value)) {
                                field.set(t, true);
                            } else {
                                field.set(t, false);
                            }
                        } else {
                            if (field.getType() == Byte.class || field.getType().getName().equals("byte")) {
                                Integer integer = (Integer) value;
                                field.set(t, (byte) integer.intValue());
                            } else if (field.getType() == Short.class || field.getType().getName().equals("short")) {
                                Integer integer = (Integer) value;
                                field.set(t, (short) integer.intValue());
                            } else {
                                field.set(t, value);
                            }
                        }
                    } else {
                        field.set(t, resumeConversionObject(field, value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            dataList.add(t);
        }
        if (cursor != null) {
            cursor.close();
        }
        return dataList;
    }

    /**
     * 查询数据
     * @param sql 整段的sql
     * @return
     */
    public List<T> searchDataBySql(String sql) {
        Cursor cursor = searchBySQL(sql);
        return getDataListByCursor(cursor);
    }

    /**
     * 提供给懒得写转换类型代码的人
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    private SQLFieldType defaultGetSQLFieldType(String classFieldName, Class typeClass) {
        return new SQLFieldType(getSqlStringType(typeClass), null);
    }

    private T getT(Class dataClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] constructors = dataClass.getDeclaredConstructors();
        Constructor constructor = constructors[0];
        constructor.setAccessible(true);
        Class[] classArray = constructor.getParameterTypes();
        if (classArray != null && classArray.length > 0) {
            Object[] objects = new Object[classArray.length];
            for (int i = 0; i < classArray.length; i++) {
                Object value = reflectHelper.getDefaultValue(classArray[i]);
                objects[i] = value;
            }
            return (T) constructor.newInstance(objects);
        } else {
            return (T) constructor.newInstance();
        }
    }

    /**
     * 根据字段的名字和类型返回相应的数据库中的保存类型
     * example：boolean -> Integer
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    protected SQLFieldType getSqlFieldType(String classFieldName, Class typeClass) {
        return defaultGetSQLFieldType(classFieldName, typeClass);
    }

    /**
     * 根据字段的名字和类型返回相应的数据库中的保存类型
     * example：int age -> age
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    protected String getSQLFieldName(String classFieldName, Class typeClass) {
        return classFieldName;
    }

    protected Object setBooleanValue(String classFieldName, boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 非基本类型转换为使用者自定义的类型
     *
     * @param field
     * @return
     */
    private SQLFieldType conversionSQLiteType(Field field) {
        if(reflectHelper.isDataStructure(field)){
            //如果是数据结构类型的属性，SQL中存储的类型为TEXT且无线长度类型
            SQLFieldType sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT,-1);
            return  sqlFieldType;
        }
        SqlType sqlType = field.getAnnotation(SqlType.class);
        SQLFieldTypeEnum sqlFieldTypeEnum = sqlType.sqlFiledType();
        int length = sqlType.length();
        SQLFieldType sqlFieldType;
        if (length <= 0) {
            sqlFieldType = new SQLFieldType(sqlFieldTypeEnum, null);
        } else {
            sqlFieldType = new SQLFieldType(sqlFieldTypeEnum, length);
        }
        return sqlFieldType;
    }

    /**
     * 将非基本类型的值转换为数据库中存储的值
     *
     * @param t
     * @return
     */
    private Object setConversionValue(Field field, T t) {
        //先判断是不是数据结构类型
        if (reflectHelper.isDataStructure(field)) {
            return getDataStructureJson(t,field);
        }
        //先看转换类型的操作
        Class typeClass = field.getType();
        Operate operate = field.getAnnotation(Operate.class);
        OperateEnum operateEnum = operate.operate();
        Object value = null;
        if (operateEnum == OperateEnum.VALUE) {
            //仅仅存值
            String valueName = operate.valueName();
            Field valueField = null;
            try {
                valueField = typeClass.getDeclaredField(valueName);
                valueField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            Object zValue = reflectHelper.getValue(t, field);
            if (zValue != null) {
                value = reflectHelper.getValue(zValue, valueField);
            } else {
                value = null;
            }
        } else {
            //Json存储
            Object zValue = reflectHelper.getValue(t, field);
            value = toJson(zValue, typeClass);
        }
        return value;
    }

    private String toJson(Object obj, Class targetClass) {
        return jsonHelper.toJson(obj);
    }

    private <E> E resumeValue(String json, Class targetClass) {
        return jsonHelper.toObject(json, targetClass);
    }

    protected Object resumeDataStructure(String classFieldName,String json){
        return null;
    }

    private String getDataStructureJson(T t,Field field){
        String json = null;
        if(field.getType().isArray()) {
            //数组
            Object[] objectArray = reflectHelper.getValueArray(t, field);
            if (objectArray != null) {
                List list = Arrays.asList(objectArray);
                json = jsonHelper.toJsonByList(list);
            }
        }else {
            //其他数据结构
            Object obj = reflectHelper.getValue(t,field);
            if(obj != null) {
                json = jsonHelper.toJson(obj);
            }
        }
        return json;
    }

    private Object getDataStructureObj(Field field,String json){
        if(json != null) {
            return resumeDataStructure(field.getName(), json);
        }else {
            return null;
        }
    }

    /**
     * 数据库中数据恢复为转换类时回调
     *
     * @param value
     * @return
     */
    private Object resumeConversionObject(Field field, Object value) {
        //先判断是不是数据结构类型
        if (reflectHelper.isDataStructure(field)) {
            return getDataStructureObj(field, (String) value);
        }
        Operate operate = field.getAnnotation(Operate.class);
        String filedName = operate.valueName();
        OperateEnum operateEnum = operate.operate();
        Class typeClass = field.getType();
        Object object = null;
        if (operateEnum == OperateEnum.VALUE) {
            try {
                object = getT(typeClass);
                Field targetField = typeClass.getDeclaredField(filedName);
                targetField.setAccessible(true);
                if (value != null && object != null) {
                    targetField.set(object, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            object = resumeValue((String) value, typeClass);
        }
        return object;
    }

    protected JsonLibraryType getJsonLibraryType() {
        return JsonLibraryType.Gson;
    }

    private SQLFieldTypeEnum getSqlStringType(Class<?> fieldJavaType) {
        return reflectHelper.getSqlStringType(fieldJavaType);
    }

    public interface OnCreateSQLiteCallback {
        void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL);

        void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL);

        void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL);
    }

    public interface OnRenameTableCallback {
        void onRenameFailure(String errMessage, String currentName, String newName, String reNameTableSQL);

        void onRenameSuccess(String oldName, String newName, String reNameTableSQL);
    }

    public interface OnDeleteTableCallback {
        void onDeleteTableFailure(String errMessage, String deleteTableSQL);

        void onDeleteTableSuccess(String deleteTableSQL);
    }
}
