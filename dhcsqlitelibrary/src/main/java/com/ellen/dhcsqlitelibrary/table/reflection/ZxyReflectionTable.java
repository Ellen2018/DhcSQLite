package com.ellen.dhcsqlitelibrary.table.reflection;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.AutoDesignOperate;
import com.ellen.dhcsqlitelibrary.table.Proxy.AutoOperateProxy;
import com.ellen.dhcsqlitelibrary.table.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.annotation.Operate;
import com.ellen.dhcsqlitelibrary.table.exception.NoPrimaryKeyException;
import com.ellen.dhcsqlitelibrary.table.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.json.JsonLibraryType;
import com.ellen.dhcsqlitelibrary.table.annotation.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.annotation.NoBasicType;
import com.ellen.dhcsqlitelibrary.table.annotation.NotNull;
import com.ellen.dhcsqlitelibrary.table.annotation.OperateEnum;
import com.ellen.dhcsqlitelibrary.table.annotation.Primarykey;
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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 此类是基于反射对类进行自动建表
 * 完全类似于LitePal的用法
 */
public abstract class ZxyReflectionTable<T,O extends AutoDesignOperate> extends ZxyTable {

    private Class dataClass;

    private List<SQLField> sqlFieldList;
    private String tableName;
    private O autoDesignOperate;
    private ReflactionHelper<T> reflactionHelper;
    private HashMap<SQLField, Field> sqlNameMap;
    private Field primarykeyField = null;
    private SQLField primarykeySqlField = null;
    private JsonHelper jsonHelper;

    private ZxyChangeListener zxyChangeListener;
    private Class<? extends AutoDesignOperate> autoClass;

    public ZxyReflectionTable(SQLiteDatabase db, Class<? extends T> dataClass,Class<? extends AutoDesignOperate> autoClass) {
        super(db);
        init(dataClass.getSimpleName(), dataClass,autoClass);
    }

    public O getAutoDesignOperate() {
        return autoDesignOperate;
    }

    public ZxyReflectionTable(SQLiteDatabase db, Class<? extends T> dataClass, String autoTableName,Class<? extends AutoDesignOperate> autoClass) {
        super(db);
        init(autoTableName, dataClass,autoClass);
    }

    private void init(String tableName, Class<? extends T> dataClass,Class<? extends AutoDesignOperate> autoClass) {
        this.dataClass = dataClass;
        this.tableName = tableName;
        reflactionHelper = new ReflactionHelper<>();
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
        List<Field> fieldList = reflactionHelper.getClassFieldList(dataClass, false);
        for (Field field : fieldList) {
            String fieldType = null;
            String fieldName = null;
            if (reflactionHelper.isBasicType(field)) {
                fieldType = getSqlFieldType(field.getName(), field.getType()).getSQLFieldTypeString();
            } else {
                //不是基本类型
                fieldType = conversionSQLiteType(field).getSQLFieldTypeString();
            }
            DhcSqlFieldName dhcSqlFieldName = field.getAnnotation(DhcSqlFieldName.class);
            if (dhcSqlFieldName == null) {
                fieldName = getSQLFieldName(field.getName(), field.getType());
            } else {
                fieldName = dhcSqlFieldName.value();
            }
            Primarykey primarykey = field.getAnnotation(Primarykey.class);
            SQLField sqlField = null;
            if (primarykey != null) {
                //这里是主键
                sqlField = SQLField.getPrimaryKeyField(fieldName, fieldType, false);
                primarykeyField = field;
                primarykeySqlField = sqlField;
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
    public void deleteTable(String tableName) {
        String deleteTableSQL = getDeleteTable().setTableName(tableName).createSQL();
        exeSQL(deleteTableSQL);
    }

    /**
     * 删除表 by 表名字 & 带回调
     *
     * @param tableName
     */
    public void deleteTable(String tableName, OnDeleteTableCallback onDeleteTableCallback) {
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

    public void deleteData(String deleteSql){
        exeSQL(deleteSql);
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
            if (reflactionHelper.isBasicType(field)) {
                value = reflactionHelper.getValue(data, field);
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
                if (reflactionHelper.isBasicType(field)) {
                    value = reflactionHelper.getValue(dataList.get(i), field);
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
    public void saveDataAndDeleteAgo(List<T> dataList) {
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
    public void saveDataAndDeleteAgo(T data) {
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
    public void saveOrUpdateByPrimaryKey(T t) {
        if (isContainsByPrimaryKey(t)) {
            //进行更新
            updateByPrimaryKey(t);
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
    public void saveOrUpdateByPrimaryKey(List<T> tList) {
        List<T> saveList = new ArrayList<>();
        for (T t : tList) {
            if (isContainsByPrimaryKey(t)) {
                updateByPrimaryKey(t);
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
    public void updateByPrimaryKey(T t) {
        if (primarykeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoPrimaryKeyException("没有主键,无法根据主键更新数据!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(primarykeySqlField.getName(), WhereSymbolEnum.EQUAL, reflactionHelper.getValue(t, primarykeyField))
                    .createSQL();
            update(t, whereSql);
        }
    }

    /**
     * 判断是否存在该条数据(根据主键判断)
     *
     * @param t
     * @return
     */
    public boolean isContainsByPrimaryKey(T t) {
        boolean isContains = false;
        if (primarykeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoPrimaryKeyException("没有主键,无法根据主键查询数据的存在!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(primarykeySqlField.getName(), WhereSymbolEnum.EQUAL, reflactionHelper.getValue(t, primarykeyField))
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
            if (reflactionHelper.isBasicType(field)) {
                value = reflactionHelper.getValue(t, field);
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
        SerachTableData serachTableData = getSerachTableData().setTableName(tableName);
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

    public List<T> search(String whereSQL, String orderSQL) {
        List<T> dataList = new ArrayList<>();
        SerachTableData serachTableData = getSerachTableData().setTableName(tableName);
        serachTableData.setIsAddField(false);
        String serachSQL = null;
        if (orderSQL != null) {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL, orderSQL);
        } else {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL);
        }
        return searchDataBySql(serachSQL);
    }

    public List<T> searchDataBySql(String sql) {
        List<T> dataList = new ArrayList<>();
        Cursor cursor = searchBySQL(sql);
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
                if (reflactionHelper.isBasicType(field)) {
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
                    if (reflactionHelper.isBasicType(field)) {
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
     * 提供给懒得写转换类型代码的人
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    protected SQLFieldType defaultGetSQLFieldType(String classFieldName, Class typeClass) {
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
                Object value = reflactionHelper.getDefaultValue(classArray[i]);
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
        if(reflactionHelper.isDataStructure(field)){
            //如果是数据结构类型的属性，SQL中存储的类型为TEXT且无线长度类型
            SQLFieldType sqlFieldType = new SQLFieldType(SQLFieldTypeEnum.TEXT,-1);
            return  sqlFieldType;
        }
        NoBasicType noBasicType = field.getAnnotation(NoBasicType.class);
        SQLFieldTypeEnum sqlFieldTypeEnum = noBasicType.sqlFiledType();
        int length = noBasicType.length();
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
        if (reflactionHelper.isDataStructure(field)) {
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
            Object zValue = reflactionHelper.getValue(t, field);
            if (zValue != null) {
                value = reflactionHelper.getValue(zValue, valueField);
            } else {
                value = null;
            }
        } else {
            //Json存储
            Object zValue = reflactionHelper.getValue(t, field);
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
            Object[] objectArray = reflactionHelper.getValueArray(t, field);
            if (objectArray != null) {
                List list = Arrays.asList(objectArray);
                json = jsonHelper.toJsonByList(list);
            }
        }else {
            //其他数据结构
            Object obj = reflactionHelper.getValue(t,field);
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
        if (reflactionHelper.isDataStructure(field)) {
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
        return reflactionHelper.getSqlStringType(fieldJavaType);
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
