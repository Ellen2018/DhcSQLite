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
import com.ellen.dhcsqlitelibrary.table.operate.DebugListener;
import com.ellen.dhcsqlitelibrary.table.operate.create.OnCreateTableCallback;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyLibrary;
import com.ellen.dhcsqlitelibrary.table.type.TypeSupport;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldType;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.where.Where;

import java.lang.reflect.Field;
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

        studentTable.setDebugListener(new DebugListener() {
            @Override
            public void exeSql(String sql) {
                Log.e("Ellen2018","执行sql语句:"+sql);
            }
        });

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
        studentTable.saveData(studentList);

        String whereSql = Where.getInstance(false).addAndWhereValue("your_age", WhereSymbolEnum.MORE_THAN, 3).createSQL();

        for (Student student1 : studentTable.getAllData()) {
            Log.e("Ellen2018", "数据:" + student1.toString());
        }

    }
}
