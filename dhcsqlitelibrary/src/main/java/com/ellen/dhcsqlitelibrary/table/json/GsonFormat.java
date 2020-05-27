package com.ellen.dhcsqlitelibrary.table.json;

import com.google.gson.Gson;

class GsonFormat implements JsonFormat {

    private Object gsonObject;
    private Gson gson;

    GsonFormat(Object gsonObject) {
        this.gsonObject = gsonObject;
        gson = new Gson();
    }


    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return (E) new Gson().fromJson(json, jsonClass);
    }

}
