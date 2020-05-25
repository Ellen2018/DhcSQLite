package com.ellen.dhcsqlitelibrary.table.json;

import android.util.Log;

import com.ellen.dhcsqlitelibrary.table.exception.JsonNoCanFormatException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JsonHelper implements JsonFormat {

    private JsonFormat jsonFormat;

    public JsonHelper(JsonLibraryType jsonLibraryType) {
        Class gsonClass = null;
        Class fastJsonClass = null;
        try {
            if (jsonLibraryType == JsonLibraryType.Gson) {
                //使用Gson
                gsonClass = Class.forName("com.google.gson.Gson");
            } else if (jsonLibraryType == JsonLibraryType.FastJson) {
                //使用FastJson
                fastJsonClass = Class.forName("com.alibaba.fastjson.JSONObject");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            gsonClass = null;
            fastJsonClass = null;
        }

        if (gsonClass == null && fastJsonClass == null) {
            //报异常
            throw new JsonNoCanFormatException("无法进行json映射,可能是没有导入Gson或者FastJson库");
        } else {
            try {
                if (gsonClass != null) {
                    jsonFormat = new GsonFormat(getT(gsonClass));
                } else if (fastJsonClass != null) {
                    jsonFormat = new FastJsonFormat(fastJsonClass);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private <T> T getT(Class dataClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] constructors = dataClass.getDeclaredConstructors();
        Constructor targetConstructor = null;
        for (Constructor constructor : constructors) {
            constructor.setAccessible(true);
            Class[] classArray = constructor.getParameterTypes();
            if (classArray != null && classArray.length == 0) {
                targetConstructor = constructor;
                break;
            }
        }
        return (T) targetConstructor.newInstance();
    }

    @Override
    public String toJson(Object obj) {
        return jsonFormat.toJson(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return jsonFormat.toObject(json, jsonClass);
    }
}
