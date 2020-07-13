package com.ellen.dhcsqlitelibrary.table.operate;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Check;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Default;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.EndAutoString;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.MajorKey;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.NotNull;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Unique;
import com.ellen.dhcsqlitelibrary.table.exception.NoMajorKeyException;
import com.ellen.dhcsqlitelibrary.table.exception.SqlFieldDuplicateException;
import com.ellen.dhcsqlitelibrary.table.helper.CursorHelper;
import com.ellen.dhcsqlitelibrary.table.helper.ReflectHelper;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonHelper;
import com.ellen.dhcsqlitelibrary.table.operate.add.Add;
import com.ellen.dhcsqlitelibrary.table.operate.create.Create;
import com.ellen.dhcsqlitelibrary.table.operate.create.OnCreateTableCallback;
import com.ellen.dhcsqlitelibrary.table.operate.delete.Delete;
import com.ellen.dhcsqlitelibrary.table.operate.search.Search;
import com.ellen.dhcsqlitelibrary.table.operate.table.Table;
import com.ellen.dhcsqlitelibrary.table.operate.update.Update;
import com.ellen.dhcsqlitelibrary.table.type.BasicTypeSupport;
import com.ellen.dhcsqlitelibrary.table.type.DataStructureSupport;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;
import com.ellen.dhcsqlitelibrary.table.type.Intercept;
import com.ellen.dhcsqlitelibrary.table.type.ObjectTypeSupport;
import com.ellen.dhcsqlitelibrary.table.type.TypeSupport;
import com.ellen.sqlitecreate.createsql.add.AddManyRowToTable;
import com.ellen.sqlitecreate.createsql.add.AddSingleRowToTable;
import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.Value;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.serach.SerachTableData;
import com.ellen.sqlitecreate.createsql.serach.SerachTableExist;
import com.ellen.sqlitecreate.createsql.update.UpdateTableDataRow;
import com.ellen.sqlitecreate.createsql.where.Where;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 包含增删改查等等操作
 *
 * @param <T>
 */
public class SqlOperate<T> extends BaseOperate<T> implements Create, Add<T>, Search<T>, Delete, Update<T>, Table {

    private Field majorKeyField = null;
    private SQLField majorKeySqlField = null;
    private boolean isAutoIncrement = false;
    //Helper
    private JsonFormat jsonFormat;
    private ZxyTable zxyTable;

    public SqlOperate(SQLiteDatabase db, Class<T> dataClass, JsonFormat jsonFormat,
                      ReflectHelper<T> reflectHelper, DataStructureSupport dataStructureSupport,
                      BasicTypeSupport basicTypeSupport, ZxyTable zxyTable) {
        super(db, zxyTable);
        this.dataClass = dataClass;
        this.jsonFormat = jsonFormat;
        this.reflectHelper = reflectHelper;
        this.basicTypeSupport = basicTypeSupport;
        this.dataStructureSupport = dataStructureSupport;
        this.zxyTable = zxyTable;
        init();
        //进行解析
        parsing();

    }

    private void init() {
        cursorHelper = new CursorHelper();
        objectTypeSupport = new ObjectTypeSupport(reflectHelper, jsonFormat);
        sqlFieldList = new ArrayList<>();
        sqlNameMap = new HashMap<>();
    }

    @Override
    public void setDebugListener(DebugListener debugListener) {
        super.setDebugListener(debugListener);
    }

    /**
     * 判断是否是主键且自增
     *
     * @return
     */
    protected boolean isHaveMajorKeyAndAuto() {
        boolean isHaveMajorAndAuto = false;
        if (majorKeySqlField != null) {
            if (isAutoIncrement) {
                isHaveMajorAndAuto = true;
            }
        }
        return isHaveMajorAndAuto;
    }

    /**
     * 进行解析
     */
    private void parsing() {
        List<Field> fieldList = reflectHelper.getClassFieldList(dataClass, false);
        for (Field field : fieldList) {
            TypeSupport typeSupport = getTypeSupport(field);
            SQLFieldType sqlFieldType = typeSupport.setSQLiteType(field);
            SQLField sqlField = handlerSqlField(field, typeSupport.setSqlFieldName(field), sqlFieldType.getSQLFieldTypeString());
            sqlNameMap.put(sqlField, field);
            for (SQLField s : sqlFieldList) {
                if (s.getName().equals(sqlField.getName())) {
                    throw new SqlFieldDuplicateException("存在重复字段名:" + sqlField.getName() + "");
                }
            }
            sqlFieldList.add(sqlField);
        }
    }

    /**
     * 处理映射的字段sql数据
     *
     * @param field
     * @param filedName
     * @param filedType
     * @return
     */
    private SQLField handlerSqlField(Field field, String filedName, String filedType) {
        SQLField sqlField = null;
        //先判断有没有EndAutoString
        EndAutoString endAutoString = field.getAnnotation(EndAutoString.class);
        if (endAutoString != null) {
            sqlField = SQLField.getAutoEndStringField(filedName, filedType, endAutoString.value());
        } else {
            sqlField = SQLField.getInstance(filedName, filedType);
            //判断有没有主键
            MajorKey majorKey = field.getAnnotation(MajorKey.class);
            if (majorKey != null) {
                sqlField.setMajorKey(true);
                if (majorKey.isAutoIncrement()) {
                    sqlField.setAuto(true);
                    isAutoIncrement = true;
                }
                majorKeySqlField = sqlField;
                majorKeyField = field;
            }
            if (field.getAnnotation(NotNull.class) != null) {
                sqlField.setNotNull(true);
            }
            if (field.getAnnotation(Unique.class) != null) {
                sqlField.setUnique(true);
            }
            Default defaultA = field.getAnnotation(Default.class);
            if (defaultA != null) {
                sqlField.setDefaultValue(reflectHelper.getDefaultAValue(defaultA));
            }
            Check check = field.getAnnotation(Check.class);
            if (check != null) {
                String checkWhereSql = check.value();
                if (checkWhereSql.contains("{}")) {
                    checkWhereSql = checkWhereSql.replace("{}", filedName);
                }
                sqlField.setCheckWhereSql(checkWhereSql);
            }
            sqlField.createSqlFiled();
        }
        return sqlField;
    }

    @Override
    public String getMajorKeyName() {
        String majorKeyName = null;
        if (majorKeySqlField != null) {
            majorKeyName = majorKeySqlField.getName();
        }
        return majorKeyName;
    }

    @Override
    public void exeSql(String sql) {
        super.exeSql(sql);
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public void saveData(T data) {
        if (data == null) {
            return;
        }
        AddSingleRowToTable addSingleRowToTable = getAddSingleRowToTable();
        addSingleRowToTable.setTableName(getTableName());
        for (int i = 0; i < sqlFieldList.size(); i++) {
            SQLField sqlField = sqlFieldList.get(i);
            Field field = sqlNameMap.get(sqlFieldList.get(i));
            Object value = null;
            TypeSupport typeSupport = getTypeSupport(field);
            value = typeSupport.toValue(field, reflectHelper.getValue(data, field));
            //先判断是否有主键且自增
            if (isHaveMajorKeyAndAuto()) {
                if (!sqlField.getName().equals(majorKeySqlField.getName())) {
                    addSingleRowToTable.addData(new Value(sqlFieldList.get(i).getName(), value));
                }
            } else {
                addSingleRowToTable.addData(new Value(sqlFieldList.get(i).getName(), value));
            }
        }
        String addDataSql = addSingleRowToTable.createSQL();
        exeSql(addDataSql);
    }

    @Override
    public void saveData(List<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        AddManyRowToTable addManyRowToTable = getAddManyRowToTable();
        addManyRowToTable.setTableName(getTableName());
        if (isHaveMajorKeyAndAuto()) {
            List<SQLField> currentSqlFieldList = new ArrayList<>();
            for (SQLField sqlField : sqlFieldList) {
                if (!sqlField.getName().equals(majorKeySqlField.getName())) {
                    currentSqlFieldList.add(sqlField);
                }
            }
            addManyRowToTable.addFieldList(currentSqlFieldList);
        } else {
            addManyRowToTable.addFieldList(sqlFieldList);
        }
        for (int i = 0; i < dataList.size(); i++) {
            List list = new ArrayList();
            for (int j = 0; j < sqlFieldList.size(); j++) {
                Field field = sqlNameMap.get(sqlFieldList.get(j));
                Object value = null;
                TypeSupport typeSupport = getTypeSupport(field);
                value = typeSupport.toValue(field, reflectHelper.getValue(dataList.get(i), field));
                //此处可以添加记录数据库数据之前的操作:加密数据等
                if (isHaveMajorKeyAndAuto()) {
                    if (!sqlFieldList.get(j).getName().equals(majorKeySqlField.getName())) {
                        list.add(value);
                    }
                } else {
                    list.add(value);
                }
            }
            addManyRowToTable.addValueList(list);
        }
        String addDataSql = addManyRowToTable.createSQL();
        exeSql(addDataSql);
    }

    private void saveDataToSegment(List<T> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }

        AddManyRowToTable addManyRowToTable = getAddManyRowToTable();
        addManyRowToTable.setTableName(getTableName());
        if (isHaveMajorKeyAndAuto()) {
            List<SQLField> currentSqlFieldList = new ArrayList<>();
            for (SQLField sqlField : sqlFieldList) {
                if (!sqlField.getName().equals(majorKeySqlField.getName())) {
                    currentSqlFieldList.add(sqlField);
                }
            }
            addManyRowToTable.addFieldList(currentSqlFieldList);
        } else {
            addManyRowToTable.addFieldList(sqlFieldList);
        }
        for (int i = 0; i < dataList.size(); i++) {
            List list = new ArrayList();
            for (int j = 0; j < sqlFieldList.size(); j++) {
                Field field = sqlNameMap.get(sqlFieldList.get(j));
                Object value = null;
                TypeSupport typeSupport = getTypeSupport(field);
                value = typeSupport.toValue(field, reflectHelper.getValue(dataList.get(i), field));
                //此处可以添加记录数据库数据之前的操作:加密数据等
                if (isHaveMajorKeyAndAuto()) {
                    if (!sqlFieldList.get(j).getName().equals(majorKeySqlField.getName())) {
                        list.add(value);
                    }
                } else {
                    list.add(value);
                }
            }
            addManyRowToTable.addValueList(list);
        }
        String addDataSql = addManyRowToTable.createSQL();
        exeSql(addDataSql);
    }

    @Override
    public void saveDataAndDeleteAgo(List<T> dataList) {
        clear();
        saveData(dataList);
    }

    @Override
    public void saveDataAndDeleteAgo(T data) {
        clear();
        saveData(data);
    }

    /**
     * 分组存储
     * 避免数据太多而导致SQL语句太长问题
     * @param dataList 存储的集合
     * @param segmentCount 每组的数目个数
     */
    @Override
    public void saveData(List<T> dataList, int segmentCount) {
        if (dataList == null && dataList.size() == 0) {
            return;
        }
        int current = 0;
        int sCount = dataList.size() / segmentCount + 1;
        if (sCount == 0 && segmentCount > dataList.size()) {
            //当分组的数目大于集合本身
            saveData(dataList);
        } else {
            for (int i = 0; i < sCount; i++) {
                current = segmentCount * i;
                int end = current + segmentCount;
                if (end > dataList.size()) {
                    end = dataList.size();
                }
                List<T> zList = dataList.subList(current, end);
                saveDataToSegment(zList);
            }
        }
    }

    @Override
    public void onCreateTable() {
        String createTableSql = getCreateTable()
                .setTableName(getTableName())
                .addField(sqlFieldList)
                .createSQL();

        exeSql(createTableSql);
    }

    @Override
    public void onCreateTable(OnCreateTableCallback onCreateTableCallback) {
        boolean isException = false;
        String createTableSql = getCreateTable()
                .setTableName(getTableName())
                .addField(sqlFieldList)
                .createSQL();
        try {
            exeSql(createTableSql);
        } catch (SQLException e) {
            onCreateTableCallback.onCreateTableFailure(e.getMessage(), getTableName(), createTableSql);
            isException = true;
        }
        if (!isException)
            onCreateTableCallback.onCreateTableSuccess(getTableName(), createTableSql);
    }

    @Override
    public void onCreateTableIfNotExits() {

        String createTableSql = getCreateTable()
                .setTableName(getTableName())
                .addField(sqlFieldList)
                .createSQLIfNotExists();

        exeSql(createTableSql);
    }

    @Override
    public void onCreateTableIfNotExits(OnCreateTableCallback onCreateTableCallback) {
        boolean isException = false;
        String createTableSql = getCreateTable()
                .setTableName(getTableName())
                .addField(sqlFieldList)
                .createSQLIfNotExists();
        try {
            exeSql(createTableSql);
        } catch (SQLException e) {
            onCreateTableCallback.onCreateTableFailure(e.getMessage(), getTableName(), createTableSql);
            isException = true;
        }
        if (!isException)
            onCreateTableCallback.onCreateTableSuccess(getTableName(), createTableSql);
    }

    private String getTableName() {
        return zxyTable.getTableName();
    }

    @Override
    public List<T> search(String whereSQL, String orderSQL) {
        SerachTableData serachTableData = getSearchTableData().setTableName(getTableName());
        serachTableData.setIsAddField(false);
        String searchSql = null;
        if (orderSQL != null) {
            searchSql = serachTableData.createSQLAutoWhere(whereSQL, orderSQL);
        } else {
            searchSql = serachTableData.createSQLAutoWhere(whereSQL);
        }
        return searchDataBySql(searchSql);
    }

    @Override
    public List<T> getAllData() {
        return getAllData(null);
    }

    @Override
    public List<T> getAllData(String orderSql) {
        SerachTableData serachTableData = getSearchTableData().setTableName(getTableName());
        serachTableData.setIsAddField(false);
        String getAllTableDataSQL = serachTableData.getTableAllDataSQL(orderSql);
        return searchDataBySql(getAllTableDataSQL);
    }

    @Override
    public List<T> searchDataBySql(String sql) {
        return search(sql);
    }

    /**
     * 是否包含此主键
     *
     * @param t
     * @return
     */
    @Override
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

    @Override
    public T searchByMajorKey(Object value) {
        T t = null;
        if (majorKeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键查询数据!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, value)
                    .createSQL();
            List<T> tList = search(whereSql, null);
            if (tList != null & tList.size() > 0) {
                t = tList.get(0);
            }
        }
        return t;
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    @Override
    public boolean isExist() {
        String searchTableExistSql = SerachTableExist.getInstance()
                .setTableName(getTableName())
                .createSQL();
        Cursor cursor = searchReturnCursor(searchTableExistSql);
        int count = cursor.getCount();
        if (cursor != null) {
            cursor.close();
        }
        return count != 0;
    }

    @Override
    public boolean deleteTable() {
        boolean isDelete = false;
        if (isExist()) {
            isDelete = true;
            String deleteTableSQL = getDeleteTable().setTableName(getTableName()).createSQL();
            exeSql(deleteTableSQL);
        }
        return isDelete;
    }

    @Override
    public int deleteReturnCount(String whereSql) {
        int count = getSearchDataCount(whereSql);
        if (count > 0) {
            String deleteSQL = getDeleteTableDataRow().setTableName(getTableName()).createSQLAutoWhere(whereSql);
            exeSql(deleteSQL);
            return count;
        }
        return 0;
    }

    @Override
    public void delete(String whereSql) {
        String deleteSQL = getDeleteTableDataRow().setTableName(getTableName()).createSQLAutoWhere(whereSql);
        exeSql(deleteSQL);
    }

    private boolean isHaveData(String whereSql) {
        return getSearchDataCount(whereSql) > 0;
    }

    private int getSearchDataCount(String whereSql) {
        List<T> tList = search(whereSql, null);
        if (tList != null && tList.size() > 0) {
            return tList.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean deleteByMajorKey(Object majorKeyValue) {
        if (majorKeyField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键删除数据!");
        } else {
            String whereSql = getWhere(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, majorKeyValue)
                    .createSQL();
            return deleteReturnCount(whereSql) > 0;
        }
    }

    @Override
    public void clear() {
        String clearTableSQL = getDeleteTableDataRow().setTableName(getTableName()).createDeleteAllDataSQL();
        exeSql(clearTableSQL);
    }

    @Override
    public void saveOrUpdateByMajorKey(T t) {
        if (isContainsByMajorKey(t)) {
            //进行更新
            updateByMajorKey(t);
        } else {
            saveData(t);
        }
    }

    @Override
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

    @Override
    public boolean updateByMajorKeyReturn(T t) {
        if (majorKeySqlField == null) {
            //说明没有主键,抛出无主键异常
            throw new NoMajorKeyException("没有主键,无法根据主键更新数据!");
        } else {
            String whereSql = Where.getInstance(false)
                    .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, reflectHelper.getValue(t, majorKeyField))
                    .createSQL();
            if (isHaveData(whereSql)) {
                update(t, whereSql);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void updateByMajorKey(T t) {
        String whereSql = Where.getInstance(false)
                .addAndWhereValue(majorKeySqlField.getName(), WhereSymbolEnum.EQUAL, reflectHelper.getValue(t, majorKeyField))
                .createSQL();
        update(t, whereSql);
    }

    @Override
    public int updateReturnCount(T t, String whereSQL) {
        int count = getSearchDataCount(whereSQL);
        if (count > 0) {
            update(t, whereSQL);
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public void update(T t, String whereSQL) {
        UpdateTableDataRow updateTableDataRow = getUpdateTableDataRow();
        updateTableDataRow.setTableName(getTableName());
        for (int i = 0; i < sqlFieldList.size(); i++) {
            String fieldName = sqlFieldList.get(i).getName();
            SQLField sqlField = sqlFieldList.get(i);
            Field field = sqlNameMap.get(sqlFieldList.get(i));
            Object value = null;
            TypeSupport typeSupport = getTypeSupport(field);
            value = typeSupport.toValue(field, reflectHelper.getValue(t, field));
            if (isHaveMajorKeyAndAuto()) {
                //不更新主键
                if (!sqlField.getName().equals(majorKeySqlField.getName())) {
                    //不是主键
                    updateTableDataRow.addSetValue(fieldName, value);
                }
            } else {
                updateTableDataRow.addSetValue(fieldName, value);
            }
        }
        String updateSql = updateTableDataRow.createSQLAutoWhere(whereSQL);
        exeSql(updateSql);
    }

    @Override
    public boolean reNameTable(String newName) {
        //判断新表是否存在
        String searchTableExistSql = SerachTableExist.getInstance()
                .setTableName(newName)
                .createSQL();
        Cursor cursor = db.rawQuery(searchTableExistSql, null);
        if (cursor.getCount() != 0) {
            return false;
        }
        String reNameTableSql = getUpdateTableName()
                .setOldTableName(getTableName())
                .setNewTableName(newName)
                .createSQL();
        exeSql(reNameTableSql);
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }

    @Override
    public void addIntercept(Intercept intercept) {
        super.addIntercept(intercept);
    }

    @Override
    public void removeIntercept(Intercept intercept) {
        super.removeIntercept(intercept);
    }
}
