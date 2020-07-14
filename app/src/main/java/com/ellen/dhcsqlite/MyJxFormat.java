package com.ellen.dhcsqlite;


import com.ellen.dhcsqlitelibrary.table.helper.json.JxFormat;
import com.google.gson.Gson;

/**
 * 自定义Json解析器
 */
public class MyJxFormat implements JxFormat {

    private Gson gson;

    public MyJxFormat(){
        gson = new Gson();
    }

    @Override
    public String toJxString(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return (E) gson.fromJson(json,jsonClass);
    }
}
