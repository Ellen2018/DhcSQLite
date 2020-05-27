package com.ellen.dhcsqlitelibrary.table.operate.table;

import android.database.Cursor;

public interface Table {
    String getMajorKeyName();
    void exeSql(String sql);
}
