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

import java.util.ArrayList;
import java.util.List;

public class NewActivtiy extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ZxyLibrary zxyLibrary = new AppLibrary(this, "sqlite_library", 1);
        zxyLibrary.clearLibrary();
        SQLiteDatabase sqLiteDatabase = zxyLibrary.getWriteDataBase();

        sqLiteDatabase.enableWriteAheadLogging();//开启数据库的多线程读写

        NewStudentTable studentTable = new NewStudentTable(zxyLibrary,Student.class, MyAutoDesignOperate.class);


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
            father = new Father("Ellen2019", "1", "尼玛" + i);
            student.setFather(father);
            if(i == 3){
                student.setMan(true);
            }
            studentList.add(student);
        }
        studentTable.saveData(studentList,4);

        //通过代理接口调用
        studentTable.getAutoDesignOperate().update1("新的名字","Ellen2018_0");

        for (Student student1 : studentTable.getAllData()) {
            Log.e("Ellen2018", "数据:" + student1.toString());
        }

    }
}
