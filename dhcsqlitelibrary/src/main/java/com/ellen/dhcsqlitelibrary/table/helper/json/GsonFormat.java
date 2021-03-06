package com.ellen.dhcsqlitelibrary.table.helper.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class GsonFormat implements JxFormat {

    private Object gsonObject;

    GsonFormat(Object gsonObject) {
        this.gsonObject = gsonObject;
    }

    @Override
    public String toJxString(Object obj) {
        Class gsonClass = gsonObject.getClass();
        Method toJson = null;
        String json = null;
        try {
            toJson = gsonClass.getDeclaredMethod("toJson",Object.class);
            toJson.setAccessible(true);
            json = (String) toJson.invoke(gsonObject,obj);
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
        Class gsonClass = gsonObject.getClass();
        Method toObject = null;
        Object obj = null;
        try {
            toObject = gsonClass.getDeclaredMethod("fromJson",String.class,Class.class);
            toObject.setAccessible(true);
            obj = toObject.invoke(gsonObject,json,jsonClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (E) obj;
    }

}
