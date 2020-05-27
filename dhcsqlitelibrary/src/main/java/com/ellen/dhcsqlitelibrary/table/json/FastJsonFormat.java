package com.ellen.dhcsqlitelibrary.table.json;

import com.alibaba.fastjson.JSONObject;

class FastJsonFormat implements JsonFormat {

    @Override
    public String toJson(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return (E) JSONObject.parseObject(json,jsonClass);
    }
}
