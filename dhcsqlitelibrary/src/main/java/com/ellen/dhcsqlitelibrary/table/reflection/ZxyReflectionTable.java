package com.ellen.dhcsqlitelibrary.table.reflection;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.exception.NoPrimaryKeyExcepition;
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
import java.util.HashMap;
import java.util.List;

/**
 * 此类是基于反射对类进行自动建表
 * 完全类似于LitePal的用法
 */
public abstract class ZxyReflectionTable<T> extends ZxyTable {

    private Class dataClass;
    private List<SQLField> sqlFieldList;
    private String tableName;
    private ReflactionHelper<T> reflactionHelper;
    private HashMap<SQLField, Field> sqlNameMap;
    private Field primarykeyField = null;
    private SQLField primarykeySqlField = null;

    public ZxyReflectionTable(SQLiteDatabase db, Class<? extends T> dataClass) {
        super(db);
        this.dataClass = dataClass;
        this.tableName = dataClass.getSimpleName();
        reflactionHelper = new ReflactionHelper<>();
        sqlNameMap = new HashMap<>();
        getFields();
        //自动创建表
        if (isAutoCreateTable()) {
            onCreateTableIfNotExits();
        }
    }

    public ZxyReflectionTable(SQLiteDatabase db, Class<? extends T> dataClass, String autoTableName) {
        super(db);
        this.dataClass = dataClass;
        this.tableName = autoTableName;
        reflactionHelper = new ReflactionHelper<>();
        sqlNameMap = new HashMap<>();
        getFields();
        //自动创建表
        if (isAutoCreateTable()) {
            onCreateTableIfNotExits();
        }
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
                fieldType = getSQLFieldType(field.getName(), field.getType()).getSQLFieldTypeString();
            } else {
                fieldType = conversionSQLiteType(field.getName(), field.getType()).getSQLFieldTypeString();
            }
            fieldName = getSQLFieldName(field.getName(), field.getType());
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
     * @param onRenameTableCallbcak
     */
    public void reNameTable(String newName, OnRenameTableCallbcak onRenameTableCallbcak) {
        boolean isException = false;
        String reNameTableSql = getUpdateTableName()
                .setOldTableName(tableName)
                .setNewTableName(newName)
                .createSQL();
        try {
            exeSQL(reNameTableSql);
        } catch (SQLException e) {
            isException = true;
            onRenameTableCallbcak.onRenameFailure(e.getMessage(), tableName, newName, reNameTableSql);
        }
        if (!isException) {
            this.tableName = newName;
            onRenameTableCallbcak.onRenameSuccess(tableName, newName, reNameTableSql);
        }
    }

    public void deleteTable(String tableName) {
        String deleteTableSQL = getDeleteTable().setTableName(tableName).createSQL();
        exeSQL(deleteTableSQL);
    }

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
                value = setConversionValue(data, field.getName(), field.getType());
            }
            if (ZxyReflectionTable.this instanceof EncryptionInterFace) {
                //进行加密
                EncryptionInterFace encryptionInterFace = (EncryptionInterFace) this;
                value = encryptionInterFace.encryption(field.getName(), sqlFieldList.get(i).getName(), field.getType(), value);
            }
            addSingleRowToTable.addData(new Value(sqlFieldList.get(i).getName(), value));
        }
        String addDataSql = addSingleRowToTable.createSQL();
        exeSQL(addDataSql);
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
                    value = setConversionValue(dataList.get(i), field.getName(), field.getType());
                }
                if (ZxyReflectionTable.this instanceof EncryptionInterFace) {
                    //进行加密
                    EncryptionInterFace encryptionInterFace = (EncryptionInterFace) this;
                    value = encryptionInterFace.encryption(field.getName(), sqlFieldList.get(j).getName(), field.getType(), value);
                }
                list.add(value);
            }
            addManyRowToTable.addValueList(list);
        }
        String addDataSql = addManyRowToTable.createSQL();
        exeSQL(addDataSql);
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
        clear();
        saveData(dataList);
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
    }

    /**
     * 清空数据
     */
    public void clear() {
        String clearTableSQL = getDeleteTableDataRow().setTableName(tableName).createDeleteAllDataSQL();
        exeSQL(clearTableSQL);
    }

    /**
     * 根据主键选择更新或者保存
     * 如果存在就选择更新
     * 如果不存在选择保存
     * @param t
     */
    public void saveOrUpdateByPrimaryKey(T t){
        if(isContainsByPrimaryKey(t)){
            //进行更新
            updateByPrimaryKey(t);
        }else {
            saveData(t);
        }
    }

    /**
     * 根据主键选择更新或者保存
     * 如果存在就选择更新
     * 如果不存在选择保存
     * @param tList
     */
    public void saveOrUpdateByPrimaryKey(List<T> tList){
        List<T> saveList = new ArrayList<>();
        for(T t:tList){
            if(isContainsByPrimaryKey(t)){
                updateByPrimaryKey(t);
            }else {
                saveList.add(t);
            }
        }
        saveData(saveList);
    }

    /**
     * 按照主键进行修改数据
     * @param t
     */
    public void updateByPrimaryKey(T t){
        if(primarykeySqlField == null){
            //说明没有主键,抛出无主键异常
            throw new NoPrimaryKeyExcepition("没有主键,无法根据主键更新数据!");
        }else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(primarykeySqlField.getName(), WhereSymbolEnum.EQUAL,reflactionHelper.getValue(t,primarykeyField))
                    .createSQL();
            update(t,whereSql);
        }
    }

    /**
     * 判断是否存在该条数据(根据主键判断)
     * @param t
     * @return
     */
    public boolean isContainsByPrimaryKey(T t){
        boolean isContains = false;
        if(primarykeySqlField == null){
            //说明没有主键,抛出无主键异常
            throw new NoPrimaryKeyExcepition("没有主键,无法根据主键查询数据的存在!");
        }else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(primarykeySqlField.getName(), WhereSymbolEnum.EQUAL,reflactionHelper.getValue(t,primarykeyField))
                    .createSQL();
            List<T> tList = serach(whereSql,null);
            if(tList != null && tList.size() > 0){
                isContains = true;
            }else {
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
                value = setConversionValue(t, field.getName(), field.getType());
            }
            if (this instanceof EncryptionInterFace) {
                EncryptionInterFace encryptionInterFace = (EncryptionInterFace) this;
                value = encryptionInterFace.encryption(field.getName(), fieldName, field.getType(), value);
            }
            updateTableDataRow.addSetValue(fieldName, value);
        }
        String updateSql = updateTableDataRow.createSQLAutoWhere(whereSQL);
        exeSQL(updateSql);
    }

    /**
     * 获取表中所有数据
     *
     * @param orderSQL
     * @return
     */
    public List<T> getAllDatas(String orderSQL) {
        SerachTableData serachTableData = getSerachTableData().setTableName(tableName);
        serachTableData.setIsAddField(false);
        String getAllTableDataSQL = serachTableData.getTableAllDataSQL(orderSQL);
        return serachDatasBySQL(getAllTableDataSQL);
    }

    public List<T> serach(String whereSQL, String orderSQL) {
        List<T> dataList = new ArrayList<>();
        SerachTableData serachTableData = getSerachTableData().setTableName(tableName);
        serachTableData.setIsAddField(false);
        String serachSQL = null;
        if (orderSQL != null) {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL, orderSQL);
        } else {
            serachSQL = serachTableData.createSQLAutoWhere(whereSQL);
        }
        return serachDatasBySQL(serachSQL);
    }

    private List<T> serachDatasBySQL(String sql) {
        List<T> dataList = new ArrayList<>();
        Cursor cursor = serachBySQL(sql);
        while (cursor.moveToNext()) {
            T t = null;
            try {
                t = getT();
            } catch (Exception e) {

            }
            for (int i = 0; i < sqlFieldList.size(); i++) {
                Field field = sqlNameMap.get(sqlFieldList.get(i));
                int index = cursor.getColumnIndex(sqlFieldList.get(i).getName());
                String sqlDataType = null;
                if (reflactionHelper.isBasicType(field)) {
                    sqlDataType = getSQLFieldType(field.getName(), field.getType()).getTypeString();
                } else {
                    sqlDataType = conversionSQLiteType(field.getName(), field.getType()).getTypeString();
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

                //进行解密
                if (this instanceof EncryptionInterFace) {
                    EncryptionInterFace encryptionInterFace = (EncryptionInterFace) this;
                    value = encryptionInterFace.decrypt(field.getName(), sqlFieldList.get(i).getName(), field.getType(), value);
                }

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
                        field.set(t, resumeConversionObject(value, field.getName(), field.getType()));
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
        return new SQLFieldType(getSQlStringType(typeClass), null);
    }

    private T getT() throws IllegalAccessException, InvocationTargetException, InstantiationException {
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
    protected abstract SQLFieldType getSQLFieldType(String classFieldName, Class typeClass);

    /**
     * 根据字段的名字和类型返回相应的数据库中的保存类型
     * example：int -> Integer
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    protected abstract String getSQLFieldName(String classFieldName, Class typeClass);

    protected abstract Object setBooleanValue(String classFieldName, boolean value);

    protected abstract boolean isAutoCreateTable();

    /**
     * 非基本类型转换为使用者自定义的类型
     *
     * @param classFieldName
     * @param typeClass
     * @return
     */
    protected abstract SQLFieldType conversionSQLiteType(String classFieldName, Class typeClass);

    /**
     * 将非基本类型的值转换为数据库中存储的值
     *
     * @param t
     * @param classFieldName
     * @param typeClass
     * @param <E>
     * @return
     */
    protected abstract <E> E setConversionValue(T t, String classFieldName, Class typeClass);

    /**
     * 数据库中数据恢复为转换类时回调
     *
     * @param value
     * @param classFieldName
     * @param typeClass
     * @param <E>
     * @return
     */
    protected abstract <E> E resumeConversionObject(Object value, String classFieldName, Class typeClass);

    public SQLFieldTypeEnum getSQlStringType(Class<?> ziDuanJavaType) {
        return reflactionHelper.getSQlStringType(ziDuanJavaType);
    }

    public interface OnCreateSQLiteCallback {
        void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL);

        void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL);

        void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL);
    }

    public interface OnRenameTableCallbcak {
        void onRenameFailure(String errMessage, String currentName, String newName, String reNameTableSQL);

        void onRenameSuccess(String oldName, String newName, String reNameTableSQL);
    }

    public interface OnDeleteTableCallback {
        void onDeleteTableFailure(String errMessage, String deleteTableSQL);

        void onDeleteTableSuccess(String deleteTableSQL);
    }
}
