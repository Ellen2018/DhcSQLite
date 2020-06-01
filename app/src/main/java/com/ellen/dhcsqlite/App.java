package com.ellen.dhcsqlite;

import android.app.Application;
import android.util.Log;

import com.ellen.dhcsqlitelibrary.table.operate.TotalListener;
import com.ellen.dhcsqlitelibrary.table.impl.ZxyTable;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //全局sql语句操作监听
        ZxyTable.setTotalListener(new TotalListener() {
            @Override
            public void exeSql(String tableName, String sql) {
                Log.e("Ellen2018","全局监听SQL语句("+tableName+"):"+sql);
            }
        });

    }
}
