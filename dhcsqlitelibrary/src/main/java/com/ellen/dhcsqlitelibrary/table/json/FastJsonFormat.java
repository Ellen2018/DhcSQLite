package com.ellen.dhcsqlitelibrary.table.json;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class FastJsonFormat implements JsonFormat {

    private Class fastJsonClass;


    public FastJsonFormat(Class fastJsonClass) {
        this.fastJsonClass = fastJsonClass;
    }

    @Override
    public String toJson(Object obj) {
//        String json = null;
//        try {
//            Method toJson = fastJsonClass.getMethod("toJSONString", Object.class);
//            toJson.setAccessible(true);
//            json = (String) toJson.invoke(null,obj);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return (String) JSONObject.toJSONString(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
//        Object object =  null;
//        try {
//            Method m = fastJsonClass.getMethod("parseObject", String.class,Class.class);
//            object = m.invoke(null,json,jsonClass);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return (E) JSONObject.parseObject(json,jsonClass);
    }

    @Override
    public <T> String toJsonByList(List<T> tList) {
        return (String) JSONObject.toJSONString(tList);
    }

}
