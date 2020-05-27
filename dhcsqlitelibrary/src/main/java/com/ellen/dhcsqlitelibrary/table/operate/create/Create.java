package com.ellen.dhcsqlitelibrary.table.operate.create;

public interface Create {
    void onCreateTable();
    void onCreateTable(OnCreateTableCallback onCreateTableCallback);
    void onCreateTableIfNotExits();
    void onCreateTableIfNotExits(OnCreateTableCallback onCreateTableCallback);
}
