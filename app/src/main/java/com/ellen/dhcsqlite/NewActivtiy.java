package com.ellen.dhcsqlite;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlite.bean.Student;
import com.ellen.dhcsqlite.sql.AppLibrary;
import com.ellen.dhcsqlite.sql.MyAutoDesignOperate;
import com.ellen.dhcsqlite.sql.NewStudentTable;
import com.ellen.dhcsqlitelibrary.table.operate.create.OnCreateTableCallback;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyLibrary;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.where.Where;

import java.util.ArrayList;
import java.util.List;

public class NewActivtiy extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
        SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();
        NewStudentTable studentTable = new NewStudentTable(sqLiteDatabase, Student.class, MyAutoDesignOperate.class);

        if (studentTable.isExist()) {
            studentTable.deleteTable();
        }

        Log.e("Ellen2018", "表是否存在:" + studentTable.isExist());

        studentTable.onCreateTableIfNotExits(new OnCreateTableCallback() {
            @Override
            public void onCreateTableFailure(String errMessage, String tableName, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, String createSQL) {
                Log.e("Ellen2018", "创建数据库:" + createSQL);
            }
        });

        Log.e("Ellen2018", "表是否存在:" + studentTable.isExist());

        Student student = new Student("Ellen2018", 19, "18272167574", "火星");
        student.setMan(true);
        Father father = new Father("Ellen2019", "1", "尼玛");
        student.setFather(father);

        //单条数据添加
        studentTable.saveData(student);

        List<Student> studentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            student = new Student("Ellen2018_" + i, i, "18272167574", "火星");
            student.setMan(true);
            father = new Father("Ellen2019", "1", "尼玛" + i);
            student.setFather(father);
            studentList.add(student);
        }
        studentTable.saveData(studentList);

        String whereSql = Where.getInstance(false).addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 3).createSQL();
        int count = studentTable.update(student, whereSql);

        Log.e("Ellen2018", "修改的数据个数:" + count);

        for (Student student1 : studentTable.getAutoDesignOperate().getSearchList2()) {
            Log.e("Ellen2018", "数据:" + student1.toString());
        }

        Log.e("Ellen2018","查询:"+studentTable.getDataByMajorKey(3));
    }
}
