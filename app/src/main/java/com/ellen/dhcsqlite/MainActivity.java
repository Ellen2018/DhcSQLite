package com.ellen.dhcsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlite.sql.AppLibrary;
import com.ellen.dhcsqlite.sql.MyAutoDesignOperate;
import com.ellen.dhcsqlite.sql.StudentTable;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyChangeListener;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyLibrary;
import com.ellen.dhcsqlitelibrary.table.reflection.ZxyTable;
import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;
import com.ellen.sqlitecreate.createsql.delete.DeleteTableDataRow;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.order.Order;
import com.ellen.sqlitecreate.createsql.serach.SerachTableData;
import com.ellen.sqlitecreate.createsql.update.UpdateTableDataRow;
import com.ellen.sqlitecreate.createsql.where.Where;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvAll;
    private StudentTable studentTable;
    private AppLibrary appLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAll = findViewById(R.id.tv_all);
        ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
        SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();
        StudentTable studentTable = new StudentTable(sqLiteDatabase, Student.class, MyAutoDesignOperate.class);

        this.appLibrary = (AppLibrary) zxyLibrary;
        this.studentTable = studentTable;

        //创建表
        onCreateTable();

        studentTable.clear();

        //增加数据
        addData();
        //删除数据
        //deleteData();
        //修改数据
        //updateData();
        //查询数据
        //searchData();
        //其他用法
        //other();
        //元操作
        yuanOperate();
    }

    private void yuanOperate() {
        MyAutoDesignOperate myAutoDesignOperate = studentTable.getAutoDesignOperate();
        myAutoDesignOperate.getStudentByName("Ellen2018");
        myAutoDesignOperate.delete(3,"周杰伦");
    }

    private void other() {

        //是否包含该条数据 & 根据主键来判断
        //注意这种方式换成search方式完成，并不一定使用该方法，当然这只是笔者提供的一个方法而已
        Student student = new Student(-1, "Ellen2018", 19, "18272167574", "火星");
        Father father = new Father("Ellen2019", "1");
        student.setFather(father);
        boolean isContains = studentTable.isContainsByMajorKey(student);
        if(isContains){
            //存在该数据
        }else {
            //不存在该数据
        }

        //获取表名字
        String tableName = studentTable.getTableName();

        studentTable.reNameTable("修改的表名");

        //修改表名
        studentTable.reNameTable("my_student", new ZxyTable.OnRenameTableCallback() {
            @Override
            public void onRenameFailure(String errMessage, String currentName, String newName, String reNameTableSQL) {
                //修改失败回调这里
            }

            @Override
            public void onRenameSuccess(String oldName, String newName, String reNameTableSQL) {
                //修改成功回调这里
            }
        });

        //删除表
        studentTable.deleteTable();

        //删除表带回调
        studentTable.deleteTable(new ZxyTable.OnDeleteTableCallback() {
            @Override
            public void onDeleteTableFailure(String errMessage, String deleteTableSQL) {

            }

            @Override
            public void onDeleteTableSuccess(String deleteTableSQL) {

            }
        });

        //也可以通过Library对象进行删除
        appLibrary.deleteTable(studentTable.getTableName());

        //获取主键字段名
        String majorKeyName = studentTable.getMajorKeyName();
        if(majorKeyName == null){
            //说明无主键
        }

        //删除表 & 带回调
        studentTable.deleteTable(new ZxyTable.OnDeleteTableCallback() {
            @Override
            public void onDeleteTableFailure(String errMessage, String deleteTableSQL) {
                //删除失败回调这里
            }

            @Override
            public void onDeleteTableSuccess(String deleteTableSQL) {
                //删除成功回调这里
            }
        });
    }

    private void searchData() {

        Student student = studentTable.searchByMajorKey(3);

        //查询my_name字段中含有"Ellen"的数据,然后根据your_age进行排序(Desc方式)
        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.LIKE, "%Ellen%")
                        .createSQL();
        String orderSql = Order.getInstance(false)
                .setFirstOrderFieldName("your_age")
                .setIsDesc(true)
                .createSQL();

        List<Student> studentList = studentTable.search(whereSql, orderSql);

        //查询表中所有数据，没有排列顺序
        List<Student> studentList1 = studentTable.getAllData(null);
        //查询表中所有数据，根据age进行排序(Desc方式)
        List<Student> studentList2 = studentTable.getAllData(orderSql);
    }

    private void updateData() {
        //根据主键进行修改
        //注意如果你的bean类没有声明主键，那么调用此方法就会抛 NoPrimaryKeyException
        Student student = new Student(-1, "Ellen2018", 19, "18272167574", "火星");
        Father father = new Father("Ellen2019", "1");
        student.setFather(father);
        studentTable.updateByMajorKey(student);

        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.EQUAL, "Ellen2018")
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();
        //注意这种修改方式为全映射修改，如果只修改部分数据，请使用下面的方式
        //什么是全映射修改？就是将对象的整个属性数据覆盖在whereSql满足的条件里
        studentTable.update(student, whereSql);


        //自定义updateSql进行修改数据
        //将大于age > 20岁年龄的数据的 age 的值全部修改为 age = 18,my_name = "永远18岁"
        String whereSqlByAge =
                Where.getInstance(false)
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();

        String updateSql = UpdateTableDataRow.getInstance()
                .setTableName(studentTable.getTableName())
                .addSetValue("your_age", 18)
                .addSetValue("my_name", "永远18岁")
                .createSQLAutoWhere(whereSqlByAge);

        studentTable.exeSQL(updateSql);

        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            student = new Student(i, "Ellen2018", 19, "18272167574", "火星");
            studentList.add(student);
        }
        //save or update 根据主键判断,根据主键查询没有该数据就存储，有就进行更新
        studentTable.saveOrUpdateByMajorKey(student);
        studentTable.saveOrUpdateByMajorKey(studentList);

    }

    private void deleteData() {
        //先构建Where SQL语句

        //删除my_name为Ellen2018 且age > 20 的数据
        String whereSql =
                Where.getInstance(false)
                        .addAndWhereValue("my_name", WhereSymbolEnum.EQUAL, "Ellen2018")
                        .addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 20)
                        .createSQL();

        studentTable.delete(whereSql);

        //也可以自己完全构造出删除的SQL语句进行删除
        String deleteSql =
                DeleteTableDataRow.getInstance()
                        .setTableName(studentTable.getTableName())
                        .createSQLAutoWhere(whereSql);
        studentTable.exeSQL(deleteSql);

        //清空数据
        studentTable.clear();

        //根据主键删除数据
        studentTable.deleteByMajorKey(3);

    }

    /**
     * 创建表
     */
    private void onCreateTable() {

        //创建表带回调
        studentTable.onCreateTableIfNotExits(new ZxyTable.OnCreateSQLiteCallback() {
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {
                BaseLog.log("创建表", createSQL);
                tvAll.setText(createSQL);
            }
        });

        //创建表不带回调-->建议使用这种
        studentTable.onCreateTableIfNotExits();

        //不建议使用这种
        //studentTable.onCreateTable();

        studentTable.onCreateTable(new ZxyTable.OnCreateSQLiteCallback() {
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }
        });
    }

    private void addData() {
        Student student = new Student(-1, "Ellen2018", 19, "18272167574", "火星");
        student.setMan(true);
        Father father = new Father("Ellen2019", "1");
        student.setFather(father);

        //单条数据添加
        studentTable.saveData(student);

        //多条数据添加
        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            student = new Student(i, "Ellen2018", i, "18272167574", "火星");
            father = new Father("Ellen2019", ""+i);
            student.setFather(father);
            student.setMan(true);
            studentList.add(student);
        }
        studentTable.saveData(studentList);

        //存储数据之前清空数据
        //studentTable.saveOrUpdateByMajorKey(studentList);
        //studentTable.saveOrUpdateByMajorKey(student);
    }
}
