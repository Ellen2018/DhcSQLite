package com.ellen.dhcsqlitelibrary;

import com.ellen.sqlitecreate.createsql.add.AddManyRowToTable;
import com.ellen.sqlitecreate.createsql.add.AddSingleRowToTable;
import com.ellen.sqlitecreate.createsql.add.AddTableColumn;
import com.ellen.sqlitecreate.createsql.create.createtable.CreateTable;
import com.ellen.sqlitecreate.createsql.delete.DeleteTable;
import com.ellen.sqlitecreate.createsql.delete.DeleteTableDataRow;
import com.ellen.sqlitecreate.createsql.order.Order;
import com.ellen.sqlitecreate.createsql.serach.SerachTableData;
import com.ellen.sqlitecreate.createsql.serach.SerachTableExist;
import com.ellen.sqlitecreate.createsql.update.UpdateTableDataRow;
import com.ellen.sqlitecreate.createsql.update.UpdateTableName;
import com.ellen.sqlitecreate.createsql.where.Between;
import com.ellen.sqlitecreate.createsql.where.Where;
import com.ellen.sqlitecreate.createsql.where.WhereIn;

public class ZxySQLite {

    /**
     * 创建表的SQL语句生产类
     */
    public CreateTable getCreateTable(){
        return CreateTable.getInstance();
    }

    /**
     * 删除表SQL语句生产类
     * @return
     */
    public DeleteTable getDeleteTable() {
        return DeleteTable.getInstance();
    }
    /**
     * 动态添加表的列的SQL语句生产类
     * @return
     */
    public AddTableColumn getAddTableColumn() {
        return AddTableColumn.getInstance();
    }
    /**
     * 修改表的名字的SQL语句生产类
     * @return
     */
    public UpdateTableName getUpdateTableName() {
        return UpdateTableName.getInstance();
    }
    /**
     * 查询表是否存在的SQL语句生产类
     */
    public SerachTableExist getSerachTableExist() {
        return SerachTableExist.getInstance();
    }
    /**
     * 增加数据（单条）的SQL语句生产类
     */
    public AddSingleRowToTable getAddSingleRowToTable() {
        return AddSingleRowToTable.getInstance();
    }
    /**
     * 增加数据（多条）的SQL语句生产类
     * example:
     * INSERT INTO student (id,name,sex) VALUES (3,'李三'，'男'),(4,'王五','女');
     */
    public AddManyRowToTable getAddManyRowToTable() {
        return AddManyRowToTable.getInstance();
    }
    /**
     * 删除数据的SQL语句生产类
     */
    public DeleteTableDataRow getDeleteTableDataRow() {
        return DeleteTableDataRow.getInstance();
    }
    /**
     * 修改数据的SQL语句生产类
     */
    public UpdateTableDataRow getUpdateTableDataRow() {
        return UpdateTableDataRow.getInstance();
    }
    /**
     * 查询数据的SQL语句生产类
     */
    public SerachTableData getSerachTableData() {
        return SerachTableData.getInstance();
    }
    /**
     * 普通where生产
     * example:
     * WHERE id = 5 AND name = 'Ellen'
     * @param isContainsWhere 生产出的Between是否包含Where
     *
     */
    public Where getWhere(boolean isContainsWhere) {
        return Where.getInstance(isContainsWhere);
    }

    /**
     * WhereIn生产的SQL语句生产类
     * example:
     * WHERE name IN ('李一','王二','张三')
     * @param isContainsWhere 生产出的Between是否包含Where
     * @return
     */
    public WhereIn getWhereIn(boolean isContainsWhere) {
        return WhereIn.getInstance(isContainsWhere);
    }

    /**
     * Between语句生产类
     * example:
     * BETWEEN 3 AND 8
     * @param isContainsWhere 生产出的Between是否包含Where
     * @return
     */
    public Between getBetween(boolean isContainsWhere) {
        return Between.getInstance(isContainsWhere);
    }

    /**
     * 排序Order生成的SQL语句生产类
     * example1:
     * ORDER BY id AESC
     * example2:
     * ORDER BY id,name ASC
     * @param isContainsOrderBy 生产出的Order是否包含ORDER BY
     * @return
     */
    public Order getOrder(boolean isContainsOrderBy) {
        return Order.getInstance(isContainsOrderBy);
    }
}
