package com.ellen.dhcsqlitelibrary.table.helper.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class FastJxFormat implements JxFormat {

    private Class fastJsonClass;

    public FastJxFormat(Class fastJsonClass) {
        this.fastJsonClass = fastJsonClass;
    }

    @Override
    public String toJxString(Object obj) {
        String json = null;
        try {
            Method toJson = fastJsonClass.getMethod("toJSONString", Object.class);
            toJson.setAccessible(true);
            json = (String) toJson.invoke(null,obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public <E> E toObject(String json, Class jsonClass) {
        Object object =  null;
        try {
            Method m = fastJsonClass.getMethod("parseObject", String.class,Class.class);
            object = m.invoke(null,json,jsonClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (E) object;
    }
}
