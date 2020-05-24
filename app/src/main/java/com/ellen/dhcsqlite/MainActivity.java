package com.ellen.dhcsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ellen.dhcsqlitelibrary.table.reflection.ZxyReflectionTable;
import com.ellen.sqlitecreate.createsql.create.createtable.SQLField;
import com.ellen.sqlitecreate.createsql.helper.WhereSymbolEnum;
import com.ellen.sqlitecreate.createsql.order.Order;
import com.ellen.sqlitecreate.createsql.where.Where;
import com.ellen.sqlitecreate.createsql.where.WhereIn;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAll = findViewById(R.id.tv_all);
        SQliteLibrary sQliteLibrary = new SQliteLibrary(this,"sqlite_library",1);
        StudentTable studentTable = new StudentTable(sQliteLibrary.getWriteDataBase(),Student.class);

        //创建表带回调
        studentTable.onCreateTableIfNotExits(new ZxyReflectionTable.OnCreateSQLiteCallback() {
            @Override
            public void onCreateTableBefore(String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableFailure(String errMessage, String tableName, List<SQLField> sqlFieldList, String createSQL) {

            }

            @Override
            public void onCreateTableSuccess(String tableName, List<SQLField> sqlFieldList, String createSQL) {
                BaseLog.log("创建表",createSQL);
                tvAll.setText(createSQL);
            }
        });
    }
}
