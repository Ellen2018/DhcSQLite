package com.ellen.dhcsqlite;

import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.google.gson.Gson;

/**
 * 自定义Json解析器
 */
public class MyJsonFormat implements JsonFormat {

    private Gson gson;

    public MyJsonFormat(){
        gson = new Gson();
    }

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return (E) gson.fromJson(json,jsonClass);
    }
}
