package com.ellen.dhcsqlitelibrary.table.json;

import android.renderscript.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GsonFormat implements JsonFormat {

    private Object gsonObject;
    private Gson gson;

    public GsonFormat(Object gsonObject) {
        this.gsonObject = gsonObject;
        gson = new Gson();
    }


    @Override
    public String toJson(Object obj) {
//        Class gsonClass = gsonObject.getClass();
//        Method toJson = null;
//        String json = null;
//        try {
//            toJson = gsonClass.getDeclaredMethod("toJson",Object.class);
//            toJson.setAccessible(true);
//            json = (String) toJson.invoke(gsonObject,obj);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return gson.toJson(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
//        Class gsonClass = gsonObject.getClass();
//        Method toObject = null;
//        Object obj = null;
//        try {
//            toObject = gsonClass.getDeclaredMethod("fromJson",String.class,Class.class);
//            toObject.setAccessible(true);
//            obj = toObject.invoke(gsonObject,json,jsonClass);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return (E) new Gson().fromJson(json, jsonClass);
    }

    @Override
    public <T> String toJsonByList(List<T> tList) {
        return gson.toJson(tList);
    }
}
