package com.ellen.dhcsqlitelibrary.table.helper.json;

import com.ellen.dhcsqlitelibrary.table.exception.JsonNoCanFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JxHelper implements JxFormat {

    private JxFormat jxFormat;

    public JxHelper(JsonLibraryType jsonLibraryType) {
        Class gsonClass = null;
        Class fastJsonClass = null;
        try {
            if (jsonLibraryType == JsonLibraryType.GSON) {
                //使用Gson
                gsonClass = Class.forName("com.google.gson.Gson");
            } else if (jsonLibraryType == JsonLibraryType.FAST_JSON) {
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
            throw new JsonNoCanFormatException("无法进行json映射,无可用的Json解析器");
        } else {
            try {
                if (gsonClass != null) {
                    jxFormat = new GsonFormat(getT(gsonClass));
                } else if (fastJsonClass != null) {
                    jxFormat = new FastJxFormat(fastJsonClass);
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
    public String toJxString(Object obj) {
        return jxFormat.toJxString(obj);
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        return jxFormat.toObject(json, jsonClass);
    }
}
