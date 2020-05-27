package com.ellen.dhcsqlitelibrary.table.json;

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
            if(jsonLibraryType == JsonLibraryType.Gson) {
                throw new JsonNoCanFormatException("无法进行json映射,因为您选择了使用com.google.gson.Gson进行Json映射,但您未导入此库。");
            }else {
                throw new JsonNoCanFormatException("无法进行json映射,因为您选择了使用com.alibaba.fastjson.JSONObject进行Json映射,但您未导入此库。");
            }
        } else {
            try {
                if (gsonClass != null) {
                    jsonFormat = new GsonFormat(getT(gsonClass));
                } else if (fastJsonClass != null) {
                    jsonFormat = new FastJsonFormat();
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
