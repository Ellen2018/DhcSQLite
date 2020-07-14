package com.ellen.dhcsqlitelibrary.table.impl;

import com.ellen.dhcsqlitelibrary.table.helper.json.JsonFormat;
import com.ellen.dhcsqlitelibrary.table.helper.json.JsonLibraryType;

/**
 * 设置库内部表操作的公共配置
 */
public class CommonSetting {

    /**
     * 自定义Json解析器
     */
    private JsonFormat jsonFormat = null;
    /**
     * 指定内部Json解析器
     */
    private JsonLibraryType jsonLibraryType = JsonLibraryType.Gson;
    /**
     * 是否开启多线程模式
     */
    private boolean isMultiThreadSafety = false;

    public JsonFormat getJsonFormat() {
        return jsonFormat;
    }

    public void setJsonFormat(JsonFormat jsonFormat) {
        this.jsonFormat = jsonFormat;
    }

    public JsonLibraryType getJsonLibraryType() {
        return jsonLibraryType;
    }

    public void setJsonLibraryType(JsonLibraryType jsonLibraryType) {
        this.jsonLibraryType = jsonLibraryType;
    }

    public boolean isMultiThreadSafety() {
        return isMultiThreadSafety;
    }

    public void setMultiThreadSafety(boolean multiThreadSafety) {
        isMultiThreadSafety = multiThreadSafety;
    }
}
